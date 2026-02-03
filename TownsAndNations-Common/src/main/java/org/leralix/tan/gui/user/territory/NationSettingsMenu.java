package org.leralix.tan.gui.user.territory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.data.territory.NationData;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.NationDeletedInternalEvent;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.service.requirements.LeaderRequirement;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import static org.leralix.lib.data.SoundEnum.GOOD;
import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class NationSettingsMenu extends SettingsMenus {

    private final NationData nationData;

    public NationSettingsMenu(Player player, NationData nationData) {
        super(player, Lang.HEADER_SETTINGS, nationData, 3);
        this.nationData = nationData;
        open();
    }

    @Override
    public void open() {
        gui.setItem(1, 5, getTerritoryInfo());
        gui.getFiller().fillTop(GuiUtil.getUnnamedItem(Material.ORANGE_STAINED_GLASS_PANE));

        gui.setItem(2, 2, getRenameButton());
        gui.setItem(2, 3, getChangeDescriptionButton());
        gui.setItem(2, 4, getChangeColorButton());
        gui.setItem(2, 5, setBannerButton());

        gui.setItem(2, 6, getChangeOwnershipButton());
        gui.setItem(2, 7, getChangeCapitalButton());
        gui.setItem(2, 8, getDeleteButton());

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new NationMenu(player, nationData), langType));

        gui.open(player);
    }

    private @NotNull dev.triumphteam.gui.guis.GuiItem getChangeOwnershipButton() {
        return iconManager.get(IconKey.TERRITORY_CHANGE_OWNER_ICON)
                .setName(Lang.GUI_NATION_SETTINGS_TRANSFER_OWNERSHIP.get(tanPlayer))
                .setRequirements(new LeaderRequirement(territoryData, tanPlayer))
                .setDescription(
                        Lang.GUI_NATION_SETTINGS_TRANSFER_OWNERSHIP_DESC1.get(),
                        Lang.GUI_NATION_SETTINGS_TRANSFER_OWNERSHIP_DESC2.get()
                )
                .setAction(event -> new SelectNewOwnerForNationMenu(player, nationData, this::open))
                .asGuiItem(player, langType);
    }

    private @NotNull dev.triumphteam.gui.guis.GuiItem getChangeCapitalButton() {
        TerritoryData capital = nationData.getCapital();
        String capitalName = capital == null ? Lang.NO_REGION.get(tanPlayer) : capital.getName();

        return iconManager.get(IconKey.NATION_CHANGE_CAPITAL_ICON)
                .setName(Lang.GUI_NATION_CHANGE_CAPITAL.get(tanPlayer))
                .setDescription(
                        Lang.GUI_NATION_CHANGE_CAPITAL_DESC1.get(capitalName),
                        Lang.GUI_NATION_CHANGE_CAPITAL_DESC2.get()
                )
                .setAction(event -> {
                    event.setCancelled(true);
                    if (!nationData.isLeader(tanPlayer)) {
                        TanChatUtils.message(player, Lang.PLAYER_ONLY_LEADER_CAN_PERFORM_ACTION.get(tanPlayer), NOT_ALLOWED);
                        return;
                    }
                    new NationChangeCapitalMenu(player, nationData);
                })
                .asGuiItem(player, langType);
    }

    private @NotNull dev.triumphteam.gui.guis.GuiItem getDeleteButton() {
        return iconManager.get(IconKey.NATION_DELETE_NATION_ICON)
                .setName(Lang.GUI_NATION_DELETE.get(tanPlayer))
                .setDescription(
                        Lang.GUI_NATION_DELETE_DESC1.get(nationData.getName()),
                        Lang.GUI_NATION_DELETE_DESC2.get()
                )
                .setRequirements(new LeaderRequirement(territoryData, tanPlayer))
                .setAction(event -> {
                    event.setCancelled(true);

                    if (!WarStorage.getInstance().getWarsOfTerritory(territoryData).isEmpty()) {
                        TanChatUtils.message(player, Lang.CANNOT_DELETE_TERRITORY_IF_AT_WAR.get(langType), SoundEnum.NOT_ALLOWED);
                        return;
                    }

                    if (!player.hasPermission("tan.base.nation.disband")) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
                        return;
                    }

                    new ConfirmMenu(
                            player,
                            Lang.GUI_CONFIRM_DELETE_NATION.get(nationData.getName()),
                            () -> {
                                FileUtil.addLineToHistory(Lang.NATION_DELETED_NEWSLETTER.get(player.getName(), nationData.getName()));
                                EventManager.getInstance().callEvent(new NationDeletedInternalEvent(nationData, tanPlayer));
                                nationData.delete();
                                player.closeInventory();
                                SoundUtil.playSound(player, GOOD);
                            },
                            this::open
                    );
                })
                .asGuiItem(player, langType);
    }
}
