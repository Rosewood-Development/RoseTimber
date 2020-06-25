package dev.rosewood.rosetimber.manager;

import dev.rosewood.rosetimber.RoseTimber;
import dev.rosewood.rosetimber.animation.TreeAnimationType;
import dev.rosewood.rosetimber.config.CommentedFileConfiguration;
import dev.rosewood.rosetimber.tree.OnlyToppleWhile;
import dev.rosewood.rosetimber.utils.TimberUtils;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurationManager extends Manager {

    private static final String[] HEADER = new String[] {
            "__________                  ___________ __       ___",
            "\\______   \\ ____  ______ ___\\__    ___/|__| _____\\_ |__   ___________",
            " |       _//  _ \\/  ___// __ \\|    |   |  |/     \\| __ \\_/ __ \\_  __ \\",
            " |    |   (  <_> )___ \\\\  ___/|    |   |  |  Y Y  \\ \\_\\ \\  ___/|  | \\/",
            " |____|_  /\\____/____  >\\___  >____|   |__|__|_|  /___  /\\___  >__|",
            "        \\/           \\/     \\/                  \\/    \\/     \\/"
    };

    private static final String[] FOOTER = new String[] {
            "That's everything! You reached the end of the configuration.",
            "Enjoy the plugin!"
    };

    public enum Setting {
        LOCALE("locale", "en_US", "The locale to use in the /locale folder"),
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

        /**
         * @return the setting as a boolean
         */
        public boolean getBoolean() {
            this.loadValue();
            return (boolean) this.value;
        }

        /**
         * @return the setting as an int
         */
        public int getInt() {
            this.loadValue();
            return (int) this.getNumber();
        }

        /**
         * @return the setting as a long
         */
        public long getLong() {
            this.loadValue();
            return (long) this.getNumber();
        }

        /**
         * @return the setting as a double
         */
        public double getDouble() {
            this.loadValue();
            return this.getNumber();
        }

        /**
         * @return the setting as a float
         */
        public float getFloat() {
            this.loadValue();
            return (float) this.getNumber();
        }

        /**
         * @return the setting as a String
         */
        public String getString() {
            this.loadValue();
            return (String) this.value;
        }

        private double getNumber() {
            if (this.value instanceof Integer) {
                return (int) this.value;
            } else if (this.value instanceof Short) {
                return (short) this.value;
            } else if (this.value instanceof Byte) {
                return (byte) this.value;
            } else if (this.value instanceof Float) {
                return (float) this.value;
            }

            return (double) this.value;
        }

        /**
         * @return the setting as a string list
         */
        @SuppressWarnings("unchecked")
        public List<String> getStringList() {
            this.loadValue();
            return (List<String>) this.value;
        }

        public boolean setIfNotExists(CommentedFileConfiguration fileConfiguration) {
            this.loadValue();

            if (fileConfiguration.get(this.key) == null) {
                List<String> comments = Stream.of(this.comments).collect(Collectors.toList());
                if (!(this.defaultValue instanceof List) && this.defaultValue != null) {
                    String defaultComment = "Default: ";
                    if (this.defaultValue instanceof String) {
                        if (TimberUtils.containsConfigSpecialCharacters((String) this.defaultValue)) {
                            defaultComment += "'" + this.defaultValue + "'";
                        } else {
                            defaultComment += this.defaultValue;
                        }
                    } else {
                        defaultComment += this.defaultValue;
                    }
                    comments.add(defaultComment);
                }

                if (this.defaultValue != null) {
                    fileConfiguration.set(this.key, this.defaultValue, comments.toArray(new String[0]));
                } else {
                    fileConfiguration.addComments(comments.toArray(new String[0]));
                }

                return true;
            }

            return false;
        }

        /**
         * Resets the cached value
         */
        public void reset() {
            this.value = null;
        }

        /**
         * @return true if this setting is only a section and doesn't contain an actual value
         */
        public boolean isSection() {
            return this.defaultValue == null;
        }

        /**
         * Loads the value from the config and caches it if it isn't set yet
         */
        private void loadValue() {
            if (this.value != null)
                return;

            this.value = RoseTimber.getInstance().getManager(ConfigurationManager.class).getConfig().get(this.key);
        }
    }

    private CommentedFileConfiguration configuration;

    public ConfigurationManager(RoseTimber roseTimber) {
        super(roseTimber);
    }

    @Override
    public void reload() {
        File configFile = new File(this.roseTimber.getDataFolder(), "config.yml");
        boolean setHeaderFooter = !configFile.exists();
        boolean changed = setHeaderFooter;

        this.configuration = CommentedFileConfiguration.loadConfiguration(this.roseTimber, configFile);

        if (setHeaderFooter)
            this.configuration.addComments(HEADER);

        for (Setting setting : Setting.values()) {
            setting.reset();
            changed |= setting.setIfNotExists(this.configuration);
        }

        if (setHeaderFooter)
            this.configuration.addComments(FOOTER);

        if (changed)
            this.configuration.save();
    }

    @Override
    public void disable() {
        for (Setting setting : Setting.values())
            setting.reset();
    }

    /**
     * @return the config.yml as a CommentedFileConfiguration
     */
    public CommentedFileConfiguration getConfig() {
        return this.configuration;
    }

}
