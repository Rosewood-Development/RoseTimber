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
        return new LinkedHashMap<>() {{
            this.put("#0", "Plugin Message Prefix");
            this.put("prefix", "&7[<g:#8A2387:#E94057:#F27121>RoseTimber&7] ");

            this.put("#1", "Base Command Message");
            this.put("base-command-color", "&e");
            this.put("base-command-help", "&eUse &b/%cmd% help &efor command information.");

            this.put("#2", "Help Command");
            this.put("command-help-description", "Displays the help menu... You have arrived");
            this.put("command-help-title", "&eAvailable Commands:");
            this.put("command-help-list-description", "&8 - &d/%cmd% %subcmd% %args% &7- %desc%");
            this.put("command-help-list-description-no-args", "&8 - &d/%cmd% %subcmd% &7- %desc%");

            this.put("#3", "Reload Command");
            this.put("command-reload-description", "Reloads the plugin");
            this.put("command-reload-reloaded", "&ePlugin data, configuration, and locale files were reloaded.");

            this.put("#4", "Toggle Command");
            this.put("command-toggle-description", "Toggles your chopping mode");
            this.put("command-toggle-enabled", "&eTree chopping is now &aenabled&e.");
            this.put("command-toggle-disabled", "&eTree chopping is now &cdisabled&e.");

            this.put("#5", "Cooldown Message");
            this.put("on-cooldown", "&eYou are on cooldown and cannot topple trees right now.");

            this.put("#6", "Generic Command Messages");
            this.put("no-permission", "&cYou don't have permission for that!");
            this.put("only-player", "&cThis command can only be executed by a player.");
            this.put("unknown-command", "&cUnknown command, use &b/%cmd% help &cfor more info.");
            this.put("unknown-command-error", "&cAn unknown error occurred; details have been printed to console. Please contact a server administrator.");
            this.put("invalid-subcommand", "&cInvalid subcommand.");
            this.put("invalid-argument", "&cInvalid argument: %message%.");
            this.put("invalid-argument-null", "&cInvalid argument: %name% was null.");
            this.put("missing-arguments", "&cMissing arguments, &b%amount% &crequired.");
            this.put("missing-arguments-extra", "&cMissing arguments, &b%amount%+ &crequired.");

            this.put("7", "Argument Handler Error Messages");
            this.put("argument-handler-enum", "%enum% type [%input%] does not exist");
            this.put("argument-handler-enum-list", "%enum% type [%input%] does not exist. Valid types: %types%");
            this.put("argument-handler-string", "String cannot be empty");
            this.put("argument-handler-integer", "Integer [%input%] must be a whole number between -2^31 and 2^31-1 inclusively");
            this.put("argument-handler-player", "No Player with the username [%input%] was found online");
            this.put("argument-handler-material", "No material with the name [%input%] was found");
        }};
    }
}
