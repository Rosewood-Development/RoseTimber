package dev.rosewood.rosetimber.animation;

import dev.rosewood.rosetimber.RoseTimber;
import dev.rosewood.rosetimber.manager.SaplingManager;
import dev.rosewood.rosetimber.tree.DetectedTree;
import dev.rosewood.rosetimber.tree.FallingTreeBlock;
import dev.rosewood.rosetimber.tree.ITreeBlock;
import dev.rosewood.rosetimber.tree.TreeBlock;
import dev.rosewood.rosetimber.tree.TreeBlockSet;
import dev.rosewood.rosetimber.tree.TreeBlockType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class TreeAnimation {

    protected final DetectedTree detectedTree;
    protected final Player player;
    protected final boolean hasSilkTouch;
    protected TreeBlockSet<FallingBlock> fallingTreeBlocks;

    TreeAnimation(DetectedTree detectedTree, Player player) {
        this.detectedTree = detectedTree;
        this.player = player;

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = itemInHand.getItemMeta();
        this.hasSilkTouch = itemMeta != null && itemMeta.hasEnchant(Enchantment.SILK_TOUCH);

        this.fallingTreeBlocks = new TreeBlockSet<>(); // Should be overridden in any subclasses that need to use it
    }

    /**
     * Plays this tree topple animation
     *
     * @param whenFinished The runnable to run when the animation is done
     */
    public abstract void playAnimation(Runnable whenFinished);

    /**
     * @return the detected tree
     */
    public DetectedTree getDetectedTree() {
        return this.detectedTree;
    }

    /**
     * @return the Player who started this tree animation
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * @return true if this animation has silk touch, otherwise false
     */
    public boolean hasSilkTouch() {
        return this.hasSilkTouch;
    }

    /**
     * Gets a TreeBlockSet of the active falling tree blocks
     * May return null if the animation type does not use falling blocks
     *
     * @return A tree block set
     */
    public TreeBlockSet<FallingBlock> getFallingTreeBlocks() {
        return this.fallingTreeBlocks;
    }

    /**
     * Converts a TreeBlock into a FallingTreeBlock
     *
     * @param treeBlock The TreeBlock to convert
     * @return A FallingTreeBlock that has been converted from a TreeBlock
     */
    protected FallingTreeBlock convertToFallingBlock(TreeBlock treeBlock) {
        Location location = treeBlock.getLocation().clone().add(0.5, 0, 0.5);
        Block block = treeBlock.getBlock();

        if (block.getType().equals(Material.AIR)) {
            this.replaceBlock(treeBlock);
            return null;
        }

        FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(location, block.getBlockData());
        fallingBlock.setGravity(false);
        fallingBlock.setDropItem(false);
        fallingBlock.setHurtEntities(false);

        FallingTreeBlock fallingTreeBlock = new FallingTreeBlock(fallingBlock, treeBlock.getTreeBlockType());
        this.replaceBlock(treeBlock);
        return fallingTreeBlock;
    }

    /**
     * Plays particles to indicate a tree block has started falling
     *
     * @param treeBlock The TreeBlock to play the particles for
     */
    public void playFallingParticles(ITreeBlock<?> treeBlock) {
        Location location = treeBlock.getLocation().clone().add(0.5, 0.5, 0.5);
        treeBlock.getWorld().spawnParticle(Particle.BLOCK, location, 10, treeBlock.getBlockData());
    }

    /**
     * Plays particles to indicate a tree block has hit the ground
     *
     * @param treeBlock The TreeBlock to play the particles for
     */
    public void playLandingParticles(ITreeBlock<?> treeBlock) {
        Location location = treeBlock.getLocation().clone().add(0.5, 0.5, 0.5);
        treeBlock.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, location, 10, treeBlock.getBlockData());
    }

    /**
     * Plays a sound to indicate a tree block has started falling
     *
     * @param treeBlock The TreeBlock to play the sound for
     */
    public void playFallingSound(ITreeBlock<?> treeBlock) {
        Location location = treeBlock.getLocation();
        treeBlock.getWorld().playSound(location, Sound.BLOCK_CHEST_OPEN, 2F, 0.1F);
    }

    /**
     * Plays a sound to indicate a tree block has hit the ground
     *
     * @param treeBlock The TreeBlock to play the sound for
     */
    public void playLandingSound(ITreeBlock<?> treeBlock) {
        Location location = treeBlock.getLocation();
        if (treeBlock.getTreeBlockType().equals(TreeBlockType.LOG)) {
            treeBlock.getWorld().playSound(location, Sound.BLOCK_WOOD_FALL, 2F, 0.1F);
        } else {
            treeBlock.getWorld().playSound(location, Sound.BLOCK_GRASS_BREAK, 0.5F, 0.75F);
        }
    }

    /**
     * Replaces a given block with a new one
     *
     * @param treeBlock The tree block to replace
     */
    public void replaceBlock(TreeBlock treeBlock) {
        treeBlock.getBlock().setType(Material.AIR);
        RoseTimber.getInstance().getManager(SaplingManager.class).replantSapling(this.detectedTree.getTreeDefinition(), treeBlock);
    }

    /**
     * Removes a falling block from the animation
     *
     * @param fallingBlock The FallingBlock to remove
     */
    public void removeFallingBlock(FallingBlock fallingBlock) {
        for (ITreeBlock<FallingBlock> fallingTreeBlock : this.fallingTreeBlocks.getAllTreeBlocks()) {
            if (fallingTreeBlock.getBlock().equals(fallingBlock)) {
                this.fallingTreeBlocks.remove(fallingTreeBlock);
                return;
            }
        }
    }

}
