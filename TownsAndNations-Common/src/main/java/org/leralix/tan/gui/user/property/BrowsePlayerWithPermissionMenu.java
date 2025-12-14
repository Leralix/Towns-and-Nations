package org.leralix.tan.gui.user.property;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.PermissionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BrowsePlayerWithPermissionMenu extends IteratorGUI {


    private final PermissionManager permissionManager;
    private final ChunkPermissionType chunkPermission;
    private final BasicGui returnMenu;

    public BrowsePlayerWithPermissionMenu(Player player, PermissionManager permissionManager, ChunkPermissionType permission, BasicGui returnMenu) {
        super(player, permission.getLabel(), 3);
        this.permissionManager = permissionManager;
        this.chunkPermission = permission;
        this.returnMenu = returnMenu;

        open();
    }

    @Override
    public void open() {
        iterator(getAuthorizedPlayers(), p -> returnMenu.open());

        gui.setItem(3, 5, getAddPlayerButton());

        gui.open(player);
    }

    private GuiItem getAddPlayerButton() {
        return iconManager.get(IconKey.ADD_PLAYER_ICON)
                .setName(Lang.GUI_GENERIC_ADD_BUTTON.get(tanPlayer))
                .setAction(event -> {
                            event.setCancelled(true);
                            new AddPlayerWithPermissionMenu(
                                    player,
                                    permissionManager,
                                    chunkPermission,
                                    this
                            );
                        }
                )
                .asGuiItem(player, langType);

    }

    private List<GuiItem> getAuthorizedPlayers() {
        List<GuiItem> guiItems = new ArrayList<>();

        for (String authorizedPlayerID : permissionManager.get(chunkPermission).getAuthorizedPlayers()) {
            OfflinePlayer authorizedPlayer = Bukkit.getOfflinePlayer(UUID.fromString(authorizedPlayerID));

            guiItems.add(
                    iconManager.get(authorizedPlayer)
                            .setName(authorizedPlayer.getName())
                            .setDescription(
                                    Lang.GUI_TOWN_MEMBER_DESC3.get()
                            )
                            .setAction(action -> {
                                action.setCancelled(true);

                                if (action.isRightClick()) {
                                    permissionManager.get(chunkPermission).removeSpecificPlayerPermission(authorizedPlayerID);
                                    open();
                                }
                            })
                            .asGuiItem(player, langType)
            );
        }
        return guiItems;
    }

}

