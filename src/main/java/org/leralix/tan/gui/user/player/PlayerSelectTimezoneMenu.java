package org.leralix.tan.gui.user.player;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.timezone.TimeZoneEnum;

import java.util.ArrayList;
import java.util.List;

public class PlayerSelectTimezoneMenu extends IteratorGUI {


    public PlayerSelectTimezoneMenu(Player player){
        super(player, Lang.HEADER_SELECT_TIMEZONE.get(player), 4);
        open();
    }

    @Override
    public void open() {
        iterator(getTimezones(), PlayerMenu::new);

        gui.open(player);
    }

    private List<GuiItem> getTimezones() {
        List<GuiItem> timezones = new ArrayList<>();

        for(TimeZoneEnum timeZoneEnum : TimeZoneEnum.values()){
            timezones.add(
                    iconManager.get(IconKey.TIMEZONE_BUTTON)
                            .setName(timeZoneEnum.getName(tanPlayer.getLang()))
                            .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_MODIFY)
                            .setAction(
                                    action -> {
                                        tanPlayer.setTimeZone(timeZoneEnum);
                                        new PlayerMenu(player);
                                    }
                            )
                            .asGuiItem(player, langType)
            );
        }
        return timezones;
    }
}
