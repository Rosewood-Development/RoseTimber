package dev.rosewood.rosetimber.hook;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.BlockActionInfo;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.JobsPlayer;
import dev.rosewood.rosetimber.tree.ITreeBlock;
import dev.rosewood.rosetimber.tree.TreeBlockSet;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class JobsHook implements TimberHook {

    @Override
    public void applyExperience(Player player, TreeBlockSet<Block> treeBlocks, boolean singular) {
        if (player.getGameMode() == GameMode.CREATIVE)
            return;

        JobsPlayer jplayer = Jobs.getPlayerManager().getJobsPlayer(player);
        if (jplayer == null)
            return;

        for (ITreeBlock<Block> treeBlock : treeBlocks.getLogBlocks()) {
            Block block = treeBlock.getBlock();
            BlockActionInfo info = new BlockActionInfo(block, ActionType.BREAK);
            Jobs.action(jplayer, info, block);

            if (singular)
                break;
        }
    }

    @Override
    public boolean shouldApplyDoubleDrops(Player player) {
        return false;
    }

    @Override
    public boolean isUsingAbility(Player player) {
        return true;
    }

}
