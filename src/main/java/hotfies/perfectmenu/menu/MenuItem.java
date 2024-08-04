package hotfies.perfectmenu.menu;

import dev.lone.itemsadder.api.CustomStack;
import hotfies.perfectmenu.PerfectMenu;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuItem {

    private final PerfectMenu plugin;
    private final String materialName;
    private final int customModelData;
    private final int slot;
    private final int priority;
    private final String displayName;
    private final List<String> lore;
    private final List<String> leftClickCommands;
    private final List<String> rightClickCommands;
    private final boolean hideAttributes;
    private final String permission;

    public MenuItem(PerfectMenu plugin, ConfigurationSection config) {
        this.plugin = plugin;
        this.materialName = config.getString("material");
        this.customModelData = config.getInt("CustomModelData", -1);
        this.slot = config.getInt("slot");
        this.priority = config.getInt("priority", 0);
        this.displayName = config.getString("display_name");
        this.lore = config.getStringList("lore");
        this.leftClickCommands = config.getStringList("left_click_commands");
        this.rightClickCommands = config.getStringList("right_click_commands");
        this.hideAttributes = config.getBoolean("hide_attributes", false);
        this.permission = config.getString("permission");

        Bukkit.getLogger().info("Loaded MenuItem: " + displayName + ", hide_attributes: " + hideAttributes + ", priority: " + priority + ", permission: " + permission);
    }

    public String getMaterialName() {
        return materialName;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public int getSlot() {
        return slot;
    }

    public int getPriority() {
        return priority;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public List<String> getLeftClickCommands() {
        return leftClickCommands;
    }

    public List<String> getRightClickCommands() {
        return rightClickCommands;
    }

    public boolean isHideAttributes() {
        return hideAttributes;
    }

    public String getPermission() {
        return permission;
    }

    public ItemStack toItemStack(Player player) {
        ItemStack item;

        if (materialName != null && materialName.startsWith("itemsadder_")) {
            String itemsAdderItemName = materialName.substring("itemsadder_".length());
            CustomStack customStack = CustomStack.getInstance(itemsAdderItemName);
            if (customStack != null) {
                item = customStack.getItemStack();
            } else {
                item = new ItemStack(Material.BARRIER);
                plugin.getLogger().severe("Invalid ItemsAdder item: " + itemsAdderItemName);
            }
        } else {
            Material material = Material.getMaterial(materialName);
            item = new ItemStack(material != null ? material : Material.BARRIER); // Default to BARRIER if material is null
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String finalDisplayName = processPlaceholders(player, displayName);
            meta.setDisplayName(finalDisplayName);

            List<String> translatedLore = new ArrayList<>();
            for (String line : lore) {
                translatedLore.add(processPlaceholders(player, line));
            }
            meta.setLore(translatedLore);

            if (customModelData != -1) {
                meta.setCustomModelData(customModelData);
            }
            item.setItemMeta(meta);

            if (hideAttributes) {
                hideItemAttributes(item);
                Bukkit.getLogger().info("Hiding attributes for item: " + displayName);
            }
        }
        return item;
    }

    public void executeCommands(Player player, boolean isLeftClick) {
        List<String> commands = isLeftClick ? leftClickCommands : rightClickCommands;
        for (String command : commands) {
            if (command.startsWith("[console]")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring(9).trim());
            } else if (command.startsWith("[player]")) {
                player.performCommand(command.substring(8).trim());
            } else if (command.startsWith("[openmenu]")) {
                String menuName = command.substring(10).trim();
                plugin.getMenuManager().loadMenuForPlayer(player, menuName);
            } else if (command.equals("[refresh]")) {
                player.closeInventory();
                String currentMenuName = plugin.getMenuManager().getCurrentMenuName(player.getUniqueId());
                plugin.getMenuManager().loadMenuForPlayer(player, currentMenuName);
            } else if (command.equals("[close]")) {
                player.closeInventory();
            } else if (command.startsWith("[message]")) {
                player.sendMessage(processPlaceholders(player, command.substring(9).trim()));
            }
        }
    }

    private void hideItemAttributes(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // Скрываем атрибуты и зачарования предмета
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

            // Скрытие атрибутов для оружия и инструментов
            if (isToolOrWeapon(item.getType())) {
                meta.addAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE, new org.bukkit.attribute.AttributeModifier(java.util.UUID.randomUUID(), "generic.attackDamage", 0, org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER));
                meta.addAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_ATTACK_SPEED, new org.bukkit.attribute.AttributeModifier(java.util.UUID.randomUUID(), "generic.attackSpeed", 0, org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER));
            }

            // Скрытие атрибутов для брони
            if (isArmor(item.getType())) {
                meta.addAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_ARMOR, new org.bukkit.attribute.AttributeModifier(java.util.UUID.randomUUID(), "generic.armor", 0, org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER));
                meta.addAttributeModifier(org.bukkit.attribute.Attribute.GENERIC_ARMOR_TOUGHNESS, new org.bukkit.attribute.AttributeModifier(java.util.UUID.randomUUID(), "generic.armorToughness", 0, org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER));
            }

            // Устанавливаем метаданные обратно на предмет
            item.setItemMeta(meta);
        }
    }

    private boolean isToolOrWeapon(Material material) {
        switch (material) {
            case WOODEN_SWORD:
            case STONE_SWORD:
            case IRON_SWORD:
            case GOLDEN_SWORD:
            case DIAMOND_SWORD:
            case NETHERITE_SWORD:
            case WOODEN_AXE:
            case STONE_AXE:
            case IRON_AXE:
            case GOLDEN_AXE:
            case DIAMOND_AXE:
            case NETHERITE_AXE:
            case WOODEN_SHOVEL:
            case STONE_SHOVEL:
            case IRON_SHOVEL:
            case GOLDEN_SHOVEL:
            case DIAMOND_SHOVEL:
            case NETHERITE_SHOVEL:
            case WOODEN_PICKAXE:
            case STONE_PICKAXE:
            case IRON_PICKAXE:
            case GOLDEN_PICKAXE:
            case DIAMOND_PICKAXE:
            case NETHERITE_PICKAXE:
            case WOODEN_HOE:
            case STONE_HOE:
            case IRON_HOE:
            case GOLDEN_HOE:
            case DIAMOND_HOE:
            case NETHERITE_HOE:
            case TRIDENT:
            case MACE: // Булава
                return true;
            default:
                return false;
        }
    }

    private boolean isArmor(Material material) {
        switch (material) {
            case LEATHER_HELMET:
            case CHAINMAIL_HELMET:
            case IRON_HELMET:
            case GOLDEN_HELMET:
            case DIAMOND_HELMET:
            case NETHERITE_HELMET:
            case LEATHER_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case IRON_CHESTPLATE:
            case GOLDEN_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
            case NETHERITE_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case IRON_LEGGINGS:
            case GOLDEN_LEGGINGS:
            case DIAMOND_LEGGINGS:
            case NETHERITE_LEGGINGS:
            case LEATHER_BOOTS:
            case CHAINMAIL_BOOTS:
            case IRON_BOOTS:
            case GOLDEN_BOOTS:
            case DIAMOND_BOOTS:
            case NETHERITE_BOOTS:
                return true;
            default:
                return false;
        }
    }

    private String processPlaceholders(Player player, String text) {
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        // Обработка специальных плейсхолдеров с использованием регулярного выражения
        Pattern pattern = Pattern.compile(":offset_-?\\d+::menu_[^:]+:");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String placeholder = matcher.group();
            text = text.replace(placeholder, processCustomPlaceholder(placeholder));
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private String processCustomPlaceholder(String placeholder) {
        // Логика обработки специального плейсхолдера
        // Например, вы можете вернуть название меню без плейсхолдера
        return placeholder.replaceAll(":offset_-?\\d+::menu_", "").replaceAll(":", "");
    }
}