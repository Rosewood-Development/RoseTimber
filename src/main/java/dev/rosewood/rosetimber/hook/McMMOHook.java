package dev.rosewood.rosetimber.hook;

import com.gmail.nossr50.api.AbilityAPI;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
import dev.rosewood.rosetimber.tree.ITreeBlock;
import dev.rosewood.rosetimber.tree.TreeBlockSet;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class McMMOHook implements TimberHook {

    @Override
    public void applyExperience(Player player, TreeBlockSet<Block> treeBlocks, boolean singular) {
        if (player.getGameMode() == GameMode.CREATIVE)
            return;

        ArrayList<BlockState> blockStates = new ArrayList<>();
        treeBlocks.getLogBlocks().forEach(x -> blockStates.add(x.getBlock().getState()));
        treeBlocks.getAllTreeBlocks().stream()
                .map(ITreeBlock::getBlock)
                .map(Block::getState)
                .forEach(blockStates::add); // feel free to change it to the line above if you want.

        ExperienceAPI.addXpFromBlocksBySkill(blockStates, UserManager.getPlayer(player), PrimarySkillType.WOODCUTTING);
    }

    @Override
    public boolean shouldApplyDoubleDrops(Player player) {
        if (PrimarySkillType.WOODCUTTING.getDoubleDropsDisabled())
            return false;

        return Permissions.isSubSkillEnabled(player, SubSkillType.WOODCUTTING_HARVEST_LUMBER)
                && RankUtils.hasReachedRank(1, player, SubSkillType.WOODCUTTING_HARVEST_LUMBER)
                && RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.WOODCUTTING_HARVEST_LUMBER, player);
    }

    @Override
    public boolean isUsingAbility(Player player) {
        return AbilityAPI.treeFellerEnabled(player);
    }

}
