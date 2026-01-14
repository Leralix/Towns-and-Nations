package org.leralix.tan.gui.user.territory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.storage.PermissionManager;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.constants.SpecificChunkConfig;

import java.util.function.Consumer;

public class TerritoryChunkSettingsMenu extends CommonChunkSettingsMenu {

    private final TerritoryData territoryData;

    public TerritoryChunkSettingsMenu(Player player, TerritoryData territoryData, BasicGui returnGui) {
        super(player, territoryData.getChunkSettings().getChunkPermissions(), returnGui);
        this.territoryData = territoryData;
        open();
    }

    @Override
    protected SpecificChunkConfig getSpecificChunkConfig(ChunkPermissionType type) {
        if (territoryData instanceof TownData) {
            return Constants.getChunkPermissionConfig().getTownPermission(type);
        }
        return Constants.getChunkPermissionConfig().getRegionPermission(type);
    }

    @Override
    protected Consumer<InventoryClickEvent> getAction(ChunkPermissionType type, PermissionManager permissionManager) {
        return event -> {
            event.setCancelled(true);
            if (event.isLeftClick()) {
                permissionManager.nextPermission(type);
                open();
            } else if (event.isRightClick()) {
                if (event.isShiftClick()) {
                    new OpenRankListForChunkPermission(player, territoryData, type, this);
                } else {
                    new OpenPlayerListForChunkPermission(player, territoryData, type, this);
                }
            }
        };
    }


}
