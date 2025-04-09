package dev.rosewood.rosetimber.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosetimber.animation.TreeAnimation;
import dev.rosewood.rosetimber.animation.TreeAnimationCrumble;
import dev.rosewood.rosetimber.animation.TreeAnimationDisintegrate;
import dev.rosewood.rosetimber.animation.TreeAnimationNone;
import dev.rosewood.rosetimber.animation.TreeAnimationTopple;
import dev.rosewood.rosetimber.config.SettingsKey;
import dev.rosewood.rosetimber.tree.DetectedTree;
import dev.rosewood.rosetimber.tree.ITreeBlock;
import dev.rosewood.rosetimber.tree.TreeDefinition;
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
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class TreeAnimationManager extends Manager implements Listener, Runnable {

    private final Random random;
    private final Map<String, Constructor<? extends TreeAnimation>> registeredTreeAnimations;
    private final Set<TreeAnimation> activeAnimations;
    private BukkitTask task;

    public TreeAnimationManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.random = new Random();
        this.registeredTreeAnimations = new LinkedHashMap<>();
        this.activeAnimations = new HashSet<>();
        Bukkit.getPluginManager().registerEvents(this, rosePlugin);

        // Register default tree animations
        this.registerTreeAnimation("NONE", TreeAnimationNone.class);
        this.registerTreeAnimation("TOPPLE", TreeAnimationTopple.class);
        this.registerTreeAnimation("DISINTEGRATE", TreeAnimationDisintegrate.class);
        this.registerTreeAnimation("CRUMBLE", TreeAnimationCrumble.class);
    }

    @Override
    public void reload() {
        this.task = Bukkit.getScheduler().runTaskTimer(this.rosePlugin, this, 0, 1L);
    }

    @Override
    public void disable() {
        if (this.task != null)
            this.task.cancel();

        this.activeAnimations.clear();
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
     * Registers a new tree animation type
     *
     * @param animationName      The name of the tree animation, must be unique
     * @param treeAnimationClass The class of the tree animation
     * @return true if the animation was successfully registered, otherwise false
     */
    public boolean registerTreeAnimation(String animationName, Class<? extends TreeAnimation> treeAnimationClass) {
        String name = animationName.toUpperCase();
        if (this.registeredTreeAnimations.keySet().stream().anyMatch(name::equals))
            return false;

        try {
            // Make sure this constructor exists so we can actually create new instances of the class
            Constructor<? extends TreeAnimation> constructor = treeAnimationClass.getConstructor(DetectedTree.class, Player.class);
            this.registeredTreeAnimations.put(name, constructor);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public Collection<String> getRegisteredTreeAnimationNames() {
        return Collections.unmodifiableCollection(this.registeredTreeAnimations.keySet());
    }

    /**
     * Plays an animation for toppling a tree
     *
     * @param detectedTree The DetectedTree
     * @param player       The Player who toppled the tree
     */
    public void runAnimation(DetectedTree detectedTree, Player player) {
        List<String> treeAnimationTypes = detectedTree.getTreeDefinition().getTreeAnimationTypes();
        String animationType;
        if (treeAnimationTypes.isEmpty()) {
            animationType = "TOPPLE";
        } else {
            animationType = treeAnimationTypes.get(this.random.nextInt(treeAnimationTypes.size()));
        }

        Constructor<? extends TreeAnimation> treeAnimationConstructor = this.registeredTreeAnimations.get(animationType);
        if (treeAnimationConstructor == null)
            treeAnimationConstructor = this.registeredTreeAnimations.values().iterator().next();

        TreeAnimation treeAnimation;
        try {
            treeAnimation = treeAnimationConstructor.newInstance(detectedTree, player);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
            return;
        }

        this.activeAnimations.add(treeAnimation);
        treeAnimation.playAnimation(() -> this.activeAnimations.remove(treeAnimation));
    }

    /**
     * Checks if the given block is in an animation
     *
     * @param block The block to check
     * @return true if the block is in an animation, otherwise false
     */
    public boolean isBlockInAnimation(Block block) {
        for (TreeAnimation treeAnimation : this.activeAnimations)
            for (ITreeBlock<Block> treeBlock : treeAnimation.getDetectedTree().getDetectedTreeBlocks().getAllTreeBlocks())
                if (treeBlock.getBlock().equals(block))
                    return true;
        return false;
    }

    /**
     * Gets a TreeAnimation that a given falling block is in
     *
     * @param fallingBlock The falling block to check for
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
     * Reacts to a falling block hitting the ground
     *
     * @param treeAnimation The tree animation for the falling block
     * @param treeBlock     The tree block to impact
     */
    public void runFallingBlockImpact(TreeAnimation treeAnimation, ITreeBlock<FallingBlock> treeBlock) {
        TreeDefinitionManager treeDefinitionManager = this.rosePlugin.getManager(TreeDefinitionManager.class);
        boolean useCustomSound = SettingsKey.USE_CUSTOM_SOUNDS.get();
        boolean useCustomParticles = SettingsKey.USE_CUSTOM_PARTICLES.get();
        TreeDefinition treeDefinition = treeAnimation.getDetectedTree().getTreeDefinition();

        if (useCustomParticles)
            treeAnimation.playLandingParticles(treeBlock);

        if (useCustomSound)
            treeAnimation.playLandingSound(treeBlock);

        treeDefinitionManager.dropTreeLoot(treeDefinition, treeBlock, treeAnimation.getPlayer(), treeAnimation.hasSilkTouch(), false);
        this.rosePlugin.getManager(SaplingManager.class).replantSaplingWithChance(treeDefinition, treeBlock);
        treeAnimation.getFallingTreeBlocks().remove(treeBlock);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFallingBlockLand(EntityChangeBlockEvent event) {
        if (!event.getEntityType().equals(EntityType.FALLING_BLOCK))
            return;

        FallingBlock fallingBlock = (FallingBlock) event.getEntity();
        TreeAnimation treeAnimation = this.getAnimationForBlock(fallingBlock);
        if (treeAnimation == null)
            return;

        if (SettingsKey.FALLING_BLOCKS_DEAL_DAMAGE.get()) {
            int damage = SettingsKey.FALLING_BLOCK_DAMAGE.get();
            for (Entity entity : fallingBlock.getNearbyEntities(0.5, 0.5, 0.5)) {
                if (!(entity instanceof Damageable))
                    continue;

                Entity damageSource = treeAnimation.getPlayer();
                ((Damageable) entity).damage(damage, damageSource);
            }
        }

        if (treeAnimation.getDetectedTree().getTreeDefinition().shouldScatterTreeBlocksOnGround()) {
            treeAnimation.removeFallingBlock(fallingBlock);
            return;
        }

        event.setCancelled(true);
    }

}
