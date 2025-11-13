package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.permissions.GeneralChunkSetting;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.leralix.lib.data.SoundEnum.ADD;
import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class TerritoryChunkGeneralSettings extends IteratorGUI {

    private final TerritoryData territoryData;

    public TerritoryChunkGeneralSettings(Player player, TerritoryData territoryData) {
        super(player, Lang.HEADER_CHUNK_GENERAL_SETTINGS, 3);
        this.territoryData = territoryData;
        open();
    }

    @Override
    public void open() {
        iterator(getSettings(), p -> new ChunkSettingsMenu(player, territoryData));

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new ChunkSettingsMenu(player, territoryData)));
        gui.open(player);
    }

    private List<GuiItem> getSettings() {
        List<GuiItem> res = new ArrayList<>();
        Map<GeneralChunkSetting, Boolean> generalSettings = territoryData.getChunkSettings().getChunkSetting();

        for (GeneralChunkSetting generalChunkSetting : GeneralChunkSetting.values()) {

            boolean settingType = generalSettings.getOrDefault(generalChunkSetting, false);

            res.add(generalChunkSetting.getIcon(iconManager, settingType, tanPlayer.getLang())
                    .setAction(action -> {
                        if (!territoryData.doesPlayerHavePermission(player, RolePermission.MANAGE_CLAIM_SETTINGS)) {
                            TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
                            return;
                        }
                        generalSettings.put(generalChunkSetting, !generalSettings.get(generalChunkSetting));
                        SoundUtil.playSound(player, ADD);
                        open();
                    })
                    .asGuiItem(player, langType));
        }
        return res;
    }
}
