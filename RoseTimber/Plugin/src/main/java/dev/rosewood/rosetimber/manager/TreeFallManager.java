package dev.rosewood.rosetimber.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosetimber.events.TreeFallEvent;
import dev.rosewood.rosetimber.events.TreeFellEvent;
import dev.rosewood.rosetimber.manager.ConfigurationManager.Setting;
import dev.rosewood.rosetimber.tree.DetectedTree;
import dev.rosewood.rosetimber.tree.OnlyToppleWhile;
import dev.rosewood.rosetimber.tree.TreeBlockSet;
import dev.rosewood.rosetimber.utils.TimberUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class TreeFallManager extends Manager implements Listener {

    public TreeFallManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        Bukkit.getPluginManager().registerEvents(this, rosePlugin);
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        TreeDefinitionManager treeDefinitionManager = this.rosePlugin.getManager(TreeDefinitionManager.class);
        TreeDetectionManager treeDetectionManager = this.rosePlugin.getManager(TreeDetectionManager.class);
        TreeAnimationManager treeAnimationManager = this.rosePlugin.getManager(TreeAnimationManager.class);
        ChoppingManager choppingManager = this.rosePlugin.getManager(ChoppingManager.class);
        SaplingManager saplingManager = this.rosePlugin.getManager(SaplingManager.class);
        HookManager hookManager = this.rosePlugin.getManager(HookManager.class);

        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack tool = player.getInventory().getItemInMainHand();

        // Protect saplings
        if (saplingManager.isSaplingProtected(block)) {
            event.setCancelled(true);
            return;
        }

        // Condition checks
        boolean isValid = true;

        if (Setting.DISABLED_WORLDS.getStringList().contains(player.getWorld().getName()))
            isValid = false;

        if (!Setting.ALLOW_CREATIVE_MODE.getBoolean() && player.getGameMode().equals(GameMode.CREATIVE))
            isValid = false;

        if (!this.checkToppleWhile(player))
            isValid = false;

        if (Setting.REQUIRE_CHOP_PERMISSION.getBoolean() && !player.hasPermission("rosetimber.chop"))
            isValid = false;

        if (!choppingManager.isChopping(player))
            isValid = false;

        if (choppingManager.isInCooldown(player))
            isValid = false;

        if (treeAnimationManager.isBlockInAnimation(block)) {
            isValid = false;
            event.setCancelled(true);
        }

        if (!treeDefinitionManager.isToolValidForAnyTreeDefinition(tool))
            isValid = false;

        if (!hookManager.isUsingAbilityHooks(player))
            isValid = false;

        boolean alwaysReplantSapling = Setting.ALWAYS_REPLANT_SAPLING.getBoolean();
        if (!isValid && !alwaysReplantSapling)
            return;

        DetectedTree detectedTree = treeDetectionManager.detectTree(block);
        if (detectedTree == null)
            return;

        if (alwaysReplantSapling) {
            Bukkit.getScheduler().runTask(this.rosePlugin, () ->
                    saplingManager.replantSapling(detectedTree.getTreeDefinition(), detectedTree.getDetectedTreeBlocks().getInitialLogBlock()));

            if (!isValid)
                return;
        }

        if (!treeDefinitionManager.isToolValidForTreeDefinition(detectedTree.getTreeDefinition(), tool))
            return;

        int toolDamage = this.getToolDamage(detectedTree.getDetectedTreeBlocks(), tool.containsEnchantment(Enchantment.SILK_TOUCH));
        if (Setting.PROTECT_TOOL.getBoolean() && !TimberUtils.hasEnoughDurability(tool, toolDamage))
            return;

        // Trigger fall event
        TreeFallEvent treeFallEvent = new TreeFallEvent(player, detectedTree);
        Bukkit.getPluginManager().callEvent(treeFallEvent);
        if (treeFallEvent.isCancelled())
            return;

        // Valid tree and meets all conditions past this point
        event.setCancelled(true);

        choppingManager.cooldownPlayer(player);

        // Destroy initiated block if enabled
        if (Setting.DESTROY_INITIATED_BLOCK.getBoolean()) {
            detectedTree.getDetectedTreeBlocks().getInitialLogBlock().getBlock().setType(Material.AIR);
            detectedTree.getDetectedTreeBlocks().remove(detectedTree.getDetectedTreeBlocks().getInitialLogBlock());
        }

        if (!player.getGameMode().equals(GameMode.CREATIVE))
            TimberUtils.applyToolDurability(player, toolDamage);

        hookManager.applyExperienceHooks(player, detectedTree.getDetectedTreeBlocks());
        treeAnimationManager.runAnimation(detectedTree, player);
        treeDefinitionManager.dropTreeLoot(detectedTree.getTreeDefinition(), detectedTree.getDetectedTreeBlocks().getInitialLogBlock(), player, false, true);

        // Trigger fell event
        TreeFellEvent treeFellEvent = new TreeFellEvent(player, detectedTree);
        Bukkit.getPluginManager().callEvent(treeFellEvent);
    }

    /**
     * Checks if a player is doing a certain action required to topple a tree
     *
     * @param player The player to check
     * @return True if the check passes, otherwise false
     */
    private boolean checkToppleWhile(Player player) {
        switch (OnlyToppleWhile.fromString(Setting.ONLY_TOPPLE_WHILE.getString())) {
            case SNEAKING:
                return player.isSneaking();
            case NOT_SNEAKING:
                return !player.isSneaking();
            default:
                return true;
        }
    }

    private int getToolDamage(TreeBlockSet<Block> treeBlocks, boolean hasSilkTouch) {
        if (!Setting.REALISTIC_TOOL_DAMAGE.getBoolean())
            return 1;

        if (Setting.APPLY_SILK_TOUCH_TOOL_DAMAGE.getBoolean() && hasSilkTouch) {
            return treeBlocks.size();
        } else {
            return treeBlocks.getLogBlocks().size();
        }
    }

}
