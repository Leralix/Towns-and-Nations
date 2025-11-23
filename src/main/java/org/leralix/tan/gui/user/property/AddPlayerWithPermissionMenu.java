package org.leralix.tan.gui.user.property;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.PermissionManager;
import org.leralix.tan.storage.stored.PlayerDataStorage;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.ADD;

public class AddPlayerWithPermissionMenu extends IteratorGUI {


    private final PermissionManager permissionManager;
    private final ChunkPermissionType chunkPermission;
    private final BrowsePlayerWithPermissionMenu returnMenu;

    public AddPlayerWithPermissionMenu(Player player, PermissionManager permissionManager, ChunkPermissionType chunkPermission, BrowsePlayerWithPermissionMenu browsePlayerWithPermissionMenu) {
        super(player, chunkPermission.getLabel(), 3);

        this.permissionManager = permissionManager;
        this.chunkPermission = chunkPermission;
        this.returnMenu = browsePlayerWithPermissionMenu;

        open();
    }

    @Override
    public void open() {
        iterator(getPlayers(), player -> {
            returnMenu.open();
        });

        gui.open(player);
    }

    private List<GuiItem> getPlayers() {

        List<GuiItem> guiItems = new ArrayList<>();
        for (Player playerToAdd : Bukkit.getOnlinePlayers()) {

            ITanPlayer playerToAddData = PlayerDataStorage.getInstance().get(playerToAdd);
            ChunkPermission permission = permissionManager.get(chunkPermission);
            //Check with town since only town can have territories
            if (permission.isAllowed(tanPlayer.getTown(), playerToAddData))
                continue;

            guiItems.add(iconManager.get(playerToAdd)
                    .setName(playerToAdd.getName())
                    .setDescription(
                            Lang.GUI_GENERIC_ADD_BUTTON.get()
                    )
                    .setAction(action -> {
                        permission.addSpecificPlayerPermission(playerToAdd.getUniqueId().toString());
                        SoundUtil.playSound(player, ADD);
                        open();
                    })
                    .asGuiItem(player, langType)
            );
        }
        return guiItems;
    }
}
