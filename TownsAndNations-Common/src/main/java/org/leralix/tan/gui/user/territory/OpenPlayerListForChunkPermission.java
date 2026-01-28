package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.permission.ChunkPermissionType;
import org.leralix.tan.data.territory.permission.ClaimedChunkSettings;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.property.AddPlayerWithPermissionMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class OpenPlayerListForChunkPermission extends IteratorGUI {

    private final TerritoryData territoryData;
    private final ChunkPermissionType chunkPermissionType;
    private final ClaimedChunkSettings chunkPermission;
    private final BasicGui backMenu;

    public OpenPlayerListForChunkPermission(Player player, TerritoryData territoryData, ChunkPermissionType type, BasicGui backMenu) {
        super(player, type.getLabel(), 3);
        this.territoryData = territoryData;
        this.chunkPermissionType = type;
        this.chunkPermission = territoryData.getChunkSettings();
        this.backMenu = backMenu;
        open();
    }

    @Override
    public void open() {
        iterator(getAuthorizedPlayer(), p -> backMenu.open());

        gui.setItem(3, 4, getAddButton());

        gui.open(player);
    }

    private @NotNull GuiItem getAddButton() {
        return iconManager.get(IconKey.ADD_NEW_PLAYER_PERMISSION_ICON)
                .setName(Lang.GUI_GENERIC_ADD_BUTTON.get(langType))
                .setAction(action -> {
                    if (!territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.MANAGE_CLAIM_SETTINGS)) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
                        return;
                    }
                    new AddPlayerWithPermissionMenu(player, chunkPermission.getChunkPermissions(), chunkPermissionType, this);
                })
                .asGuiItem(player, langType);
    }

    private List<GuiItem> getAuthorizedPlayer() {
        List<GuiItem> guiItems = new ArrayList<>();

        for (String authorizedPlayerID : chunkPermission.getChunkPermissions().get(chunkPermissionType).getAuthorizedPlayers()) {
            OfflinePlayer authorizedPlayer = Bukkit.getOfflinePlayer(UUID.fromString(authorizedPlayerID));

            guiItems.add(iconManager.get(authorizedPlayer)
                    .setName(authorizedPlayer.getName())
                    .setDescription(
                            Lang.GUI_TOWN_MEMBER_DESC3.get()
                    )
                    .setAction(action -> {
                        action.setCancelled(true);
                        if (!territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.MANAGE_CLAIM_SETTINGS)) {
                            TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                            return;
                        }
                        if (action.isRightClick()) {
                            chunkPermission.getChunkPermissions().get(chunkPermissionType).removeSpecificPlayerPermission(authorizedPlayerID);
                            open();
                        }
                    })
                    .asGuiItem(player, langType)
            );
        }
        return guiItems;
    }
}
