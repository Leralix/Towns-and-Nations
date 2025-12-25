package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.war.NeutralWarMenu;
import org.leralix.tan.gui.user.war.SecondaryWarMenu;
import org.leralix.tan.gui.user.war.WarMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.war.War;
import org.leralix.tan.war.legacy.WarRole;

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
        iterator(getWars(), p -> territoryData.openMainMenu(player));
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
        for(War war : wars) {
            guiItems.add(iconManager.get(war.getIcon())
                .setName(war.getName())
                 .setDescription(
                         Lang.ATTACK_ICON_DESC_1.get(war.getMainAttacker().getColoredName()),
                         Lang.ATTACK_ICON_DESC_2.get(war.getMainDefender().getColoredName())
                 )
                .setAction(event -> {

                    WarRole warRole = war.getTerritoryRole(territoryData);

                    if(warRole.isMain()){
                        new WarMenu(player, territoryData, war);
                    }
                    else if(warRole.isSecondary()) {
                        new SecondaryWarMenu(player, territoryData, war);
                    }
                    else {
                        new NeutralWarMenu(player, territoryData, war);
                    }
                })
                .asGuiItem(player, langType));
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

