package dev.rosewood.rosetimber.animation;

import dev.rosewood.rosetimber.RoseTimber;
import dev.rosewood.rosetimber.config.SettingKey;
import dev.rosewood.rosetimber.manager.TreeAnimationManager;
import dev.rosewood.rosetimber.tree.DetectedTree;
import dev.rosewood.rosetimber.tree.FallingTreeBlock;
import dev.rosewood.rosetimber.tree.ITreeBlock;
import dev.rosewood.rosetimber.tree.TreeBlock;
import dev.rosewood.rosetimber.tree.TreeBlockSet;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TreeAnimationTopple extends TreeAnimation {

    public TreeAnimationTopple(DetectedTree detectedTree, Player player) {
        super(detectedTree, player);
    }

    @Override
    public void playAnimation(Runnable whenFinished) {
        RoseTimber roseTimber = RoseTimber.getInstance();

        boolean useCustomSound = SettingKey.USE_CUSTOM_SOUNDS.get();
        boolean useCustomParticles = SettingKey.USE_CUSTOM_PARTICLES.get();

        ITreeBlock<Block> initialTreeBlock = this.detectedTree.getDetectedTreeBlocks().getInitialLogBlock();
        FallingTreeBlock initialFallingBlock = this.convertToFallingBlock((TreeBlock) this.detectedTree.getDetectedTreeBlocks().getInitialLogBlock());

        if (useCustomSound)
            this.playFallingSound(initialTreeBlock);

        Vector velocityVector = initialTreeBlock.getLocation().clone().subtract(this.player.getLocation().clone()).toVector().normalize().setY(0);

        // Convert all blocks into falling blocks and then begin the animation
        this.fallingTreeBlocks = new TreeBlockSet<>(initialFallingBlock);
        for (ITreeBlock<Block> treeBlock : this.detectedTree.getDetectedTreeBlocks().getAllTreeBlocks()) {
            FallingTreeBlock fallingTreeBlock = this.convertToFallingBlock((TreeBlock) treeBlock);
            if (fallingTreeBlock == null)
                continue;

            FallingBlock fallingBlock = fallingTreeBlock.getBlock();
            this.fallingTreeBlocks.add(fallingTreeBlock);

            if (useCustomParticles)
                this.playFallingParticles(fallingTreeBlock);

            double multiplier = (treeBlock.getLocation().getY() - this.player.getLocation().getY()) * 0.05;
            fallingBlock.setVelocity(velocityVector.clone().multiply(multiplier));
            fallingBlock.setVelocity(fallingBlock.getVelocity().multiply(0.3));
        }

        new BukkitRunnable() {
            int timer = 0;

            @Override
            public void run() {
                // Cause all the blocks to start falling
                if (this.timer == 0) {
                    for (ITreeBlock<FallingBlock> fallingTreeBlock : TreeAnimationTopple.this.fallingTreeBlocks.getAllTreeBlocks()) {
                        FallingBlock fallingBlock = fallingTreeBlock.getBlock();
                        fallingBlock.setGravity(true);
                        fallingBlock.setVelocity(fallingBlock.getVelocity().multiply(1.5));
                    }
                }

                // If all blocks have been destroyed, end the animation
                if (TreeAnimationTopple.this.fallingTreeBlocks.getAllTreeBlocks().isEmpty()) {
                    whenFinished.run();
                    this.cancel();
                    return;
                }

                // Make the blocks fall faster than normal
                for (ITreeBlock<FallingBlock> fallingTreeBlock : TreeAnimationTopple.this.fallingTreeBlocks.getAllTreeBlocks()) {
                    FallingBlock fallingBlock = fallingTreeBlock.getBlock();
                    fallingBlock.setVelocity(fallingBlock.getVelocity().clone().subtract(new Vector(0, 0.05, 0)));
                }

                this.timer++;

                // Expire the animation if it goes on for more than 4 seconds
                if (this.timer > 4 * 20) {
                    TreeAnimationManager treeAnimationManager = roseTimber.getManager(TreeAnimationManager.class);
                    for (ITreeBlock<FallingBlock> fallingTreeBlock : TreeAnimationTopple.this.fallingTreeBlocks.getAllTreeBlocks())
                        treeAnimationManager.runFallingBlockImpact(TreeAnimationTopple.this, fallingTreeBlock);
                    whenFinished.run();
                    this.cancel();
                }
            }
        }.runTaskTimer(roseTimber, 20L, 1L);
    }

}
