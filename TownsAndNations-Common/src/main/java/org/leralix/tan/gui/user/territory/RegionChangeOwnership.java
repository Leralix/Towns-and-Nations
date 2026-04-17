package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.file.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.leralix.lib.data.SoundEnum.GOOD;

public class RegionChangeOwnership extends IteratorGUI {

    private final Region regionData;
    private final BasicGui returnGUI;

    public RegionChangeOwnership(Player player, Region regionData, BasicGui returnGUI) {
        super(player, Lang.HEADER_CHANGE_OWNERSHIP, 6);
        this.regionData = regionData;
        this.returnGUI = returnGUI;
        open();
    }

    @Override
    public void open() {
        iterator(getCandidates(), p -> returnGUI.open());
        gui.open(player);
    }

    private List<GuiItem> getCandidates() {
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (UUID playerID : regionData.getPlayerIDList()) {

            ITanPlayer iterateTanPlayer = TownsAndNations.getPlugin().getPlayerDataStorage().get(playerID);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerID);
            guiItems.add(
                    iconManager.get(offlinePlayer)
                            .setName(offlinePlayer.getName())
                            .setAction(action -> {
                                action.setCancelled(true);

                                new ConfirmMenu(
                                        player,
                                        Lang.GUI_CONFIRM_CHANGE_LEADER.get(iterateTanPlayer.getNameStored()),
                                        () -> {
                                            regionData.setLeaderID(iterateTanPlayer.getID());

                                            regionData.broadcastMessageWithSound(Lang.GUI_REGION_SETTINGS_REGION_CHANGE_LEADER_BROADCAST.get(iterateTanPlayer.getNameStored()), GOOD);

                                            if (iterateTanPlayer.getTown() != null && !regionData.getCapital().getID().equals(iterateTanPlayer.getTown().getID())) {
                                                FileUtil.addLineToHistory(Lang.HISTORY_REGION_CAPITAL_CHANGED.get(player.getName(), regionData.getCapital().getName(), iterateTanPlayer.getTown().getName()));
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
