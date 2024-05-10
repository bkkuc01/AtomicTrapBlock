package pl.bkkuc.atomictrapblock.utilities;

import org.bukkit.*;
import org.bukkit.entity.Player;
import pl.bkkuc.atomictrapblock.Plugin;

import java.util.Arrays;

public class Utility {

    public static void doActions(Player player){
        for(String actions: Plugin.getInstance().getConfig().getStringList("actions")){
            String[] params = actions.split(" ");
            switch (params[0].toLowerCase()){
                case "[title]": {
                    String[] title = color(String.join(" ", Arrays.copyOfRange(params, 1, params.length))).split(";");
                    player.sendTitle(title[0], title.length >= 2 ? title[1] : "");
                    break;
                }
                case "[actionbar]": {
                    player.sendActionBar(color(String.join(" ", Arrays.copyOfRange(params, 1, params.length))));
                    break;
                }
                case "[message]": {
                    player.sendMessage(color(String.join(" ", Arrays.copyOfRange(params, 1, params.length))));
                    break;
                }
                case "[sound]": {
                    Sound sound;
                    try {
                        sound = Sound.valueOf(params[1].toUpperCase());
                    } catch (IllegalArgumentException e){
                        Plugin.getInstance().getLogger().warning("Звук '" + params[1].toUpperCase() + "' не найден.");
                        break;
                    }

                    float volume = params.length >= 3 ? Float.parseFloat(params[2]) : 1.0f;
                    float pitch = params.length >= 4 ? Float.parseFloat(params[3]) : 1.0f;

                    player.playSound(player.getLocation(), sound, volume, pitch);

                    break;
                }
            }
        }
    }

    public static Location toLocation(String formatLocation){
        String[] params = formatLocation.split(";");

        World world = Bukkit.getWorld(params[0]);
        if(world == null){
            throw new NullPointerException("World " + params[0] + " is not found.");
        }

        return new Location(world, Integer.parseInt(params[1]), Integer.parseInt(params[2]), Integer.parseInt(params[3]));
    }

    public static String toFormat(Location location){
        return location.getWorld().getName() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
    }

    public static String color(String text){
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String getString(String path){
        return color(Plugin.getInstance().getConfig().getString(path, "invalid path: " + path));
    }
}
