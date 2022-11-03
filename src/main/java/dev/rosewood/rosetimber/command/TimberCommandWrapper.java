package dev.rosewood.rosetimber.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TimberCommandWrapper extends RoseCommandWrapper {

    public TimberCommandWrapper(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public String getDefaultName() {
        return "rt";
    }

    @Override
    public List<String> getDefaultAliases() {
        return Arrays.asList("rosetimber", "timber");
    }

    @Override
    public List<String> getCommandPackages() {
        return Collections.singletonList("dev.rosewood.rosetimber.command.command");
    }

    @Override
    public boolean includeBaseCommand() {
        return true;
    }

    @Override
    public boolean includeHelpCommand() {
        return true;
    }

    @Override
    public boolean includeReloadCommand() {
        return true;
    }

}
