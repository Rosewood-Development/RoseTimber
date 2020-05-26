package dev.rosewood.rosetimber.tree.loot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TreeLootPool implements TreeLoot {

    private final List<TreeLoot> loot;

    public TreeLootPool(List<TreeLoot> loot) {
        this.loot = loot;
    }

    public TreeLootPool(TreeLoot... loot) {
        this(Arrays.asList(loot));
    }

    @Override
    public TreeLootResult roll(double bonusMultiplier) {
        List<TreeLootResult> results = new ArrayList<>();

        for (TreeLoot loot : this.loot)
            results.add(loot.roll(bonusMultiplier));

        return new TreeLootResult(results);
    }

    /**
     * @return the loot that belongs to this pool
     */
    public List<TreeLoot> getLoot() {
        return Collections.unmodifiableList(this.loot);
    }

    /**
     * @return an empty TreeLootPool
     */
    public static TreeLootPool empty() {
        return new TreeLootPool();
    }

}
