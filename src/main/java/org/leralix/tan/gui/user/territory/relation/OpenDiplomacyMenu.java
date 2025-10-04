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
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.constants.RelationConstant;
import org.leralix.tan.utils.deprecated.GuiUtil;

import java.util.ArrayList;
import java.util.List;

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
        List<String> desc = generateDescription(Lang.GUI_TOWN_RELATION_HOSTILE_DESC1.get(langType), TownRelation.WAR);

        return iconManager.get(IconKey.GUI_WAR_ICON)
                .setName(Lang.GUI_TOWN_RELATION_HOSTILE.get(langType))
                .setDescription(desc)
                .setAction(p -> new OpenRelationMenu(player, territoryData, TownRelation.WAR))
                .asGuiItem(player);
    }

    private GuiItem getEmbargoButton() {
        List<String> desc = generateDescription(Lang.GUI_TOWN_RELATION_EMBARGO_DESC1.get(langType), TownRelation.EMBARGO);

        return iconManager.get(IconKey.GUI_EMBARGO_ICON)
                .setName(Lang.GUI_TOWN_RELATION_EMBARGO.get(langType))
                .setDescription(desc)
                .setAction(p -> new OpenRelationMenu(player, territoryData, TownRelation.EMBARGO))
                .asGuiItem(player);
    }

    private GuiItem getNonAggressionPactButton() {
        List<String> desc = generateDescription(Lang.GUI_TOWN_RELATION_NAP_DESC1.get(langType), TownRelation.NON_AGGRESSION);

        return iconManager.get(IconKey.GUI_NON_AGGRESSION_PACT_ICON)
                .setName(Lang.GUI_TOWN_RELATION_NAP.get(langType))
                .setDescription(desc)
                .setAction(p -> new OpenRelationMenu(player, territoryData, TownRelation.NON_AGGRESSION))
                .asGuiItem(player);
    }

    private GuiItem getAllianceButton() {
        List<String> desc = generateDescription(Lang.GUI_TOWN_RELATION_ALLIANCE_DESC1.get(langType), TownRelation.ALLIANCE);

        return iconManager.get(IconKey.GUI_ALLIANCE_ICON)
                .setName(Lang.GUI_TOWN_RELATION_ALLIANCE.get(langType))
                .setDescription(desc)
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


    private List<String> generateDescription(String description,  TownRelation relation){

        List<String> res = new ArrayList<>();

        res.add(description);
        RelationConstant relationConstant = Constants.getRelationConstants(relation);

        if(relationConstant.canPvP()){
            res.add(Lang.GUI_RELATION_ENABLE_PVP.get(langType));
        }
        else {
            res.add(Lang.GUI_RELATION_DISABLE_PVP.get(langType));
        }

        if(relationConstant.canInteractWithProperty()){
            res.add(Lang.GUI_RELATION_ENABLE_PROPERTY.get(langType));
        }
        else {
            res.add(Lang.GUI_RELATION_DISABLE_PROPERTY.get(langType));
        }

        if(!relationConstant.canAccessTerritory()){
            res.add(Lang.GUI_RELATION_BLOCK_ENTRY.get(langType));
        }

        int blockCommandsSize = relationConstant.getBlockedCommands().size();
        if(blockCommandsSize > 0){
            res.add(Lang.GUI_RELATION_DISABLE_COMMANDS.get(langType, Integer.toString(blockCommandsSize)));
        }
        if(relation == TownRelation.WAR){
            res.add(Lang.GUI_RELATION_ENABLE_WAR.get(langType));
        }
        res.add("");
        res.add(Lang.GUI_GENERIC_CLICK_TO_OPEN.get(langType));

        return res;
    }
}
