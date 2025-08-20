package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.cosmetic.PlayerHeadIcon;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.HeadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.leralix.lib.data.SoundEnum.MINOR_GOOD;

public class SelectTerritoryHeadMenu extends IteratorGUI {

    private final TerritoryData territoryData;

    protected SelectTerritoryHeadMenu(Player player, TerritoryData territoryData) {
        super(player, Lang.HEADER_SELECT_ICON, 4);

        this.territoryData = territoryData;

        open();
    }

    @Override
    public void open() {
        iterator(getHeads(), p -> territoryData.openMainMenu(player));
        gui.open(player);
    }

    private List<GuiItem> getHeads() {

        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (String playerID : territoryData.getPlayerIDList()) {

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));
            ItemStack playerHead = HeadUtils.getPlayerHead(offlinePlayer);

            guiItems.add(
                    iconManager
                            .get(playerHead)
                            .setName(offlinePlayer.getName())
                            .setDescription(Lang.GUI_GENERIC_CLICK_TO_SELECT.get(langType))
                            .setAction(action -> {
                                territoryData.setIcon(new PlayerHeadIcon(offlinePlayer.getUniqueId().toString()));
                                SoundUtil.playSound(player, MINOR_GOOD);
                                territoryData.openMainMenu(player);
                            })
                            .asGuiItem(player)
            );
        }

        return guiItems;
    }

}
