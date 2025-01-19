package org.leralix.tan.newsletter.news;

import org.leralix.lib.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.newsletter.NewsletterScope;
import org.leralix.tan.newsletter.NewsletterType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Newsletter {

    long date;
    List<String> playerMarkAsRead;


    protected Newsletter() {
        this.date = System.currentTimeMillis();
        this.playerMarkAsRead = new ArrayList<>();
    }

    public abstract GuiItem createGuiItem(Player player, Consumer<Player> onClick);

    public abstract boolean shouldShowToPlayer(Player player, NewsletterScope scope);

    public long getDate() {
        return date;
    }

    public abstract NewsletterType getType();

    public void markAsRead(Player player){
        markAsRead(player.getUniqueId().toString());
    }
    public void markAsRead(PlayerData playerData){
        markAsRead(playerData.getID());
    }
    public void markAsRead(String playerID){
        if(playerMarkAsRead == null) playerMarkAsRead = new ArrayList<>();
        playerMarkAsRead.add(playerID);
    }

    public boolean isRead(Player player){
        return isRead(player.getUniqueId().toString());
    }
    public boolean isRead(PlayerData playerData){
        return isRead(playerData.getID());
    }
    public boolean isRead(String playerID){
        if(playerMarkAsRead == null) playerMarkAsRead = new ArrayList<>();
        return playerMarkAsRead.contains(playerID);
    }




}
