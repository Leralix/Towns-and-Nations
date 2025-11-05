package org.leralix.tan.gui.user.property;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.PermissionManager;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.HeadUtils;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.ADD;

public class AddPlayerWithPermissionMenu extends IteratorGUI {


    private final PermissionManager permissionManager;
    private final ChunkPermissionType chunkPermission;
    private final BrowsePlayerWithPermissionMenu returnMenu;

    public AddPlayerWithPermissionMenu(Player player, PermissionManager permissionManager, ChunkPermissionType chunkPermission, BrowsePlayerWithPermissionMenu browsePlayerWithPermissionMenu) {
        super(player, chunkPermission.getLabel(player), 3);

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

            ITanPlayer playerToAddData = PlayerDataStorage.getInstance().getSync(playerToAdd);
            ChunkPermission permission = permissionManager.get(chunkPermission);
            //Check with town since only town can have territories
            if (permission.isAllowed(tanPlayer.getTownSync(), playerToAddData))
                continue;

            ItemStack icon = HeadUtils.getPlayerHead(playerToAdd.getName(), playerToAdd,
                    Lang.GUI_GENERIC_ADD_BUTTON.get(tanPlayer));

            GuiItem guiItem = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);

                permission.addSpecificPlayerPermission(playerToAdd.getUniqueId().toString());
                SoundUtil.playSound(player, ADD);
                open();
            });
            guiItems.add(guiItem);
        }
        return guiItems;
    }
}
