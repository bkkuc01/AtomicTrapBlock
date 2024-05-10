package pl.bkkuc.atomictrapblock.item;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.bkkuc.atomictrapblock.Plugin;
import pl.bkkuc.atomictrapblock.utilities.Utility;

import java.util.stream.Collectors;

public class ItemBuilder {

    public static void giveItem(Player player){
        ItemStack item = new ItemStack(Material.valueOf(Plugin.getInstance().getConfig().getString("item.material", "STONE")));
        ItemMeta meta = item.getItemMeta();

        if(Plugin.getInstance().getConfig().get("item.name") != null) meta.setDisplayName(Utility.getString("item.name"));
        if(Plugin.getInstance().getConfig().get("item.lore") != null) meta.setLore(Plugin.getInstance().getConfig().getStringList("item.lore").stream().map(Utility::color).collect(Collectors.toList()));

        item.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("trapblock", "notex_gey");
        nbtItem.applyNBT(item);

        player.getInventory().addItem(item);
    }
}
