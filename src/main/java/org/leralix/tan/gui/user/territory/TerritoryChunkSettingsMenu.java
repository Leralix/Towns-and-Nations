package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public class TerritoryChunkSettingsMenu extends IteratorGUI {

    private final TerritoryData territoryData;

    public TerritoryChunkSettingsMenu(Player player, TerritoryData territoryData) {
        super(player, Lang.HEADER_CHUNK_PERMISSION, 4);
        this.territoryData = territoryData;
        open();
    }


    @Override
    public void open() {
        iterator(getChunkPermission(), p -> new ChunkSettingsMenu(player, territoryData), Material.LIME_STAINED_GLASS_PANE);
        gui.open(player);
    }

    private List<GuiItem> getChunkPermission() {
        List<GuiItem> guiItems = new ArrayList<>();
        for (ChunkPermissionType type : ChunkPermissionType.values()) {
            ChunkPermission permission = territoryData.getChunkSettings().getPermission(type);

            GuiItem item = iconManager.get(type.getIconKey())
                    .setName(type.getName().get(tanPlayer))
                    .setDescription(
                            Lang.GUI_TOWN_CLAIM_SETTINGS_DESC1.get(permission.getOverallPermission().getColoredName(langType)),
                            Lang.GUI_TOWN_CLAIM_SETTINGS_DESC_ADDITIONAL_PLAYERS.get(Integer.toString(permission.getAuthorizedPlayers().size())),
                            Lang.GUI_TOWN_CLAIM_SETTINGS_DESC_ADDITIONAL_RANKS.get(Integer.toString(permission.getAuthorizedRanks().size()))
                    )
                    .setClickToAcceptMessage(
                            Lang.GUI_GENERIC_CLICK_TO_MODIFY,
                            Lang.GUI_RIGHT_CLICK_TO_ADD_SPECIFIC_PLAYER,
                            Lang.GUI_SHIFT_RIGHT_CLICK_TO_ADD_SPECIFIC_RANK
                    )
                    .setAction(event -> {
                        event.setCancelled(true);
                        if (event.isLeftClick()) {
                            territoryData.nextPermission(type);
                            open();
                        } else if (event.isRightClick()) {
                            if(event.isShiftClick()){
                                new OpenRankListForChunkPermission(player, territoryData, type, this);
                            }
                            else {
                                new OpenPlayerListForChunkPermission(player, territoryData, type, this);
                            }
                        }
                    }).asGuiItem(player, langType);

            guiItems.add(item);
        }
        return guiItems;
    }


}
