package me.ferrybig.bukkit.plugins.minecartsplash;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class MinecartSplash extends JavaPlugin implements Listener {

    private final static Random random = new Random();
    private final static Material[] liquidBlocks = new Material[]{
        Material.WATER, Material.STATIONARY_WATER,
        Material.LAVA, Material.STATIONARY_LAVA,};

    private boolean isLiquid(Block bl) {
        for (Material m : liquidBlocks) {
            if (bl.getType() == m) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMinecartMove(VehicleMoveEvent event) {
        if (event.getVehicle() instanceof Minecart) {
            Minecart minecart = (Minecart) event.getVehicle();
            Location loc = minecart.getLocation();
            Vector speed = minecart.getVelocity();
            if (speed.lengthSquared() < 0.2) {
                return;
            }
            Block currentBlock = loc.getBlock();
            Block lastBlock = loc.toVector().
                    subtract(speed.clone().normalize().multiply(2)).
                    toLocation(loc.getWorld()).getBlock();
            if (isLiquid(currentBlock) && !isLiquid(lastBlock)) {
                speed.setY(-speed.getY());
                for (int i = 0; i < 5; i++) {
                    FallingBlock fb = minecart.getWorld().
                            spawnFallingBlock(loc, currentBlock.getType(), (byte) 0);
                    Vector r = speed.clone();
                    r.add(new Vector(
                            MinecartSplash.random.nextGaussian(),
                            MinecartSplash.random.nextGaussian(),
                            MinecartSplash.random.nextGaussian())
                    .multiply(0.2));
                    fb.setVelocity(r);
                }
            }
        }
    }
}
