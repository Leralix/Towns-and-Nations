package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.permission.RelationPermission;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public class PlayerChunkSettingsMenu extends IteratorGUI {

    private final TerritoryData territoryData;

    public PlayerChunkSettingsMenu(Player player, TerritoryData territoryData){
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
            ItemStack icon = type.getIcon(permission, playerData.getLang());
            GuiItem guiItem = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);
                if (!territoryData.doesPlayerHavePermission(player, RolePermission.MANAGE_CLAIM_SETTINGS)) {
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                    return;
                }
                if (event.isLeftClick()) {
                    territoryData.nextPermission(type);
                    open();
                } else if (event.isRightClick()) {
                    PlayerGUI.openPlayerListForChunkPermission(player, territoryData, type, 0);
                }
            });
            guiItems.add(guiItem);
        }
        return guiItems;
    }


}
