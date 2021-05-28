package dev.rosewood.rosetimber.tree;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class TreeBlock extends ITreeBlock<Block> {

    private final Block block;

    public TreeBlock(Block block, TreeBlockType treeBlockType) {
        super(treeBlockType);
        this.block = block;
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    @Override
    public BlockData getBlockData() {
        return this.block.getBlockData();
    }

    @Override
    public World getWorld() {
        return this.block.getWorld();
    }

    @Override
    public Location getLocation() {
        return this.block.getLocation();
    }

}
