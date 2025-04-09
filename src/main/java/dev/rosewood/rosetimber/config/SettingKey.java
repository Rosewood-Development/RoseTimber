package dev.rosewood.rosetimber.config;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.config.RoseSettingSerializer;
import dev.rosewood.rosetimber.RoseTimber;
import dev.rosewood.rosetimber.tree.OnlyToppleWhile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.rosewood.rosegarden.config.RoseSettingSerializers.BOOLEAN;
import static dev.rosewood.rosegarden.config.RoseSettingSerializers.DOUBLE;
import static dev.rosewood.rosegarden.config.RoseSettingSerializers.INTEGER;
import static dev.rosewood.rosegarden.config.RoseSettingSerializers.STRING;
import static dev.rosewood.rosegarden.config.RoseSettingSerializers.STRING_LIST;

public class SettingKey {

    private static final List<RoseSetting<?>> KEYS = new ArrayList<>();

    public static final RoseSetting<List<String>> DISABLED_WORLDS = create("disabled-worlds", STRING_LIST, Collections.singletonList("disabled_world_name"), "A list of worlds that the plugin is disabled in");

    public static final RoseSetting<Integer> MAX_LOGS_PER_CHOP = create("max-logs-per-chop", INTEGER, 150, "The max number of logs that can be broken at one time");
    public static final RoseSetting<Integer> LEAVES_REQUIRED_FOR_TREE = create("leaves-required-for-tree", INTEGER, 5, "The minimum number of leaves required for something to be considered a tree");
    public static final RoseSetting<Boolean> DESTROY_LEAVES = create("destroy-leaves", BOOLEAN, true, "I STRING,f leaves should be destroyed");
    public static final RoseSetting<Boolean> REALISTIC_TOOL_DAMAGE = create("realistic-tool-damage", BOOLEAN, true, "Apply realistic damage to the tools based on the number of logs chopped", "If false, only one durability will be removed from the tool");
    public static final RoseSetting<Boolean> PROTECT_TOOL = create("protect-tool", BOOLEAN, false, "Protect the tool used to chop down the tree from breaking", "Prevents the tree from being toppled if the tool would break");
    public static final RoseSetting<Boolean> APPLY_SILK_TOUCH = create("apply-silk-touch", BOOLEAN, true, "Use the silk touch enchantment if the tool has it", "Logs and leaves will drop their original block 100% of the time");
    public static final RoseSetting<Boolean> APPLY_SILK_TOUCH_TOOL_DAMAGE = create("apply-silk-touch-tool-damage", BOOLEAN, true, "Damage the tool extra for each leaf block broken, this is vanilla behavior but can be disabled here", "Does nothing if realistic-tool-damage is false");
    public static final RoseSetting<Boolean> BREAK_ENTIRE_TREE_BASE = create("break-entire-tree-base", BOOLEAN, false, "Require the entire base of the tree to be broken before it topples");
    public static final RoseSetting<Boolean> DESTROY_INITIATED_BLOCK = create("destroy-initiated-block", BOOLEAN, false, "Don't drop a block for the block that initiates the tree fall");
    public static final RoseSetting<Boolean> ONLY_DETECT_LOGS_UPWARDS = create("only-detect-logs-upwards", BOOLEAN, true, "Only detect logs above the initiated block");
    public static final RoseSetting<String> ONLY_TOPPLE_WHILE = create("only-topple-while", STRING, "ALWAYS", "Only topple trees while the player is doing something", "Valid values: " + Stream.of(OnlyToppleWhile.values()).map(Enum::name).collect(Collectors.joining(", ")));
    public static final RoseSetting<Boolean> ALLOW_CREATIVE_MODE = create("allow-creative-mode", BOOLEAN, true, "Allow toppling trees in creative mode");
    public static final RoseSetting<Boolean> REQUIRE_CHOP_PERMISSION = create("require-chop-permission", BOOLEAN, false, "Require the player to have the permission 'rosetimber.chop' to topple trees");
    public static final RoseSetting<Boolean> PLAYER_TREE_TOPPLE_COOLDOWN = create("player-tree-topple-cooldown", BOOLEAN, false, "If a player should only be allowed to chop one tree per cooldown length");
    public static final RoseSetting<Integer> PLAYER_TREE_TOPPLE_COOLDOWN_LENGTH = create("player-tree-topple-cooldown-length", INTEGER, 5, "The amount of seconds a player has to wait before they can chop a tree again", "Does nothing if player-tree-topple-cooldown is false", "The time is in seconds and must be a postive whole number");
    public static final RoseSetting<Boolean> IGNORE_REQUIRED_TOOLS = create("ignore-required-tools", BOOLEAN, false, "Allow players to topple trees regardless of what they are holding in their hand");
    public static final RoseSetting<Boolean> REPLANT_SAPLINGS = create("replant-saplings", BOOLEAN, true, "Automatically replant saplings when a tree is toppled");
    public static final RoseSetting<Boolean> ALWAYS_REPLANT_SAPLING = create("always-replant-sapling", BOOLEAN, false, "Always replant saplings for base tree blocks, regardless of player permissions");
    public static final RoseSetting<Integer> REPLANT_SAPLINGS_COOLDOWN = create("replant-saplings-cooldown", INTEGER, 3, "How many seconds to prevent players from breaking replanted saplings", "Set to 0 to disable", "Does nothing if replant-saplings is false", "The time is in seconds and must be a postive whole number");
    public static final RoseSetting<Boolean> FALLING_BLOCKS_REPLANT_SAPLINGS = create("falling-blocks-replant-saplings", BOOLEAN, true, "Give fallen leaf blocks a chance to replant saplings when they hit the ground");
    public static final RoseSetting<Integer> FALLING_BLOCKS_REPLANT_SAPLINGS_CHANCE = create("falling-blocks-replant-saplings-chance", INTEGER, 1, "The percent chance that fallen leaves have of planting a sapling", "Does nothing if falling-blocks-replant-saplings is false", "The chance is out of 100 and may contain decimals");
    public static final RoseSetting<Boolean> FALLING_BLOCKS_DEAL_DAMAGE = create("falling-blocks-deal-damage", BOOLEAN, true, "Make falling tree blocks deal damage to players if they get hit");
    public static final RoseSetting<Integer> FALLING_BLOCK_DAMAGE = create("falling-block-damage", INTEGER, 1, "The amount of damage that falling tree blocks do", "This does nothing if falling-blocks-deal-damage is false");
    public static final RoseSetting<Boolean> TRIGGER_BLOCK_BREAK_EVENTS = create("trigger-block-break-events", BOOLEAN, false, "Should a BlockBreakEvent be triggered for every broken tree block?", "You may need to disable some of the mcMMO or Jobs hooks if you enable this", "May cause a decrease in performance depending on what other plugins you have installed");
    public static final RoseSetting<Boolean> ADD_ITEMS_TO_INVENTORY = create("add-items-to-inventory", BOOLEAN, false, "Automatically add tree blocks to the player's inventory instead of dropping them");
    public static final RoseSetting<Boolean> USE_CUSTOM_SOUNDS = create("use-custom-sounds", BOOLEAN, true, "Use custom sounds when toppling trees");
    public static final RoseSetting<Boolean> USE_CUSTOM_PARTICLES = create("use-custom-particles", BOOLEAN, true, "Use custom particles when toppling trees");
    public static final RoseSetting<Double> BONUS_LOOT_MULTIPLIER = create("bonus-loot-multiplier", DOUBLE, 2.0, "The bonus loot multiplier when a player has the permission rosetimber.bonusloot", "Multiplies the chance of tree drops by this value");
    public static final RoseSetting<Boolean> IGNORE_PLACED_BLOCKS = create("ignore-placed-blocks", BOOLEAN, true, "If placed blocks should be ignored for toppling trees", "Note: This only keeps track of blocks placed during the current server load", "      If your server restarts, the placed tree blocks could be toppled again");
    public static final RoseSetting<Integer> IGNORE_PLACED_BLOCKS_MEMORY_SIZE = create("ignore-placed-blocks-memory-s STRING,ize", INTEGER, 5000, "The maximum number of blocks to keep track of in memory at once", "Use a lower number if this starts to take up too much memory or trees start taking too long to detect");

