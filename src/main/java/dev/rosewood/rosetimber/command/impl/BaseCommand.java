package dev.rosewood.rosetimber.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.HelpCommand;
import dev.rosewood.rosegarden.command.PrimaryCommand;
import dev.rosewood.rosegarden.command.ReloadCommand;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.CommandInfo;

public class BaseCommand extends PrimaryCommand {

    public BaseCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("rt")
                .aliases("rosetimber", "timber")
                .permission("rosetimber.basecommand")
                .arguments(ArgumentsDefinition.builder().requiredSub(
                        new ToggleCommand(this.rosePlugin),
                        new HelpCommand(this.rosePlugin, this),
                        new ReloadCommand(this.rosePlugin)
                ))
                .build();
    }

}
