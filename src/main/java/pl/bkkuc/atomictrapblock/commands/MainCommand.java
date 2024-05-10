package pl.bkkuc.atomictrapblock.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.bkkuc.atomictrapblock.item.ItemBuilder;

public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.isOp()) return true;

        if(args.length == 0){
            sender.sendMessage("Введите никнейм игрока.");
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if(player == null) {
            sender.sendMessage("Игрок не найден.");
            return true;
        }

        ItemBuilder.giveItem(player);

        sender.sendMessage("Был успешно выдан для " + args[0]);
        return true;
    }
}
