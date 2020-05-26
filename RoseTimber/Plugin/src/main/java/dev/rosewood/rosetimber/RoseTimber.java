package dev.rosewood.rosetimber;

import dev.rosewood.rosetimber.manager.ChoppingManager;
import dev.rosewood.rosetimber.manager.CommandManager;
import dev.rosewood.rosetimber.manager.ConfigurationManager;
import dev.rosewood.rosetimber.manager.HookManager;
import dev.rosewood.rosetimber.manager.LocaleManager;
import dev.rosewood.rosetimber.manager.Manager;
import dev.rosewood.rosetimber.manager.PlacedBlockManager;
import dev.rosewood.rosetimber.manager.SaplingManager;
import dev.rosewood.rosetimber.manager.TreeAnimationManager;
import dev.rosewood.rosetimber.manager.TreeDefinitionManager;
import dev.rosewood.rosetimber.manager.TreeDetectionManager;
import dev.rosewood.rosetimber.manager.TreeFallManager;
import dev.rosewood.rosetimber.utils.NMSUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Esophose
 */
public class RoseTimber extends JavaPlugin {

    /**
     * The running instance of RoseTimber on the server
     */
    private static RoseTimber instance;

    /**
     * The plugin managers
     */
    private Map<Class<? extends Manager>, Manager> managers;

    public static RoseTimber getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        this.getLogger().info("Detected server API version as " + NMSUtil.getVersion());
        if (!NMSUtil.isValidVersion()) {
            this.getLogger().severe("This version of RoseTimber only supports 1.13.2 and above. The plugin has been disabled.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        instance = this;

        // bStats Metrics
        new MetricsLite(this, 7599);

        // Register managers
        this.managers = new LinkedHashMap<>();

        // Load managers
        this.reload();
    }

    @Override
    public void onDisable() {
        if (instance == null)
            return;

        this.disableManagers();
        this.managers.clear();
    }

    /**
     * Reloads the plugin's settings
     */
    public void reload() {
        this.disableManagers();
        this.managers.values().forEach(Manager::reload);

        this.getManager(ConfigurationManager.class);
        this.getManager(LocaleManager.class);
        this.getManager(CommandManager.class);
        this.getManager(TreeDefinitionManager.class);
        this.getManager(TreeDetectionManager.class);
        this.getManager(TreeFallManager.class);
        this.getManager(SaplingManager.class);
        this.getManager(TreeAnimationManager.class);
        this.getManager(PlacedBlockManager.class);
        this.getManager(HookManager.class);
        this.getManager(ChoppingManager.class);
    }

    /**
     * Disables most of the plugin
     */
    public void disableManagers() {
        List<Manager> managers = new ArrayList<>(this.managers.values());
        Collections.reverse(managers);
        managers.forEach(Manager::disable);
    }

    /**
     * Gets a manager instance
     *
     * @param managerClass The class of the manager to get
     * @param <T> extends Manager
     * @return A new instance of the given manager class
     */
    @SuppressWarnings("unchecked")
    public <T extends Manager> T getManager(Class<T> managerClass) {
        if (this.managers.containsKey(managerClass))
            return (T) this.managers.get(managerClass);

        try {
            T manager = managerClass.getConstructor(RoseTimber.class).newInstance(this);
            this.managers.put(managerClass, manager);
            manager.reload();
            return manager;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
