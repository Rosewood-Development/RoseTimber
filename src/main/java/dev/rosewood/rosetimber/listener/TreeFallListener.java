package dev.rosewood.rosetimber.listener;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosetimber.config.SettingKey;
import dev.rosewood.rosetimber.events.TreeBlockBreakEvent;
import dev.rosewood.rosetimber.events.TreeFallEvent;
import dev.rosewood.rosetimber.events.TreeFellEvent;
import dev.rosewood.rosetimber.manager.ChoppingManager;
import dev.rosewood.rosetimber.manager.HookManager;
import dev.rosewood.rosetimber.manager.SaplingManager;
import dev.rosewood.rosetimber.manager.TreeAnimationManager;
import dev.rosewood.rosetimber.manager.TreeDefinitionManager;
import dev.rosewood.rosetimber.manager.TreeDetectionManager;
import dev.rosewood.rosetimber.tree.DetectedTree;
import dev.rosewood.rosetimber.tree.ITreeBlock;
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

public class TreeFallListener implements Listener {

    private final RosePlugin rosePlugin;
    private final TreeDefinitionManager treeDefinitionManager;
    private final TreeDetectionManager treeDetectionManager;
    private final TreeAnimationManager treeAnimationManager;
    private final ChoppingManager choppingManager;
    private final SaplingManager saplingManager;
    private final HookManager hookManager;

    public TreeFallListener(RosePlugin rosePlugin) {
        this.rosePlugin = rosePlugin;
        this.treeDefinitionManager = rosePlugin.getManager(TreeDefinitionManager.class);
        this.treeDetectionManager = rosePlugin.getManager(TreeDetectionManager.class);
        this.treeAnimationManager = rosePlugin.getManager(TreeAnimationManager.class);
        this.choppingManager = rosePlugin.getManager(ChoppingManager.class);
        this.saplingManager = rosePlugin.getManager(SaplingManager.class);
        this.hookManager = rosePlugin.getManager(HookManager.class);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event instanceof TreeBlockBreakEvent)
            return;

        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack tool = player.getInventory().getItemInMainHand();

        // Protect saplings
        if (this.saplingManager.isSaplingProtected(block)) {
            event.setCancelled(true);
            return;
        }

        // Condition checks
        boolean isValid = !SettingKey.DISABLED_WORLDS.get().contains(player.getWorld().getName())
                && (SettingKey.ALLOW_CREATIVE_MODE.get() || !player.getGameMode().equals(GameMode.CREATIVE))
                && this.checkToppleWhile(player)
                && (!SettingKey.REQUIRE_CHOP_PERMISSION.get() || player.hasPermission("rosetimber.chop"))
                && this.choppingManager.isChopping(player)
                && !this.choppingManager.isInCooldown(player)
                && this.treeDefinitionManager.isToolValidForAnyTreeDefinition(tool)
                && this.hookManager.isUsingAbilityHooks(player);

        if (this.treeAnimationManager.isBlockInAnimation(block)) {
            isValid = false;
            event.setCancelled(true);
        }

        boolean alwaysReplantSapling = SettingKey.ALWAYS_REPLANT_SAPLING.get();
        if (!isValid && !alwaysReplantSapling)
            return;

        DetectedTree detectedTree = this.treeDetectionManager.detectTree(block);
        if (detectedTree == null)
            return;

        if (alwaysReplantSapling) {
            Bukkit.getScheduler().runTask(this.rosePlugin, () ->
                    this.saplingManager.replantSapling(detectedTree.getTreeDefinition(), detectedTree.getDetectedTreeBlocks().getInitialLogBlock()));

            if (!isValid)
                return;
        }

        if (!this.treeDefinitionManager.isToolValidForTreeDefinition(detectedTree.getTreeDefinition(), tool))
            return;

        int toolDamage = this.getToolDamage(detectedTree.getDetectedTreeBlocks(), tool.containsEnchantment(Enchantment.SILK_TOUCH));
        if (SettingKey.PROTECT_TOOL.get() && !TimberUtils.hasEnoughDurability(tool, toolDamage))
            return;

        // Trigger fall event
        TreeFallEvent treeFallEvent = new TreeFallEvent(player, detectedTree);
        Bukkit.getPluginManager().callEvent(treeFallEvent);
        if (treeFallEvent.isCancelled())
            return;

        // Valid tree and meets all conditions past this point
        event.setCancelled(true);

        this.choppingManager.cooldownPlayer(player);

        // Destroy initiated block if enabled
        if (SettingKey.DESTROY_INITIATED_BLOCK.get()) {
            detectedTree.getDetectedTreeBlocks().getInitialLogBlock().getBlock().setType(Material.AIR);
            detectedTree.getDetectedTreeBlocks().remove(detectedTree.getDetectedTreeBlocks().getInitialLogBlock());
        }

        // Trigger block break events if enabled
        if (SettingKey.TRIGGER_BLOCK_BREAK_EVENTS.get()) {
            for (ITreeBlock<Block> treeBlock : detectedTree.getDetectedTreeBlocks()) {
                BlockBreakEvent blockBreakEvent = new TreeBlockBreakEvent(detectedTree, treeBlock.getBlock(), player);
                Bukkit.getPluginManager().callEvent(blockBreakEvent);
                if (blockBreakEvent.isCancelled())
                    detectedTree.getDetectedTreeBlocks().remove(treeBlock);
            }
        }

        if (!player.getGameMode().equals(GameMode.CREATIVE))
            TimberUtils.applyToolDurability(player, toolDamage);

        this.hookManager.applyExperienceHooks(player, detectedTree.getDetectedTreeBlocks());
        this.treeAnimationManager.runAnimation(detectedTree, player);
        this.treeDefinitionManager.dropTreeLoot(detectedTree.getTreeDefinition(), detectedTree.getDetectedTreeBlocks().getInitialLogBlock(), player, false, true);

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
        return switch (OnlyToppleWhile.fromString(SettingKey.ONLY_TOPPLE_WHILE.get())) {
            case SNEAKING -> player.isSneaking();
            case NOT_SNEAKING -> !player.isSneaking();
            default -> true;
        };
    }

    /**
     * Gets the amount of damage that should be applied to the tool
     *
     * @param treeBlocks   The tree blocks that were detected
     * @param hasSilkTouch true if the tool has silk touch, false otherwise
     * @return The amount of damage to apply to the tool
     */
    private int getToolDamage(TreeBlockSet<Block> treeBlocks, boolean hasSilkTouch) {
        if (!SettingKey.REALISTIC_TOOL_DAMAGE.get())
            return 1;

        if (SettingKey.APPLY_SILK_TOUCH_TOOL_DAMAGE.get() && hasSilkTouch) {
            return treeBlocks.size();
        } else {
            return treeBlocks.getLogBlocks().size();
        }
    }

}
