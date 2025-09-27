package org.leralix.tan.gui.user.territory.relation;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class OpenDiplomacyMenu extends BasicGui {

    private final TerritoryData territoryData;

    public OpenDiplomacyMenu(Player player, TerritoryData territoryData){
        super(player, Lang.HEADER_RELATIONS.get(player, territoryData.getName()), 3);
        this.territoryData = territoryData;
        open();
    }

    @Override
    public void open() {

        gui.setItem(9, getWarButton());
        gui.setItem(11, getEmbargoButton());
        gui.setItem(13, getNonAggressionPactButton());
        gui.setItem(15, getAllianceButton());
        gui.setItem(17, getProposalButton());

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> PlayerGUI.dispatchPlayerTown(player)));

        gui.open(player);
    }

    private GuiItem getWarButton() {
        return iconManager.get(IconKey.GUI_WAR_ICON)
                .setName(Lang.GUI_TOWN_RELATION_HOSTILE.get(langType))
                .setDescription(
                        Lang.GUI_TOWN_RELATION_HOSTILE_DESC1.get(langType, territoryData.getName()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(langType)
                )
                .setAction(p -> new OpenRelationMenu(player, territoryData, TownRelation.WAR))
                .asGuiItem(player);
    }

    private GuiItem getEmbargoButton() {
        return iconManager.get(IconKey.GUI_EMBARGO_ICON)
                .setName(Lang.GUI_TOWN_RELATION_EMBARGO.get(langType))
                .setDescription(
                        Lang.GUI_TOWN_RELATION_EMBARGO_DESC1.get(langType, territoryData.getName()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(langType)
                )
                .setAction(p -> new OpenRelationMenu(player, territoryData, TownRelation.EMBARGO))
                .asGuiItem(player);
    }

    private GuiItem getNonAggressionPactButton() {
        return iconManager.get(IconKey.GUI_NON_AGGRESSION_PACT_ICON)
                .setName(Lang.GUI_TOWN_RELATION_HOSTILE.get(langType))
                .setDescription(
                        Lang.GUI_TOWN_RELATION_HOSTILE_DESC1.get(langType, territoryData.getName()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(langType)
                )
                .setAction(p -> new OpenRelationMenu(player, territoryData, TownRelation.NON_AGGRESSION))
                .asGuiItem(player);
    }

    private GuiItem getAllianceButton() {
        return iconManager.get(IconKey.GUI_ALLIANCE_ICON)
                .setName(Lang.GUI_TOWN_RELATION_ALLIANCE.get(langType))
                .setDescription(
                        Lang.GUI_TOWN_RELATION_ALLIANCE_DESC1.get(langType, territoryData.getName()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(langType)
                )
                .setAction(p -> new OpenRelationMenu(player, territoryData, TownRelation.ALLIANCE))
                .asGuiItem(player);
    }

    private @NotNull GuiItem getProposalButton() {
        return iconManager.get(IconKey.GUI_PROPOSALS_ICON)
                .setName(Lang.GUI_TOWN_RELATION_DIPLOMACY_PROPOSAL.get(langType))
                .setDescription(
                        Lang.GUI_TOWN_RELATION_DIPLOMACY_PROPOSAL_DESC1.get(tanPlayer),
                        Lang.GUI_TOWN_RELATION_DIPLOMACY_PROPOSAL_DESC2.get(tanPlayer, Integer.toString(territoryData.getAllDiplomacyProposal().size()))
                )
                .setAction(action -> {
                    if (!territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.MANAGE_TOWN_RELATION)) {
                        player.sendMessage(Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                        return;
                    }
                    new OpenDiplomacyProposalsMenu(player, territoryData);
                })
                .asGuiItem(player);
    }
}
