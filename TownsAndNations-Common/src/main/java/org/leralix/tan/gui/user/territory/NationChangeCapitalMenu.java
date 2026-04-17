package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.Nation;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public class NationChangeCapitalMenu extends IteratorGUI {

    private final Nation nationData;

    public NationChangeCapitalMenu(Player player, Nation nationData) {
        super(player, Lang.HEADER_CHANGE_NATION_CAPITAL, 6);
        this.nationData = nationData;
        open();
    }

    @Override
    public void open() {
        iterator(getCandidates(), p -> new NationSettingsMenu(player, nationData, this));
        gui.open(player);
    }

    private List<GuiItem> getCandidates() {
        ArrayList<GuiItem> guiItems = new ArrayList<>();

        for (Territory territory : nationData.getVassalsInternal()) {
            if (!(territory instanceof Region regionData)) {
                continue;
            }

            guiItems.add(
                    regionData.getIconWithInformations(tanPlayer.getLang())
                            .setAction(action -> {
                                action.setCancelled(true);

                                new ConfirmMenu(
                                        player,
                                        Lang.GUI_CONFIRM_CHANGE_NATION_CAPITAL.get(regionData.getName()),
                                        () -> {
                                            nationData.setCapital(regionData.getID());
                                            new NationSettingsMenu(player, nationData, this);
                                        },
                                        this::open
                                );
                            })
                            .asGuiItem(player, langType)
            );
        }

        return guiItems;
    }
}
