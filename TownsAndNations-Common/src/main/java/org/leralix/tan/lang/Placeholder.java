package org.leralix.tan.lang;

import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.player.ITanPlayer;

public interface Placeholder {

    String translate(LangType langType);

    default String translate(ITanPlayer player) {
        return translate(player.getLang());
    }

    default String translate(Player player) {
        return translate(TownsAndNations.getPlugin().getPlayerDataStorage().get(player));
    }

    default String translateDefault() {
        return translate(Lang.getServerLang());
    }
}