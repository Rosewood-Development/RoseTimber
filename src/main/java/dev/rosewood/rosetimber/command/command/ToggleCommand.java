package dev.rosewood.rosetimber.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosetimber.manager.ChoppingManager;
import dev.rosewood.rosetimber.manager.LocaleManager;
import org.bukkit.entity.Player;

public class ToggleCommand extends RoseCommand {

    public ToggleCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        ChoppingManager choppingManager = this.rosePlugin.getManager(ChoppingManager.class);
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);


        String enabled = choppingManager.togglePlayer((Player) context.getSender()) ? "enabled" : "disabled";
        locale.sendMessage(context.getSender(), "command-toggle-" + enabled);
    }

    @Override
    protected String getDefaultName() {
        return "toggle";
    }

    @Override
    public String getDescriptionKey() {
        return "command-toggle-description";
    }

    @Override
    public String getRequiredPermission() {
        return "rosetimber.toggle";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

}
