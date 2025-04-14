//package dev.rosewood.rosetimber.hook;
//
//import com.willfp.ecoskills.api.EcoSkillsAPI;
//import com.willfp.ecoskills.skills.Skill;
//import com.willfp.ecoskills.skills.Skills;
//import dev.rosewood.rosetimber.tree.ITreeBlock;
//import dev.rosewood.rosetimber.tree.TreeBlockSet;
//import org.bukkit.GameMode;
//import org.bukkit.block.Block;
//import org.bukkit.entity.Player;
//import org.bukkit.event.block.BlockBreakEvent;
//
//public class EcoSkillsHook implements TimberHook {
//
//    @Override
//    public void applyExperience(Player player, TreeBlockSet<Block> treeBlocks, boolean singular) {
//        if (player.getGameMode() == GameMode.CREATIVE)
//            return;
//
//        Skill skill = Skills.INSTANCE.get("woodcutting");
//        skill.
//        SkillWoodcutting skill = (SkillWoodcutting) Skills.WOODCUTTING;
//        treeBlocks.getAllTreeBlocks().stream()
//                .map(ITreeBlock::getBlock)
//                .forEach(x -> skill.handleLevelling(new BlockBreakEvent(x, player)));
//    }
//
//    @Override
//    public boolean shouldApplyDoubleDrops(Player player) {
//        return false;
//    }
//
//    @Override
//    public boolean isUsingAbility(Player player) {
//        return false;
//    }
//
//}
