package org.leralix.tan.gui.user.player;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.user.territory.NoTownMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class ApplyToTownMenu extends IteratorGUI {


    public ApplyToTownMenu(Player player) {
        super(player, Lang.HEADER_TOWN_LIST.get(player), 6);
        open();
    }

    @Override
    public void open() {

        GuiUtil.createIterator(gui, getTowns(), page, player,
                p -> new NoTownMenu(player),
                p -> nextPage(),
                p -> previousPage());

        gui.open(player);
    }


    public List<GuiItem> getTowns() {
        ArrayList<GuiItem> towns = new ArrayList<>();

        for (TownData specificTownData : TownDataStorage.getInstance().getAll().values()) {

            towns.add(specificTownData
                    .getIconWithInformations(tanPlayer.getLang())
                    .addDescription((specificTownData.isRecruiting()) ?
                            Lang.GUI_TOWN_INFO_IS_RECRUITING.get() :
                            Lang.GUI_TOWN_INFO_IS_NOT_RECRUITING.get()
                    )
                    .setClickToAcceptMessage(
                            (specificTownData.isPlayerAlreadyRequested(player)) ?
                                    Lang.GUI_TOWN_INFO_RIGHT_CLICK_TO_CANCEL :
                                    Lang.GUI_TOWN_INFO_LEFT_CLICK_TO_JOIN
                    )
                    .setAction(action -> {
                        if (action.isLeftClick()) {

                            if (!player.hasPermission("tan.base.town.join")) {
                                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
                                return;
                            }
                            if (specificTownData.isPlayerAlreadyRequested(player)) {
                                return;
                            }
                            if (!specificTownData.isRecruiting()) {
                                TanChatUtils.message(player, Lang.PLAYER_TOWN_NOT_RECRUITING.get(tanPlayer));
                                return;
                            }
                            specificTownData.addPlayerJoinRequest(player);
                            TanChatUtils.message(player, Lang.PLAYER_ASK_TO_JOIN_TOWN_PLAYER_SIDE.get(tanPlayer, specificTownData.getName()));
                            open();
                        }
                        if (action.isRightClick()) {
                            if (!specificTownData.isPlayerAlreadyRequested(player)) {
                                return;
                            }
                            specificTownData.removePlayerJoinRequest(player);
                            TanChatUtils.message(player, Lang.PLAYER_REMOVE_ASK_TO_JOIN_TOWN_PLAYER_SIDE.get(tanPlayer));
                            open();
                        }
                    })
                    .asGuiItem(player, langType)
            );
        }
        return towns;
    }
}