    /**
     * Plugin Hook Settings
     */
    public static final RoseSetting<CommentedConfigurationSection> HOOKS = create("hooks", "Settings for  STRING,hooks into other plugins");
    public static final RoseSetting<Boolean> HOOKS_MCMMO_APPLY_EXPERIENCE = create("hooks.mcmmo-apply-experience", BOOLEAN, true, "Applies experience when using mcMMO for each log in the tree", "Only does something if mcMMO is installed");
    public static final RoseSetting<Boolean> HOOKS_JOBS_APPLY_EXPERIENCE = create("hooks.jobs-apply-experience", BOOLEAN, true, "Grants money when using Jobs for each log in the tree", "Only does something if Jobs is installed");
    public static final RoseSetting<Boolean> HOOKS_APPLY_EXTRA_DROPS = create("hooks.apply-extra-drops", BOOLEAN, true, "Applies extra drops passive ability when using mcMMO", "Only does something if mcMMO is installed");
    public static final RoseSetting<Boolean> HOOKS_REQUIRE_ABILITY_ACTIVE = create("hooks.require-ability-active", BOOLEAN, false, "Requires the tree feller ability in mcMMO to be active to use timber", "Only does something if mcMMO is installed");


    private static <T> RoseSetting<T> create(String key, RoseSettingSerializer<T> serializer, T defaultValue, String... comments) {
        RoseSetting<T> setting = RoseSetting.backed(RoseTimber.getInstance(), key, serializer, defaultValue, comments);
        KEYS.add(setting);
        return setting;
    }

    private static RoseSetting<CommentedConfigurationSection> create(String key, String... comments) {
        RoseSetting<CommentedConfigurationSection> setting = RoseSetting.backedSection(RoseTimber.getInstance(), key, comments);
        KEYS.add(setting);
        return setting;
    }

    public static List<RoseSetting<?>> getKeys() {
        return KEYS;
    }

}
