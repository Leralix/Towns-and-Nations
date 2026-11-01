package org.leralix.tan.gui.user.player;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.data.territory.permission.RecruitingPolicy;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.user.territory.NoTownMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public class ApplyToTownMenu extends IteratorGUI {


    public ApplyToTownMenu(Player player) {
        super(player, Lang.HEADER_TOWN_LIST, 6);
        open();
    }

    @Override
    public void open() {

        iterator(getTowns(), p -> new NoTownMenu(player));

        gui.open(player);
    }


    public List<GuiItem> getTowns() {
        ArrayList<GuiItem> towns = new ArrayList<>();

        for (Town specificTownData : TownsAndNations.getPlugin().getTownStorage().getAll().values()) {

            var recruitingPolicy = specificTownData.getRecruitingPolicy();
            towns.add(specificTownData
                    .getIconWithInformations(tanPlayer.getLang())
                    .addDescription(
                            Lang.GUI_TOWN_RECRUITING_POLICY.get(
                                    switch (recruitingPolicy) {
                                        case AUTHORIZE_ALL -> Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_OPEN.translate(langType);
                                        case APPLICATION_OPEN -> Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_ACCEPT.translate(langType);
                                        case CLOSED -> Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_NOT_ACCEPT.translate(langType);
                                    }
                            )
                    )
                    .setClickToAcceptMessage(
                            specificTownData.isPlayerAlreadyRequested(player) ?
                                    Lang.GUI_TOWN_INFO_RIGHT_CLICK_TO_CANCEL :
                                    Lang.GUI_TOWN_INFO_LEFT_CLICK_TO_JOIN
                    )
                    .setAction(action -> {
                        if (action.isLeftClick()) {

                            if(recruitingPolicy == RecruitingPolicy.CLOSED){
                                SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                                return;
                            }
                            else if(recruitingPolicy == RecruitingPolicy.AUTHORIZE_ALL){
                                specificTownData.addPlayer(tanPlayer);
                            }
                            else {
                                if (!player.hasPermission("tan.base.town.join")) {
                                    TanChatUtils.message(tanPlayer, Lang.PLAYER_NO_PERMISSION, SoundEnum.NOT_ALLOWED);
                                    return;
                                }
                                if (specificTownData.isPlayerAlreadyRequested(player)) {
                                    return;
                                }

                                specificTownData.addPlayerJoinRequest(player);
                                TanChatUtils.message(tanPlayer, Lang.PLAYER_ASK_TO_JOIN_TOWN_PLAYER_SIDE.get(specificTownData.getName()));
                                open();
                            }
                        }
                        if (action.isRightClick() && specificTownData.isPlayerAlreadyRequested(player)) {
                            specificTownData.removePlayerJoinRequest(player.getUniqueId());
                            TanChatUtils.message(player, Lang.PLAYER_REMOVE_ASK_TO_JOIN_TOWN_PLAYER_SIDE.translate(tanPlayer));
                            open();
                        }
                    })
                    .asGuiItem(player, langType)
            );
        }
        return towns;
    }
}
