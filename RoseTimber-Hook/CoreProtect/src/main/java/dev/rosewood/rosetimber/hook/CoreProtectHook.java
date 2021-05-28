package dev.rosewood.rosetimber.hook;

import dev.rosewood.rosetimber.tree.ITreeBlock;
import dev.rosewood.rosetimber.tree.TreeBlockSet;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class CoreProtectHook implements TimberHook {

    private CoreProtectAPI api;

    public CoreProtectHook() {
        this.api = CoreProtect.getInstance().getAPI();
    }

    @Override
    public void applyExperience(Player player, TreeBlockSet<Block> treeBlocks, boolean singlular) {
        if (!this.api.isEnabled())
            return;

        for (ITreeBlock<Block> treeBlock : treeBlocks.getAllTreeBlocks())
            this.api.logRemoval(player.getName(), treeBlock.getLocation(), treeBlock.getBlock().getType(), treeBlock.getBlock().getBlockData());
    }

    @Override
    public boolean shouldApplyDoubleDrops(Player player) {
        return false;
    }

    @Override
    public boolean isUsingAbility(Player player) {
        return false;
    }

}
