package dev.rosewood.rosetimber.tree;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

public interface ITreeBlock<BlockType> {

    /**
     * @return the Block of this TreeBlock
     */
    BlockType getBlock();

    /**
     * @return the BlockData of this TreeBlock
     */
    BlockData getBlockData();

    /**
     * @return the World of this TreeBlock
     */
    World getWorld();

    /**
     * @return the Location of this TreeBlock
     */
    Location getLocation();

    /**
     * @return The TreeBlockType of this TreeBlockType
     */
    TreeBlockType getTreeBlockType();

}
