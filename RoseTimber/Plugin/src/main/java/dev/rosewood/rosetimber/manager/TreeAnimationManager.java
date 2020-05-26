package dev.rosewood.rosetimber.manager;

import dev.rosewood.rosetimber.RoseTimber;
import dev.rosewood.rosetimber.animation.TreeAnimation;
import dev.rosewood.rosetimber.animation.TreeAnimationCrumble;
import dev.rosewood.rosetimber.animation.TreeAnimationDisintegrate;
import dev.rosewood.rosetimber.animation.TreeAnimationTopple;
import dev.rosewood.rosetimber.animation.TreeAnimationNone;
import dev.rosewood.rosetimber.animation.TreeAnimationType;
import dev.rosewood.rosetimber.manager.ConfigurationManager.Setting;
import dev.rosewood.rosetimber.tree.DetectedTree;
import dev.rosewood.rosetimber.tree.ITreeBlock;
import dev.rosewood.rosetimber.tree.TreeDefinition;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class TreeAnimationManager extends Manager implements Listener, Runnable {

    private final Set<TreeAnimation> activeAnimations;
    private int taskId;

    public TreeAnimationManager(RoseTimber roseTimber) {
        super(roseTimber);
        this.activeAnimations = new HashSet<>();
        this.taskId = -1;
        Bukkit.getPluginManager().registerEvents(this, roseTimber);
        Bukkit.getScheduler().runTaskTimer(this.roseTimber, this, 0, 1L);
    }

    @Override
    public void reload() {
        this.activeAnimations.clear();
    }

    @Override
    public void disable() {
        this.activeAnimations.clear();
        Bukkit.getScheduler().cancelTask(this.taskId);
    }

    @Override
    public void run() {
        for (TreeAnimation treeAnimation : this.activeAnimations) {
            Set<ITreeBlock<FallingBlock>> groundedBlocks = new HashSet<>();
            for (ITreeBlock<FallingBlock> fallingTreeBlock : treeAnimation.getFallingTreeBlocks().getAllTreeBlocks()) {
                FallingBlock fallingBlock = fallingTreeBlock.getBlock();
                if (fallingBlock.isDead())
                    groundedBlocks.add(fallingTreeBlock);
            }

            for (ITreeBlock<FallingBlock> fallingBlock : groundedBlocks) {
                this.runFallingBlockImpact(treeAnimation, fallingBlock);
                treeAnimation.getFallingTreeBlocks().remove(fallingBlock);
            }
        }
    }

    /**
     * Plays an animation for toppling a tree
     *
     * @param detectedTree The DetectedTree
     * @param player The Player who toppled the tree
     */
    public void runAnimation(DetectedTree detectedTree, Player player) {
        switch (TreeAnimationType.fromString(Setting.TREE_ANIMATION_TYPE.getString())) {
            case TOPPLE:
                this.registerTreeAnimation(new TreeAnimationTopple(detectedTree, player));
                break;
            case DISINTEGRATE:
                this.registerTreeAnimation(new TreeAnimationDisintegrate(detectedTree, player));
                break;
            case CRUMBLE:
                this.registerTreeAnimation(new TreeAnimationCrumble(detectedTree, player));
                break;
            case NONE:
                this.registerTreeAnimation(new TreeAnimationNone(detectedTree, player));
                break;
        }
    }

    /**
     * Checks if the given block is in an animation
     *
     * @param block The block to check
     */
    public boolean isBlockInAnimation(Block block) {
        for (TreeAnimation treeAnimation : this.activeAnimations)
            for (ITreeBlock<Block> treeBlock : treeAnimation.getDetectedTree().getDetectedTreeBlocks().getAllTreeBlocks())
                if (treeBlock.getBlock().equals(block))
                    return true;
        return false;
    }

    /**
     * Checks if the given falling block is in an animation
     *
     * @param fallingBlock The falling block to check
     */
    public boolean isBlockInAnimation(FallingBlock fallingBlock) {
        for (TreeAnimation treeAnimation : this.activeAnimations)
            for (ITreeBlock<FallingBlock> treeBlock : treeAnimation.getFallingTreeBlocks().getAllTreeBlocks())
                if (treeBlock.getBlock().equals(fallingBlock))
                    return true;
        return false;
    }

    /**
     * Gets a TreeAnimation that a given falling block is in
     *
     * @return A TreeAnimation
     */
    private TreeAnimation getAnimationForBlock(FallingBlock fallingBlock) {
        for (TreeAnimation treeAnimation : this.activeAnimations)
            for (ITreeBlock<FallingBlock> treeBlock : treeAnimation.getFallingTreeBlocks().getAllTreeBlocks())
                if (treeBlock.getBlock().equals(fallingBlock))
                    return treeAnimation;
        return null;
    }

    /**
     * Registers and runs a tree animation
     */
    private void registerTreeAnimation(TreeAnimation treeAnimation) {
        this.activeAnimations.add(treeAnimation);
        treeAnimation.playAnimation(() -> this.activeAnimations.remove(treeAnimation));
    }

    /**
     * Reacts to a falling block hitting the ground
     *
     * @param treeAnimation The tree animation for the falling block
     * @param treeBlock The tree block to impact
     */
    public void runFallingBlockImpact(TreeAnimation treeAnimation, ITreeBlock<FallingBlock> treeBlock) {
        TreeDefinitionManager treeDefinitionManager = this.roseTimber.getManager(TreeDefinitionManager.class);
        boolean useCustomSound = Setting.USE_CUSTOM_SOUNDS.getBoolean();
        boolean useCustomParticles = Setting.USE_CUSTOM_PARTICLES.getBoolean();
        TreeDefinition treeDefinition = treeAnimation.getDetectedTree().getTreeDefinition();

        if (useCustomParticles)
            treeAnimation.playLandingParticles(treeBlock);

        if (useCustomSound)
            treeAnimation.playLandingSound(treeBlock);

        treeDefinitionManager.dropTreeLoot(treeDefinition, treeBlock, treeAnimation.getPlayer(), treeAnimation.hasSilkTouch(), false);
        this.roseTimber.getManager(SaplingManager.class).replantSaplingWithChance(treeDefinition, treeBlock);
        treeAnimation.getFallingTreeBlocks().remove(treeBlock);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFallingBlockLand(EntityChangeBlockEvent event) {
        if (!event.getEntityType().equals(EntityType.FALLING_BLOCK))
            return;

        FallingBlock fallingBlock = (FallingBlock) event.getEntity();
        if (!this.isBlockInAnimation(fallingBlock))
            return;

        TreeAnimation treeAnimation = this.getAnimationForBlock(fallingBlock);
        if (Setting.FALLING_BLOCKS_DEAL_DAMAGE.getBoolean()) {
            int damage = Setting.FALLING_BLOCK_DAMAGE.getInt();
            for (Entity entity : fallingBlock.getNearbyEntities(0.5, 0.5, 0.5)) {
                if (!(entity instanceof Damageable))
                    continue;

                Entity damageSource = treeAnimation == null ? fallingBlock : treeAnimation.getPlayer();
                ((Damageable) entity).damage(damage, damageSource);
            }
        }

        if (Setting.SCATTER_TREE_BLOCKS_ON_GROUND.getBoolean()) {
            if (treeAnimation != null) {
                treeAnimation.removeFallingBlock(fallingBlock);
                return;
            }
        }

        event.setCancelled(true);
    }

}
