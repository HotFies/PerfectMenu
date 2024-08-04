package hotfies.perfectmenu.menu;

import hotfies.perfectmenu.PerfectMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class MenuListener implements Listener {

    private final PerfectMenu plugin;

    public MenuListener(PerfectMenu plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();
        if (inventory != null && inventory.getHolder() == null && event.getView().getTitle().equals(plugin.getMenuManager().getCurrentMenuTitle(player.getUniqueId()))) {
            event.setCancelled(true); // Отменяем все клики по предметам в меню

            if (event.getSlot() >= 0) {
                CustomMenu menu = plugin.getMenuManager().getCurrentMenu(player.getUniqueId());
                MenuItem item = menu.getItem(event.getSlot());
                if (item != null) {
                    item.executeCommands(player, event.isLeftClick());
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        if (inventory != null && inventory.getHolder() == null && event.getView().getTitle().equals(plugin.getMenuManager().getCurrentMenuTitle(player.getUniqueId()))) {
            event.setCancelled(true); // Отменяем перетаскивание предметов в меню
        }
    }
}