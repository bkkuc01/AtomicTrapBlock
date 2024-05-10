package pl.bkkuc.atomictrapblock.listeners;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import pl.bkkuc.atomictrapblock.Plugin;

public class PlayerListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    private void onBlockPlace(BlockPlaceEvent e){
        ItemStack item = e.getItemInHand();
        NBTItem nbtItem = new NBTItem(item);

        if(!nbtItem.hasTag("trapblock")) return;

        Block block = e.getBlockPlaced();
        block.setMetadata("trapblock", new FixedMetadataValue(Plugin.getInstance(), true));
        Plugin.getInstance().getTrapManager().register(block, e.getPlayer(), true);
    }

    @EventHandler(ignoreCancelled = true)
    private void onBlockBreak(BlockBreakEvent e){
        Block block = e.getBlock();
        e.setDropItems(false);
        Plugin.getInstance().getTrapManager().unregister(block, true);
    }
}
