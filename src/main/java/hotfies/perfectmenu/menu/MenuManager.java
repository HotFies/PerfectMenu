package hotfies.perfectmenu.menu;

import hotfies.perfectmenu.PerfectMenu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuManager {

    private final PerfectMenu plugin;
    private final Map<UUID, CustomMenu> currentMenus;

    public MenuManager(PerfectMenu plugin) {
        this.plugin = plugin;
        this.currentMenus = new HashMap<>();
    }

    public void loadMenuForPlayer(Player player, String menuName) {
        UUID playerUUID = player.getUniqueId();
        String playerLanguage = plugin.getDatabaseManager().getPlayerLanguage(playerUUID);
        File menuFile = new File(plugin.getDataFolder(), "menus/menus_" + playerLanguage + ".yml");
        FileConfiguration menuConfig = YamlConfiguration.loadConfiguration(menuFile);

        if (menuConfig.contains(menuName)) {
            CustomMenu menu = new CustomMenu(plugin, menuName, menuConfig.getConfigurationSection(menuName));
            if (menu.getPermission() == null || player.hasPermission(menu.getPermission())) {
                menu.open(player);
                currentMenus.put(playerUUID, menu);
            } else {
                for (String message : menu.getDenyMessage(player)) {
                    player.sendMessage(message);
                }
            }
        } else {
            player.sendMessage("Menu not found: " + menuName);
        }
    }

    public String getCurrentMenuTitle(UUID playerUUID) {
        CustomMenu menu = currentMenus.get(playerUUID);
        return menu != null ? menu.getTitle() : "";
    }

    public String getCurrentMenuName(UUID playerUUID) {
        CustomMenu menu = currentMenus.get(playerUUID);
        return menu != null ? menu.getMenuName() : "";
    }

    public CustomMenu getCurrentMenu(UUID playerUUID) {
        return currentMenus.get(playerUUID);
    }

    public void reloadMenus() {
        currentMenus.clear();
    }
}