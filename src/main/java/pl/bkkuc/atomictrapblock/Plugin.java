package pl.bkkuc.atomictrapblock;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import pl.bkkuc.atomictrapblock.commands.MainCommand;
import pl.bkkuc.atomictrapblock.listeners.PlayerListener;
import pl.bkkuc.atomictrapblock.manager.TrapManager;
import pl.bkkuc.atomictrapblock.utilities.FileUtility;

@Getter
public final class Plugin extends JavaPlugin {

    @Getter
    private static Plugin instance;

    private FileConfiguration dataFile;

    private TrapManager trapManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        dataFile = FileUtility.get("data.yml");

        trapManager = new TrapManager();
        trapManager.loadBlocks();

        getCommand("trapblock").setExecutor(new MainCommand());
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
