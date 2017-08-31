package com.likeapig.elimination;

import java.io.File;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Settings {
	
	private static Settings instance;
	private File mapFile;
	private FileConfiguration mapConfig;
	private Plugin plugin;
	
	static {
		instance = new Settings();
	}
	
	public static Settings get() {
		return instance;
	}
	
	public void setup(Plugin p) {
		plugin = p;
		if (!p.getDataFolder().exists()) {
			p.getDataFolder().mkdirs();
		}
		mapFile = new File(p.getDataFolder() + "/maps.yml");
		if (!mapFile.exists()) {
			try {
				mapFile.createNewFile();
			}
			catch (Exception e) {
				p.getLogger().info("Failed to generate map file!");
				e.printStackTrace();
			}
		}
		mapConfig = (FileConfiguration)YamlConfiguration.loadConfiguration(mapFile);
	}
	
	public void set(String path, Object value) {
		mapConfig.set(path, value);
		try {
			mapConfig.save(mapFile);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public <T> T get(String path) {
		return (T)mapConfig.get(path);
	}
	
	public ConfigurationSection getConfigSection() {
		return mapConfig.getConfigurationSection("maps");
	}
	
	public ConfigurationSection createConfiguration(final String path) {
        final ConfigurationSection s = this.mapConfig.createSection(path);
        try {
            this.mapConfig.save(this.mapFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
    
    public Plugin getPlugin() {
        return this.plugin;
    }

}
