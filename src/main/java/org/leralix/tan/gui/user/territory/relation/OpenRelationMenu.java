package org.leralix.tan.gui.user.territory.relation;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.war.WarMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.war.War;

import java.util.ArrayList;
import java.util.List;

public class OpenRelationMenu extends IteratorGUI {

    private final TerritoryData territoryData;
    private final TownRelation relation;

    public OpenRelationMenu(Player player, TerritoryData territoryData, TownRelation relation) {
        super(player, Lang.HEADER_RELATION_WITH.get(player, relation.getName(PlayerDataStorage.getInstance().get(player).getLang())), 6);
        this.territoryData = territoryData;
        this.relation = relation;
        open();
    }

    @Override
    public void open() {

        iterator(getTerritories(), p -> new OpenDiplomacyMenu(player, territoryData));

        gui.setItem(6, 4, getRemoveTerritoryButton());
        gui.setItem(6, 5, getAddTerritoryButton());

        gui.open(player);
    }

    private GuiItem getRemoveTerritoryButton() {
        return iconManager.get(IconKey.GUI_REMOVE_TERRITORY_ICON)
                .setName(Lang.GUI_TOWN_RELATION_REMOVE_TOWN.get(tanPlayer))
                .setAction(
                        action -> {
                            if (!territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.MANAGE_TOWN_RELATION)) {
                                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                                return;
                            }
                            new RemoveRelationMenu(player, territoryData, relation);
                        })
                .asGuiItem(player);
    }

    private GuiItem getAddTerritoryButton() {
        return iconManager.get(IconKey.GUI_ADD_TERRITORY_ICON)
                .setName(Lang.GUI_TOWN_RELATION_ADD_TOWN.get(tanPlayer))
                .setAction(
                        action -> {
                            if (!territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.MANAGE_TOWN_RELATION)) {
                                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                                return;
                            }
                            new AddRelationMenu(player, territoryData, relation);
                        })
                .asGuiItem(player);
    }

    private List<GuiItem> getTerritories() {
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (String territoryID : territoryData.getRelations().getTerritoriesIDWithRelation(relation)) {

            TerritoryData otherTerritory = TerritoryUtil.getTerritory(territoryID);
            ItemStack icon = otherTerritory.getIconWithInformationAndRelation(territoryData, tanPlayer.getLang());

            if (relation == TownRelation.WAR) {
                ItemMeta meta = icon.getItemMeta();
                assert meta != null;
                List<String> lore = meta.getLore();
                assert lore != null;
                lore.add(Lang.GUI_TOWN_ATTACK_TOWN_DESC1.get(tanPlayer));
                meta.setLore(lore);
                icon.setItemMeta(meta);
            }

            GuiItem townButton = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);

                if (relation == TownRelation.WAR && event.isRightClick()) {
                    WarStorage warStorage = WarStorage.getInstance();
                    if (warStorage.isTerritoryAtWarWith(territoryData, otherTerritory)) {
                        TanChatUtils.message(player, Lang.GUI_TOWN_ATTACK_ALREADY_ATTACKING.get(tanPlayer));
                        SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                        return;
                    }

                    territoryData.broadcastMessageWithSound(Lang.GUI_WAR_DECLARED.get(territoryData.getColoredName(), otherTerritory.getColoredName()), SoundEnum.WAR);
                    otherTerritory.broadcastMessageWithSound(Lang.GUI_WAR_DECLARED.get(territoryData.getColoredName(), otherTerritory.getColoredName()), SoundEnum.WAR);
                    War newWar = warStorage.newWar(territoryData, otherTerritory);
                    new WarMenu(player, territoryData, newWar);
                }
            });
            guiItems.add(townButton);
        }
        return guiItems;
    }
}
