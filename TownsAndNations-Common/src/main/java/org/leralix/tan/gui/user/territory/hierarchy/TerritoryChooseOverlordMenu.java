package org.leralix.tan.gui.user.territory.hierarchy;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TerritoryChooseOverlordMenu extends IteratorGUI {

    private final TerritoryData territoryData;
    private final Consumer<Player> onCLose;

    public TerritoryChooseOverlordMenu(Player player, TerritoryData territoryData, Consumer<Player> onClose) {
        super(player, Lang.HEADER_TERRITORY_SELECT_OVERLORD, 6);
        this.territoryData = territoryData;
        this.onCLose = onClose;
        open();
    }

    @Override
    public void open() {
        List<GuiItem> guiItems = getAllSubjugationProposals();

        iterator(guiItems, onCLose);

        gui.open(player);
    }

    public List<GuiItem> getAllSubjugationProposals() {
        ArrayList<GuiItem> proposals = new ArrayList<>();
        LangType langType = tanPlayer.getLang();

        for (String proposalID : territoryData.getOverlordsProposals()) {
            TerritoryData proposalOverlord = TerritoryUtil.getTerritory(proposalID);
            if (proposalOverlord == null) continue;

            proposals.add(proposalOverlord
                    .getIconWithInformations(langType)
                    .setClickToAcceptMessage(
                            Lang.GUI_GENERIC_LEFT_CLICK_TO_ACCEPT,
                            Lang.RIGHT_CLICK_TO_REFUSE
                    )
                    .setAction(action -> {
                        if (action.isLeftClick()) {
                            if (territoryData.haveOverlord()) {
                                TanChatUtils.message(player, Lang.TOWN_ALREADY_HAVE_OVERLORD.get(langType), SoundEnum.NOT_ALLOWED);
                                return;
                            }

                            territoryData.setOverlord(proposalOverlord);
                            territoryData.broadcastMessageWithSound(Lang.ACCEPTED_VASSALISATION_PROPOSAL_ALL.get(territoryData.getColoredName(), proposalOverlord.getName()), SoundEnum.GOOD);
                            new HierarchyMenu(player, territoryData);
                        } else if (action.isRightClick()) {
                            territoryData.getOverlordsProposals().remove(proposalID);
                            open();
                        }
                    })
                    .asGuiItem(player, langType)
            );
        }
        return proposals;
    }
}
