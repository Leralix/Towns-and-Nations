package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.RegionDeletednternalEvent;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.service.requirements.LeaderRequirement;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import static org.leralix.lib.data.SoundEnum.GOOD;
import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class RegionSettingsMenu extends SettingsMenus {

    private final RegionData regionData;

    public RegionSettingsMenu(Player player, RegionData regionData) {
        super(player, Lang.HEADER_SETTINGS, regionData, 3);
        this.regionData = regionData;
        open();
    }

    @Override
    public void open() {
        gui.setItem(1, 5, getTerritoryInfo());
        gui.getFiller().fillTop(GuiUtil.getUnnamedItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE));

        gui.setItem(2, 2, getRenameButton());
        gui.setItem(2, 3, getChangeDescriptionButton());
        gui.setItem(2, 4, getChangeColorButton());

        gui.setItem(2, 6, getChangeOwnershipButton());
        gui.setItem(2, 7, getDeleteButton());

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new RegionMenu(player, regionData)));

        gui.open(player);
    }

    private @NotNull GuiItem getChangeOwnershipButton() {
        return iconManager.get(IconKey.REGION_CHANGE_OWNERSHIP_ICON)
                .setName(Lang.GUI_REGION_CHANGE_CAPITAL.get(tanPlayer))
                .setDescription(
                        Lang.GUI_REGION_CHANGE_CAPITAL_DESC1.get(regionData.getCapital().getName())
                )
                .setClickToAcceptMessage(Lang.GUI_REGION_CHANGE_CAPITAL_DESC2)
                .setAction(event -> {
                    event.setCancelled(true);
                    if (!regionData.isLeader(tanPlayer)) {
                        TanChatUtils.message(player, Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get(tanPlayer));
                        return;
                    }
                    new RegionChangeOwnership(player, regionData);
                })
                .asGuiItem(player, langType);
    }

    private GuiItem getDeleteButton() {
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
                        TanChatUtils.message(player, Lang.CANNOT_DELETE_TERRITORY_IF_CAPITAL.get(tanPlayer, regionData.getOverlord().get().getBaseColoredName()));
                        return;
                    }

                    if(!WarStorage.getInstance().getWarsOfTerritory(territoryData).isEmpty()){
                        TanChatUtils.message(player, Lang.CANNOT_DELETE_TERRITORY_IF_AT_WAR.get(langType));
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
