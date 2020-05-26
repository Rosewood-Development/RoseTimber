package dev.rosewood.rosetimber.locale;

import java.util.LinkedHashMap;
import java.util.Map;

public class EnglishLocale implements Locale {

    @Override
    public String getLocaleName() {
        return "en_US";
    }

    @Override
    public String getTranslatorName() {
        return "Esophose";
    }

    @Override
    public Map<String, Object> getDefaultLocaleValues() {
        return new LinkedHashMap<String, Object>() {{
            this.put("#0", "Plugin Message Prefix");
            this.put("prefix", "&7[&aRoseTimber&7] ");

            this.put("#1", "No Permission Message");
            this.put("no-permission", "&cYou don't have permission for that!");

            this.put("#2", "Reload Command Messages");
            this.put("reload-description", "&8 - &a/rt reload &7 - Reloads the config.");
            this.put("reload-reloaded", "&eConfiguration and locale files were reloaded.");

            this.put("#3", "Toggle Command Messages");
            this.put("toggle-description", "&8 - &a/rt toggle &7 - Toggles your chopping mode");
            this.put("toggle-enabled", "&eTree chopping is now &aenabled&e.");
            this.put("toggle-disabled", "&eTree chopping is now &cdisabled&e.");

            this.put("#4", "Cooldown Message");
            this.put("on-cooldown", "&eYou are on cooldown and cannot topple trees right now.");
        }};
    }
}
