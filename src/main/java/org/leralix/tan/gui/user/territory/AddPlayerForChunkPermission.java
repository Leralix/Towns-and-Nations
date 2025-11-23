package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public class AddPlayerForChunkPermission extends IteratorGUI {

    private final TerritoryData territoryData;
    private final ChunkPermissionType type;
    private final BasicGui backMenu;

    public AddPlayerForChunkPermission(Player player, TerritoryData territoryData, ChunkPermissionType type, BasicGui backMenu) {
        super(player, Lang.HEADER_AUTHORIZE_PLAYER, 6);
        this.territoryData = territoryData;
        this.type = type;
        this.backMenu = backMenu;
        open();
    }

    @Override
    public void open() {
        iterator(getPeopleToAuthorized(), p -> backMenu.open());
        gui.open(player);
    }

    private List<GuiItem> getPeopleToAuthorized() {
        ITanPlayer playerStat = PlayerDataStorage.getInstance().get(player.getUniqueId().toString());

        List<GuiItem> guiItems = new ArrayList<>();

        for (Player playerToAdd : Bukkit.getOnlinePlayers()) {

            ITanPlayer playerToAddData = PlayerDataStorage.getInstance().get(playerToAdd);
            if (territoryData.getPermission(type).isAllowed(territoryData, playerToAddData))
                continue;

            guiItems.add(iconManager.get(playerToAdd)
                    .setName(playerToAdd.getName())
                    .setDescription(
                            Lang.GUI_GENERIC_ADD_BUTTON.get()
                    )
                    .setAction(action -> {
                        if (!territoryData.doesPlayerHavePermission(playerStat, RolePermission.MANAGE_CLAIM_SETTINGS)) {
                            TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                            return;
                        }
                        territoryData.getPermission(type).addSpecificPlayerPermission(playerToAdd.getUniqueId().toString());
                        open();
                        SoundUtil.playSound(player, SoundEnum.ADD);
                    })
                    .asGuiItem(player, langType)
            );
        }

       return guiItems;
    }
}
