package com.likeapig.elimination.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Cuboid {
	
	private final Location loc1;
    private final Location loc2;
    private final int minX;
    private final int minY;
    private final int minZ;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private final World world;
    private List<BlockState> backup;
    
    public Cuboid(final Location loc1, final Location loc2) {
        this.backup = new ArrayList<BlockState>();
        if (loc1.getWorld() == loc2.getWorld()) {
            this.loc1 = loc1;
            this.loc2 = loc2;
            this.world = loc1.getWorld();
            if (loc1.getX() > loc2.getX()) {
                this.minX = loc2.getBlockX();
                this.maxX = loc1.getBlockX();
            }
            else {
                this.maxX = loc2.getBlockX();
                this.minX = loc1.getBlockX();
            }
            if (loc1.getY() > loc2.getY()) {
                this.minY = loc2.getBlockY();
                this.maxY = loc1.getBlockY();
            }
            else {
                this.maxY = loc2.getBlockY();
                this.minY = loc1.getBlockY();
            }
            if (loc1.getZ() > loc2.getZ()) {
                this.minZ = loc2.getBlockZ();
                this.maxZ = loc1.getBlockZ();
            }
            else {
                this.maxZ = loc2.getBlockZ();
                this.minZ = loc1.getBlockZ();
            }
            return;
        }
        throw new IllegalArgumentException("specified locations aren't in the same world");
    }
    
    public Location getMiddleBottomBlock() {
        final double x = this.minX + (this.maxX - this.minX) / 2;
        final double y = this.minY;
        final double z = this.minZ + (this.maxZ - this.minZ) / 2;
        final Location l = new Location(this.world, x, y, z);
        return l;
    }
    
    public void killEntities() {
        for (final Entity e : this.loc1.getWorld().getEntities()) {
            if (!(e instanceof Player) && this.contains(e.getLocation())) {
                e.remove();
            }
        }
    }
    
    public void save() {
        this.backup.clear();
        for (final Block b : this.getBlocks()) {
            this.backup.add(b.getState());
        }
    }
    
    public void restoreBackup() {
        if (this.backup.size() <= 0) {
            return;
        }
        for (final BlockState b : this.backup) {
            if (!b.getLocation().getBlock().getType().equals((Object)b.getType())) {
                b.getLocation().getBlock().setType(b.getType());
            }
            b.update();
        }
        this.killEntities();
    }
    
    public List<Block> getBlocks() {
        final List<Block> blocks = new ArrayList<Block>();
        final World w = this.loc1.getWorld();
        for (int x2 = this.minX; x2 < this.maxX; ++x2) {
            for (int y2 = this.minY; y2 < this.maxY; ++y2) {
                for (int z2 = this.minZ; z2 < this.maxZ; ++z2) {
                    blocks.add(w.getBlockAt(x2, y2, z2));
                }
            }
        }
        return blocks;
    }
    
    public List<BlockState> removeBlocks(final Material material) {
        final List<BlockState> states = new ArrayList<BlockState>();
        for (final Block b : this.getBlocks()) {
            if (b.getType().equals((Object)material)) {
                states.add(b.getState());
                b.setType(Material.AIR);
            }
        }
        return states;
    }
    
    public List<BlockState> removeBlocks(final Material material, final byte data) {
        final List<BlockState> states = new ArrayList<BlockState>();
        for (final Block b : this.getBlocks()) {
            if (b.getType().equals((Object)material) && b.getData() == data) {
                states.add(b.getState());
                b.setType(Material.AIR);
            }
        }
        return states;
    }
    
    public Location getLoc1() {
        return this.loc1;
    }
    
    public Location getLoc2() {
        return this.loc2;
    }
    
    public boolean contains(final Location l) {
        final int x = l.getBlockX();
        final int y = l.getBlockY();
        final int z = l.getBlockZ();
        return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY && z >= this.minZ && z <= this.maxZ;
    }

}
