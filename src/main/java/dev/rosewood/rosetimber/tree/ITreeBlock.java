package dev.rosewood.rosetimber.tree;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import java.util.Objects;

public abstract class ITreeBlock<T> {

    protected final TreeBlockType treeBlockType;

    public ITreeBlock(TreeBlockType treeBlockType) {
        this.treeBlockType = treeBlockType;
    }

    /**
     * @return The TreeBlockType of this TreeBlock
     */
    public TreeBlockType getTreeBlockType() {
        return this.treeBlockType;
    }

    /**
     * @return the Block of this TreeBlock
     */
    public abstract T getBlock();

    /**
     * @return the BlockData of this TreeBlock
     */
    public abstract BlockData getBlockData();

    /**
     * @return the World of this TreeBlock
     */
    public abstract World getWorld();

    /**
     * @return the Location of this TreeBlock
     */
    public abstract Location getLocation();

    @Override
    public int hashCode() {
        return Objects.hash(this.getBlock(), this.treeBlockType);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ITreeBlock)) return false;
        if (o == this) return true;
        ITreeBlock<?> other = (ITreeBlock<?>) o;
        return other.getBlock().equals(this.getBlock()) && other.getTreeBlockType().equals(this.getTreeBlockType());
    }

}
