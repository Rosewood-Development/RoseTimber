package dev.rosewood.rosetimber.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosetimber.manager.ConfigurationManager.Setting;
import dev.rosewood.rosetimber.tree.ITreeBlock;
import dev.rosewood.rosetimber.tree.TreeBlockType;
import dev.rosewood.rosetimber.tree.TreeDefinition;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class SaplingManager extends Manager {

    private final Random random;
    private final Set<Location> protectedSaplings;

    public SaplingManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.random = new Random();
        this.protectedSaplings = new HashSet<>();
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {
        this.protectedSaplings.clear();
    }

    /**
     * Replants a sapling given a TreeDefinition and Location
     * Takes into account config settings
     *
     * @param treeDefinition The TreeDefinition of the sapling
     * @param treeBlock The ITreeBlock to replant for
     */
    public void replantSapling(TreeDefinition treeDefinition, ITreeBlock<?> treeBlock) {
        if (!Setting.REPLANT_SAPLINGS.getBoolean())
            return;

        Block block = treeBlock.getLocation().getBlock();
        if (!block.getType().equals(Material.AIR) || treeBlock.getTreeBlockType().equals(TreeBlockType.LEAF))
            return;

        Bukkit.getScheduler().runTask(this.rosePlugin, () -> this.internalReplant(treeDefinition, treeBlock));
    }

    /**
     * Randomly replants a sapling given a TreeDefinition and Location
     * Takes into account config settings
     *
     * @param treeDefinition The TreeDefinition of the sapling
     * @param treeBlock The ITreeBlock to replant for
     */
    public void replantSaplingWithChance(TreeDefinition treeDefinition, ITreeBlock<?> treeBlock) {
        if (!Setting.FALLING_BLOCKS_REPLANT_SAPLINGS.getBoolean() || !treeBlock.getLocation().getBlock().getType().equals(Material.AIR))
            return;

        double chance = Setting.FALLING_BLOCKS_REPLANT_SAPLINGS_CHANCE.getDouble();
        if (this.random.nextDouble() > chance / 100)
            return;

        Bukkit.getScheduler().runTask(this.rosePlugin, () -> this.internalReplant(treeDefinition, treeBlock));
    }

    /**
     * Replants a sapling given a TreeDefinition and Location
     *
     * @param treeDefinition The TreeDefinition of the sapling
     * @param treeBlock The ITreeBlock to replant for
     */
    private void internalReplant(TreeDefinition treeDefinition, ITreeBlock<?> treeBlock) {
        TreeDefinitionManager treeDefinitionManager = this.rosePlugin.getManager(TreeDefinitionManager.class);

        Block block = treeBlock.getLocation().getBlock();
        Block blockBelow = block.getRelative(BlockFace.DOWN);
        boolean isValidSoil = false;
        for (Material soilBlockType : treeDefinitionManager.getPlantableSoilBlockTypes(treeDefinition)) {
            if (soilBlockType == blockBelow.getType()) {
                isValidSoil = true;
                break;
            }
        }

        if (!isValidSoil)
            return;

        block.setType(treeDefinition.getSaplingBlockType());

        int cooldown = Setting.REPLANT_SAPLINGS_COOLDOWN.getInt();
        if (cooldown != 0) {
            this.protectedSaplings.add(block.getLocation());
            Bukkit.getScheduler().runTaskLater(this.rosePlugin, () -> this.protectedSaplings.remove(block.getLocation()), cooldown * 20L);
        }
    }

    /**
     * Gets if a sapling is protected
     *
     * @param block The Block to check
     * @return True if the sapling is protected, otherwise false
     */
    public boolean isSaplingProtected(Block block) {
        return this.protectedSaplings.contains(block.getLocation());
    }

}
