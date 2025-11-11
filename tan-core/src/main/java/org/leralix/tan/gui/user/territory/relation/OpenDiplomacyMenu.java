package org.leralix.tan.gui.user.territory.relation;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.constants.RelationConstant;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class OpenDiplomacyMenu extends BasicGui {

  private final TerritoryData territoryData;

  private OpenDiplomacyMenu(Player player, ITanPlayer tanPlayer, TerritoryData territoryData) {
    super(
        player,
        tanPlayer,
        Lang.HEADER_RELATIONS.get(tanPlayer.getLang(), territoryData.getName()),
        3);
    this.territoryData = territoryData;
  }

  public static void open(Player player, TerritoryData territoryData) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new OpenDiplomacyMenu(player, tanPlayer, territoryData).open();
            });
  }

  @Override
  public void open() {

    gui.setItem(9, getWarButton());
    gui.setItem(11, getEmbargoButton());
    gui.setItem(13, getNonAggressionPactButton());
    gui.setItem(15, getAllianceButton());
    gui.setItem(17, getProposalButton());

    gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> territoryData.openMainMenu(player)));

    gui.open(player);
  }

  private GuiItem getWarButton() {
    List<FilledLang> desc =
        generateDescription(Lang.GUI_TOWN_RELATION_HOSTILE_DESC1.get(), TownRelation.WAR);

    return iconManager
        .get(IconKey.GUI_WAR_ICON)
        .setName(Lang.GUI_TOWN_RELATION_HOSTILE.get(langType))
        .setDescription(desc)
        .setAction(p -> OpenRelationMenu.open(player, territoryData, TownRelation.WAR))
        .asGuiItem(player, langType);
  }

  private GuiItem getEmbargoButton() {
    List<FilledLang> desc =
        generateDescription(Lang.GUI_TOWN_RELATION_EMBARGO_DESC1.get(), TownRelation.EMBARGO);

    return iconManager
        .get(IconKey.GUI_EMBARGO_ICON)
        .setName(Lang.GUI_TOWN_RELATION_EMBARGO.get(langType))
        .setDescription(desc)
        .setAction(p -> OpenRelationMenu.open(player, territoryData, TownRelation.EMBARGO))
        .asGuiItem(player, langType);
  }

  private GuiItem getNonAggressionPactButton() {
    List<FilledLang> desc =
        generateDescription(Lang.GUI_TOWN_RELATION_NAP_DESC1.get(), TownRelation.NON_AGGRESSION);

    return iconManager
        .get(IconKey.GUI_NON_AGGRESSION_PACT_ICON)
        .setName(Lang.GUI_TOWN_RELATION_NAP.get(langType))
        .setDescription(desc)
        .setAction(p -> OpenRelationMenu.open(player, territoryData, TownRelation.NON_AGGRESSION))
        .asGuiItem(player, langType);
  }

  private GuiItem getAllianceButton() {
    List<FilledLang> desc =
        generateDescription(Lang.GUI_TOWN_RELATION_ALLIANCE_DESC1.get(), TownRelation.ALLIANCE);

    return iconManager
        .get(IconKey.GUI_ALLIANCE_ICON)
        .setName(Lang.GUI_TOWN_RELATION_ALLIANCE.get(langType))
        .setDescription(desc)
        .setAction(p -> OpenRelationMenu.open(player, territoryData, TownRelation.ALLIANCE))
        .asGuiItem(player, langType);
  }

  private @NotNull GuiItem getProposalButton() {
    return iconManager
        .get(IconKey.GUI_PROPOSALS_ICON)
        .setName(Lang.GUI_TOWN_RELATION_DIPLOMACY_PROPOSAL.get(langType))
        .setDescription(
            Lang.GUI_TOWN_RELATION_DIPLOMACY_PROPOSAL_DESC1.get(),
            Lang.GUI_TOWN_RELATION_DIPLOMACY_PROPOSAL_DESC2.get(
                Integer.toString(territoryData.getAllDiplomacyProposal().size())))
        .setAction(
            action -> {
              if (!territoryData.doesPlayerHavePermission(
                  tanPlayer, RolePermission.MANAGE_TOWN_RELATION)) {
                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                return;
              }
              OpenDiplomacyProposalsMenu.open(player, territoryData);
            })
        .asGuiItem(player, langType);
  }

  private List<FilledLang> generateDescription(FilledLang description, TownRelation relation) {

    List<FilledLang> res = new ArrayList<>();

    res.add(description);
    RelationConstant relationConstant = Constants.getRelationConstants(relation);

    int trucePeriod = relationConstant.trucePeriod();
    if (trucePeriod > 0) {
      res.add(Lang.GUI_RELATION_TRUCE.get(Integer.toString(trucePeriod)));
    }

    if (relationConstant.canPvP()) {
      res.add(Lang.GUI_RELATION_ENABLE_PVP.get());
    } else {
      res.add(Lang.GUI_RELATION_DISABLE_PVP.get());
    }

    if (relationConstant.canInteractWithProperty()) {
      res.add(Lang.GUI_RELATION_ENABLE_PROPERTY.get());
    } else {
      res.add(Lang.GUI_RELATION_DISABLE_PROPERTY.get());
    }

    if (!relationConstant.canAccessTerritory()) {
      res.add(Lang.GUI_RELATION_BLOCK_ENTRY.get());
    }

    int blockCommandsSize = relationConstant.getBlockedCommands().size();
    if (blockCommandsSize > 0) {
      res.add(Lang.GUI_RELATION_DISABLE_COMMANDS.get(Integer.toString(blockCommandsSize)));
    }
    if (relation == TownRelation.WAR) {
      res.add(Lang.GUI_RELATION_ENABLE_WAR.get());
    }

    return res;
  }
}
