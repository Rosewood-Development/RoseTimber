package dev.rosewood.rosetimber.tree;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;

public class FallingTreeBlock extends ITreeBlock<FallingBlock> {

    private final FallingBlock fallingBlock;

    public FallingTreeBlock(FallingBlock fallingBlock, TreeBlockType treeBlockType) {
        super(treeBlockType);
        this.fallingBlock = fallingBlock;
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

}
