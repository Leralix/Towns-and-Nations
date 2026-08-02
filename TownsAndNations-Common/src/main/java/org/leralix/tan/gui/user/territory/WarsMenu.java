package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.war.WarMenuDispatch;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.war.War;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WarsMenu extends IteratorGUI {

    private final Territory territoryData;

    public WarsMenu(Player player, Territory territoryData) {
        super(player, Lang.HEADER_WARS_MENU, 4);
        this.territoryData = territoryData;
        open();
    }

    @Override
    public void open() {
        iterator(getWars(), p -> territoryData.openMainMenu(player, tanPlayer));
        gui.setItem(4, 2, getChunkInfo());
        gui.setItem(4, 4, getAttackButton());
        gui.setItem(4, 5, getDeclareWarButton());
        gui.open(player);
    }

    private @NotNull GuiItem getChunkInfo() {

        List<FilledLang> desc = new ArrayList<>();

        for(Map.Entry<String, Integer> availableClaims : territoryData.getAvailableEnemyClaims().entrySet()){
            String territoryID = availableClaims.getKey();
            Integer quantity = availableClaims.getValue();

            Territory territory = TerritoryUtil.getTerritory(territoryID);
            if(territory == null){
                continue;
            }

            desc.add(Lang.CHUNK_CONQUER_DESC.get(territoryData.getColoredName(), Integer.toString(quantity)));
        }

        if(desc.isEmpty()){
            desc.add(Lang.CHUNK_CONQUER_NO_CURRENT.get());
        }

        return iconManager.get(IconKey.CHUNK_CONQUER_ICON)
                .setName(Lang.CHUNK_CONQUER_INFO.get(langType))
                .setDescription(desc)
                .asGuiItem(player, langType);
    }

    private GuiItem getDeclareWarButton() {
        return iconManager.get(IconKey.DECLARE_WAR_ICON)
                .setName(Lang.DECLARE_WAR_BUTTON.get(langType))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_OPEN)
                .setAction(
                        action -> new DeclareWarMenu(player, territoryData, this)
                )
                .asGuiItem(player, langType);
    }

    public List<GuiItem> getWars() {

        List<GuiItem> guiItems = new ArrayList<>();
        for (War war : TownsAndNations.getPlugin().getWarStorage().getWarsOfTerritory(territoryData)) {
            guiItems.add(
                    war.getIcon()
                    .setAction(event -> WarMenuDispatch.openMenu(player, war, territoryData))
                    .asGuiItem(player, langType)
            );
        }

        return guiItems;
    }

    private @NotNull GuiItem getAttackButton() {
        return iconManager.get(IconKey.ATTACKS_LIST_ICON)
                .setName(Lang.OPEN_ATTACK_BUTTON.get(langType))
                .setAction(p -> new AttackMenu(player, territoryData))
                .asGuiItem(player, langType);
    }

}

