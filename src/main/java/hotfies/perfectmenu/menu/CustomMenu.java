package hotfies.perfectmenu.menu;

import hotfies.perfectmenu.PerfectMenu;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CustomMenu {

    private final PerfectMenu plugin;
    private final String title;
    private final String menuName;
    private final int size;
    private final List<MenuItem> items;
    private final String permission;
    private final List<String> denyMessage;
    private final List<Sound> openSounds;

    public CustomMenu(PerfectMenu plugin, String menuName, ConfigurationSection config) {
        this.plugin = plugin;
        this.menuName = menuName;
        this.title = ChatColor.translateAlternateColorCodes('&', config.getString("menu_title"));
        this.size = config.getInt("size");
        this.items = new ArrayList<>();
        this.permission = config.getConfigurationSection("permission").getString("permission");
        this.denyMessage = config.getConfigurationSection("permission").getStringList("deny_message");

        this.openSounds = new ArrayList<>();
        List<String> soundStrings = config.getStringList("open_sound");
        for (String soundString : soundStrings) {
            try {
                Sound sound = Sound.valueOf(soundString);
                this.openSounds.add(sound);
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().severe("Invalid sound: " + soundString);
            }
        }

        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                items.add(new MenuItem(plugin, itemsSection.getConfigurationSection(key)));
            }
        }
    }

    public void open(Player player) {
        String finalTitle = PlaceholderAPI.setPlaceholders(player, title);
        Inventory inventory = Bukkit.createInventory(null, size, finalTitle);

        items.stream()
                .filter(item -> item.getPermission() == null || player.hasPermission(item.getPermission()))
                .sorted(Comparator.comparingInt(MenuItem::getPriority).reversed())
                .forEach(item -> inventory.setItem(item.getSlot(), item.toItemStack(player)));

        player.openInventory(inventory);

        // Play open sounds
        for (Sound sound : openSounds) {
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getPermission() {
        return permission;
    }

    public List<String> getDenyMessage(Player player) {
        List<String> messages = new ArrayList<>();
        for (String message : denyMessage) {
            messages.add(PlaceholderAPI.setPlaceholders(player, ChatColor.translateAlternateColorCodes('&', message)));
        }
        return messages;
    }

    public MenuItem getItem(int slot) {
        for (MenuItem item : items) {
            if (item.getSlot() == slot) {
                return item;
            }
        }
        return null;
    }
}