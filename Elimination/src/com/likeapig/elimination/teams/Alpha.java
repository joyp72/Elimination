package com.likeapig.elimination.teams;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import com.likeapig.elimination.Main;
import com.likeapig.elimination.maps.Map;
import com.likeapig.elimination.particles.ParticleEffect;

import net.md_5.bungee.api.ChatColor;

public class Alpha {

	private UUID id;
	private Location loc;
	private Map map;
	private GameMode gm;
	private ItemStack[] inv;
	private ItemStack[] armor;
	private int level;
	private float xp;
	private Location deathLoc;
	private boolean isDead;
	private double health;
	private ArmorStand AS;
	private int pe1;
	private int cdStage;

	public Alpha(Player p, Map m) {
		loc = p.getLocation();
		id = p.getUniqueId();
		map = m;
		gm = p.getGameMode();
		inv = p.getInventory().getContents();
		armor = p.getInventory().getArmorContents();
		level = p.getLevel();
		xp = p.getExp();
		health = p.getHealth();
		cdStage = 1;
	}

	public void ready() {
		Player p = Bukkit.getPlayer(id);
		if (p.getGameMode() != GameMode.SURVIVAL) {
			p.setGameMode(GameMode.SURVIVAL);
		}
		p.getInventory().clear();
		p.setLevel(0);
		p.setExp(0f);
		p.setHealth(20);
		p.setFoodLevel(20);
		removeNameTag();
	}

	public void restore() {
		Player p = Bukkit.getPlayer(id);
		p.teleport(loc);
		p.setGameMode(gm);
		p.getInventory().setContents(inv);
		p.getInventory().setArmorContents(armor);
		p.setLevel(level);
		p.setExp(xp);
		p.setHealth(health);
	}

	public Map getMap() {
		return map;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(id);
	}

	public Location getLoc() {
		return loc;
	}

	public void setDeathLoc(Location l) {
		deathLoc = l;
	}

	public Location getDeathLoc() {
		return deathLoc;
	}
	
	public int getCDStage() {
		return cdStage;
	}
	
	public void setCDStage(int i) {
		cdStage = i;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean b) {
		isDead = b;
	}

	public void addNameTag(Location loc) {
		Location l = loc.clone();
		
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		{
			SkullMeta hmeta = (SkullMeta) head.getItemMeta();
			hmeta.setOwner(getPlayer().getName());
			head.setItemMeta(hmeta);
		}
		
		AS = l.getWorld().spawn(loc, ArmorStand.class);
		AS.setGravity(true);
		AS.setCollidable(false);
		AS.setVisible(false);
		AS.setInvulnerable(true);
		AS.setCustomNameVisible(true);
		AS.setCustomName(ChatColor.RED + getPlayer().getName());
		AS.setSilent(true);
		AS.setGliding(false);
		AS.setCanPickupItems(false);
		AS.setHelmet(head);
		AS.setSmall(true);
		AS.setAI(false);
	}
	
	public void removeNameTag() {
		if (AS != null) {
			AS.remove();
		}
	}

