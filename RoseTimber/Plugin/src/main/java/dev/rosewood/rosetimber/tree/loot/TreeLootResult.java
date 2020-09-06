package dev.rosewood.rosetimber.tree.loot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class TreeLootResult {

    private final List<ItemStack> items;
    private final List<String> commands;

    public TreeLootResult(List<ItemStack> items, List<String> commands) {
        this.items = items == null ? Collections.emptyList() : items;
        this.commands = commands == null ? Collections.emptyList() : commands;
    }

    public TreeLootResult(List<TreeLootResult> results) {
        this.items = new ArrayList<>();
        this.commands = new ArrayList<>();

        for (TreeLootResult result : results) {
            this.items.addAll(result.getItems());
            this.commands.addAll(result.getCommands());
        }
    }

    /**
     * @return the items of this loot result
     */
    public List<ItemStack> getItems() {
        return Collections.unmodifiableList(this.items);
    }

    /**
     * @return the commands of this loot result
     */
    public List<String> getCommands() {
        return Collections.unmodifiableList(this.commands);
    }

    /**
     * @return an empty TreeLootResult
     */
    public static TreeLootResult empty() {
        return new TreeLootResult(Collections.emptyList(), Collections.emptyList());
    }

}
