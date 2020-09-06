package dev.rosewood.rosetimber.animation;

import dev.rosewood.rosetimber.RoseTimber;
import dev.rosewood.rosetimber.manager.ConfigurationManager.Setting;
import dev.rosewood.rosetimber.manager.TreeDefinitionManager;
import dev.rosewood.rosetimber.tree.DetectedTree;
import dev.rosewood.rosetimber.tree.ITreeBlock;
import dev.rosewood.rosetimber.tree.TreeBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TreeAnimationNone extends TreeAnimation {

    public TreeAnimationNone(DetectedTree detectedTree, Player player) {
        super(TreeAnimationType.NONE, detectedTree, player);
    }

    @Override
    public void playAnimation(Runnable whenFinished) {
        TreeDefinitionManager treeDefinitionManager = RoseTimber.getInstance().getManager(TreeDefinitionManager.class);

        if (Setting.USE_CUSTOM_SOUNDS.getBoolean())
            this.playFallingSound(this.detectedTree.getDetectedTreeBlocks().getInitialLogBlock());

        if (Setting.USE_CUSTOM_PARTICLES.getBoolean())
            for (ITreeBlock<Block> treeBlock : this.detectedTree.getDetectedTreeBlocks().getAllTreeBlocks())
                this.playFallingParticles(treeBlock);

        for (ITreeBlock<Block> treeBlock : this.detectedTree.getDetectedTreeBlocks().getAllTreeBlocks()) {
            treeDefinitionManager.dropTreeLoot(this.detectedTree.getTreeDefinition(), treeBlock, this.player, this.hasSilkTouch, false);
            this.replaceBlock((TreeBlock) treeBlock);
        }

        whenFinished.run();
    }

}
