package dev.rosewood.rosetimber.utils;

import dev.rosewood.rosetimber.tree.ITreeBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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
        if (treeBlock.getBlock() instanceof Block block) {
            if (block.getType().equals(Material.AIR))
                return drops;
            drops.add(new ItemStack(block.getType()));
        } else if (treeBlock.getBlock() instanceof FallingBlock fallingBlock) {
            drops.add(new ItemStack(fallingBlock.getBlockData().getMaterial()));
        }
        return drops;
    }

    public static void applyToolDurability(Player player, int damage) {
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool.getType().getMaxDurability() < 1 || (tool.getItemMeta() != null && tool.getItemMeta().isUnbreakable()))
            return;

        int unbreakingLevel = tool.getEnchantmentLevel(Enchantment.UNBREAKING);
        Damageable damageable = (Damageable) tool.getItemMeta();

        int actualDamage = 0;
        for (int i = 0; i < damage; i++)
            if (checkUnbreakingChance(unbreakingLevel))
                actualDamage++;

        // This could decrease the durability more than intended, we'll just have to live with that
        PlayerItemDamageEvent event = new PlayerItemDamageEvent(player, tool, actualDamage, damage);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;

        damageable.setDamage(damageable.getDamage() + event.getDamage());
        tool.setItemMeta((ItemMeta) damageable); // Older versions do not have Damageable as implementing ItemMeta, do not remove cast

        if (!hasEnoughDurability(tool, 1))
            player.getInventory().setItemInMainHand(null);
    }

    public static boolean hasEnoughDurability(ItemStack tool, int requiredAmount) {
        if (!tool.hasItemMeta() || !(tool.getItemMeta() instanceof Damageable damageable) || tool.getType().getMaxDurability() < 1)
            return true;

        int durabilityRemaining = tool.getType().getMaxDurability() - damageable.getDamage();
        return durabilityRemaining > requiredAmount;
    }

}
