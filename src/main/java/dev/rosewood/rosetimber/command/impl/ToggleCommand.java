package dev.rosewood.rosetimber.command.impl;


import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosetimber.manager.ChoppingManager;
import dev.rosewood.rosetimber.manager.LocaleManager;
import org.bukkit.entity.Player;

public class ToggleCommand extends BaseRoseCommand {

    public ToggleCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }
    
    @RoseExecutable
    public void execute(CommandContext context) {
        ChoppingManager choppingManager = this.rosePlugin.getManager(ChoppingManager.class);
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        String enabled = choppingManager.togglePlayer((Player) context.getSender()) ? "enabled" : "disabled";
        locale.sendMessage(context.getSender(), "command-toggle-" + enabled);
    }
    
    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("toggle")
                .descriptionKey("command-toggle-description")
                .permission("rosetimber.toggle")
                .playerOnly(true)
                .build();
    }

}
