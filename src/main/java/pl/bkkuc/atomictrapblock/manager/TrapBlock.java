package pl.bkkuc.atomictrapblock.manager;

import com.destroystokyo.paper.ParticleBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pl.bkkuc.atomictrapblock.Plugin;
import pl.bkkuc.atomictrapblock.utilities.Utility;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TrapBlock {

    Block block;
    String ownerName;

    @NonFinal
    BukkitTask task;

    @NonFinal
    boolean active;

    @NonFinal
    ParticleBuilder particleBuilder;

    @NonFinal
    BlockState state;
    @NonFinal
    boolean broken;

    public TrapBlock(String formatLocation){
        Block b = Utility.toLocation(formatLocation).getBlock();
        if(b.hasMetadata("trapblock")){
            this.block = b;
        }
        else {
            throw new IllegalArgumentException("Block in location '" + formatLocation + "' hasn't meta data!");
        }
        this.ownerName = Plugin.getInstance().getDataFile().getString("blocks." + Utility.toFormat(block.getLocation()));
        if(Plugin.getInstance().getConfig().getBoolean("particle.enable", true)){
            Particle particle = null;
            try {
                particle = Particle.valueOf(Plugin.getInstance().getConfig().getString("particle.particle", "FLAME").toUpperCase());
            } catch (IllegalArgumentException e){
                Plugin.getInstance().getLogger().warning("Партикл '" + Plugin.getInstance().getConfig().getString("particle.particle", "FLAME").toUpperCase());
            }
            if(particle != null){
                this.particleBuilder = new ParticleBuilder(particle);
                if(Plugin.getInstance().getConfig().get("particle.count") != null){
                    particleBuilder.count(Plugin.getInstance().getConfig().getInt("particle.count"));
                }
                if(Plugin.getInstance().getConfig().get("particle.extra") != null){
                    particleBuilder.extra(Plugin.getInstance().getConfig().getDouble("particle.extra"));
                }
                particleBuilder.location(block.getLocation().clone().add(0.5, 1, 0.5));
            }
        }
        this.broken = false;
        this.active = false;

        task = new BukkitRunnable(){
            int particleDelay = 5;
            @Override
            public void run() {
                try {
                    if (!Plugin.getInstance().getTrapManager().isRegistered(block)) {
                        cancel();
                        return;
                    }
                    if(particleBuilder != null) {
                        if(particleDelay == 0) {
                            if (Plugin.getInstance().getConfig().getBoolean("particle.always")) {
                                particleBuilder.spawn();
                            } else {
                                if (!broken) {
                                    particleBuilder.spawn();
                                }
                            }
                            particleDelay = 5;
                        }
                        else {
                            particleDelay--;
                        }
                    }
                    if (!active) {
                        List<Player> players = block.getLocation().clone().add(0.5, 1.5, 0.5).getNearbyPlayers(0.2)
                                .stream().filter(player -> !player.hasPermission("trapblock.bypass")).collect(Collectors.toList());

                        if(Plugin.getInstance().getConfig().getBoolean("owner-can-trapped", true)){
                            players.stream().filter(player -> player.getName().equals(ownerName)).findFirst().ifPresent(players::remove);
                        }

                        if(!players.isEmpty()){
                            destroyTask();
                        }
                        players.forEach(player -> {
                            if (player.hasPermission("atomictrapblock.info")) {
                                Utility.doActions(player);
                            }
                        });
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    cancel();
                }
            }
        }.runTaskTimer(Plugin.getInstance(), 0L, 5L);
    }

    public void destroyTask() {
        active = true;
        state = block.getState();
        new BukkitRunnable() {
            @Override
            public void run() {
                block.setType(Material.AIR);
                broken = true;
                restoreTask(false);
            }
        }.runTaskLater(Plugin.getInstance(), 20L * Plugin.getInstance().getConfig().getInt("item.time.break", 15));
    }

    public void restoreTask(boolean force){
        if(force){
            if(state != null) {
                state.update(true);
                broken = false;
            }
            active = false;
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if(state != null) {
                    state.update(true);
                    broken = false;
                }
                active = false;
            }
        }.runTaskLater(Plugin.getInstance(), 20L * Plugin.getInstance().getConfig().getInt("item.time.restore", 15));
    }

    public void cancelTask(){
        if(task != null) task.cancel();
    }
}
