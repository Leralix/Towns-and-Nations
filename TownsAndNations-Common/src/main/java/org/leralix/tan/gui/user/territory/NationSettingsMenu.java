package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.NationData;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
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
        gui.getFiller().fillTop(GuiUtil.getUnnamedItem(Material.PURPLE_STAINED_GLASS_PANE));

        gui.setItem(2, 2, getRenameButton());
        gui.setItem(2, 3, getChangeDescriptionButton());
        gui.setItem(2, 4, getChangeColorButton());

        gui.setItem(2, 5, setBannerButton());

        gui.setItem(2, 8, getDeleteButton());

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new NationMenu(player, nationData)));

        gui.open(player);
    }

    private GuiItem getDeleteButton() {
        return IconManager.getInstance().get(IconKey.REGION_DELETE_REGION_ICON)
                .setName(Lang.GUI_REGION_DELETE.get(tanPlayer))
                .setDescription(
                        Lang.GUI_REGION_DELETE_DESC1.get(nationData.getName()),
                        Lang.GUI_REGION_DELETE_DESC2.get(),
                        Lang.GUI_REGION_DELETE_DESC3.get()
                )
                .setRequirements(
                        new org.leralix.tan.gui.service.requirements.LeaderRequirement(territoryData, tanPlayer)
                )
                .setAction(event -> {
                    event.setCancelled(true);

                    if (nationData.isCapital()) {
                        TanChatUtils.message(player, Lang.CANNOT_DELETE_TERRITORY_IF_CAPITAL.get(tanPlayer, nationData.getOverlord().get().getBaseColoredName()));
                        return;
                    }

                    if(!WarStorage.getInstance().getWarsOfTerritory(territoryData).isEmpty()){
                        TanChatUtils.message(player, Lang.CANNOT_DELETE_TERRITORY_IF_AT_WAR.get(langType), SoundEnum.NOT_ALLOWED);
                        return;
                    }

                    if (!player.hasPermission("tan.base.nation.disband")) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
                        return;
                    }

                    new org.leralix.tan.gui.common.ConfirmMenu(
                            player,
                            Lang.GUI_CONFIRM_DELETE_REGION.get(nationData.getName()),
                            () -> {
                                nationData.delete();
                                SoundUtil.playSound(player, GOOD);
                                new MainMenu(player);
                            },
                            this::open
                    );
                })
                .asGuiItem(player, langType);
    }
}
