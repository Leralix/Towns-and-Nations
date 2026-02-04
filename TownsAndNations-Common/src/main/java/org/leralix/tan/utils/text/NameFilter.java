package org.leralix.tan.utils.text;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.lang.Lang;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

public class NameFilter {

    private static final String DEFAULT_WORDS_FILE = "banned_words.yml";

    private static volatile boolean enabled = true;
    private static volatile boolean normalizeDiacritics = true;
    private static volatile boolean normalizeLeetspeak = false;

    private static volatile boolean applyToTown = true;
    private static volatile boolean applyToRegion = true;
    private static volatile boolean applyToNation = true;

    private static Set<String> blockedWords = Collections.emptySet();

    private static volatile File activeWordsFile;

    private static final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{M}+");

    private NameFilter() {
        throw new IllegalStateException("Utility class");
    }

    public enum Scope {
        TOWN,
        REGION,
        NATION
    }

    public static synchronized void reload(YamlConfiguration config) {
        enabled = config.getBoolean("EnableNameFilter", true);

        normalizeDiacritics = config.getBoolean("NameFilterNormalizeDiacritics", true);
        normalizeLeetspeak = config.getBoolean("NameFilterNormalizeLeetspeak", false);

        applyToTown = config.getBoolean("NameFilterApplyToTown", true);
        applyToRegion = config.getBoolean("NameFilterApplyToRegion", true);
        applyToNation = config.getBoolean("NameFilterApplyToNation", true);

        if (!enabled) {
            blockedWords = Collections.emptySet();
            return;
        }

        String fileName = config.getString("NameFilterFile", DEFAULT_WORDS_FILE);
        if (fileName == null || fileName.isBlank()) {
            fileName = DEFAULT_WORDS_FILE;
        }

        TownsAndNations plugin = TownsAndNations.getPlugin();
        plugin.saveResource(DEFAULT_WORDS_FILE, false);

        File wordsFile = ensureWordsFileExists(plugin, fileName);
        activeWordsFile = wordsFile;
        YamlConfiguration bannedWordConfig = YamlConfiguration.loadConfiguration(wordsFile);

        List<String> words = bannedWordConfig.getStringList("blockedWords");
        Set<String> normalized = new HashSet<>();
        for (String w : words) {
            if (w == null) {
                continue;
            }
            String cleaned = normalize(w);
            if (!cleaned.isEmpty()) {
                normalized.add(cleaned);
            }
        }

        blockedWords = Collections.unmodifiableSet(normalized);
    }

    private static File ensureWordsFileExists(TownsAndNations plugin, String fileName) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            return new File(dataFolder, fileName);
        }

        File target = new File(dataFolder, fileName);
        if (target.exists()) {
            return target;
        }

        File fallback = new File(dataFolder, DEFAULT_WORDS_FILE);
        if (!fallback.exists()) {
            plugin.saveResource(DEFAULT_WORDS_FILE, false);
        }

        if (!fileName.equals(DEFAULT_WORDS_FILE) && fallback.exists()) {
            try {
                Path targetPath = target.toPath();
                Files.copy(fallback.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ignored) {
                // Fallback to using default file below
            }
        }

        return target.exists() ? target : fallback;
    }

    public static boolean isNameAllowed(String name) {
        return isNameAllowed(name, null);
    }

    public static boolean isNameAllowed(String name, Scope scope) {
        if (!enabled) {
            return true;
        }
        if (scope != null && !isScopeEnabled(scope)) {
            return true;
        }
        if (name == null) {
            return true;
        }

        String normalizedName = normalize(name);

        if (normalizedName.isEmpty()) {
            return true;
        }

        for (String word : blockedWords) {
            if (!word.isEmpty() && normalizedName.contains(word)) {
                return false;
            }
        }
        return true;
    }

    public static boolean validateOrWarn(CommandSender sender, String name) {
        if (isNameAllowed(name, null)) {
            return true;
        }
        sendBlockedNameWarning(sender);
        return false;
    }

    public static boolean validateOrWarn(CommandSender sender, String name, Scope scope) {
        if (isNameAllowed(name, scope)) {
            return true;
        }
        sendBlockedNameWarning(sender);
        return false;
    }

    private static void sendBlockedNameWarning(CommandSender sender) {
        if (sender instanceof Player player) {
            TanChatUtils.message(player, Lang.NAME_FILTER_BLOCKED_NAME.get());
        } else if (sender != null) {
            sender.sendMessage(Lang.NAME_FILTER_BLOCKED_NAME.getDefault());
        }
    }

    private static boolean isScopeEnabled(Scope scope) {
        return switch (scope) {
            case TOWN -> applyToTown;
            case REGION -> applyToRegion;
            case NATION -> applyToNation;
        };
    }

    private static String normalize(String input) {
        if (input == null) {
            return "";
        }
        String s = ChatColor.stripColor(input);
        if (s == null) {
            s = input;
        }
        s = s.trim().toLowerCase(Locale.ROOT);
        if (s.isEmpty()) {
            return "";
        }
        if (normalizeDiacritics) {
            s = Normalizer.normalize(s, Normalizer.Form.NFKD);
            s = DIACRITICS_PATTERN.matcher(s).replaceAll("");
        }
        if (normalizeLeetspeak) {
            s = s
                    .replace('0', 'o')
                    .replace('1', 'i')
                    .replace('3', 'e')
                    .replace('4', 'a')
                    .replace('5', 's')
                    .replace('7', 't')
                    .replace('@', 'a')
                    .replace('$', 's');
        }
        return s;
    }
}
