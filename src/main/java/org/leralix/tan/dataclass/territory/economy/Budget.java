package org.leralix.tan.dataclass.territory.economy;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.text.StringUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Budget {


    private final List<ProfitLine> profitList;

    public Budget() {
        profitList = new LinkedList<>();
    }

    public void addProfitLine(ProfitLine profitLine) {
        profitList.add(profitLine);
    }


    public List<String> createLore(LangType langType) {
        double total = 0;
        LinkedList<String> lore = new LinkedList<>();

        Collections.sort(profitList);
        for (ProfitLine profitLine : profitList) {
            if (profitLine.isRecurrent()) {
                lore.add(profitLine.getLine(langType));
                total += profitLine.getMoney();
            }
        }
        lore.addFirst(Lang.TOTAL_ESTIMATED_EVOLUTION.get(langType, StringUtil.getColoredMoney(total)));
        return lore;
    }

    public void createGui(Gui gui, Player player, LangType langType) {
        for (ProfitLine profitLine : profitList) {
            profitLine.addItems(gui, player, langType);
        }
    }
}
