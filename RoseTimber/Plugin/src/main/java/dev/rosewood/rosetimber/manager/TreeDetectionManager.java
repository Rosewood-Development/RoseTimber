package dev.rosewood.rosetimber.manager;

import dev.rosewood.rosetimber.RoseTimber;
import dev.rosewood.rosetimber.manager.ConfigurationManager.Setting;
import dev.rosewood.rosetimber.tree.DetectedTree;
import dev.rosewood.rosetimber.tree.ITreeBlock;
import dev.rosewood.rosetimber.tree.TreeBlock;
import dev.rosewood.rosetimber.tree.TreeBlockSet;
import dev.rosewood.rosetimber.tree.TreeBlockType;
import dev.rosewood.rosetimber.tree.TreeDefinition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class TreeDetectionManager extends Manager {

    private final List<Vector> VALID_TRUNK_OFFSETS, VALID_BRANCH_OFFSETS, VALID_LEAF_OFFSETS;

    private TreeDefinitionManager treeDefinitionManager;
    private PlacedBlockManager placedBlockManager;
    private int maxLogBlocksAllowed, numLeavesRequiredForTree;
    private boolean onlyBreakLogsUpwards, entireTreeBase, destroyLeaves;

    public TreeDetectionManager(RoseTimber roseTimber) {
        super(roseTimber);

        this.VALID_BRANCH_OFFSETS = new ArrayList<>();
        this.VALID_TRUNK_OFFSETS = new ArrayList<>();
        this.VALID_LEAF_OFFSETS = new ArrayList<>();

        // 3x2x3 centered around log, excluding -y axis
        for (int y = 0; y <= 1; y++)
            for (int x = -1; x <= 1; x++)
                for (int z = -1; z <= 1; z++)
                    this.VALID_BRANCH_OFFSETS.add(new Vector(x, y, z));

        // 3x3x3 centered around log
        for (int y = -1; y <= 1; y++)
            for (int x = -1; x <= 1; x++)
                for (int z = -1; z <= 1; z++)
                    this.VALID_TRUNK_OFFSETS.add(new Vector(x, y, z));

        // Adjacent blocks to log
        for (int i = -1; i <= 1; i += 2) {
            this.VALID_LEAF_OFFSETS.add(new Vector(i, 0, 0));
            this.VALID_LEAF_OFFSETS.add(new Vector(0, i, 0));
            this.VALID_LEAF_OFFSETS.add(new Vector(0, 0, i));
        }
    }

    @Override
    public void reload() {
        this.treeDefinitionManager = this.roseTimber.getManager(TreeDefinitionManager.class);
        this.placedBlockManager = this.roseTimber.getManager(PlacedBlockManager.class);
        this.maxLogBlocksAllowed = Setting.MAX_LOGS_PER_CHOP.getInt();
        this.numLeavesRequiredForTree = Setting.LEAVES_REQUIRED_FOR_TREE.getInt();
        this.onlyBreakLogsUpwards = Setting.ONLY_DETECT_LOGS_UPWARDS.getBoolean();
        this.entireTreeBase = Setting.BREAK_ENTIRE_TREE_BASE.getBoolean();
        this.destroyLeaves = Setting.DESTROY_LEAVES.getBoolean();
    }

    @Override
    public void disable() {

    }

    /**
     * Detects a tree given an initial starting block
     *
     * @param initialBlock The starting Block of the detection
     * @return A DetectedTree if one was found, otherwise null
     */
    public DetectedTree detectTree(Block initialBlock) {
        TreeDefinitionManager treeDefinitionManager = this.roseTimber.getManager(TreeDefinitionManager.class);

        TreeBlock initialTreeBlock = new TreeBlock(initialBlock, TreeBlockType.LOG);
        TreeBlockSet<Block> detectedTreeBlocks = new TreeBlockSet<>(initialTreeBlock);
        List<TreeDefinition> possibleTreeDefinitions = this.treeDefinitionManager.getTreeDefinitionsForLog(initialBlock);

        if (possibleTreeDefinitions.isEmpty())
            return null;

        // Detect tree trunk
        List<Block> trunkBlocks = new ArrayList<>();
        trunkBlocks.add(initialBlock);
        Block targetBlock = initialBlock;
        while (this.isValidLogType(possibleTreeDefinitions, null, (targetBlock = targetBlock.getRelative(BlockFace.UP)))) {
            trunkBlocks.add(targetBlock);
            possibleTreeDefinitions.retainAll(this.treeDefinitionManager.narrowTreeDefinition(possibleTreeDefinitions, targetBlock, TreeBlockType.LOG));
        }

        if (!this.onlyBreakLogsUpwards) {
            targetBlock = initialBlock;
            while (this.isValidLogType(possibleTreeDefinitions, null, (targetBlock = targetBlock.getRelative(BlockFace.DOWN)))) {
                trunkBlocks.add(targetBlock);
                possibleTreeDefinitions.retainAll(this.treeDefinitionManager.narrowTreeDefinition(possibleTreeDefinitions, targetBlock, TreeBlockType.LOG));
            }
        }

        // Lowest blocks at the front of the list
        Collections.reverse(trunkBlocks);

        // Detect branches off the main trunk
        for (Block trunkBlock : trunkBlocks)
            this.recursiveBranchSearch(possibleTreeDefinitions, trunkBlocks, detectedTreeBlocks, trunkBlock, initialBlock.getLocation().getBlockY());

        // Detect leaves off the trunk/branches
        List<ITreeBlock<Block>> branchBlocks = new ArrayList<>(detectedTreeBlocks.getLogBlocks());
        for (ITreeBlock<Block> branchBlock : branchBlocks)
            this.recursiveLeafSearch(possibleTreeDefinitions, detectedTreeBlocks, branchBlock.getBlock(), new ArrayList<>());

        // Use the first tree definition in the set
        TreeDefinition actualTreeDefinition = possibleTreeDefinitions.iterator().next();

        // Trees need at least a certain number of leaves
        if (detectedTreeBlocks.getLeafBlocks().size() < this.numLeavesRequiredForTree)
            return null;

        // Remove leaves if we don't care about the leaves
        if (!this.destroyLeaves)
            detectedTreeBlocks.removeAll(TreeBlockType.LEAF);

        // Check that the tree isn't on the ground if enabled
        if (this.entireTreeBase) {
            List<Block> groundBlocks = new ArrayList<>();
            for (ITreeBlock<Block> treeBlock : detectedTreeBlocks.getLogBlocks())
                if (treeBlock != detectedTreeBlocks.getInitialLogBlock() && treeBlock.getLocation().getBlockY() == initialBlock.getLocation().getBlockY())
                    groundBlocks.add(treeBlock.getBlock());

            for (Block block : groundBlocks) {
                Block blockBelow = block.getRelative(BlockFace.DOWN);
                boolean blockBelowIsLog = this.isValidLogType(possibleTreeDefinitions, null, blockBelow);
                boolean blockBelowIsSoil = false;
                for (Material blockType : treeDefinitionManager.getPlantableSoilBlockTypes(actualTreeDefinition)) {
                    if (blockType == blockBelow.getType()) {
                        blockBelowIsSoil = true;
                        break;
                    }
                }

                if (blockBelowIsLog || blockBelowIsSoil)
                    return null;
            }
        }

        return new DetectedTree(actualTreeDefinition, detectedTreeBlocks);
    }

    /**
     * Recursively searches for branches off a given block
     *
     * @param treeDefinitions The possible tree definitions
     * @param trunkBlocks     The tree trunk blocks
     * @param treeBlocks      The detected tree blocks
     * @param block           The next block to check for a branch
     * @param startingBlockY  The Y coordinate of the initial block
     */
    private void recursiveBranchSearch(List<TreeDefinition> treeDefinitions, List<Block> trunkBlocks, TreeBlockSet<Block> treeBlocks, Block block, int startingBlockY) {
        if (treeBlocks.size() > this.maxLogBlocksAllowed)
            return;

        for (Vector offset : this.onlyBreakLogsUpwards ? this.VALID_BRANCH_OFFSETS : this.VALID_TRUNK_OFFSETS) {
            Block targetBlock = block.getRelative(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ());
            TreeBlock treeBlock = new TreeBlock(targetBlock, TreeBlockType.LOG);
            if (this.isValidLogType(treeDefinitions, trunkBlocks, targetBlock) && !treeBlocks.contains(treeBlock)) {
                treeBlocks.add(treeBlock);
                treeDefinitions.retainAll(this.treeDefinitionManager.narrowTreeDefinition(treeDefinitions, targetBlock, TreeBlockType.LOG));
                if (!this.onlyBreakLogsUpwards || targetBlock.getLocation().getBlockY() > startingBlockY)
                    this.recursiveBranchSearch(treeDefinitions, trunkBlocks, treeBlocks, targetBlock, startingBlockY);
            }
        }
    }

    /**
     * Recursively searches for leaves that are next to this tree
     *
     * @param treeDefinitions The possible tree definitions
     * @param treeBlocks      The detected tree blocks
     * @param block           The next block to check for a leaf
     */
    private void recursiveLeafSearch(List<TreeDefinition> treeDefinitions, TreeBlockSet<Block> treeBlocks, Block block, List<Block> visitedBlocks) {
        boolean detectLeavesDiagonally = treeDefinitions.stream().anyMatch(TreeDefinition::shouldDetectLeavesDiagonally);

        for (Vector offset : !detectLeavesDiagonally ? this.VALID_LEAF_OFFSETS : this.VALID_TRUNK_OFFSETS) {
            Block targetBlock = block.getRelative(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ());
            if (visitedBlocks.contains(targetBlock))
                continue;

            visitedBlocks.add(targetBlock);
            TreeBlock treeBlock = new TreeBlock(targetBlock, TreeBlockType.LEAF);
            if (this.isValidLeafType(treeDefinitions, treeBlocks, targetBlock) && !treeBlocks.contains(treeBlock) && !this.doesLeafBorderInvalidLog(treeDefinitions, treeBlocks, targetBlock)) {
                treeBlocks.add(treeBlock);
                treeDefinitions.retainAll(this.treeDefinitionManager.narrowTreeDefinition(treeDefinitions, targetBlock, TreeBlockType.LEAF));
                this.recursiveLeafSearch(treeDefinitions, treeBlocks, targetBlock, visitedBlocks);
            }
        }
    }

    /**
     * Checks if a leaf is bordering a log that isn't part of this tree
     *
     * @param treeDefinitions The possible tree definitions
     * @param treeBlocks      The detected tree blocks
     * @param block           The block to check
     * @return True if the leaf borders an invalid log, otherwise false
     */
    private boolean doesLeafBorderInvalidLog(List<TreeDefinition> treeDefinitions, TreeBlockSet<Block> treeBlocks, Block block) {
        for (Vector offset : this.VALID_TRUNK_OFFSETS) {
            Block targetBlock = block.getRelative(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ());
            if (this.isValidLogType(treeDefinitions, null, targetBlock) && !treeBlocks.contains(new TreeBlock(targetBlock, TreeBlockType.LOG)))
                return true;
        }
        return false;
    }

    /**
     * Checks if a given block is valid for the given TreeDefinitions
     *
     * @param treeDefinitions The List of TreeDefinitions to compare against
     * @param trunkBlocks     The trunk blocks of the tree for checking the distance
     * @param block           The Block to check
     * @return True if the block is a valid log type, otherwise false
     */
    private boolean isValidLogType(List<TreeDefinition> treeDefinitions, List<Block> trunkBlocks, Block block) {
        // Check if block is placed
        if (this.placedBlockManager.isBlockPlaced(block))
            return false;

        // Check if it matches the tree definition
        boolean isCorrectType = false;
        for (TreeDefinition treeDefinition : treeDefinitions) {
            for (Material logBlockType : treeDefinition.getLogBlockTypes()) {
                if (logBlockType == block.getType()) {
                    isCorrectType = true;
                    break;
                }
            }
        }

        if (!isCorrectType)
            return false;

        // Check that it is close enough to the trunk
        if (trunkBlocks == null || trunkBlocks.isEmpty())
            return true;

        Location location = block.getLocation();
        for (TreeDefinition treeDefinition : treeDefinitions) {
            double maxDistance = treeDefinition.getMaxLogDistanceFromTrunk() * treeDefinition.getMaxLogDistanceFromTrunk();
            if (!this.onlyBreakLogsUpwards) // Help detect logs more often if the tree isn't broken at the base
                maxDistance *= 1.5;
            for (Block trunkBlock : trunkBlocks)
                if (location.distanceSquared(trunkBlock.getLocation()) < maxDistance)
                    return true;
        }

        return false;
    }

    /**
     * Checks if a given block is valid for the given TreeDefinitions
     *
     * @param treeDefinitions The List of TreeDefinitions to compare against
     * @param treeBlocks      The detected blocks of the tree for checking leaf distance
     * @param block           The Block to check
     * @return True if the block is a valid log type, otherwise false
     */
    private boolean isValidLeafType(List<TreeDefinition> treeDefinitions, TreeBlockSet<Block> treeBlocks, Block block) {
        // Check if block is placed
        if (this.placedBlockManager.isBlockPlaced(block))
            return false;

        // Check if it matches the tree definition
        boolean isCorrectType = false;
        for (TreeDefinition treeDefinition : treeDefinitions) {
            for (Material leafBlockType : treeDefinition.getLeafBlockTypes()) {
                if (leafBlockType == block.getType()) {
                    isCorrectType = true;
                    break;
                }
            }
        }

        if (!isCorrectType)
            return false;

        // Check that it is close enough to a log
        if (treeBlocks == null || treeBlocks.isEmpty())
            return true;

        double maxDistanceFromLog = treeDefinitions.stream().map(TreeDefinition::getMaxLeafDistanceFromLog).max(Double::compareTo).orElse(0.0);
        return treeBlocks.getLogBlocks().stream().anyMatch(x -> x.getLocation().distanceSquared(block.getLocation()) < maxDistanceFromLog * maxDistanceFromLog);
    }

}
