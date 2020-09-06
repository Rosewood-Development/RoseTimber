package dev.rosewood.rosetimber;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.rosetimber.manager.ChoppingManager;
import dev.rosewood.rosetimber.manager.CommandManager;
import dev.rosewood.rosetimber.manager.ConfigurationManager;
import dev.rosewood.rosetimber.manager.HookManager;
import dev.rosewood.rosetimber.manager.LocaleManager;
import dev.rosewood.rosetimber.manager.PlacedBlockManager;
import dev.rosewood.rosetimber.manager.SaplingManager;
import dev.rosewood.rosetimber.manager.TreeAnimationManager;
import dev.rosewood.rosetimber.manager.TreeDefinitionManager;
import dev.rosewood.rosetimber.manager.TreeDetectionManager;
import dev.rosewood.rosetimber.manager.TreeFallManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;

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
            this.getLogger().severe("This version of RoseTimber only supports 1.13.2 and above. The plugin has been disabled.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void disable() {

    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return Arrays.asList(
                CommandManager.class,
                TreeDefinitionManager.class,
                TreeDetectionManager.class,
                TreeFallManager.class,
                SaplingManager.class,
                TreeAnimationManager.class,
                PlacedBlockManager.class,
                HookManager.class,
                ChoppingManager.class
        );
    }

    @Override
    public List<DataMigration> getDataMigrations() {
        return Collections.emptyList();
    }

}
