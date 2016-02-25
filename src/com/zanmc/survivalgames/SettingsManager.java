package com.zanmc.survivalgames;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class SettingsManager {

	private SettingsManager() { }
    
    static SettingsManager instance = new SettingsManager();
   
    public static SettingsManager getInstance() {
            return instance;
    }
   
    Plugin p;
   
    FileConfiguration data;
    File dfile;
    
    FileConfiguration chests;
    File cfile;
   
    public void setup(Plugin p) {
           
            if (!p.getDataFolder().exists()) {
                    p.getDataFolder().mkdir();
            }
           
            dfile = new File(p.getDataFolder(), "data.yml");
           
            if (!dfile.exists()) {
                    try {
                            dfile.createNewFile();
                    }
                    catch (IOException e) {
                            Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create data.yml!");
                    }
            }
           
            chests = YamlConfiguration.loadConfiguration(cfile);
            
            cfile = new File(p.getDataFolder(), "chests.yml");
            
            if (!cfile.exists()) {
                    try {
                            cfile.createNewFile();
                    }
                    catch (IOException e) {
                            Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create data.yml!");
                    }
            }
           
            chests = YamlConfiguration.loadConfiguration(cfile);
    }
   
    public FileConfiguration getData() {
            return data;
    }
    
    public FileConfiguration getChests() {
        return chests;
}
   
    public void saveData() {
            try {
                    data.save(dfile);
            }
            catch (IOException e) {
                    Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save data.yml!");
            }
    }
    
    public void saveChests() {
        try {
                chests.save(cfile);
        }
        catch (IOException e) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save chests.yml!");
        }
}
   
    public void reloadData() {
            data = YamlConfiguration.loadConfiguration(dfile);
    }
    
    public void reloadChests() {
        chests = YamlConfiguration.loadConfiguration(cfile);
}
   
    public PluginDescriptionFile getDesc() {
            return p.getDescription();
    }

}
