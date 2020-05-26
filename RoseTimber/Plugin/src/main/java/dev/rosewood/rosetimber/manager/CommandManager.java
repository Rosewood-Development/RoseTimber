package dev.rosewood.rosetimber.manager;

import dev.rosewood.rosetimber.RoseTimber;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class CommandManager extends Manager implements TabExecutor {

    public CommandManager(RoseTimber plugin) {
        super(plugin);

        PluginCommand command = plugin.getCommand("rosetimber");
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        LocaleManager localeManager = this.roseTimber.getManager(LocaleManager.class);

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (commandSender instanceof Player && this.doesntHavePermission(commandSender, "rosetimber.reload", localeManager))
                    return true;

                this.roseTimber.reload();
                localeManager.sendMessage(commandSender, "reload-reloaded");
                return true;
            } else if (args[0].equalsIgnoreCase("toggle")) {
                if (!(commandSender instanceof Player)) {
                    localeManager.sendCustomMessage(commandSender, "&cConsole cannot toggle chopping mode!");
                    return true;
                }

                if (this.doesntHavePermission(commandSender, "rosetimber.toggle", localeManager))
                    return true;

                if (RoseTimber.getInstance().getManager(ChoppingManager.class).togglePlayer((Player) commandSender)) {
                    localeManager.sendMessage(commandSender, "toggle-enabled");
                } else {
                    localeManager.sendMessage(commandSender, "toggle-disabled");
                }

                return true;
            }
        }

        commandSender.sendMessage("");
        localeManager.sendCustomMessage(commandSender, localeManager.getLocaleMessage("prefix") + "&7Plugin created by &5" + this.roseTimber.getDescription().getAuthors().get(0) + "&7. (&ev" + this.roseTimber.getDescription().getVersion() + "&7)");
        localeManager.sendSimpleMessage(commandSender, "reload-description");
        localeManager.sendSimpleMessage(commandSender, "toggle-description");
        commandSender.sendMessage("");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length < 1)
            return completions;

        Set<String> possibleCompletions = new HashSet<>();

        if (commandSender.hasPermission("rosetimber.reload"))
            possibleCompletions.add("reload");

        if (commandSender.hasPermission("rosetimber.toggle") && commandSender instanceof Player)
            possibleCompletions.add("toggle");

        StringUtil.copyPartialMatches(args[0], possibleCompletions, completions);

        return completions;
    }

    /**
     * Checks if a player does have a permission
     * Sends them an error message if they don't
     *
     * @param sender     The CommandSender to check
     * @param permission The permission to check for
     * @param localeManager     The LocaleManager instance
     * @return True if the player has permission, otherwise false and sends a message
     */
    private boolean doesntHavePermission(CommandSender sender, String permission, LocaleManager localeManager) {
        if (!sender.hasPermission(permission)) {
            localeManager.sendMessage(sender, "no-permission");
            return true;
        }
        return false;
    }

}
