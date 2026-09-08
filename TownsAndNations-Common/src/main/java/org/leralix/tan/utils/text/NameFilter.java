package org.leralix.tan.utils.text;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.lang.Lang;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public class NameFilter {

    private static final String DEFAULT_WORDS_FILE = "banned_words.yml";

    private static volatile boolean enabled;
    private static volatile boolean normalizeDiacritics;
    private static volatile boolean normalizeLeetspeak;

    private static volatile boolean applyToTown;
    private static volatile boolean applyToRegion;
    private static volatile boolean applyToNation;
    private static volatile boolean applyToRank ;

    private static volatile File wordsFile;

    private static final Set<String> blockedWords = new HashSet<>();

    private static final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{M}+");

    private NameFilter() {
        throw new IllegalStateException("Utility class");
    }

    public enum Scope {
        TOWN,
        REGION,
        NATION,
        RANK,
    }

    public static synchronized void reload(YamlConfiguration config) {
        enabled = config.getBoolean("EnableNameFilter", true);

        if (!enabled) {
            return;
        }

        normalizeDiacritics = config.getBoolean("NameFilterNormalizeDiacritics", true);
        normalizeLeetspeak = config.getBoolean("NameFilterNormalizeLeetspeak", false);

        applyToTown = config.getBoolean("NameFilterApplyToTown", true);
        applyToRegion = config.getBoolean("NameFilterApplyToRegion", true);
        applyToNation = config.getBoolean("NameFilterApplyToNation", true);
        applyToRank = config.getBoolean("NameFilterApplyToRank", true);

        String fileName = config.getString("NameFilterFile", DEFAULT_WORDS_FILE);
        if (fileName.isBlank()) {
            fileName = DEFAULT_WORDS_FILE;
        }

        TownsAndNations plugin = TownsAndNations.getPlugin();
        plugin.saveResource(DEFAULT_WORDS_FILE, false);

        wordsFile = ensureWordsFileExists(plugin, fileName);
        YamlConfiguration bannedWordConfig = YamlConfiguration.loadConfiguration(wordsFile);

        List<String> words = bannedWordConfig.getStringList("blockedWords");
        for (String w : words) {
            if (w == null) {
                continue;
            }
            String cleaned = normalize(w);
            if (!cleaned.isEmpty()) {
                blockedWords.add(cleaned);
            }
        }

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

    public static boolean isNameAllowed(String name, Scope scope) {
        if (!isScopeEnabled(scope)) {
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

    public static boolean validateOrWarn(CommandSender sender, String name, Scope scope) {
        if (isNameAllowed(name, scope)) {
            return true;
        }
        TanChatUtils.message(sender, Lang.NAME_FILTER_BLOCKED_NAME);
        return false;
    }

    private static boolean isScopeEnabled(Scope scope) {
        return switch (scope) {
            case TOWN -> applyToTown;
            case REGION -> applyToRegion;
            case NATION -> applyToNation;
            case RANK -> applyToRank;
        };
    }

    private static String normalize(String input) {
        if (input == null) {
            return "";
        }
        String s = ChatColor.stripColor(input);
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

    public static Set<String> getBlockedWords() {
        return blockedWords;
    }

    public static void addWord(String newWord) {
        if (newWord == null || newWord.isBlank()) {
            return;
        }
        
        String normalized = normalize(newWord);
        if (normalized.isEmpty()) {
            return;
        }
        
        blockedWords.add(normalized);
        saveWordsToFile();
    }

    public static void removeWord(String wordToRemove) {
        if (wordToRemove == null || wordToRemove.isBlank()) {
            return;
        }
        
        String normalized = normalize(wordToRemove);
        if (normalized.isEmpty()) {
            return;
        }
        
        blockedWords.remove(normalized);
        saveWordsToFile();
    }

    private static void saveWordsToFile() {
        if (wordsFile == null) {
            return;
        }

        try {
            YamlConfiguration config = new YamlConfiguration();
            config.set("blockedWords", List.copyOf(blockedWords));
            config.save(wordsFile);
        } catch (IOException e) {
            TownsAndNations.getPlugin().getLogger().warning("Failed to save blocked words to file: " + e.getMessage());
        }
    }
}
