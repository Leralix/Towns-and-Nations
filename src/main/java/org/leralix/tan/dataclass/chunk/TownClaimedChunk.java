package org.leralix.tan.dataclass.chunk;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.territory.ChunkUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.war.legacy.CurrentAttack;

public class TownClaimedChunk extends TerritoryChunk {
    public TownClaimedChunk(Chunk chunk, String owner) {
        super(chunk, owner);
    }

    public TownClaimedChunk(int x, int z, String worldUUID, String ownerID) {
        super(x, z, worldUUID, ownerID);
    }

    public String getName() {
        return getTown().getName();
    }

    public TownData getTown() {
        return TownDataStorage.getInstance().get(ownerID);
    }

    @Override
    protected boolean canPlayerDoInternal(Player player, ChunkPermissionType permissionType, Location location) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

        //Location is in a property and players owns or rent it
        TownData ownerTown = getTown();
        PropertyData property = ownerTown.getProperty(location);
        if (property != null) {
            if (property.isPlayerAllowed(permissionType, tanPlayer)) {
                return true;
            } else {
                TanChatUtils.message(player, property.getDenyMessage(tanPlayer.getLang()));
                return false;
            }
        }

        //Player is at war with the town
        for (CurrentAttack currentAttacks : ownerTown.getCurrentAttacks()) {
            if (currentAttacks.containsPlayer(tanPlayer))
                return true;
        }

        ChunkPermission chunkPermission = ownerTown.getPermission(permissionType);
        if (chunkPermission.isAllowed(ownerTown, tanPlayer))
            return true;

        playerCantPerformAction(player);
        return false;
    }


    public void unclaimChunk(Player player) {
        ITanPlayer playerStat = PlayerDataStorage.getInstance().get(player);
        LangType langType = playerStat.getLang();
        TownData playerTown = playerStat.getTown();

        if (playerTown == null) {
            TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(langType));
            return;
        }

        if (!playerTown.doesPlayerHavePermission(playerStat, RolePermission.UNCLAIM_CHUNK)) {
            TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(langType), SoundEnum.NOT_ALLOWED);
            return;
        }


        if (!getOwner().equals(playerTown)) {
            TanChatUtils.message(player, Lang.UNCLAIMED_CHUNK_NOT_RIGHT_TOWN.get(langType, getOwner().getName()));
            return;
        }

        for (PropertyData propertyData : getTown().getProperties()) {
            if (propertyData.isInChunk(this)) {
                TanChatUtils.message(player, Lang.PROPERTY_IN_CHUNK.get(langType, propertyData.getName()));
                return;
            }
        }

        if(ChunkUtil.chunkContainsBuildings(this, playerTown)){
            TanChatUtils.message(player, Lang.BUILDINGS_OR_CAPITAL_IN_CHUNK.get(langType));
            return;
        }

        NewClaimedChunkStorage.getInstance().unclaimChunkAndUpdate(this);
        TanChatUtils.message(player, Lang.UNCLAIMED_CHUNK_SUCCESS_TOWN.get(langType, Integer.toString(playerTown.getNumberOfClaimedChunk()), Integer.toString(playerTown.getLevel().getChunkCap())));

    }

    public void playerEnterClaimedArea(Player player, boolean displayTerritoryColor) {
        TownData townTo = getTown();

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

        TextComponent name = displayTerritoryColor ? townTo.getCustomColoredName() : new TextComponent(townTo.getBaseColoredName());
        String message = Lang.PLAYER_ENTER_TERRITORY_CHUNK.get(tanPlayer.getPlayer(), name.toLegacyText());
        player.sendTitle("", message, 5, 40, 20);

        TextComponent textComponent = new TextComponent(townTo.getDescription());
        textComponent.setColor(ChatColor.GRAY);
        textComponent.setItalic(true);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textComponent);


        TownData playerTown = tanPlayer.getTown();
        if (playerTown == null) {
            return;
        }
        TownRelation relation = playerTown.getRelationWith(townTo);

        if (relation == TownRelation.WAR && Constants.notifyWhenEnemyEnterTerritory()) {
            TanChatUtils.message(player, Lang.CHUNK_ENTER_TOWN_AT_WAR.get(tanPlayer.getLang()), SoundEnum.BAD);
            townTo.broadcastMessageWithSound(Lang.CHUNK_INTRUSION_ALERT.get(TownDataStorage.getInstance().get(player).getName(), player.getName()), SoundEnum.BAD);
        }
    }

    @Override
    public boolean canEntitySpawn(EntityType entityType) {
        return getTown().getChunkSettings().getSpawnControl(entityType.toString()).canSpawn();
    }

    @Override
    public boolean canTerritoryClaim(TerritoryData territoryData) {
        return territoryData.canConquerChunk(this);
    }

    @Override
    public boolean isClaimed() {
        return true;
    }

    @Override
    public ChunkType getType() {
        return ChunkType.TOWN;
    }

    @Override
    public void notifyUpdate() {
        if(!Constants.allowNonAdjacentChunksForTown()){
            ChunkUtil.unclaimIfNoLongerSupplied(this);
        }
    }
}
