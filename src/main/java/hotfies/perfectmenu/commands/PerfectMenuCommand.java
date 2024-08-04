package hotfies.perfectmenu.commands;

import hotfies.perfectmenu.PerfectMenu;
import hotfies.perfectmenu.menu.MenuManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PerfectMenuCommand implements CommandExecutor {

    private final PerfectMenu plugin;
    private final MenuManager menuManager;

    public PerfectMenuCommand(PerfectMenu plugin) {
        this.plugin = plugin;
        this.menuManager = plugin.getMenuManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("reload")) {
                boolean success = plugin.reloadConfigs();
                if (success) {
                    sender.sendMessage("§aConfigs reload success!");
                } else {
                    sender.sendMessage("§cOooopsss... Error!");
                }
                return true;
            } else if (sender instanceof Player) {
                Player player = (Player) sender;
                String menuName = args[0];
                menuManager.loadMenuForPlayer(player, menuName);
                return true;
            }
        }

        if (sender instanceof Player) {
            //sender.sendMessage("Please specify a menu name or use /perfectmenu reload.");
        } else {
            //sender.sendMessage("Please specify a menu name or use /perfectmenu reload.");
        }

        return false;
    }
}