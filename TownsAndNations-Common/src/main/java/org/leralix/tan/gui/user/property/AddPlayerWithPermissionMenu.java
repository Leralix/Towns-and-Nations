package org.leralix.tan.gui.user.property;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.PermissionManager;
import org.leralix.tan.storage.stored.PlayerDataStorage;

import java.util.ArrayList;
import java.util.List;

public class AddPlayerWithPermissionMenu extends IteratorGUI {


    private final PermissionManager permissionManager;
    private final ChunkPermissionType chunkPermission;
    private final BasicGui returnMenu;

    public AddPlayerWithPermissionMenu(
            Player player,
            PermissionManager permissionManager,
            ChunkPermissionType chunkPermission,
            BasicGui returnMenu
    ) {
        super(player, chunkPermission.getLabel(), 3);

        this.permissionManager = permissionManager;
        this.chunkPermission = chunkPermission;
        this.returnMenu = returnMenu;

        open();
    }

    @Override
    public void open() {
        iterator(getPlayers(), player -> returnMenu.open());

        gui.open(player);
    }

    private List<GuiItem> getPlayers() {

        List<GuiItem> guiItems = new ArrayList<>();
        for (Player playerToAdd : Bukkit.getOnlinePlayers()) {

            ITanPlayer playerToAddData = PlayerDataStorage.getInstance().get(playerToAdd);
            ChunkPermission permission = permissionManager.get(chunkPermission);

            //If player has already been added, do not show
            if(permission.getAuthorizedPlayers().contains(playerToAdd.getUniqueId().toString())) {
                continue;
            }


            boolean doesPlayerAlreadyHavePermission = permission.isAllowed(tanPlayer.getTown(), playerToAddData);

            guiItems.add(iconManager.get(playerToAdd)
                    .setName(playerToAdd.getName())
                    .setDescription(
                            doesPlayerAlreadyHavePermission ?
                                    Lang.GUI_ADD_PLAYER_WITH_PERMISSION_ALREADY_HAS_PERMISSION_DESCRIPTION.get() :
                                    Lang.GUI_ADD_PLAYER_WITH_PERMISSION_DESCRIPTION.get()
                    )
                    .setClickToAcceptMessage(
                            Lang.GUI_GENERIC_ADD_BUTTON
                    )
                    .setAction(action -> {
                        permission.addSpecificPlayerPermission(playerToAdd.getUniqueId().toString());
                        SoundUtil.playSound(player, SoundEnum.ADD);
                        open();
                    })
                    .asGuiItem(player, langType)
            );
        }
        return guiItems;
    }
}
