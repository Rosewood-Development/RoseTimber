package dev.rosewood.rosetimber.tree;

import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class TreeBlock implements ITreeBlock<Block> {

    private final Block block;
    private final TreeBlockType treeBlockType;

    public TreeBlock(Block block, TreeBlockType treeBlockType) {
        this.block = block;
        this.treeBlockType = treeBlockType;
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

    @Override
    public TreeBlockType getTreeBlockType() {
        return this.treeBlockType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.block, this.treeBlockType);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TreeBlock)) return false;
        if (o == this) return true;
        TreeBlock oTreeBlock = (TreeBlock)o;
        return oTreeBlock.block.equals(this.block) && oTreeBlock.treeBlockType.equals(this.treeBlockType);
    }

}
