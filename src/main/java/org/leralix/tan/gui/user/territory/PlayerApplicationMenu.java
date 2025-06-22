package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.newsletter.storage.NewsletterStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.GuiUtil;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.TanChatUtils;

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
                Material.LIME_STAINED_GLASS_PANE
        );
        gui.open(player);
    }

    private List<GuiItem> getApplicationList() {
        List<GuiItem> guiItems = new ArrayList<>();
        for (String playerUUID: townData.getPlayerJoinRequestSet()) {

            OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            ITanPlayer playerIterateData = PlayerDataStorage.getInstance().get(playerUUID);

            ItemStack playerHead = HeadUtils.getPlayerHead(playerIterate,
                    Lang.GUI_PLAYER_ASK_JOIN_PROFILE_DESC2.get(ITanPlayer),
                    Lang.GUI_PLAYER_ASK_JOIN_PROFILE_DESC3.get(ITanPlayer));

            GuiItem playerButton = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                if(event.isLeftClick()){
                    if(!townData.doesPlayerHavePermission(ITanPlayer, RolePermission.INVITE_PLAYER)){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(ITanPlayer));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    if(townData.isFull()){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.INVITATION_TOWN_FULL.get(ITanPlayer));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    townData.addPlayer(playerIterateData);
                }
                else if(event.isRightClick()){
                    if(!townData.doesPlayerHavePermission(ITanPlayer, RolePermission.KICK_PLAYER)){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(ITanPlayer));
                        return;
                    }
                    townData.removePlayerJoinRequest(playerIterateData.getID());
                }
                NewsletterStorage.removePlayerJoinRequest(playerIterateData, townData);
                open();
            });
            guiItems.add(playerButton);
        }
        return guiItems;
    }

}
