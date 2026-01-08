package org.leralix.tan.gui.user.territory.hierarchy;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.KingdomData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.gui.service.requirements.RankPermissionRequirement;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.BAD;
import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class VassalsMenu extends IteratorGUI {

    private final TerritoryData territoryData;

    public VassalsMenu(Player player, TerritoryData territoryData) {
        super(player, Lang.HEADER_VASSALS, 4);
        this.territoryData = territoryData;
        open();
    }


    @Override
    public void open() {
        iterator(getVassals(), p -> PlayerGUI.openHierarchyMenu(player, territoryData));

        gui.setItem(4, 3, getAddVassalButton());

        gui.open(player);
    }

    private @NotNull GuiItem getAddVassalButton() {

        return iconManager.get(IconKey.ADD_VASSALS_ICON)
                .setName((territoryData instanceof KingdomData) ? Lang.GUI_INVITE_REGION_TO_KINGDOM.get(tanPlayer) : Lang.GUI_INVITE_TOWN_TO_REGION.get(tanPlayer))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.TOWN_ADMINISTRATOR))
                .setAction(action -> new AddVassalMenu(player, territoryData))
                .asGuiItem(player, langType);
    }

    private List<GuiItem> getVassals() {

        List<GuiItem> res = new ArrayList<>();

        for (TerritoryData vassal : territoryData.getVassals()) {

            GuiItem vassalButton = iconManager.get(vassal.getIcon())
                    .setName(vassal.getColoredName())
                    .setDescription(
                            Lang.GUI_TOWN_INFO_DESC0.get(vassal.getDescription()),
                            Lang.GUI_TOWN_INFO_DESC1.get(vassal.getLeaderName()),
                            Lang.GUI_TOWN_INFO_DESC2.get(Integer.toString(vassal.getPlayerIDList().size())),
                            Lang.GUI_TOWN_INFO_DESC3.get(Integer.toString(vassal.getNumberOfClaimedChunk()))
                    )
                    .setClickToAcceptMessage(Lang.GUI_GENERIC_RIGHT_CLICK_TO_REMOVE)
                    .setAction(action -> {
                        if(!action.isRightClick()){
                            return;
                        }
                        if (vassal.isCapital()) {
                            TanChatUtils.message(player,
                                    (territoryData instanceof KingdomData) ?
                                            Lang.CANT_KICK_KINGDOM_CAPITAL.get(tanPlayer, vassal.getName()) :
                                            Lang.CANT_KICK_REGIONAL_CAPITAL.get(tanPlayer, vassal.getName()),
                                    NOT_ALLOWED);
                            return;
                        }

                        new ConfirmMenu(
                                player,
                                Lang.CONFIRM_VASSAL_KICK.get(vassal.getColoredName(), territoryData.getColoredName()),
                                () -> {
                                    territoryData.broadcastMessageWithSound(
                                            (territoryData instanceof KingdomData) ?
                                                    Lang.GUI_KINGDOM_KICK_REGION_BROADCAST.get(vassal.getName()) :
                                                    Lang.GUI_REGION_KICK_TOWN_BROADCAST.get(vassal.getName()),
                                            BAD);
                                    vassal.removeOverlord();
                                    player.closeInventory();
                                },
                                this::open
                        );
                    })
                    .asGuiItem(player, langType);
            res.add(vassalButton);
        }
        return res;
    }
}
