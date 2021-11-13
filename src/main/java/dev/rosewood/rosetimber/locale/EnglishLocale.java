package dev.rosewood.rosetimber.locale;

import dev.rosewood.rosegarden.locale.Locale;
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
            this.put("prefix", "&7[<g:#8A2387:#E94057:#F27121>RoseTimber&7] ");

            this.put("#1", "No Permission Message");
            this.put("no-permission", "&cYou don't have permission for that!");

            this.put("#2", "Base Command Message");
            this.put("base-command-color", "&e");
            this.put("base-command-help", "&eUse &b/rt help &efor command information.");

            this.put("#3", "Help Command");
            this.put("command-help-description", "&8 - &d/rt help &7- Displays the help menu... You have arrived");
            this.put("command-help-title", "&eAvailable Commands:");

            this.put("#4", "Reload Command Messages");
            this.put("command-reload-description", "&8 - &d/rt reload &7- Reloads the config.");
            this.put("command-reload-reloaded", "&eConfiguration and locale files were reloaded.");

            this.put("#5", "Toggle Command Messages");
            this.put("command-toggle-description", "&8 - &d/rt toggle &7- Toggles your chopping mode");
            this.put("command-toggle-enabled", "&eTree chopping is now &aenabled&e.");
            this.put("command-toggle-disabled", "&eTree chopping is now &cdisabled&e.");

            this.put("#6", "Cooldown Message");
            this.put("on-cooldown", "&eYou are on cooldown and cannot topple trees right now.");
        }};
    }
}
