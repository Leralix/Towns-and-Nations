package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.permission.RelationPermission;
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
            RelationPermission permission = territoryData.getPermission(type).getOverallPermission();

            GuiItem item = iconManager.get(type.getIconKey())
                    .setName(type.getName().get(tanPlayer))
                    .setDescription(Lang.GUI_TOWN_CLAIM_SETTINGS_DESC1.get(tanPlayer, permission.getColoredName(langType)))
                    .setClickToAcceptMessage(
                            Lang.GUI_GENERIC_CLICK_TO_MODIFY,
                            Lang.GUI_RIGHT_CLICK_TO_ADD_SPECIFIC_PLAYER
                    )
                    .setAction(event -> {
                        event.setCancelled(true);
                        if (event.isLeftClick()) {
                            territoryData.nextPermission(type);
                            open();
                        } else if (event.isRightClick()) {
                            new OpenPlayerListForChunkPermission(player, territoryData, type, this);
                        }
                    }).asGuiItem(player);

            guiItems.add(item);
        }
        return guiItems;
    }


}
