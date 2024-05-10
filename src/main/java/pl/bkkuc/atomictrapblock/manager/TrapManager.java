package pl.bkkuc.atomictrapblock.manager;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import pl.bkkuc.atomictrapblock.Plugin;
import pl.bkkuc.atomictrapblock.utilities.FileUtility;
import pl.bkkuc.atomictrapblock.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TrapManager {

    List<TrapBlock> trapBlocks;

    public TrapManager(){
        this.trapBlocks = new ArrayList<>();
    }

    public void loadBlocks(){
        ConfigurationSection section = Plugin.getInstance().getDataFile().getConfigurationSection("blocks");
        if(section == null) return;

        for(String formatLocation: section.getKeys(false)){
            TrapBlock trapBlock = new TrapBlock(formatLocation);
            trapBlocks.add(trapBlock);
        }
    }

    public void register(Block block, Player owner, boolean createData){
        if(!block.hasMetadata("trapblock")) return;

        TrapBlock trapBlock = new TrapBlock(Utility.toFormat(block.getLocation()));
        trapBlocks.add(trapBlock);

        if(createData){
            Plugin.getInstance().getDataFile().set("blocks." + Utility.toFormat(block.getLocation()), owner.getName());
            FileUtility.save(Plugin.getInstance().getDataFile(), "data.yml");
        }
    }

    public void unregister(Block block, boolean removeData){
        TrapBlock trapBlock = getTrapBlockByBlock(block);
        if(trapBlock == null) return;

        trapBlock.cancelTask();

        trapBlocks.remove(trapBlock);

        if(removeData) {
            Plugin.getInstance().getDataFile().set("blocks." + Utility.toFormat(block.getLocation()), null);
            FileUtility.save(Plugin.getInstance().getDataFile(), "data.yml");
        }
    }

    public boolean isRegistered(Block block){
        return Plugin.getInstance().getDataFile().get("blocks." + Utility.toFormat(block.getLocation())) != null;
    }

    public TrapBlock getTrapBlockByBlock(Block block){
        return trapBlocks.stream().filter(trapBlock -> trapBlock.getBlock().getLocation().distance(block.getLocation()) == 0 && block.hasMetadata("trapblock")).findFirst().orElse(null);
    }
}
