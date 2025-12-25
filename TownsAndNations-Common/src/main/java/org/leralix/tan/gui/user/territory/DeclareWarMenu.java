package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.gui.user.war.WarMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.war.War;

import java.util.ArrayList;
import java.util.List;

public class DeclareWarMenu extends IteratorGUI {

    private final BasicGui returnMenu;

    private final TerritoryData territoryData;

    public DeclareWarMenu(Player player, TerritoryData territoryData, BasicGui returnMenu) {
        super(player, Lang.HEADER_DECLARE_WAR, 3);
        this.territoryData = territoryData;
        this.returnMenu = returnMenu;
        open();
    }

    @Override
    public void open() {
        iterator(getTerritoryToDeclareOn(), p -> returnMenu.open());
        gui.open(player);
    }

    private List<GuiItem> getTerritoryToDeclareOn() {
        List<GuiItem> res = new ArrayList<>();
        WarStorage warStorage = WarStorage.getInstance();

        for (String TerritoryID : territoryData.getRelations().getTerritoriesIDWithRelation(TownRelation.WAR)) {
            TerritoryData iterateTerritory = TerritoryUtil.getTerritory(TerritoryID);

            res.add(getDeclareWarButton(iterateTerritory, warStorage));
        }
        return res;
    }

    private GuiItem getDeclareWarButton(TerritoryData iterateTerritory, WarStorage warStorage) {
        return iterateTerritory.getIconWithInformationAndRelation(territoryData, langType)
                .setClickToAcceptMessage(Lang.GUI_TOWN_ATTACK_TOWN_DESC1)
                .setAction(action -> {
                    if (warStorage.isTerritoryAtWarWith(territoryData, iterateTerritory)) {
                        TanChatUtils.message(player, Lang.GUI_TOWN_ATTACK_ALREADY_ATTACKING.get(tanPlayer));
                        SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                        return;
                    }

                    int nbAllies = iterateTerritory.getRelations().getTerritoriesIDWithRelation(TownRelation.ALLIANCE).size();

                    List<FilledLang> confirmDescription = List.of(
                            Lang.DECLARE_WAR_CONFIRM_MESSAGE.get(
                                    territoryData.getColoredName(),
                                    iterateTerritory.getColoredName()
                            ),
                            Lang.DECLARE_WAR_NUMBER_OF_ALLIES.get(
                                    iterateTerritory.getColoredName(),
                                    Integer.toString(nbAllies)
                            )
                    );

                    new ConfirmMenu(
                            player,
                            confirmDescription,
                            () -> {
                                War newWar = warStorage.newWar(territoryData, iterateTerritory);
                                new WarMenu(player, territoryData, newWar);
                            },
                            this::open
                    );
                })
                .asGuiItem(player, langType);
    }
}
