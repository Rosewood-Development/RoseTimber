package dev.rosewood.rosetimber.events;

import dev.rosewood.rosetimber.tree.DetectedTree;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

/**
 * Abstract tree event containing tree's blocks and broke block
 */
public abstract class TreeEvent extends PlayerEvent {

	protected final DetectedTree detectedTree;

    public TreeEvent(Player player, DetectedTree detectedTree) {
        super(player);
        this.detectedTree = detectedTree;
    }

    /**
     * @return the blocks that are part of the tree
     */
    public DetectedTree getDetectedTree() {
        return this.detectedTree;
    }
    
}
