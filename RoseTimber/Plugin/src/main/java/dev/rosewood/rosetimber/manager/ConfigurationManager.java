package dev.rosewood.rosetimber.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;
import dev.rosewood.rosetimber.RoseTimber;
import dev.rosewood.rosetimber.animation.TreeAnimationType;
import dev.rosewood.rosetimber.tree.OnlyToppleWhile;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurationManager extends AbstractConfigurationManager {

    public enum Setting implements RoseSetting {
        DISABLED_WORLDS("disabled-worlds", Collections.singletonList("disabled_world_name"), "A list of worlds that the plugin is disabled in"),

        MAX_LOGS_PER_CHOP("max-logs-per-chop", 150, "The max number of logs that can be broken at one time"),
        LEAVES_REQUIRED_FOR_TREE("leaves-required-for-tree", 5, "The minimum number of leaves required for something to be considered a tree"),
        DESTROY_LEAVES("destroy-leaves", true, "If leaves should be destroyed"),
        REALISTIC_TOOL_DAMAGE("realistic-tool-damage", true, "Apply realistic damage to the tools based on the number of logs chopped", "If false, only one durability will be removed from the tool"),
        PROTECT_TOOL("protect-tool", false, "Protect the tool used to chop down the tree from breaking", "Prevents the tree from being toppled if the tool would break"),
        APPLY_SILK_TOUCH("apply-silk-touch", true, "Use the silk touch enchantment if the tool has it", "Logs and leaves will drop their original block 100% of the time"),
        APPLY_SILK_TOUCH_TOOL_DAMAGE("apply-silk-touch-tool-damage", true, "Damage the tool extra for each leaf block broken, this is vanilla behavior but can be disabled here", "Does nothing if realistic-tool-damage is false"),
        BREAK_ENTIRE_TREE_BASE("break-entire-tree-base", false, "Require the entire base of the tree to be broken before it topples"),
        DESTROY_INITIATED_BLOCK("destroy-initiated-block", false, "Don't drop a block for the block that initiates the tree fall"),
        ONLY_DETECT_LOGS_UPWARDS("only-detect-logs-upwards", true, "Only detect logs above the initiated block"),
        ONLY_TOPPLE_WHILE("only-topple-while", "ALWAYS", "Only topple trees while the player is doing something", "Valid values: " + Stream.of(OnlyToppleWhile.values()).map(Enum::name).collect(Collectors.joining(", "))),
        ALLOW_CREATIVE_MODE("allow-creative-mode", true, "Allow toppling trees in creative mode"),
        REQUIRE_CHOP_PERMISSION("require-chop-permission", false, "Require the player to have the permission 'rosetimber.chop' to topple trees"),
        PLAYER_TREE_TOPPLE_COOLDOWN("player-tree-topple-cooldown", false, "If a player should only be allowed to chop one tree per cooldown length"),
        PLAYER_TREE_TOPPLE_COOLDOWN_LENGTH("player-tree-topple-cooldown-length", 5, "The amount of seconds a player has to wait before they can chop a tree again", "Does nothing if player-tree-topple-cooldown is false", "The time is in seconds and must be a postive whole number"),
        IGNORE_REQUIRED_TOOLS("ignore-required-tools", false, "Allow players to topple trees regardless of what they are holding in their hand"),
        REPLANT_SAPLINGS("replant-saplings", true, "Automatically replant saplings when a tree is toppled"),
        ALWAYS_REPLANT_SAPLING("always-replant-sapling", false, "Always replant saplings for base tree blocks, regardless of player permissions"),
        REPLANT_SAPLINGS_COOLDOWN("replant-saplings-cooldown", 3, "How many seconds to prevent players from breaking replanted saplings", "Set to 0 to disable", "Does nothing if replant-saplings is false", "The time is in seconds and must be a postive whole number"),
        FALLING_BLOCKS_REPLANT_SAPLINGS("falling-blocks-replant-saplings", true, "Give fallen leaf blocks a chance to replant saplings when they hit the ground"),
        FALLING_BLOCKS_REPLANT_SAPLINGS_CHANCE("falling-blocks-replant-saplings-chance", 1, "The percent chance that fallen leaves have of planting a sapling", "Does nothing if falling-blocks-replant-saplings is false", "The chance is out of 100 and may contain decimals"),
        FALLING_BLOCKS_DEAL_DAMAGE("falling-blocks-deal-damage", true, "Make falling tree blocks deal damage to players if they get hit"),
        FALLING_BLOCK_DAMAGE("falling-block-damage", 1, "The amount of damage that falling tree blocks do", "This does nothing if falling-blocks-deal-damage is false"),
        ADD_ITEMS_TO_INVENTORY("add-items-to-inventory", false, "Automatically add tree blocks to the player's inventory instead of dropping them"),
        USE_CUSTOM_SOUNDS("use-custom-sounds", true, "Use custom sounds when toppling trees"),
        USE_CUSTOM_PARTICLES("use-custom-particles", true, "Use custom particles when toppling trees"),
        BONUS_LOOT_MULTIPLIER("bonus-loot-multiplier", 2.0, "The bonus loot multiplier when a player has the permission rosetimber.bonusloot", "Multiplies the chance of tree drops by this value"),
        IGNORE_PLACED_BLOCKS("ignore-placed-blocks", true, "If placed blocks should be ignored for toppling trees", "Note: This only keeps track of blocks placed during the current server load", "      If your server restarts, the placed tree blocks could be toppled again"),
        IGNORE_PLACED_BLOCKS_MEMORY_SIZE("ignore-placed-blocks-memory-size", 5000, "The maximum number of blocks to keep track of in memory at once", "Use a lower number if this starts to take up too much memory or trees start taking too long to detect"),
        HOOKS_APPLY_EXPERIENCE("hooks-apply-experience", true, "Applies experience when using Jobs/mcMMO", "Only does something if Jobs or mcMMO is installed"),
        HOOKS_APPLY_EXTRA_DROPS("hooks-apply-extra-drops", true, "Applies extra drops passive ability when using mcMMO", "Only does something if mcMMO is installed"),
        HOOKS_REQUIRE_ABILITY_ACTIVE("hooks-require-ability-active", false, "Requires the tree feller ability in mcMMO to be active to use timber", "Only does something if mcMMO is installed"),

        // TODO: Move these two settings to the tree definitions
        TREE_ANIMATION_TYPE("tree-animation-type", "TOPPLE", "The type of animation to use for tree toppling", "Types: " + Stream.of(TreeAnimationType.values()).map(Enum::name).collect(Collectors.joining(", "))),
        SCATTER_TREE_BLOCKS_ON_GROUND("scatter-tree-blocks-on-ground", false, "If the tree-animation-type is TOPPLE or CRUMBLE, make the blocks stick to the ground", "Does nothing if tree-animation-type is not TOPPLE or CRUMBLE");

        private final String key;
        private final Object defaultValue;
        private final String[] comments;
        private Object value = null;

        Setting(String key, Object defaultValue, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String[] getComments() {
            return this.comments;
        }

        @Override
        public Object getCachedValue() {
            return this.value;
        }

        @Override
        public void setCachedValue(Object value) {
            this.value = value;
        }

        @Override
        public CommentedFileConfiguration getBaseConfig() {
            return RoseTimber.getInstance().getManager(ConfigurationManager.class).getConfig();
        }
    }

    public ConfigurationManager(RosePlugin rosePlugin) {
        super(rosePlugin, Setting.class);
    }

    @Override
    protected String[] getHeader() {
        return new String[] {
                "__________                  ___________ __       ___",
                "\\______   \\ ____  ______ ___\\__    ___/|__| _____\\_ |__   ___________",
                " |       _//  _ \\/  ___// __ \\|    |   |  |/     \\| __ \\_/ __ \\_  __ \\",
                " |    |   (  <_> )___ \\\\  ___/|    |   |  |  Y Y  \\ \\_\\ \\  ___/|  | \\/",
                " |____|_  /\\____/____  >\\___  >____|   |__|__|_|  /___  /\\___  >__|",
                "        \\/           \\/     \\/                  \\/    \\/     \\/"
        };
    }

}
