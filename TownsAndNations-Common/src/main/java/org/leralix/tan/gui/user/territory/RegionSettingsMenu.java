package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.RegionDeletednternalEvent;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.service.requirements.LeaderRequirement;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import static org.leralix.lib.data.SoundEnum.GOOD;
import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class RegionSettingsMenu extends SettingsMenus {

    private final Region regionData;

    public RegionSettingsMenu(Player player, Region regionData, BasicGui returnGUI) {
        super(player, Lang.HEADER_SETTINGS, regionData, 4, returnGUI);
        this.regionData = regionData;
        open();
    }

    @Override
    public void open() {
        gui.setItem(1, 5, getTerritoryInfo());
        gui.getFiller().fillTop(getUnnamedItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE));

        gui.setItem(2, 2, getRenameButton());
        gui.setItem(2, 3, getChangeDescriptionButton());
        gui.setItem(2, 4, getChangeColorButton());

        gui.setItem(3, 2, setBannerButton());
        if (Constants.allowShareOfTeleportation()) {
            gui.setItem(3, 3, getAuthorizedTeleportationButton());
        }


        gui.setItem(2, 6, getChangeOwnershipButton());
        gui.setItem(2, 7, getChangeCapitalButton());
        gui.setItem(2, 8, getDeleteButton());

        gui.setItem(4, 1, createBackArrow(player, p -> returnGui.open(), langType));

        gui.open(player);
    }

    protected @NotNull GuiItem getChangeOwnershipButton() {
        return iconManager.get(IconKey.TERRITORY_CHANGE_OWNER_ICON)
                .setName(Lang.GUI_TERRITORY_CHANGE_OWNERSHIP.get(tanPlayer))
                .setDescription(Lang.GUI_TERRITORY_CHANGE_OWNERSHIP_DESC1.get(regionData.getCapital().getLeaderData().getNameStored()))
                .setRequirements(new LeaderRequirement(territoryData, tanPlayer))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_MODIFY)
                .setAction(event -> new RegionChangeOwnership(player, regionData, this))
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getChangeCapitalButton() {
        Territory capital = regionData.getCapital();
        String capitalName = capital == null ? Lang.NO_REGION.get(tanPlayer) : capital.getColoredName();

        return iconManager.get(IconKey.TERRITORY_CHANGE_CAPITAL_ICON)
                .setName(Lang.GUI_TERRITORY_CHANGE_CAPITAL.get(tanPlayer))
                .setDescription(Lang.GUI_TERRITORY_CHANGE_CAPITAL_DESC1.get(capitalName))
                .setRequirements(new LeaderRequirement(capital, tanPlayer))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_MODIFY)
                .setAction(event -> new RegionChangeCapitalMenu(player, regionData, this))
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getDeleteButton() {
        return iconManager.get(IconKey.REGION_DELETE_REGION_ICON)
                .setName(Lang.GUI_REGION_DELETE.get(tanPlayer))
                .setDescription(
                        Lang.GUI_REGION_DELETE_DESC1.get(regionData.getName()),
                        Lang.GUI_REGION_DELETE_DESC2.get(),
                        Lang.GUI_REGION_DELETE_DESC3.get()
                )
                .setRequirements(
                        new LeaderRequirement(territoryData, tanPlayer)
                )
                .setAction(event -> {
                    event.setCancelled(true);

                    if (regionData.isCapital()) {
                        TanChatUtils.message(player, Lang.CANNOT_DELETE_TERRITORY_IF_CAPITAL.get(tanPlayer, regionData.getOverlordInternal().get().getColoredName()));
                        return;
                    }

                    if (!TownsAndNations.getPlugin().getWarStorage().getWarsOfTerritory(territoryData).isEmpty()) {
                        TanChatUtils.message(player, Lang.CANNOT_DELETE_TERRITORY_IF_AT_WAR.get(langType), SoundEnum.NOT_ALLOWED);
                        return;
                    }

                    if (!player.hasPermission("tan.base.region.disband")) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
                        return;
                    }

                    new ConfirmMenu(
                            player,
                            Lang.GUI_CONFIRM_DELETE_REGION.get(regionData.getName()),
                            () -> {
                                FileUtil.addLineToHistory(Lang.REGION_DELETED_NEWSLETTER.get(player.getName(), regionData.getName()));

                                EventManager.getInstance().callEvent(new RegionDeletednternalEvent(regionData, tanPlayer));
                                regionData.delete();
                                SoundUtil.playSound(player, GOOD);
                                new MainMenu(player);
                            },
                            this::open
                    );
                })
                .asGuiItem(player, langType);
    }
}
