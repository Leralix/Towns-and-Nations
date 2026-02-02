package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.war.WarMenuDispatch;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.war.War;

import java.util.ArrayList;
import java.util.List;

public class WarsMenu extends IteratorGUI {

    private final TerritoryData territoryData;

    public WarsMenu(Player player, TerritoryData territoryData) {
        super(player, Lang.HEADER_WARS_MENU, 4);
        this.territoryData = territoryData;
        open();
    }

    @Override
    public void open() {
        iterator(getWars(), p -> territoryData.openMainMenu(player, tanPlayer));
        gui.setItem(4, 4, getAttackButton());
        gui.setItem(4, 5, getDeclareWarButton());
        gui.open(player);
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

        List<War> wars = WarStorage.getInstance().getWarsOfTerritory(territoryData);


        List<GuiItem> guiItems = new ArrayList<>();
        for (War war : wars) {
            guiItems.add(
                    war.getIcon()
                    .setAction(event -> WarMenuDispatch.openMenu(player, war, territoryData))
                    .asGuiItem(player, langType)
            );
        }

        return guiItems;
    }

    private @NotNull GuiItem getAttackButton() {
        return iconManager.get(new ItemStack(Material.BOW))
                .setName(Lang.OPEN_ATTACK_BUTTON.get(langType))
                .setAction(p -> new AttackMenu(player, territoryData))
                .asGuiItem(player, langType);
    }

}

