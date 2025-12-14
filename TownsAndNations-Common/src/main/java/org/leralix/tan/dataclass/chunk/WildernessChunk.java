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
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.ClaimBlacklistStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

public class WildernessChunk extends ClaimedChunk2 {

    public WildernessChunk(int x, int z, String worldUUID) {
        super(x, z, worldUUID, "wilderness");
    }

    public WildernessChunk(Chunk chunk) {
        super(chunk, "wilderness");
    }

    @Override
    protected boolean canPlayerDoInternal(Player player, ChunkPermissionType permissionType, Location location) {

        World world = location.getWorld();

        if(Constants.getWildernessRules().canPlayerDoInWilderness(world, permissionType)){
            return true;
        }
        TanChatUtils.message(player, Lang.WILDERNESS_NO_PERMISSION.getDefault());
        return false;
    }

    @Override
    public void unclaimChunk(Player player) {
        //No need to unclaim wilderness chunks
    }

    @Override
    public void playerEnterClaimedArea(Player player, boolean displayTerritoryColor) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Lang.WILDERNESS.get(player)));
    }

    @Override
    public String getName() {
        return Lang.WILDERNESS.getDefault();
    }

    @Override
    public boolean canEntitySpawn(EntityType entityType) {
        return true;
    }

    @Override
    public TextComponent getMapIcon(LangType langType) {

        if (ClaimBlacklistStorage.cannotBeClaimed(this)) {
            TextComponent textComponent = new TextComponent("✖");
            textComponent.setColor(ChatColor.RED);
            textComponent.setHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new Text("x : " + super.getMiddleX() + " z : " + super.getMiddleZ() + "\n" +
                            Lang.WILDERNESS.get(langType) + "\n" +
                            Lang.CHUNK_IS_BLACKLISTED.get(langType))));
            return textComponent;
        }

        TextComponent textComponent = new TextComponent("⬜");
        textComponent.setColor(ChatColor.WHITE);
        textComponent.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new Text("x : " + super.getMiddleX() + " z : " + super.getMiddleZ() + "\n" +
                        Lang.WILDERNESS.get(langType) + "\n" +
                        Lang.LEFT_CLICK_TO_CLAIM.get(langType))));
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
    public boolean canMobGrief() {
        return true;
    }

    @Override
    public ChunkType getType() {
        return ChunkType.WILDERNESS;
    }

    @Override
    public void notifyUpdate() {
        // Wilderness chunks do not need to notify updates
    }
}
