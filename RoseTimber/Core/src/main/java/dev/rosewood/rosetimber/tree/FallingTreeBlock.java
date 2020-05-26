package dev.rosewood.rosetimber.tree;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;

public class FallingTreeBlock implements ITreeBlock<FallingBlock> {

    private final FallingBlock fallingBlock;
    private final TreeBlockType treeBlockType;

    public FallingTreeBlock(FallingBlock fallingBlock, TreeBlockType treeBlockType) {
        this.fallingBlock = fallingBlock;
        this.treeBlockType = treeBlockType;
    }

    @Override
    public FallingBlock getBlock() {
        return this.fallingBlock;
    }

    @Override
    public BlockData getBlockData() {
        return this.fallingBlock.getBlockData();
    }

    @Override
    public World getWorld() {
        return this.fallingBlock.getWorld();
    }

    @Override
    public Location getLocation() {
        return this.fallingBlock.getLocation();
    }

    @Override
    public TreeBlockType getTreeBlockType() {
        return this.treeBlockType;
    }

}
