package org.leralix.tan.dataclass.chunk;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.storage.ClaimBlacklistStorage;
import org.leralix.tan.storage.WildernessRules;

import java.util.Optional;

public class WildernessChunk extends ClaimedChunk2 {


    public WildernessChunk(Chunk chunk) {
        super(chunk, "wilderness");
    }

    @Override
    protected boolean canPlayerDoInternal(Player player, ChunkPermissionType permissionType, Location location) {

        World world = location.getWorld();

        if(WildernessRules.getInstance().canPlayerDoInWilderness(world, permissionType)){
            return true;
        }
        player.sendMessage(Lang.WILDERNESS_NO_PERMISSION.get());
        return false;
    }

    @Override
    public void unclaimChunk(Player player) {
        //No need to unclaim wilderness chunks
    }

    @Override
    public void playerEnterClaimedArea(Player player, boolean displayTerritoryColor) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Lang.WILDERNESS.get()));
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

        if (ClaimBlacklistStorage.cannotBeClaimed(this)) {
            TextComponent textComponent = new TextComponent("✖");
            textComponent.setColor(ChatColor.RED);
            textComponent.setHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new Text("x : " + super.getMiddleX() + " z : " + super.getMiddleZ() + "\n" +
                            Lang.WILDERNESS.get() + "\n" +
                            Lang.CHUNK_IS_BLACKLISTED.get())));
            return textComponent;
        }

        TextComponent textComponent = new TextComponent("⬜");
        textComponent.setColor(ChatColor.WHITE);
        textComponent.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new Text("x : " + super.getMiddleX() + " z : " + super.getMiddleZ() + "\n" +
                        Lang.WILDERNESS.get() + "\n" +
                        Lang.LEFT_CLICK_TO_CLAIM.get())));
        return textComponent;
    }

    @Override
    public boolean canTerritoryClaim(TerritoryData territoryData) {
        return true;
    }

    @Override
    public boolean isClaimed() {
        return false;
    }

    @Override
    public boolean canExplosionGrief() {
        return true;
    }

    @Override
    public boolean canFireGrief() {
        return true;
    }

    @Override
    public boolean canPVPHappen() {
        return true;
    }

    @Override
    public ChunkType getType() {
        return ChunkType.WILDERNESS;
    }
}
