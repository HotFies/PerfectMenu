package hotfies.perfectmenu;

import hotfies.perfectmenu.commands.PerfectMenuCommand;
import hotfies.perfectmenu.config.ConfigManager;
import hotfies.perfectmenu.menu.MenuManager;
import hotfies.perfectmenu.menu.MenuListener;
import hotfies.perfectmenu.database.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PerfectMenu extends JavaPlugin {

    private MenuManager menuManager;
    private DatabaseManager databaseManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        configManager.createMenusFolder();

        databaseManager = new DatabaseManager(this);
        databaseManager.connectToDatabase();
        menuManager = new MenuManager(this);

        getCommand("perfectmenu").setExecutor(new PerfectMenuCommand(this));
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
    }

    @Override
    public void onDisable() {
        databaseManager.closeDatabaseConnection();
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public boolean reloadConfigs() {
        try {
            reloadConfig();
            databaseManager.closeDatabaseConnection();
            databaseManager.connectToDatabase();
            menuManager.reloadMenus();
            configManager.createMenusFolder();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}