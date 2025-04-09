package dev.rosewood.rosetimber.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosetimber.config.SettingKey;
import dev.rosewood.rosetimber.hook.CoreProtectHook;
import dev.rosewood.rosetimber.hook.McMMOHook;
import dev.rosewood.rosetimber.hook.TimberHook;
import dev.rosewood.rosetimber.tree.TreeBlockSet;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class HookManager extends Manager {

    private final Set<TimberHook> hooks;

    public HookManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.hooks = new HashSet<>();
    }

    @Override
    public void reload() {
        this.hooks.clear();

        this.tryHook("CoreProtect", CoreProtectHook.class);

        Plugin mcMMO = Bukkit.getPluginManager().getPlugin("mcMMO");
        if (mcMMO != null) {
            this.tryHook("mcMMO", McMMOHook.class);
        }
    }

    @Override
    public void disable() {
        this.hooks.clear();
    }

    /**
     * Tries to hook into a compatible plugin
     *
     * @param pluginName The name of the plugin
     * @param hookClass  The hook class
     */
    private void tryHook(String pluginName, Class<? extends TimberHook> hookClass) {
        if (!Bukkit.getPluginManager().isPluginEnabled(pluginName))
            return;

        try {
            this.hooks.add(hookClass.getConstructor().newInstance());
            this.rosePlugin.getLogger().info(String.format("Hooks: Hooked into %s!", pluginName));
        } catch (Exception ex) {
            this.rosePlugin.getLogger().info(String.format("Hooks: Unable to hook with %s, the version installed is not supported!", pluginName));
        }
    }

    /**
     * Applies experience to the loaded hooks
     *
     * @param player     The player to apply experience to
     * @param treeBlocks The blocks of the tree that were broken
     */
    public void applyExperienceHooks(Player player, TreeBlockSet<Block> treeBlocks) {
        for (TimberHook hook : this.hooks) {
            // mcMMO
            if (SettingKey.HOOKS_MCMMO_APPLY_EXPERIENCE.get())
                hook.applyExperience(player, treeBlocks, false);
        }
    }

    /**
     * Checks if double drops should be applied from the loaded hooks
     *
     * @param player The player to check
     */
    public boolean shouldApplyDoubleDropsHooks(Player player) {
        if (!SettingKey.HOOKS_APPLY_EXTRA_DROPS.get())
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
        if (!SettingKey.HOOKS_REQUIRE_ABILITY_ACTIVE.get() || this.hooks.isEmpty())
            return true;

        for (TimberHook hook : this.hooks)
            if (hook.isUsingAbility(player))
                return true;
        return false;
    }

}
