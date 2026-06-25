package org.leralix.tan.war.wargoals;


import org.bukkit.Material;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.wargoals.Tribute;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.constants.Constants;
import org.tan.api.interfaces.war.wargoals.TanCaptureChunkWargoal;

import java.util.ArrayList;
import java.util.List;

public class TributeWarGoal extends WarGoal implements TanCaptureChunkWargoal {

    private final int dailyAmount;

    public TributeWarGoal(int dailyAmount) {
        this.dailyAmount = dailyAmount;
    }

    @Override
    public IconBuilder getIcon(LangType langType) {

        List<FilledLang> description = new ArrayList<>();
        description.add(Lang.TRIBUTE_WAR_GOAL_DESC.get(Integer.toString(Constants.getTributeDuration())));
        description.add(Lang.TRIBUTE_WAR_GOAL_DESC1.get(Integer.toString(dailyAmount), Integer.toString(Constants.getTributeDuration())));

        return buildIcon(Material.GOLD_INGOT, description, langType);
    }

    @Override
    public String getDisplayName(LangType langType) {
        return Lang.TRIBUTE_WAR_GOAL.get(langType);
    }

    @Override
    public void applyWarGoal(Territory winner, Territory loser) {
        if (winner == null || loser == null)
            return;
        Tribute tribute = new Tribute(winner, loser, dailyAmount * Constants.getTributeDuration());
        TownsAndNations.getPlugin().getTributeStorage().registerTribute(tribute);
    }

    @Override
    public String getCurrentDesc(LangType langType) {
        return Lang.TRIBUTE_WAR_GOAL_DESC1.get(langType, Integer.toString(dailyAmount), Integer.toString(Constants.getTributeDuration()));
    }

    @Override
    public int getAmount() {
        return dailyAmount;
    }
}
