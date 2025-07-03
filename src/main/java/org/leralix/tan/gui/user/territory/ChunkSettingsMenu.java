package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.DynamicLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.GuiUtil;
import org.leralix.tan.utils.TanChatUtils;

public class ChunkSettingsMenu extends BasicGui {

    private final TerritoryData territoryData;

    public ChunkSettingsMenu(Player player, TerritoryData territoryData){
        super(player, Lang.HEADER_TOWN_MENU.get(player, territoryData.getName()), 3);
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
                .setDescription(Lang.GUI_GENERIC_CLICK_TO_OPEN.get(tanPlayer))
                .setAction(event -> new TerritoryChunkSettingsMenu(player, territoryData))
                .asGuiItem(player);
    }

    private GuiItem getChunkGeneralSettings(){
        return iconManager.get(IconKey.GENERAL_SETTINGS_ICON)
                .setName(Lang.CHUNK_GENERAL_SETTINGS.get(tanPlayer))
                .setDescription(Lang.GUI_GENERIC_CLICK_TO_OPEN.get(tanPlayer))
                .setAction(event -> PlayerGUI.openChunkGeneralSettings(player, territoryData))
                .asGuiItem(player);
    }

    private GuiItem getChunkMobSpawnSettings(){
        return iconManager.get(IconKey.MOBS_SPAWN_SETTINGS_ICON)
                .setName(Lang.GUI_TOWN_CHUNK_MOB.get(tanPlayer))
                .setDescription(Lang.GUI_GENERIC_CLICK_TO_OPEN.get(tanPlayer))
                .setAction(event -> {
                    if (territoryData instanceof TownData townData) {
                        if (townData.getLevel().getBenefitsLevel("UNLOCK_MOB_BAN") >= 1)
                            PlayerGUI.openTownChunkMobSettings(player, 0);
                        else {
                            player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_LEVEL.get(tanPlayer, DynamicLang.get(tanPlayer.getLang(), "UNLOCK_MOB_BAN")));
                            SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                        }
                    }
                })
                .asGuiItem(player);
    }
}
