package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.KingdomData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public class KingdomChangeCapitalMenu extends IteratorGUI {

    private final KingdomData kingdomData;

    public KingdomChangeCapitalMenu(Player player, KingdomData kingdomData) {
        super(player, Lang.HEADER_CHANGE_KINGDOM_CAPITAL, 6);
        this.kingdomData = kingdomData;
        open();
    }

    @Override
    public void open() {
        iterator(getCandidates(), p -> new KingdomSettingsMenu(player, kingdomData));
        gui.open(player);
    }

    private List<GuiItem> getCandidates() {
        ArrayList<GuiItem> guiItems = new ArrayList<>();

        for (TerritoryData territory : kingdomData.getVassals()) {
            if (!(territory instanceof RegionData regionData)) {
                continue;
            }

            guiItems.add(
                    regionData.getIconWithInformations(tanPlayer.getLang())
                            .setAction(action -> {
                                action.setCancelled(true);

                                new ConfirmMenu(
                                        player,
                                        Lang.GUI_CONFIRM_CHANGE_KINGDOM_CAPITAL.get(regionData.getName()),
                                        () -> {
                                            kingdomData.setCapital(regionData.getID());
                                            new KingdomSettingsMenu(player, kingdomData);
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
