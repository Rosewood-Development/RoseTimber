package dev.rosewood.rosetimber.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.rosewood.rosetimber.RoseTimber;
import dev.rosewood.rosetimber.config.SettingKey;
import dev.rosewood.rosetimber.tree.ITreeBlock;
import dev.rosewood.rosetimber.tree.TreeBlockType;
import dev.rosewood.rosetimber.tree.TreeDefinition;
import dev.rosewood.rosetimber.tree.loot.TreeLoot;
import dev.rosewood.rosetimber.tree.loot.TreeLootEntry;
import dev.rosewood.rosetimber.tree.loot.TreeLootPool;
import dev.rosewood.rosetimber.tree.loot.TreeLootResult;
import dev.rosewood.rosetimber.utils.TimberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TreeDefinitionManager extends Manager {

    private static final String FILE_NAME = "tree-definitions.yml";

    private final List<TreeDefinition> treeDefinitions;
    private final List<Material> globalPlantableSoil;
    private TreeLoot globalLogLoot, globalLeafLoot, globalEntireTreeLoot;
    private final List<ItemStack> globalRequiredTools;

    public TreeDefinitionManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.treeDefinitions = new ArrayList<>();
        this.globalPlantableSoil = new ArrayList<>();
        this.globalRequiredTools = new ArrayList<>();
    }

    @Override
    public void reload() {
        File file = new File(this.rosePlugin.getDataFolder(), FILE_NAME);
        this.createFileIfNotExists(file);

        this.treeDefinitions.clear();
        this.globalPlantableSoil.clear();
        this.globalRequiredTools.clear();

        CommentedFileConfiguration config = CommentedFileConfiguration.loadConfiguration(file);

        // Load tree settings
        ConfigurationSection treeSection = config.getConfigurationSection("trees");
        if (treeSection == null) {
            this.rosePlugin.getLogger().severe("Failed to log tree-definitions.yml due to missing configuration section 'trees'");
            return;
        }
        
        for (String key : treeSection.getKeys(false)) {
            ConfigurationSection tree = treeSection.getConfigurationSection(key);

            List<Material> logBlockTypes = new ArrayList<>();
            List<Material> leafBlockTypes = new ArrayList<>();
            Material saplingBlockType;
            List<Material> plantableSoilBlockTypes = new ArrayList<>();
            double maxLogDistanceFromTrunk;
            int maxLeafDistanceFromLog;
            boolean detectLeavesDiagonally;
            boolean dropOriginalLog;
            boolean dropOriginalLeaf;
            boolean scatterTreeBlocksOnGround;
            boolean onlyDetectUpwards;
            TreeLoot logLoot, leafLoot, entireTreeLoot;
            List<ItemStack> requiredTools = new ArrayList<>();
            List<String> treeAnimationTypes;

            for (String blockDataString : tree.getStringList("logs"))
                logBlockTypes.add(Material.matchMaterial(blockDataString));

            for (String blockDataString : tree.getStringList("leaves"))
                leafBlockTypes.add(Material.matchMaterial(blockDataString));

            saplingBlockType = Material.matchMaterial(tree.getString("sapling"));

            for (String blockDataString : tree.getStringList("plantable-soil"))
                plantableSoilBlockTypes.add(Material.matchMaterial(blockDataString));

            maxLogDistanceFromTrunk = tree.getDouble("max-log-distance-from-trunk");
            maxLeafDistanceFromLog = tree.getInt("max-leaf-distance-from-log");
            detectLeavesDiagonally = tree.getBoolean("search-for-leaves-diagonally");
            dropOriginalLog = tree.getBoolean("drop-original-log");
            dropOriginalLeaf = tree.getBoolean("drop-original-leaf");
            scatterTreeBlocksOnGround = tree.getBoolean("scatter-tree-blocks-on-ground");
            onlyDetectUpwards = tree.getBoolean("only-detect-upwards");

            ConfigurationSection logLootSection = tree.getConfigurationSection("log-loot");
            if (logLootSection != null) {
                logLoot = this.getTreeLootPool(logLootSection);
            } else {
                logLoot = TreeLootPool.empty();
            }

            ConfigurationSection leafLootSection = tree.getConfigurationSection("leaf-loot");
            if (leafLootSection != null) {
                leafLoot = this.getTreeLootPool(leafLootSection);
            } else {
                leafLoot = TreeLootPool.empty();
            }

            ConfigurationSection entireTreeLootSection = tree.getConfigurationSection("entire-tree-loot");
            if (entireTreeLootSection != null) {
                entireTreeLoot = this.getTreeLootPool(entireTreeLootSection);
            } else {
                entireTreeLoot = TreeLootPool.empty();
            }

            for (String itemStackString : tree.getStringList("required-tools"))
                requiredTools.add(new ItemStack(Material.matchMaterial(itemStackString)));

            treeAnimationTypes = tree.getStringList("tree-animation-types");

            this.treeDefinitions.add(new TreeDefinition(key, logBlockTypes, leafBlockTypes, saplingBlockType, plantableSoilBlockTypes, maxLogDistanceFromTrunk,
                    maxLeafDistanceFromLog, detectLeavesDiagonally, dropOriginalLog, dropOriginalLeaf, scatterTreeBlocksOnGround, onlyDetectUpwards, logLoot, leafLoot,
                    entireTreeLoot, requiredTools, treeAnimationTypes));
        }

        // Load global plantable soil
        for (String blockDataString : config.getStringList("global-plantable-soil"))
            this.globalPlantableSoil.add(Material.matchMaterial(blockDataString));

        // Load global log drops
        ConfigurationSection logSection = config.getConfigurationSection("global-log-loot");
        if (logSection != null) {
            this.globalLogLoot = this.getTreeLootPool(logSection);
        } else {
            this.globalLogLoot = TreeLootPool.empty();
        }

        // Load global leaf drops
        ConfigurationSection leafSection = config.getConfigurationSection("global-leaf-loot");
        if (leafSection != null) {
            this.globalLeafLoot = this.getTreeLootPool(leafSection);
        } else {
            this.globalLeafLoot = TreeLootPool.empty();
        }

        // Load global entire tree drops
        ConfigurationSection entireTreeSection = config.getConfigurationSection("global-entire-tree-loot");
        if (entireTreeSection != null) {
            this.globalEntireTreeLoot = this.getTreeLootPool(entireTreeSection);
        } else {
            this.globalEntireTreeLoot = TreeLootPool.empty();
        }

        // Load global tools
        for (String itemStackString : config.getStringList("global-required-tools"))
            this.globalRequiredTools.add(new ItemStack(Material.matchMaterial(itemStackString)));
    }

    @Override
    public void disable() {
        this.treeDefinitions.clear();
    }

    /**
     * Creates the tree definitions file if it does not exist
     */
    private void createFileIfNotExists(File file) {
        if (file.exists())
            return;

        try {
            file.createNewFile();
        } catch (IOException ex) {
            this.rosePlugin.getLogger().severe("Failed to create 'tree-definitions' file: " + ex.getMessage());
            return;
        }
        
        CommentedFileConfiguration config = CommentedFileConfiguration.loadConfiguration(file);
        config.addComments(
                "Tree configuration",
                "Allows for extreme fine-tuning of tree detection and what are considered trees",
                "Multiple log and leaf types are allowed, only one sapling type is allowed",
                "You can add your own custom tree types here, just add a new section",
                "Tree animation types: " + String.join(", ", RoseTimber.getInstance().getManager(TreeAnimationManager.class).getRegisteredTreeAnimationNames())
        );

        CommentedConfigurationSection treeSection = config.createSection("trees");
        for (TreeDefinition treeDefinition : TreeDefinition.getDefaultTreeDefinitions()) {
            CommentedConfigurationSection definitionSection = treeSection.createSection(treeDefinition.getKey());
            definitionSection.set("logs", treeDefinition.getLogBlockTypes().stream().map(Enum::name).collect(Collectors.toList()));
            definitionSection.set("leaves", treeDefinition.getLeafBlockTypes().stream().map(Enum::name).collect(Collectors.toList()));
            definitionSection.set("sapling", treeDefinition.getSaplingBlockType().name());
            definitionSection.set("plantable-soil", treeDefinition.getPlantableSoilBlockTypes().stream().map(Enum::name).collect(Collectors.toList()));
            definitionSection.set("max-log-distance-from-trunk", treeDefinition.getMaxLogDistanceFromTrunk());
            definitionSection.set("max-leaf-distance-from-log", treeDefinition.getMaxLeafDistanceFromLog());
            definitionSection.set("search-for-leaves-diagonally", treeDefinition.shouldDetectLeavesDiagonally());
            definitionSection.set("drop-original-log", treeDefinition.shouldDropOriginalLog());
            definitionSection.set("drop-original-leaf", treeDefinition.shouldDropOriginalLeaf());
            definitionSection.set("scatter-tree-blocks-on-ground", treeDefinition.shouldScatterTreeBlocksOnGround());
            this.writeTreeLootToSection(definitionSection, "log-loot", treeDefinition.getLogLoot());
            this.writeTreeLootToSection(definitionSection, "leaf-loot", treeDefinition.getLeafLoot());
            this.writeTreeLootToSection(definitionSection, "entire-tree-loot", treeDefinition.getEntireTreeLoot());
            definitionSection.set("required-tools", treeDefinition.getRequiredTools().stream().map(ItemStack::getType).map(Enum::name).collect(Collectors.toList()));
            definitionSection.set("tree-animation-types", treeDefinition.getTreeAnimationTypes());
        }

        config.set(
                "global-plantable-soil",
                TreeDefinition.getDefaultGlobalPlantableSoils().stream().map(Enum::name).collect(Collectors.toList()),
                "All soil types that the tree type's saplings can be planted on"
        );

        config.addComments(
                "Custom loot that is available for all tree types",
                "The loot applies to each log broken in the tree",
                "To add more, increment the number by 1",
                "The chance is out of 100 and can contain decimals",
                "The default examples here are to show what you can do with custom loot",
                "Valid command placeholders: %player%, %type%, %xPos%, %yPos%, %zPos%, %world%"
        );
        this.writeTreeLootToSection(config, "global-log-loot", TreeDefinition.getDefaultGlobalLogLoot());

        config.addComments(
                "Custom loot that is available for all tree types",
                "The loot applies to each leaf broken in the tree",
                "To add more, increment the number by 1",
                "The chance is out of 100 and can contain decimals",
                "Valid command placeholders: %player%, %type%, %xPos%, %yPos%, %zPos%, %world%"
        );
        this.writeTreeLootToSection(config, "global-leaf-loot", TreeDefinition.getDefaultGlobalLeafLoot());

        config.addComments(
                "Custom entire tree loot that is available for all tree types",
                "The loot will be dropped only one time for the entire tree",
                "To add more, increment the number by 1",
                "The chance is out of 100 and can contain decimals",
                "Valid command placeholders: %player%, %type%, %xPos%, %yPos%, %zPos%, %world%"
        );
        this.writeTreeLootToSection(config, "global-entire-tree-loot", TreeDefinition.getDefaultGlobalEntireTreeLoot());

        config.set(
                "global-required-tools",
                TreeDefinition.getDefaultGlobalRequiredTools().stream().map(Enum::name).collect(Collectors.toList()),
                "Tools that must be used to topple over a tree",
                "Applies to all tree types"
        );

        config.save(file);
    }

    /**
     * Writes a list of TreeLoot to a config section
     *
     * @param section  The section to write to
     * @param name     The name of the value
     * @param treeLoot The tree loot to write
     */
    private void writeTreeLootToSection(CommentedConfigurationSection section, String name, TreeLoot treeLoot) {
        List<TreeLootEntry> entries = new ArrayList<>();
        if (treeLoot instanceof TreeLootPool) {
            TreeLootPool pool = (TreeLootPool) treeLoot;
            for (TreeLoot loot : pool.getLoot()) {
                TreeLootEntry entry = (TreeLootEntry) loot; // TODO: This may be unsafe in the future
                entries.add(entry);
            }
        } else if (treeLoot instanceof TreeLootEntry) {
            entries.add((TreeLootEntry) treeLoot);
        }

        if (entries.isEmpty()) {
            section.set(name, new ArrayList<String>());
            return;
        }

        CommentedConfigurationSection lootSection = section.createSection(name);
        for (int i = 0; i < entries.size(); i++) {
            CommentedConfigurationSection arraySection = lootSection.createSection(String.valueOf(i));
            TreeLootEntry entry = entries.get(i);

            arraySection.set("chance", entry.getChance());

            if (entry.hasItem())
                arraySection.set("material", entry.getItem().getType().name());

            if (entry.hasCommand())
                arraySection.set("command", entry.getCommand());

            if (entry.hasMinMax()) {
                arraySection.set("min", entry.getMin());
                arraySection.set("max", entry.getMax());
            }
        }
    }

    /**
     * Gets a List of possible TreeDefinitions that match the given Block
     *
     * @param block The Block to check
     * @return A List of TreeDefinitions for the given Block
     */
    public List<TreeDefinition> getTreeDefinitionsForLog(Block block) {
        return this.narrowTreeDefinition(this.treeDefinitions, block, TreeBlockType.LOG);
    }

    /**
     * Narrows a List of TreeDefinitions down to ones matching the given Block and TreeBlockType
     *
     * @param possibleTreeDefinitions The possible TreeDefinitions
     * @param block                   The Block to narrow to
     * @param treeBlockType           The TreeBlockType of the given Block
     * @return A Set of TreeDefinitions narrowed down
     */
    public List<TreeDefinition> narrowTreeDefinition(List<TreeDefinition> possibleTreeDefinitions, Block block, TreeBlockType treeBlockType) {
        List<TreeDefinition> matchingTreeDefinitions = new ArrayList<>();
        switch (treeBlockType) {
            case LOG:
                for (TreeDefinition treeDefinition : possibleTreeDefinitions) {
                    for (Material logBlockType : treeDefinition.getLogBlockTypes()) {
                        if (logBlockType == block.getType()) {
                            matchingTreeDefinitions.add(treeDefinition);
                            break;
                        }
                    }
                }
                break;
            case LEAF:
                for (TreeDefinition treeDefinition : possibleTreeDefinitions) {
                    for (Material leafBlockType : treeDefinition.getLeafBlockTypes()) {
                        if (leafBlockType == block.getType()) {
                            matchingTreeDefinitions.add(treeDefinition);
                            break;
                        }
                    }
                }
                break;
        }

        return matchingTreeDefinitions;
    }

    /**
     * Checks if a given tool is valid for any tree definitions, also takes into account global tools
     *
     * @param tool The tool to check
     * @return True if the tool is allowed for toppling any trees
     */
    public boolean isToolValidForAnyTreeDefinition(ItemStack tool) {
        if (SettingKey.IGNORE_REQUIRED_TOOLS.get())
            return true;
        for (TreeDefinition treeDefinition : this.treeDefinitions)
            for (ItemStack requiredTool : treeDefinition.getRequiredTools())
                if (requiredTool.getType().equals(tool.getType()))
                    return true;
        for (ItemStack requiredTool : this.globalRequiredTools)
            if (requiredTool.getType().equals(tool.getType()))
                return true;
        return false;
    }

    /**
     * Checks if a given tool is valid for a given tree definition, also takes into account global tools
     *
     * @param treeDefinition The TreeDefinition to use
     * @param tool           The tool to check
     * @return True if the tool is allowed for toppling the given TreeDefinition
     */
    public boolean isToolValidForTreeDefinition(TreeDefinition treeDefinition, ItemStack tool) {
        if (SettingKey.IGNORE_REQUIRED_TOOLS.get())
            return true;
        for (ItemStack requiredTool : treeDefinition.getRequiredTools())
            if (requiredTool.getType().equals(tool.getType()))
                return true;
        for (ItemStack requiredTool : this.globalRequiredTools)
            if (requiredTool.getType().equals(tool.getType()))
                return true;
        return false;
    }

    /**
     * Tries to spawn loot for a given TreeBlock with the given TreeDefinition for a given Player
     *
     * @param treeDefinition  The TreeDefinition to use
     * @param treeBlock       The TreeBlock to drop for
     * @param player          The Player to drop for
     * @param isForEntireTree If the loot is for the entire tree
     */
    public void dropTreeLoot(TreeDefinition treeDefinition, ITreeBlock<?> treeBlock, Player player, boolean hasSilkTouch, boolean isForEntireTree) {
        HookManager hookManager = this.rosePlugin.getManager(HookManager.class);

        boolean addToInventory = SettingKey.ADD_ITEMS_TO_INVENTORY.get();
        boolean hasBonusChance = player.hasPermission("rosetimber.bonusloot");
        double bonusMultiplier = hasBonusChance ? SettingKey.BONUS_LOOT_MULTIPLIER.get() : 1;
        List<ItemStack> lootedItems = new ArrayList<>();
        List<String> lootedCommands = new ArrayList<>();
        List<TreeLootResult> results = new ArrayList<>();

        // Get the loot that we should drop
        if (isForEntireTree) {
            results.add(treeDefinition.getEntireTreeLoot().roll(bonusMultiplier));
            results.add(this.globalEntireTreeLoot.roll(bonusMultiplier));
        } else {
            if (SettingKey.APPLY_SILK_TOUCH.get() && hasSilkTouch) {
                if (hookManager.shouldApplyDoubleDropsHooks(player))
                    lootedItems.addAll(TimberUtils.getBlockDrops(treeBlock));
                lootedItems.addAll(TimberUtils.getBlockDrops(treeBlock));
            } else {
                switch (treeBlock.getTreeBlockType()) {
                    case LOG -> {
                        results.add(treeDefinition.getLogLoot().roll(bonusMultiplier));
                        results.add(this.globalLogLoot.roll(bonusMultiplier));
                        if (treeDefinition.shouldDropOriginalLog()) {
                            if (hookManager.shouldApplyDoubleDropsHooks(player))
                                lootedItems.addAll(TimberUtils.getBlockDrops(treeBlock));
                            lootedItems.addAll(TimberUtils.getBlockDrops(treeBlock));
                        }
                    }
                    case LEAF -> {
                        results.add(treeDefinition.getLeafLoot().roll(bonusMultiplier));
                        results.add(this.globalLeafLoot.roll(bonusMultiplier));
                        if (treeDefinition.shouldDropOriginalLeaf()) {
                            if (hookManager.shouldApplyDoubleDropsHooks(player))
                                lootedItems.addAll(TimberUtils.getBlockDrops(treeBlock));
                            lootedItems.addAll(TimberUtils.getBlockDrops(treeBlock));
                        }
                    }
                }
            }
        }

        // Get loot and apply double drops hooks
        TreeLootResult randomDrops = new TreeLootResult(results);
        for (ItemStack item : randomDrops.getItems()) {
            lootedItems.add(item);
            if (hookManager.shouldApplyDoubleDropsHooks(player))
                lootedItems.add(item);
        }
        for (String command : randomDrops.getCommands()) {
            lootedCommands.add(command);
            if (hookManager.shouldApplyDoubleDropsHooks(player))
                lootedCommands.add(command);
        }

        // Add to inventory or drop on ground
        if (addToInventory && player.getWorld().equals(treeBlock.getLocation().getWorld())) {
            List<ItemStack> extraItems = new ArrayList<>();
            for (ItemStack lootedItem : lootedItems)
                extraItems.addAll(player.getInventory().addItem(lootedItem).values());
            Location location = player.getLocation().clone().subtract(0.5, 0, 0.5);
            for (ItemStack extraItem : extraItems)
                player.getWorld().dropItemNaturally(location, extraItem);
        } else {
            Location location = treeBlock.getLocation().clone().add(0.5, 0.5, 0.5);
            for (ItemStack lootedItem : lootedItems)
                player.getWorld().dropItemNaturally(location, lootedItem);
        }

        // Run looted commands
        StringPlaceholders placeholders = StringPlaceholders.builder("player", player.getName())
                .add("type", treeDefinition.getKey())
                .add("xPos", treeBlock.getLocation().getBlockX())
                .add("yPos", treeBlock.getLocation().getBlockY())
                .add("zPos", treeBlock.getLocation().getBlockZ())
                .add("world", treeBlock.getWorld().getName())
                .build();

        lootedCommands.stream()
                .map(placeholders::apply)
                .forEach(x -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), x));
    }

    /**
     * Gets all possible plantable soil blocks for the given tree definition
     *
     * @param treeDefinition The TreeDefinition
     * @return A List of Materials of plantable soil
     */
    public List<Material> getPlantableSoilBlockTypes(TreeDefinition treeDefinition) {
        List<Material> plantableSoilBlockTypes = new ArrayList<>();
        plantableSoilBlockTypes.addAll(treeDefinition.getPlantableSoilBlockTypes());
        plantableSoilBlockTypes.addAll(this.globalPlantableSoil);
        return plantableSoilBlockTypes;
    }

    /**
     * Gets a TreeLootPool from a ConfigurationSection
     *
     * @param configurationSection The ConfigurationSection
     * @return A TreeLoot entry from the section
     */
    private TreeLoot getTreeLootPool(ConfigurationSection configurationSection) {
        List<TreeLoot> loot = new ArrayList<>();
        for (String key : configurationSection.getKeys(false)) {
            ConfigurationSection entrySection = configurationSection.getConfigurationSection(key);
            String material = entrySection.getString("material");
            ItemStack item = material != null ? new ItemStack(Material.matchMaterial(material)) : null;
            String command = entrySection.getString("command");
            double chance = entrySection.getDouble("chance");
            int min = entrySection.getInt("min", 1);
            int max = entrySection.getInt("max", 1);
            loot.add(new TreeLootEntry(chance, item, command, min, max));
        }
        return new TreeLootPool(loot);
    }

}
