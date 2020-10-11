package dev.rosewood.rosetimber.utils;

import dev.rosewood.rosetimber.tree.ITreeBlock;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class TimberUtils {

    private static final Random RANDOM = new Random();

    /**
     * Check if durbility should be applied based on the unbreaking enchantment
     *
     * @param level The level of the unbreaking enchantment
     * @return True if durability should be applied, otherwise false
     */
    public static boolean checkUnbreakingChance(int level) {
        return (1.0 / (level + 1)) > RANDOM.nextDouble();
    }

    /**
     * Checks if a chance between 0 and 100 passes
     *
     * @param chance The chance
     * @return true if the chance passed, otherwise false
     */
    public static boolean checkChance(double chance) {
        return RANDOM.nextDouble() <= chance;
    }

    /**
     * Gets a random value between the given range, inclusively
     *
     * @param min The minimum value
     * @param max The maximum value
     * @return A value between the min and max, inclusively
     */
    public static int randomInRange(int min, int max) {
        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }
        return RANDOM.nextInt(max - min + 1) + min;
    }

    public static Collection<ItemStack> getBlockDrops(ITreeBlock<?> treeBlock) {
        Set<ItemStack> drops = new HashSet<>();
        if (treeBlock.getBlock() instanceof Block) {
            Block block = (Block) treeBlock.getBlock();
            if (block.getType().equals(Material.AIR))
                return drops;
            drops.add(new ItemStack(block.getType()));
        } else if (treeBlock.getBlock() instanceof FallingBlock) {
            FallingBlock fallingBlock = (FallingBlock) treeBlock.getBlock();
            drops.add(new ItemStack(fallingBlock.getBlockData().getMaterial()));
        }
        return drops;
    }

    public static void applyToolDurability(Player player, int damage) {
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool.getType().getMaxDurability() < 1 || (tool.getItemMeta() != null && tool.getItemMeta().isUnbreakable()))
            return;

        int unbreakingLevel = tool.getEnchantmentLevel(Enchantment.DURABILITY);
        Damageable damageable = (Damageable) tool.getItemMeta();

        int actualDamage = 0;
        for (int i = 0; i < damage; i++)
            if (checkUnbreakingChance(unbreakingLevel))
                actualDamage++;

        damageable.setDamage(damageable.getDamage() + actualDamage);
        tool.setItemMeta((ItemMeta) damageable);

        if (!hasEnoughDurability(tool, 1))
            player.getInventory().setItemInMainHand(null);
    }

    public static boolean hasEnoughDurability(ItemStack tool, int requiredAmount) {
        if (!tool.hasItemMeta() || !(tool.getItemMeta() instanceof Damageable) || tool.getType().getMaxDurability() < 1)
            return true;

        Damageable damageable = (Damageable) tool.getItemMeta();
        int durabilityRemaining = tool.getType().getMaxDurability() - damageable.getDamage();
        return durabilityRemaining > requiredAmount;
    }

}