	public void playDeathCircle(Location loc) {
		
		addNameTag(loc);
		pe1 = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.get(), new Runnable() {

			int i = 0;
			int p = 74;
			int f = 0;
			Location l = loc.clone();

			@Override
			public void run() {
				
				if (AS.getLocation().getX() != loc.getX() || AS.getLocation().getY() != loc.getY() || AS.getLocation().getZ() != loc.getZ()) {
					AS.teleport(loc);
				}
				
				final ArrayList<Location> locs = getCircle(l, 1, 75);
				final ArrayList<Location> locs2 = getCircleReverse(l, 1, 75);
				final float speed = 0.1f;
				final Vector v = locs.get(i).toVector().subtract(l.toVector()).normalize();
				final Vector v2 = locs2.get(i).toVector().subtract(l.toVector()).normalize();
				final Vector v3 = locs.get(p).toVector().subtract(l.toVector()).normalize();
				final Vector v4 = locs2.get(p).toVector().subtract(l.toVector()).normalize();
				// ParticleEffect.INSTANT_SPELL.display(v.multiply(-2), speed, locs.get(i),
				// 100.0);
				// ParticleEffect.INSTANT_SPELL.display(v2.multiply(-2), speed, locs2.get(i),
				// 100.0);
				// ParticleEffect.INSTANT_SPELL.display(v3.multiply(-2), speed, locs.get(p),
				// 100.0);
				// ParticleEffect.INSTANT_SPELL.display(v4.multiply(-2), speed, locs2.get(p),
				// 100.0);
				displayColoredParticle(locs.get(i), "FF0000");
				displayColoredParticle(locs2.get(i), "FF0000");
				displayColoredParticle(locs.get(p), "FF0000");
				displayColoredParticle(locs2.get(p), "FF0000");

				++i;
				++f;
				--p;
				if (i == 75) {
					i = 0;
				}
				if (p == 0) {
					p = 74;
				}
				if (f == 200) {
					return;
				}

				final ArrayList<Location> locs3 = getCircle(l, 1, 75);
				for (final Location ia : locs3) {
					// ParticleEffect.INSTANT_SPELL.display(0.0f, 0.0f, 0.0f, 0.0f, 1, ia, 100.0);
					displayColoredParticle(ia, "FF0000");
				}
			}
		}, 0L, 0L);
	}

	public void removeDeathCircle() {
		Bukkit.getServer().getScheduler().cancelTask(pe1);
	}

	//

	public static void displayColoredParticle(Location loc, ParticleEffect type, String hexVal, float xOffset,
			float yOffset, float zOffset) {
		int R = 0;
		int G = 0;
		int B = 0;

		if (hexVal.length() <= 6) {
			R = Integer.valueOf(hexVal.substring(0, 2), 16);
			G = Integer.valueOf(hexVal.substring(2, 4), 16);
			B = Integer.valueOf(hexVal.substring(4, 6), 16);
			if (R <= 0) {
				R = 1;
			}
		} else if (hexVal.length() <= 7 && hexVal.substring(0, 1).equals("#")) {
			R = Integer.valueOf(hexVal.substring(1, 3), 16);
			G = Integer.valueOf(hexVal.substring(3, 5), 16);
			B = Integer.valueOf(hexVal.substring(5, 7), 16);
			if (R <= 0) {
				R = 1;
			}
		}

		loc.setX(loc.getX() + Math.random() * (xOffset / 2 - -(xOffset / 2)));
		loc.setY(loc.getY() + Math.random() * (yOffset / 2 - -(yOffset / 2)));
		loc.setZ(loc.getZ() + Math.random() * (zOffset / 2 - -(zOffset / 2)));

		if (type == ParticleEffect.RED_DUST || type == ParticleEffect.REDSTONE) {
			ParticleEffect.RED_DUST.display(R, G, B, 0.004F, 0, loc, 255.0);
		} else if (type == ParticleEffect.SPELL_MOB || type == ParticleEffect.MOB_SPELL) {
			ParticleEffect.SPELL_MOB.display((float) 255 - R, (float) 255 - G, (float) 255 - B, 1, 0, loc, 255.0);
		} else if (type == ParticleEffect.SPELL_MOB_AMBIENT || type == ParticleEffect.MOB_SPELL_AMBIENT) {
			ParticleEffect.SPELL_MOB_AMBIENT.display((float) 255 - R, (float) 255 - G, (float) 255 - B, 1, 0, loc,
					255.0);
		} else {
			ParticleEffect.RED_DUST.display(0, 0, 0, 0.004F, 0, loc, 255.0D);
		}
	}

	public static void displayColoredParticle(Location loc, String hexVal) {
		int R = 0;
		int G = 0;
		int B = 0;

		if (hexVal.length() <= 6) {
			R = Integer.valueOf(hexVal.substring(0, 2), 16);
			G = Integer.valueOf(hexVal.substring(2, 4), 16);
			B = Integer.valueOf(hexVal.substring(4, 6), 16);
			if (R <= 0) {
				R = 1;
			}
		} else if (hexVal.length() <= 7 && hexVal.substring(0, 1).equals("#")) {
			R = Integer.valueOf(hexVal.substring(1, 3), 16);
			G = Integer.valueOf(hexVal.substring(3, 5), 16);
			B = Integer.valueOf(hexVal.substring(5, 7), 16);
			if (R <= 0) {
				R = 1;
			}
		}
		ParticleEffect.RED_DUST.display(R, G, B, 0.004F, 0, loc, 257D);
	}

	public static void displayColoredParticle(Location loc, String hexVal, float xOffset, float yOffset,
			float zOffset) {
		int R = 0;
		int G = 0;
		int B = 0;

		if (hexVal.length() <= 6) {
			R = Integer.valueOf(hexVal.substring(0, 2), 16);
			G = Integer.valueOf(hexVal.substring(2, 4), 16);
			B = Integer.valueOf(hexVal.substring(4, 6), 16);
			if (R <= 0) {
				R = 1;
			}
		} else if (hexVal.length() <= 7 && hexVal.substring(0, 1).equals("#")) {
			R = Integer.valueOf(hexVal.substring(1, 3), 16);
			G = Integer.valueOf(hexVal.substring(3, 5), 16);
			B = Integer.valueOf(hexVal.substring(5, 7), 16);
			if (R <= 0) {
				R = 1;
			}
		}

		loc.setX(loc.getX() + Math.random() * (xOffset / 2 - -(xOffset / 2)));
		loc.setY(loc.getY() + Math.random() * (yOffset / 2 - -(yOffset / 2)));
		loc.setZ(loc.getZ() + Math.random() * (zOffset / 2 - -(zOffset / 2)));

		ParticleEffect.RED_DUST.display(R, G, B, 0.004F, 0, loc, 257D);
	}

	public static void displayParticleVector(Location loc, ParticleEffect type, float xTrans, float yTrans,
			float zTrans) {
		if (type == ParticleEffect.FIREWORKS_SPARK) {
			ParticleEffect.FIREWORKS_SPARK.display(xTrans, yTrans, zTrans, 0.09F, 0, loc, 257D);
		} else if (type == ParticleEffect.SMOKE || type == ParticleEffect.SMOKE_NORMAL) {
			ParticleEffect.SMOKE.display(xTrans, yTrans, zTrans, 0.04F, 0, loc, 257D);
		} else if (type == ParticleEffect.LARGE_SMOKE || type == ParticleEffect.SMOKE_LARGE) {
			ParticleEffect.LARGE_SMOKE.display(xTrans, yTrans, zTrans, 0.04F, 0, loc, 257D);
		} else if (type == ParticleEffect.ENCHANTMENT_TABLE) {
			ParticleEffect.ENCHANTMENT_TABLE.display(xTrans, yTrans, zTrans, 0.5F, 0, loc, 257D);
		} else if (type == ParticleEffect.PORTAL) {
			ParticleEffect.PORTAL.display(xTrans, yTrans, zTrans, 0.5F, 0, loc, 257D);
		} else if (type == ParticleEffect.FLAME) {
			ParticleEffect.FLAME.display(xTrans, yTrans, zTrans, 0.04F, 0, loc, 257D);
		} else if (type == ParticleEffect.CLOUD) {
			ParticleEffect.CLOUD.display(xTrans, yTrans, zTrans, 0.04F, 0, loc, 257D);
		} else if (type == ParticleEffect.SNOW_SHOVEL) {
			ParticleEffect.SNOW_SHOVEL.display(xTrans, yTrans, zTrans, 0.2F, 0, loc, 257D);
		} else {
			ParticleEffect.RED_DUST.display(0, 0, 0, 0.004F, 0, loc, 257D);
		}
	}

	public static ArrayList<Location> getCircle(final Location center, final double radius, final int amount) {
		final World world = center.getWorld();
		final double increment = 6.283185307179586 / amount;
		final ArrayList<Location> locations = new ArrayList<Location>();
		for (int i = 0; i < amount; ++i) {
			final double angle = i * increment;
			final double x = center.getX() + radius * Math.cos(angle);
			final double z = center.getZ() + radius * Math.sin(angle);
			locations.add(new Location(world, x, center.getY(), z));
		}
		return locations;
	}

	public static ArrayList<Location> getCircleReverse(final Location center, final double radius, final int amount) {
		final World world = center.getWorld();
		final double increment = 6.283185307179586 / amount;
		final ArrayList<Location> locations = new ArrayList<Location>();
		for (int i = 0; i < amount; ++i) {
			final double angle = i * increment;
			final double x = center.getX() - radius * Math.cos(angle);
			final double z = center.getZ() - radius * Math.sin(angle);
			locations.add(new Location(world, x, center.getY(), z));
		}
		return locations;
	}

}
