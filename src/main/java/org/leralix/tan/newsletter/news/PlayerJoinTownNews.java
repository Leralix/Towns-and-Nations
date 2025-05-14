package org.leralix.tan.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.newsletter.NewsletterStorage;
import org.leralix.tan.newsletter.NewsletterType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.HeadUtils;

import java.util.UUID;
import java.util.function.Consumer;

import static org.leralix.tan.utils.TanChatUtils.getTANString;

public class PlayerJoinTownNews extends Newsletter {

    String playerID;
    String townID;

    public PlayerJoinTownNews(PlayerData playerData, TownData townData) {
        super();
        playerID = playerData.getID();
        townID = townData.getID();
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.PLAYER_APPLICATION;
    }

    @Override
    public void broadcast(Player player) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(playerID);
        if(playerData == null)
            return;
        TownData townData = TownDataStorage.getInstance().get(townID);
        if(townData == null)
            return;
        player.sendMessage(getTANString() + Lang.PLAYER_JOINED_TOWN_NEWSLETTER.get(playerData.getNameStored(), townData.getBaseColoredName()));
        SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
    }

    @Override
    public GuiItem createGuiItem(Player player, Consumer<Player> onClick) {
        return null;
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        TownData townData = TownDataStorage.getInstance().get(townID);
        if(townData == null)
            return false;
        return townData.doesPlayerHavePermission(player, RolePermission.INVITE_PLAYER);
    }


    public String getPlayerID() {
        return playerID;
    }

    public String getTownID() {
        return townID;
    }
}
