package dev.rosewood.rosetimber.tree.loot;

import dev.rosewood.rosetimber.utils.TimberUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class TreeLootEntry implements TreeLoot {

    private final double chance;
    private final ItemStack item;
    private final String command;
    private final int min, max;

    public TreeLootEntry(double chance, ItemStack item, String command, int min, int max) {
        this.chance = chance;
        this.item = item;
        this.command = command;
        this.min = min;
        this.max = max;
    }

    public TreeLootEntry(double chance, ItemStack item, String command) {
        this(chance, item, command, 1, 1);
    }

    @Override
    public TreeLootResult roll(double bonusMultiplier) {
        if (TimberUtils.checkChance(this.chance / 100 * bonusMultiplier)) {
            List<ItemStack> items = new ArrayList<>();
            List<String> commands = new ArrayList<>();

            int amount = TimberUtils.randomInRange(this.min, this.max);
            for (int i = 0; i < amount; i++) {
                if (this.item != null)
                    items.add(this.item);
                if (this.command != null)
                    commands.add(this.command);
            }

            return new TreeLootResult(items, commands);
        } else {
            return TreeLootResult.empty();
        }
    }

    /**
     * @return true if an item exists, otherwise false
     */
    public boolean hasItem() {
        return this.item != null;
    }

    /**
     * @return an ItemStack this tree loot can drop
     */
    public ItemStack getItem() {
        return this.item;
    }

    /**
     * @return true if a command exists, otherwise false
     */
    public boolean hasCommand() {
        return this.command != null;
    }

    /**
     * @return the command that this tree loot can run
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * @return the percent chance this tree loot can drop
     */
    public double getChance() {
        return this.chance;
    }

    /**
     * @return true if a min and max value are set, otherwise false
     */
    public boolean hasMinMax() {
        return this.min != 1 || this.max != 1;
    }

    /**
     * @return the min number of loot to drop
     */
    public int getMin() {
        return this.min;
    }

    /**
     * @return the max number of loot to drop
     */
    public int getMax() {
        return this.max;
    }

}
