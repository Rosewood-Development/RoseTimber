package dev.rosewood.rosetimber.animation;

/**
 * The types of tree animations that are available
 */
public enum TreeAnimationType {
    TOPPLE,
    DISINTEGRATE,
    CRUMBLE,
    NONE;

    /**
     * Gets a TreeAnimationType from a given string
     *
     * @param string The string
     * @return The TreeAnimationType, returns TOPPLE if the string is an invalid type
     */
    public static TreeAnimationType fromString(String string) {
        for (TreeAnimationType value : values())
            if (value.name().equalsIgnoreCase(string))
                return value;
        return TreeAnimationType.TOPPLE;
    }
}
