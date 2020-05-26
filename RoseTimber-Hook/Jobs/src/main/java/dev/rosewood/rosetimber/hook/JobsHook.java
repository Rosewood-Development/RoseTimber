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
    public void applyExperience(Player player, TreeBlockSet<Block> treeBlocks) {
        if (player.getGameMode().equals(GameMode.CREATIVE)) 
            return;

        JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        if (jPlayer == null) 
            return;
        
        for (ITreeBlock<Block> treeBlock : treeBlocks.getLogBlocks()) {
            Block block = treeBlock.getBlock();
            BlockActionInfo bInfo = new BlockActionInfo(block, ActionType.BREAK);
            Jobs.action(jPlayer, bInfo, block);
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
