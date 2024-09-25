package org.tan.TownsAndNations.DataClass.newChunkData;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.ChunkPermissionType;

public class WildernessChunk extends ClaimedChunk2 {


    public WildernessChunk(Chunk chunk) {
        super(chunk, "wilderness");
    }

    @Override
    public boolean canPlayerDo(Player player, ChunkPermissionType permissionType, Location location) {
        return true;
    }

    @Override
    public void unclaimChunk(Player player, Chunk chunk) {
    }

    @Override
    public void playerEnterClaimedArea(Player player) {
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean canEntitySpawn(EntityType entityType) {
        return true;
    }

    @Override
    public TextComponent getMapIcon(PlayerData playerData) {
        TextComponent textComponent = new TextComponent(ChatColor.WHITE + "⬜");
        textComponent.setBold(true);
        textComponent.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new Text("x : " + super.getX() + " z : " + super.getZ() + "\n" +
                        Lang.WILDERNESS.get() + "\n" +
                        Lang.LEFT_CLICK_TO_CLAIM.get())));
        return textComponent;
    }

    @Override
    public boolean canPlayerClaim(Player player, ITerritoryData townData) {
        return true;
    }
}
