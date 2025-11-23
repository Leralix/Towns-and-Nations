package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.admin.AdminManageRegion;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.file.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.leralix.lib.data.SoundEnum.GOOD;

public class RegionChangeOwnership extends IteratorGUI {

    private final RegionData regionData;

    public RegionChangeOwnership(Player player, RegionData regionData){
        super(player, Lang.HEADER_CHANGE_OWNERSHIP, 6);
        this.regionData = regionData;
        open();
    }

    @Override
    public void open() {

        iterator(getCandidates(), p -> new AdminManageRegion(player, regionData));
        gui.open(player);
    }

    private List<GuiItem> getCandidates() {
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (String playerID : regionData.getPlayerIDList()) {

            ITanPlayer iterateTanPlayer = PlayerDataStorage.getInstance().get(playerID);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));
            guiItems.add(
                    iconManager.get(offlinePlayer)
                            .setName(offlinePlayer.getName())
                            .setAction(action -> {
                                action.setCancelled(true);

                                new ConfirmMenu(
                                        player,
                                        Lang.GUI_CONFIRM_CHANGE_LEADER.get(iterateTanPlayer.getNameStored()),
                                        () -> {
                                            FileUtil.addLineToHistory(Lang.HISTORY_REGION_CAPITAL_CHANGED.get(player.getName(), regionData.getCapital().getName(), tanPlayer.getTown().getName()));
                                            regionData.setLeaderID(iterateTanPlayer.getID());

                                            regionData.broadcastMessageWithSound(Lang.GUI_REGION_SETTINGS_REGION_CHANGE_LEADER_BROADCAST.get(iterateTanPlayer.getNameStored()), GOOD);

                                            if (!regionData.getCapital().getID().equals(iterateTanPlayer.getTown().getID())) {
                                                regionData.broadCastMessage(Lang.GUI_REGION_SETTINGS_REGION_CHANGE_CAPITAL_BROADCAST.get(iterateTanPlayer.getTown().getName()));
                                                regionData.setCapital(iterateTanPlayer.getTownId());
                                            }
                                            new RegionSettingsMenu(player, regionData);
                                        },
                                        this::open
                                );
                            })
                            .asGuiItem(player, langType)
            );

        }
        return guiItems;
    }
}
