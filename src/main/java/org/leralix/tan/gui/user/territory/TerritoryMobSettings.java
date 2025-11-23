package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ClaimedChunkSettings;
import org.leralix.tan.dataclass.UpgradeStatus;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.MobChunkSpawnEnum;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.MobChunkSpawnStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.ADD;
import static org.leralix.lib.data.SoundEnum.GOOD;

public class TerritoryMobSettings extends IteratorGUI {

    private final TerritoryData territoryData;

    public TerritoryMobSettings(Player player, TerritoryData territoryData) {
        super(player, Lang.HEADER_MOB_SETTINGS, 6);
        this.territoryData = territoryData;
        open();
    }

    @Override
    public void open() {
        iterator(getMobs(), p -> new ChunkSettingsMenu(player, territoryData));
        gui.open(player);
    }

    private List<GuiItem> getMobs() {

        ArrayList<GuiItem> guiLists = new ArrayList<>();
        ClaimedChunkSettings chunkSettings = territoryData.getChunkSettings();
        Collection<MobChunkSpawnEnum> mobCollection = MobChunkSpawnStorage.getMobSpawnStorage().values();

        for (MobChunkSpawnEnum mobEnum : mobCollection) {

            UpgradeStatus upgradeStatus = chunkSettings.getSpawnControl(mobEnum);
            int cost = MobChunkSpawnStorage.getMobSpawnCost(mobEnum);
            List<FilledLang> status = generateDescription(upgradeStatus, cost);

            boolean upgradeBought = upgradeStatus.isUnlocked();

            guiLists.add(iconManager.get(mobEnum.getIconKey())
                    .setName(mobEnum.name())
                    .setDescription(status)
                    .setClickToAcceptMessage(upgradeBought ? Lang.GUI_GENERIC_CLICK_TO_SWITCH : Lang.GUI_GENERIC_CLICK_TO_UPGRADE)
                    .setAction(event -> {
                        event.setCancelled(true);
                        if (!territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.MANAGE_MOB_SPAWN)) {
                            TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                            return;
                        }
                        if (upgradeStatus.isUnlocked()) {
                            upgradeStatus.setActivated(!upgradeStatus.canSpawn());
                            SoundUtil.playSound(player, ADD);
                        } else {
                            if (territoryData.getBalance() < cost) {
                                TanChatUtils.message(player, Lang.TERRITORY_NOT_ENOUGH_MONEY.get(tanPlayer, territoryData.getColoredName(), Double.toString(cost - territoryData.getBalance())));
                                return;
                            }
                            territoryData.removeFromBalance(cost);
                            SoundUtil.playSound(player, GOOD);
                            upgradeStatus.setUnlocked(true);
                        }
                        open();
                    })
                    .asGuiItem(player, langType)
            );
        }
        return guiLists;
    }

    private @NotNull List<FilledLang> generateDescription(UpgradeStatus upgradeStatus, int cost) {
        List<FilledLang> status = new ArrayList<>();
        if (upgradeStatus.isUnlocked()) {
            if (upgradeStatus.canSpawn()) {
                status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_ACTIVATED.get());
            } else {
                status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_DEACTIVATED.get());
            }
        } else {
            status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_LOCKED.get());
            status.add(Lang.GUI_TOWN_CHUNK_MOB_SETTINGS_STATUS_LOCKED2.get(Integer.toString(cost)));
        }
        return status;
    }
}
