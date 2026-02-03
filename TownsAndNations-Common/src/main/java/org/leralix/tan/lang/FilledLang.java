package org.leralix.tan.lang;

import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;

import java.util.Collections;
import java.util.List;

public class FilledLang {

    private final Lang lang;
    private final List<String> placeholders;

    public FilledLang(Lang lang) {
        this.lang = lang;
        this.placeholders = Collections.emptyList();
    }

    public FilledLang(Lang lang, String... placeholders) {
        this.lang = lang;
        this.placeholders = List.of(placeholders);
    }

    public String getDefault() {
        return lang.get(Lang.getServerLang(), placeholders);
    }

    public String get(LangType langType) {
        return lang.get(langType, placeholders);
    }

    public String get(ITanPlayer player) {
        return get(player.getLang());
    }

    public String get(Player player) {
        return lang.get(PlayerDataStorage.getInstance().get(player).getLang(), placeholders);
    }
}
