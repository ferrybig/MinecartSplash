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
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class MinecartSplash extends JavaPlugin implements Listener {

    private final static Random random = new Random();
    private final static Material[] liquidBlocks = new Material[]{
        Material.WATER, Material.STATIONARY_WATER,
        Material.LAVA, Material.STATIONARY_LAVA,};
    private final static String UNIQUE_METADATA
            = MinecartSplash.class.getName() + "AlwaysBreak";

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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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
                    subtract(speed.clone().multiply(4)).
                    toLocation(loc.getWorld()).getBlock();
            if (isLiquid(currentBlock) && !isLiquid(lastBlock)) {
                speed.setY(-speed.getY());
                loc.add(speed.clone().normalize());
                for (int i = 0; i < 5; i++) {
                    FallingBlock fb = minecart.getWorld().
                            spawnFallingBlock(loc, currentBlock.getType(),
                                    (byte) 0);
                    Vector r = speed.clone();
                    double s;
                    if((s = r.lengthSquared()) > 5 * 5) {
                        s = Math.sqrt(s);
                        r.divide(new Vector(s,s,s));
                        r.multiply(5);
                    }
                    r.multiply(1f / Math.sqrt(r.length()));
                    r.add(new Vector(
                            MinecartSplash.random.nextDouble() -.5,
                            MinecartSplash.random.nextDouble() -.5,
                            MinecartSplash.random.nextDouble() -.5)
                            .multiply(0.5 * 0.9));
                    fb.setVelocity(r);
                    fb.setDropItem(false);
                    fb.setMetadata(UNIQUE_METADATA,
                            new FixedMetadataValue(this, loc));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(EntityChangeBlockEvent event) {
        if (!event.getEntity().hasMetadata(UNIQUE_METADATA)) {
            return;
        }
        event.setCancelled(true);
    }
}
