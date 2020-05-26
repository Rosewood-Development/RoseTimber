package dev.rosewood.rosetimber.manager;

import dev.rosewood.rosetimber.RoseTimber;
import dev.rosewood.rosetimber.hook.CoreProtectHook;
import dev.rosewood.rosetimber.hook.JobsHook;
import dev.rosewood.rosetimber.hook.McMMOClassicHook;
import dev.rosewood.rosetimber.hook.McMMOHook;
import dev.rosewood.rosetimber.hook.TimberHook;
import dev.rosewood.rosetimber.manager.ConfigurationManager.Setting;
import dev.rosewood.rosetimber.tree.TreeBlockSet;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class HookManager extends Manager {

    private Set<TimberHook> hooks;

    public HookManager(RoseTimber roseTimber) {
        super(roseTimber);
        this.hooks = new HashSet<>();
    }

    @Override
    public void reload() {
        this.hooks.clear();

        this.tryHook("Jobs", JobsHook.class);
        this.tryHook("CoreProtect", CoreProtectHook.class);

        Bukkit.getScheduler().runTaskAsynchronously(this.roseTimber, () -> {
            Plugin mcMMO = Bukkit.getPluginManager().getPlugin("mcMMO");
            if (mcMMO != null) {
                String version = mcMMO.getDescription().getVersion();
                if (version.startsWith("2")) {
                    this.tryHook("mcMMO", McMMOHook.class);
                } else {
                    this.tryHook("mcMMO", McMMOClassicHook.class);
                }
            }
        });
    }

    @Override
    public void disable() {
        this.hooks.clear();
    }
    
    /**
     * Tries to hook into a compatible plugin
     * 
     * @param pluginName The name of the plugin
     * @param hookClass The hook class
     */
    private void tryHook(String pluginName, Class<? extends TimberHook> hookClass) {
        if (!Bukkit.getPluginManager().isPluginEnabled(pluginName)) 
            return;
        
        try {
            this.hooks.add(hookClass.newInstance());
            this.roseTimber.getLogger().info(String.format("Hooks: Hooked into %s!", pluginName));
        } catch (Exception ex) {
            this.roseTimber.getLogger().info(String.format("Hooks: Unable to hook with %s, the version installed is not supported!", pluginName));
        }
    }
    
    /**
     * Applies experience to the loaded hooks
     *
     * @param player The player to apply experience to
     * @param treeBlocks The blocks of the tree that were broken
     */
    public void applyExperienceHooks(Player player, TreeBlockSet<Block> treeBlocks) {
        if (!Setting.HOOKS_APPLY_EXPERIENCE.getBoolean())
            return;

        for (TimberHook hook : this.hooks)
            hook.applyExperience(player, treeBlocks);
    }

    /**
     * Checks if double drops should be applied from the loaded hooks
     *
     * @param player The player to check
     */
    public boolean shouldApplyDoubleDropsHooks(Player player) {
        if (!Setting.HOOKS_APPLY_EXTRA_DROPS.getBoolean())
            return false;

        for (TimberHook hook : this.hooks)
            if (hook.shouldApplyDoubleDrops(player))
                return true;
        return false;
    }

    /**
     * Checks if a player is using an ability from the loaded hooks
     *
     * @param player The player to check
     */
    public boolean isUsingAbilityHooks(Player player) {
        if (!Setting.HOOKS_REQUIRE_ABILITY_ACTIVE.getBoolean() || this.hooks.isEmpty())
            return true;

        for (TimberHook hook : this.hooks)
            if (hook.isUsingAbility(player))
                return true;
        return false;
    }

}
