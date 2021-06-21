package dev.rosewood.rosetimber.events;

import dev.rosewood.rosetimber.tree.DetectedTree;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Called when a block from a detected tree is going to be broken
 */
public class TreeBlockBreakEvent extends BlockBreakEvent {

    private final DetectedTree detectedTree;

    public TreeBlockBreakEvent(DetectedTree detectedTree, Block theBlock, Player player) {
        super(theBlock, player);
        this.detectedTree = detectedTree;
    }

    /**
     * @return the detected tree associated with the block in this event
     */
    public DetectedTree getDetectedTree() {
        return this.detectedTree;
    }

}
