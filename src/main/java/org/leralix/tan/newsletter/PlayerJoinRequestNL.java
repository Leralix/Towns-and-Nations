package org.leralix.tan.newsletter;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.TownRolePermission;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.HeadUtils;

import java.util.UUID;

public class PlayerJoinRequestNL extends Newsletter {

    String playerID;
    String townID;

    public PlayerJoinRequestNL(Player player, TownData townData) {
        super();
        playerID = player.getUniqueId().toString();
        townID = townData.getID();
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.PLAYER_TOWN_JOIN_REQUEST;
    }

    @Override
    public GuiItem createGuiItem(Player player) {

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));

        ItemStack itemStack = HeadUtils.createCustomItemStack(Material.PLAYER_HEAD,
                Lang.NEWSLETTER_PLAYER_APPLICATION.get(offlinePlayer.getName()),
                Lang.NEWSLETTER_PLAYER_APPLICATION_DESC1.get(offlinePlayer.getName(), TownDataStorage.get(townID).getColoredName()),
                Lang.NEWSLETTER_PLAYER_APPLICATION_DESC2.get());

        return ItemBuilder.from(itemStack).asGuiItem(event -> {
            PlayerGUI.openTownApplications(player);
            event.setCancelled(true);
        });
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        TownData townData = TownDataStorage.get(townID);
        if(townData == null) {
            return false;
        }
        PlayerData playerData = PlayerDataStorage.get(player);

        return townData.havePlayer(playerData) && playerData.hasPermission(TownRolePermission.INVITE_PLAYER);
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getTownID() {
        return townID;
    }
}
