package hotfies.perfectmenu.config;

import hotfies.perfectmenu.PerfectMenu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public class ConfigManager {

    private final PerfectMenu plugin;

    public ConfigManager(PerfectMenu plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
    }

    public void createMenusFolder() {
        File menusFolder = new File(plugin.getDataFolder(), "menus");
        if (!menusFolder.exists()) {
            menusFolder.mkdirs();
        }

        copyDefaultMenus("menus_Ru_ru.yml");
        copyDefaultMenus("menus_En_en.yml");
    }

    private void copyDefaultMenus(String fileName) {
        File file = new File(plugin.getDataFolder(), "menus/" + fileName);
        if (!file.exists()) {
            try (InputStream in = plugin.getResource("Menus/" + fileName)) {
                if (in != null) {
                    Files.copy(in, file.toPath());
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not copy default menu " + fileName, e);
            }
        }
    }
}