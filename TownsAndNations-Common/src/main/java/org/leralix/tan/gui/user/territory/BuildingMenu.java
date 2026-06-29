package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.building.Building;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.data.upgrade.rewards.numeric.PropertyCap;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.service.requirements.IndividualRequirement;
import org.leralix.tan.gui.service.requirements.MoneyRequirement;
import org.leralix.tan.gui.service.requirements.RankPermissionRequirement;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.interact.RightClickListener;
import org.leralix.tan.listeners.interact.events.CreateFortEvent;
import org.leralix.tan.listeners.interact.events.property.CreateTerritoryPropertyEvent;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public class BuildingMenu extends IteratorGUI {

    private final BasicGui previousMenu;
    private final Territory territoryData;

    public BuildingMenu(Player player, Territory territoryData, BasicGui previousMenu) {
        super(player, Lang.HEADER_BUILDING_MENU, 4);
        this.territoryData = territoryData;
        this.previousMenu = previousMenu;
        open();
    }


    @Override
    public void open() {
        iterator(getBuildings(), p -> previousMenu.open());

        //For now, only towns can have properties
        if (territoryData instanceof Town townData) {
            gui.setItem(4, 4, getCreatePublicPropertyButton(townData));
        }
        gui.setItem(4, 5, getCreateFortButton());
        gui.open(player);
    }

    private @NotNull GuiItem getCreatePublicPropertyButton(Town townData) {

        List<FilledLang> description = new ArrayList<>();
        description.add(Lang.CREATE_PUBLIC_PROPERTY_COST.get());

        List<IndividualRequirement> requirements = new ArrayList<>();
        requirements.add(townData.getNewLevel().getStat(PropertyCap.class).getRequirement(townData));
        requirements.add(new RankPermissionRequirement(townData, tanPlayer, RolePermission.MANAGE_PROPERTY));


        return iconManager.get(IconKey.PLAYER_PROPERTY_ICON)
                .setName(Lang.CREATE_PUBLIC_PROPERTY_ICON.get(langType))
                .setDescription(description)
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setRequirements(requirements)
                .setAction(action -> {
                    RightClickListener.register(player, langType, new CreateTerritoryPropertyEvent(player, tanPlayer, townData));
                })
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getCreateFortButton() {

        List<FilledLang> desc = new ArrayList<>();
        desc.add(Lang.CREATE_FORT_DESC2.get(Double.toString(Constants.getFortProtectionRadius())));
        desc.add(Lang.CREATE_FORT_DESC3.get(Integer.toString(Constants.getFortCaptureTime())));
        if(Constants.enableFortOutpost()){
            desc.add(Lang.CREATE_FORT_DESC4.get());
        }
        if(Constants.allowFortTeleport()){
            if(Constants.allowFortTeleportDuringWar()){
                desc.add(Lang.CREATE_FORT_DESC_ALLOW_TELEPORTATION.get());
            }
            else {
                desc.add(Lang.CREATE_FORT_DESC_ALLOW_TELEPORTATION_OUTSIDE_WARS.get());
            }
        }
        List<IndividualRequirement> requirements = new ArrayList<>();
        requirements.add(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.MANAGE_WARS));
        requirements.add(new MoneyRequirement(territoryData, Constants.getFortCost()));


        return iconManager.get(IconKey.FORT_BUILDING_ICON)
                .setName(Lang.CREATE_FORT_ICON.get(langType))
                .setDescription(desc)
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setRequirements(requirements)
                .setAction(action -> {
                    TanChatUtils.message(player, Lang.RIGHT_CLICK_TO_PLACE_FORT.get(langType));
                    RightClickListener.register(player, langType, new CreateFortEvent(territoryData, tanPlayer));
                    player.closeInventory();
                })
                .asGuiItem(player, langType);
    }

    private List<GuiItem> getBuildings() {
        List<GuiItem> res = new ArrayList<>();
        for (Building building : territoryData.getBuildings()) {
            res.add(building.getGuiItem(iconManager, player, this, langType));
        }
        return res;
    }
}
