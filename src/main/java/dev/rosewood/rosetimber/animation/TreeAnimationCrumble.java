package dev.rosewood.rosetimber.animation;

import dev.rosewood.rosetimber.RoseTimber;
import dev.rosewood.rosetimber.config.SettingKey;
import dev.rosewood.rosetimber.tree.DetectedTree;
import dev.rosewood.rosetimber.tree.FallingTreeBlock;
import dev.rosewood.rosetimber.tree.ITreeBlock;
import dev.rosewood.rosetimber.tree.TreeBlock;
import dev.rosewood.rosetimber.tree.TreeBlockType;
import dev.rosewood.rosetimber.tree.TreeDefinition;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TreeAnimationCrumble extends TreeAnimation {

    public TreeAnimationCrumble(DetectedTree detectedTree, Player player) {
        super(detectedTree, player);
    }

    @Override
    public void playAnimation(Runnable whenFinished) {
        RoseTimber roseTimber = RoseTimber.getInstance();

        boolean useCustomSound = SettingKey.USE_CUSTOM_SOUNDS.get();
        boolean useCustomParticles = SettingKey.USE_CUSTOM_PARTICLES.get();

        // Order blocks by y-axis, lowest first, but shuffled randomly
        int currentY = -1;
        List<List<ITreeBlock<Block>>> treeBlocks = new ArrayList<>();
        List<ITreeBlock<Block>> currentPartition = new ArrayList<>();
        List<ITreeBlock<Block>> orderedDetectedTreeBlocks = new ArrayList<>(this.detectedTree.getDetectedTreeBlocks().getAllTreeBlocks());
        orderedDetectedTreeBlocks.sort(Comparator.comparingInt(x -> x.getLocation().getBlockY()));
        for (ITreeBlock<Block> treeBlock : orderedDetectedTreeBlocks) {
            if (currentY != treeBlock.getLocation().getBlockY()) {
                Collections.shuffle(currentPartition);
                treeBlocks.add(new ArrayList<>(currentPartition));
                currentPartition.clear();
                currentY = treeBlock.getLocation().getBlockY();
            }
            currentPartition.add(treeBlock);
        }

        Collections.shuffle(currentPartition);
        treeBlocks.add(new ArrayList<>(currentPartition));

        TreeDefinition td = this.detectedTree.getTreeDefinition();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!treeBlocks.isEmpty()) {
                    List<ITreeBlock<Block>> partition = treeBlocks.get(0);
                    for (int i = 0; i < 3 && !partition.isEmpty(); i++) {
                        ITreeBlock<Block> treeBlock = partition.remove(0);
                        if (treeBlock.getTreeBlockType().equals(TreeBlockType.LOG)) {
                            if (td.getLogBlockTypes().stream().noneMatch(treeBlock.getBlock().getType()::equals))
                                continue;
                        } else if (treeBlock.getTreeBlockType().equals(TreeBlockType.LEAF)) {
                            if (td.getLeafBlockTypes().stream().noneMatch(treeBlock.getBlock().getType()::equals))
                                continue;
                        }

                        FallingTreeBlock fallingTreeBlock = TreeAnimationCrumble.this.convertToFallingBlock((TreeBlock) treeBlock);
                        if (fallingTreeBlock == null)
                            continue;

                        fallingTreeBlock.getBlock().setGravity(true);
                        fallingTreeBlock.getBlock().setVelocity(Vector.getRandom().setY(0).subtract(new Vector(0.5, 0, 0.5)).multiply(0.15));
                        TreeAnimationCrumble.this.fallingTreeBlocks.add(fallingTreeBlock);

                        if (useCustomSound)
                            TreeAnimationCrumble.this.playLandingSound(fallingTreeBlock);

                        if (useCustomParticles)
                            TreeAnimationCrumble.this.playFallingParticles(fallingTreeBlock);
                    }

                    if (partition.isEmpty())
                        treeBlocks.remove(0);
                }

                if (treeBlocks.isEmpty() && TreeAnimationCrumble.this.fallingTreeBlocks.getAllTreeBlocks().isEmpty()) {
                    whenFinished.run();
                    this.cancel();
                }
            }
        }.runTaskTimer(roseTimber, 0, 1);
    }
}

