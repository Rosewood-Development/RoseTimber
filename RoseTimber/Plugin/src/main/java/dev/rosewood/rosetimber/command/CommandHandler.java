package dev.rosewood.rosetimber.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosetimber.manager.ChoppingManager;
import dev.rosewood.rosetimber.manager.LocaleManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class CommandHandler implements TabExecutor {

    private final RosePlugin rosePlugin;

    public CommandHandler(RosePlugin rosePlugin) {
        this.rosePlugin = rosePlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        LocaleManager localeManager = this.rosePlugin.getManager(LocaleManager.class);

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("help")) {
                localeManager.sendMessage(sender, "command-help-title");
                localeManager.sendSimpleMessage(sender, "command-help-description");
                localeManager.sendSimpleMessage(sender, "command-reload-description");
                localeManager.sendSimpleMessage(sender, "command-toggle-description");
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender instanceof Player && this.doesntHavePermission(sender, "rosetimber.reload", localeManager))
                    return true;

                this.rosePlugin.reload();
                localeManager.sendMessage(sender, "command-reload-reloaded");
                return true;
            } else if (args[0].equalsIgnoreCase("toggle")) {
                if (!(sender instanceof Player)) {
                    localeManager.sendCustomMessage(sender, "&cConsole cannot toggle chopping mode!");
                    return true;
                }

                if (this.doesntHavePermission(sender, "rosetimber.toggle", localeManager))
                    return true;

                if (this.rosePlugin.getManager(ChoppingManager.class).togglePlayer((Player) sender)) {
                    localeManager.sendMessage(sender, "command-toggle-enabled");
                } else {
                    localeManager.sendMessage(sender, "command-toggle-disabled");
                }

                return true;
            }
        }

        String baseColor = localeManager.getLocaleMessage("base-command-color");
        localeManager.sendCustomMessage(sender, baseColor + "Running <g:#8A2387:#E94057:#F27121>RoseTimber" + baseColor + " v" + this.rosePlugin.getDescription().getVersion());
        localeManager.sendCustomMessage(sender, baseColor + "Plugin created by: <g:#41e0f0:#ff8dce>" + this.rosePlugin.getDescription().getAuthors().get(0));
        localeManager.sendSimpleMessage(sender, "base-command-help");

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
     * Checks if a player doesn't have a permission.
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
