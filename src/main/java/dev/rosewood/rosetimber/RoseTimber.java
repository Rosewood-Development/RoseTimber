package dev.rosewood.rosetimber;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.rosetimber.config.SettingKey;
import dev.rosewood.rosetimber.listener.TreeFallListener;
import dev.rosewood.rosetimber.manager.ChoppingManager;
import dev.rosewood.rosetimber.manager.CommandManager;
import dev.rosewood.rosetimber.manager.HookManager;
import dev.rosewood.rosetimber.manager.LocaleManager;
import dev.rosewood.rosetimber.manager.PlacedBlockManager;
import dev.rosewood.rosetimber.manager.SaplingManager;
import dev.rosewood.rosetimber.manager.TreeAnimationManager;
import dev.rosewood.rosetimber.manager.TreeDefinitionManager;
import dev.rosewood.rosetimber.manager.TreeDetectionManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Esophose
 */
public class RoseTimber extends RosePlugin {

    /**
     * The running instance of RoseTimber on the server
     */
    private static RoseTimber instance;

    public static RoseTimber getInstance() {
        return instance;
    }

    public RoseTimber() {
        super(-1, 7599, null, LocaleManager.class, CommandManager.class);

        instance = this;
    }

    @Override
    public void enable() {
        if (NMSUtil.getVersionNumber() < 16) {
            this.getLogger().severe("RoseTimber only supports 1.16.5 or higher, The plugin has been disabled");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Regiser Listeners
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new TreeFallListener(this), this);
    }

    @Override
    public void disable() {

    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(
                TreeDefinitionManager.class,
                TreeDetectionManager.class,
                SaplingManager.class,
                TreeAnimationManager.class,
                PlacedBlockManager.class,
                HookManager.class,
                ChoppingManager.class
        );
    }

    @Override
    protected @NotNull List<RoseSetting<?>> getRoseConfigSettings() {
        return SettingKey.getKeys();
    }

    @Override
    protected @NotNull String[] getRoseConfigHeader() {
        return new String[]{
                "__________                  ___________ __       ___",
                "\\______   \\ ____  ______ ___\\__    ___/|__| _____\\_ |__   ___________",
                " |       _//  _ \\/  ___// __ \\|    |   |  |/     \\| __ \\_/ __ \\_  __ \\",
                " |    |   (  <_> )___ \\\\  ___/|    |   |  |  Y Y  \\ \\_\\ \\  ___/|  | \\/",
                " |____|_  /\\____/____  >\\___  >____|   |__|__|_|  /___  /\\___  >__|",
                "        \\/           \\/     \\/                  \\/    \\/     \\/"
        };
    }
}
