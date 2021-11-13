package dev.rosewood.rosetimber;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.rosetimber.command.CommandHandler;
import dev.rosewood.rosetimber.listener.TreeFallListener;
import dev.rosewood.rosetimber.manager.ChoppingManager;
import dev.rosewood.rosetimber.manager.ConfigurationManager;
import dev.rosewood.rosetimber.manager.HookManager;
import dev.rosewood.rosetimber.manager.LocaleManager;
import dev.rosewood.rosetimber.manager.PlacedBlockManager;
import dev.rosewood.rosetimber.manager.SaplingManager;
import dev.rosewood.rosetimber.manager.TreeAnimationManager;
import dev.rosewood.rosetimber.manager.TreeDefinitionManager;
import dev.rosewood.rosetimber.manager.TreeDetectionManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;

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
        super(-1, 7599, ConfigurationManager.class, null, LocaleManager.class);

        instance = this;
    }

    @Override
    public void enable() {
        if (NMSUtil.getVersionNumber() < 13) {
            this.getLogger().severe("RoseTimber only supports 1.13.2 and above. The plugin has been disabled.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        PluginCommand command = this.getCommand("rosetimber");
        if (command != null)
            command.setExecutor(new CommandHandler(this));

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new TreeFallListener(this), this);
    }

    @Override
    public void disable() {

    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return Arrays.asList(
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
    public List<Class<? extends DataMigration>> getDataMigrations() {
        return Collections.emptyList();
    }

}
