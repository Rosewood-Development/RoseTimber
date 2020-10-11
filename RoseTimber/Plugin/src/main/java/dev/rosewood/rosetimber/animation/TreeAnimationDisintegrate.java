package dev.rosewood.rosetimber.animation;

import dev.rosewood.rosetimber.RoseTimber;
import dev.rosewood.rosetimber.manager.ConfigurationManager.Setting;
import dev.rosewood.rosetimber.manager.TreeDefinitionManager;
import dev.rosewood.rosetimber.tree.DetectedTree;
import dev.rosewood.rosetimber.tree.ITreeBlock;
import dev.rosewood.rosetimber.tree.TreeBlock;
import dev.rosewood.rosetimber.tree.TreeBlockType;
import dev.rosewood.rosetimber.tree.TreeDefinition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TreeAnimationDisintegrate extends TreeAnimation {

    public TreeAnimationDisintegrate(DetectedTree detectedTree, Player player) {
        super(detectedTree, player);
    }

    @Override
    public void playAnimation(Runnable whenFinished) {
        RoseTimber roseTimber = RoseTimber.getInstance();
        TreeDefinitionManager treeDefinitionManager = roseTimber.getManager(TreeDefinitionManager.class);

        boolean useCustomSound = Setting.USE_CUSTOM_SOUNDS.getBoolean();
        boolean useCustomParticles = Setting.USE_CUSTOM_PARTICLES.getBoolean();

        List<ITreeBlock<Block>> orderedLogBlocks = new ArrayList<>(this.detectedTree.getDetectedTreeBlocks().getLogBlocks());
        orderedLogBlocks.sort(Comparator.comparingInt(x -> x.getLocation().getBlockY()));

        List<ITreeBlock<Block>> leafBlocks = new ArrayList<>(this.detectedTree.getDetectedTreeBlocks().getLeafBlocks());
        Collections.shuffle(leafBlocks);

        TreeDefinition td = this.detectedTree.getTreeDefinition();

        new BukkitRunnable() {
            @Override
            public void run() {
                List<ITreeBlock<Block>> toDestroy = new ArrayList<>();

                if (!orderedLogBlocks.isEmpty()) {
                    ITreeBlock<Block> treeBlock = orderedLogBlocks.remove(0);
                    toDestroy.add(treeBlock);
                } else if (!leafBlocks.isEmpty()) {
                    ITreeBlock<Block> treeBlock = leafBlocks.remove(0);
                    toDestroy.add(treeBlock);

                    if (!leafBlocks.isEmpty()) {
                        treeBlock = leafBlocks.remove(0);
                        toDestroy.add(treeBlock);
                    }
                }

                for (ITreeBlock<FallingBlock> fallingTreeBlock : TreeAnimationDisintegrate.this.fallingTreeBlocks.getAllTreeBlocks()) {
                    FallingBlock fallingBlock = fallingTreeBlock.getBlock();
                    fallingBlock.setVelocity(fallingBlock.getVelocity().clone().subtract(new Vector(0, 0.05, 0)));
                }

                if (!toDestroy.isEmpty()) {
                    ITreeBlock<Block> first = toDestroy.get(0);
                    if (useCustomSound)
                        TreeAnimationDisintegrate.this.playLandingSound(first);

                    for (ITreeBlock<Block> treeBlock : toDestroy) {
                        if (treeBlock.getTreeBlockType().equals(TreeBlockType.LOG)) {
                            if (td.getLogBlockTypes().stream().noneMatch(treeBlock.getBlock().getType()::equals))
                                continue;
                        } else if (treeBlock.getTreeBlockType().equals(TreeBlockType.LEAF)) {
                            if (td.getLeafBlockTypes().stream().noneMatch(treeBlock.getBlock().getType()::equals))
                                continue;
                        }

                        if (useCustomParticles)
                            TreeAnimationDisintegrate.this.playFallingParticles(treeBlock);

                        treeDefinitionManager.dropTreeLoot(td, treeBlock, TreeAnimationDisintegrate.this.player, TreeAnimationDisintegrate.this.hasSilkTouch, false);
                        TreeAnimationDisintegrate.this.replaceBlock((TreeBlock) treeBlock);
                    }
                } else {
                    this.cancel();
                    whenFinished.run();
                }
            }
        }.runTaskTimer(roseTimber, 0, 1);
    }

}
