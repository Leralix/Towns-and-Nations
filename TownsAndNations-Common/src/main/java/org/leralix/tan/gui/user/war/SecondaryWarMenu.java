package org.leralix.tan.gui.user.war;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.territory.WarsMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.war.War;

import static org.leralix.lib.data.SoundEnum.MINOR_GOOD;

public class SecondaryWarMenu extends AbstractWarMenu {

    private final TerritoryData territoryData;

    public SecondaryWarMenu(Player player, TerritoryData territoryData, War war) {
        super(player, Lang.HEADER_WARS_MENU, 3, war);
        this.territoryData = territoryData;
        open();
    }

    @Override
    public void open() {
        gui.setItem(2, 2, getDefendingSideInfo());
        gui.setItem(2, 3, getDefendingSideInfo());

        gui.setItem(2, 8, leaveWarButton());

    }

    private @NotNull GuiItem leaveWarButton() {
        return iconManager.get(IconKey.WAR_SURRENDER_ICON)
                .setName(Lang.WAR_SURRENDER.get(langType))
                .setAction(action -> {
                    war.removeBelligerent(territoryData);
                    territoryData.broadcastMessageWithSound(
                            Lang.TERRITORY_NO_LONGER_INVOLVED_IN_WAR_MESSAGE.get(
                                    territoryData.getName(),
                                    war.getName()
                            ), MINOR_GOOD
                    );
                    new WarsMenu(player, territoryData);
                })
                .asGuiItem(player, langType);
    }
}
