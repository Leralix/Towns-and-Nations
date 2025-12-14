package org.leralix.tan.gui.user.property;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.user.territory.CommonChunkSettingsMenu;
import org.leralix.tan.gui.user.territory.OpenRankListForChunkPermission;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.PermissionManager;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.constants.SpecificChunkConfig;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.function.Consumer;

public class PropertyChunkSettingsMenu extends CommonChunkSettingsMenu {

    public PropertyChunkSettingsMenu(Player player, PropertyData propertyData, BasicGui returnGui) {
        super(player, propertyData.getPermissionManager(), returnGui);
        open();
    }

    @Override
    protected SpecificChunkConfig getSpecificChunkConfig( ChunkPermissionType type) {
        return Constants.getChunkPermissionConfig().getPropertiesPermission(type);
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
                    //Owner may not have a town
                    TownData townData = tanPlayer.getTown();
                    if (townData == null) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_TOWN);
                        return;
                    }
                    new OpenRankListForChunkPermission(player, townData, type, this);
                } else {
                    new BrowsePlayerWithPermissionMenu(player, permissionManager, type, this);
                }
            }
        };
    }

}
