package dev.rosewood.rosetimber.utils;

import org.bukkit.Bukkit;

public class NMSUtil {

    private static String cachedVersion = null;
    private static int cachedVersionNumber = -1;

    /**
     * @return true if the server is a valid version, otherwise false
     */
    public static boolean isValidVersion() {
        return getVersionNumber() >= 13;
    }

    /**
     * @return The server version
     */
    public static String getVersion() {
        if (cachedVersion == null) {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            cachedVersion = name.substring(name.lastIndexOf('.') + 1);
        }
        return cachedVersion;
    }

    /**
     * @return the server version major release number
     */
    public static int getVersionNumber() {
        if (cachedVersionNumber == -1) {
            String name = getVersion().substring(3);
            cachedVersionNumber = Integer.parseInt(name.substring(0, name.length() - 3));
        }
        return cachedVersionNumber;
    }

}
