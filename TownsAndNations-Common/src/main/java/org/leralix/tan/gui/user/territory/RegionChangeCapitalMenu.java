package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public class RegionChangeCapitalMenu extends IteratorGUI {

    private final Region regionData;
    private final BasicGui returnGui;

    public RegionChangeCapitalMenu(Player player, Region regionData, BasicGui returnGui) {
        super(player, Lang.HEADER_CHANGE_NATION_CAPITAL, 6);
        this.regionData = regionData;
        this.returnGui = returnGui;
        open();
    }

    @Override
    public void open() {
        iterator(getCandidates(), p -> returnGui.open());
        gui.open(player);
    }

    private List<GuiItem> getCandidates() {
        ArrayList<GuiItem> guiItems = new ArrayList<>();

        for (Territory territory : regionData.getVassalsInternal()) {
            if (!(territory instanceof Town townData)) {
                continue;
            }

            guiItems.add(
                    townData.getIconWithInformations(tanPlayer.getLang())
                            .setAction(action -> {
                                action.setCancelled(true);

                                new ConfirmMenu(
                                        player,
                                        Lang.GUI_CONFIRM_CHANGE_NATION_CAPITAL.get(townData.getName()),
                                        () -> {
                                            this.regionData.setCapital(townData.getID());
                                            returnGui.open();
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
