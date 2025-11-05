package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class OpenPlayerListForChunkPermission extends IteratorGUI {

    private final TerritoryData territoryData;
    private final ChunkPermissionType chunkPermissionType;
    private final BasicGui backMenu;

    public OpenPlayerListForChunkPermission(Player player, TerritoryData territoryData, ChunkPermissionType type, BasicGui backMenu) {
        super(player, type.getLabel(player), 6);
        this.territoryData = territoryData;
        this.chunkPermissionType = type;
        this.backMenu = backMenu;
        open();
    }

    @Override
    public void open() {
        iterator(getAuthorizedPlayer(), p -> backMenu.open());

        gui.setItem(6, 4, getAddButton());

        gui.open(player);
    }

    private @NotNull GuiItem getAddButton() {

        ItemStack addIcon = HeadUtils.makeSkullB64(Lang.GUI_GENERIC_ADD_BUTTON.get(tanPlayer), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");

        return ItemBuilder.from(addIcon).asGuiItem(event -> {
            event.setCancelled(true);
            if (!territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.MANAGE_CLAIM_SETTINGS)) {
                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
                return;
            }
            new AddPlayerForChunkPermission(player, territoryData, chunkPermissionType, this);
        });
    }

    private List<GuiItem> getAuthorizedPlayer() {
        List<GuiItem> guiItems = new ArrayList<>();

        for (String authorizedPlayerID : territoryData.getPermission(chunkPermissionType).getAuthorizedPlayers()) {
            OfflinePlayer authorizedPlayer = Bukkit.getOfflinePlayer(UUID.fromString(authorizedPlayerID));
            ItemStack icon = HeadUtils.getPlayerHead(authorizedPlayer.getName(), authorizedPlayer,
                    Lang.GUI_TOWN_MEMBER_DESC3.get(tanPlayer));

            GuiItem guiItem = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);
                if (!territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.MANAGE_CLAIM_SETTINGS)) {
                    TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                    return;
                }
                if (event.isRightClick()) {
                    territoryData.getPermission(chunkPermissionType).removeSpecificPlayerPermission(authorizedPlayerID);
                    open();
                }
            });
            guiItems.add(guiItem);
        }
        return guiItems;
    }
}
