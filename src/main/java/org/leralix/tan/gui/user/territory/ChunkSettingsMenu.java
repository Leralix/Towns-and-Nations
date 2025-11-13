package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.service.requirements.BooleanStatRequirement;
import org.leralix.tan.gui.service.requirements.RankPermissionRequirement;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.upgrade.rewards.bool.EnableMobBan;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class ChunkSettingsMenu extends BasicGui {

    private final TerritoryData territoryData;

    public ChunkSettingsMenu(Player player, TerritoryData territoryData){
        super(player, Lang.HEADER_TOWN_MENU.get(territoryData.getName()), 3);
        this.territoryData = territoryData;
        open();
    }

    @Override
    public void open() {

        gui.getFiller().fillTop(GuiUtil.getUnnamedItem(Material.LIME_STAINED_GLASS_PANE));

        gui.setItem(2, 3, getChunkIcon());
        gui.setItem(2, 5, getChunkGeneralSettings());
        gui.setItem(2, 7, getChunkMobSpawnSettings());

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, territoryData::openMainMenu));

        gui.open(player);
    }

    private GuiItem getChunkIcon() {
        return iconManager.get(IconKey.LANDS_PERMISSION_ICON)
                .setName(Lang.GUI_TOWN_CHUNK_PLAYER.get(tanPlayer))
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.MANAGE_CLAIM_SETTINGS))
                .setAction(event -> new TerritoryChunkSettingsMenu(player, territoryData))
                .asGuiItem(player, langType);
    }

    private GuiItem getChunkGeneralSettings(){
        return iconManager.get(IconKey.GENERAL_SETTINGS_ICON)
                .setName(Lang.CHUNK_GENERAL_SETTINGS.get(tanPlayer))
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.MANAGE_CLAIM_SETTINGS))
                .setAction(event -> new TerritoryChunkGeneralSettings(player, territoryData))
                .asGuiItem(player, langType);
    }

    private GuiItem getChunkMobSpawnSettings(){
        return iconManager.get(IconKey.MOBS_SPAWN_SETTINGS_ICON)
                .setName(Lang.GUI_TOWN_CHUNK_MOB.get(tanPlayer))
                .setRequirements(
                        new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.MANAGE_CLAIM_SETTINGS),
                        new BooleanStatRequirement<>(territoryData, EnableMobBan.class)
                )
                .setAction(event -> {
                    boolean canAccess = territoryData.getNewLevel().getStat(EnableMobBan.class).isEnabled();
                    if (canAccess){
                        new TerritoryMobSettings(player, territoryData);
                    }
                    else {
                        TanChatUtils.message(player, Lang.TOWN_NOT_ENOUGH_LEVEL.get(langType, Lang.UNLOCK_MOB_BAN.get(langType)), SoundEnum.NOT_ALLOWED);
                    }

                })
                .asGuiItem(player, langType);
    }
}
