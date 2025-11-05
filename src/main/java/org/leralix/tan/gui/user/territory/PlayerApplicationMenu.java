package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class PlayerApplicationMenu extends IteratorGUI {

    TownData townData;

    public PlayerApplicationMenu(Player player, TownData townData) {
        super(player, Lang.HEADER_TOWN_APPLICATIONS, 3);
        this.townData = townData;
    }

    @Override
    public void open() {

        GuiUtil.createIterator(gui, getApplicationList(), page, player,
                p -> new TerritoryMemberMenu(player, townData).open(),
                p -> nextPage(),
                p -> previousPage(),
                Material.LIME_STAINED_GLASS_PANE,
                tanPlayer
        );
        gui.open(player);
    }

    private List<GuiItem> getApplicationList() {
        List<GuiItem> guiItems = new ArrayList<>();
        for (String playerUUID: townData.getPlayerJoinRequestSet()) {

            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            ITanPlayer playerIterateData = PlayerDataStorage.getInstance().getSync(playerUUID);

            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate,
                    Lang.GUI_PLAYER_ASK_JOIN_PROFILE_DESC2.get(tanPlayer),
                    Lang.GUI_PLAYER_ASK_JOIN_PROFILE_DESC3.get(tanPlayer));

            GuiItem playerButton = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                if(event.isLeftClick()){
                    if(!townData.doesPlayerHavePermission(tanPlayer, RolePermission.INVITE_PLAYER)){
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
                        return;
                    }
                    if(townData.isFull()){
                        TanChatUtils.message(player, Lang.INVITATION_TOWN_FULL.get(tanPlayer), NOT_ALLOWED);
                        return;
                    }
                    townData.addPlayer(playerIterateData);
                }
                else if(event.isRightClick()){
                    if(!townData.doesPlayerHavePermission(tanPlayer, RolePermission.KICK_PLAYER)){
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                        return;
                    }
                    townData.removePlayerJoinRequest(playerIterateData.getID());
                }
                open();
            });
            guiItems.add(playerButton);
        }
        return guiItems;
    }

}
