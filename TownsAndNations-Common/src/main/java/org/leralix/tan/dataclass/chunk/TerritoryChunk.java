package org.leralix.tan.dataclass.chunk;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.enums.permissions.GeneralChunkSetting;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.upgrade.rewards.numeric.ChunkCap;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.territory.ChunkUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.war.fort.Fort;
import org.leralix.tan.war.legacy.CurrentAttack;

import java.util.Optional;

public abstract class TerritoryChunk extends ClaimedChunk2 {

    private String occupierID;

    protected TerritoryChunk(Chunk chunk, String owner) {
        super(chunk, owner);
        this.occupierID = owner;
    }

    protected TerritoryChunk(int x, int z, String worldUUID, String owner) {
        super(x, z, worldUUID, owner);
        this.occupierID = owner;
    }


    @Override
    public TextComponent getMapIcon(LangType langType) {

        TerritoryData owner = getOwner();
        if (owner == null) {
            TextComponent textComponent = new TextComponent("?");
            textComponent.setHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new Text(
                            "x : " + super.getMiddleX() + " z : " + super.getMiddleZ() + "\n" +
                                    Lang.LEFT_CLICK_TO_CLAIM.get(langType)
                    )
            ));
            return textComponent;
        }

        TerritoryData occupier = getOccupier();
        if (occupier == null) {
            occupier = owner;
        }

        TextComponent textComponent;
        String text;
        if(isOccupied()){
            textComponent = new TextComponent("🟧");
            textComponent.setColor(occupier.getChunkColor());
            text = "x : " + super.getMiddleX() + " z : " + super.getMiddleZ() + "\n" +
                    owner.getBaseColoredName() + "\n" +
                    occupier.getBaseColoredName() + "\n" +
                    Lang.LEFT_CLICK_TO_CLAIM.get(langType);
        }
        else {
            textComponent = new TextComponent("⬛");
            textComponent.setColor(owner.getChunkColor());
            text = "x : " + super.getMiddleX() + " z : " + super.getMiddleZ() + "\n" +
                    owner.getBaseColoredName() + "\n" +
                    Lang.LEFT_CLICK_TO_CLAIM.get(langType);
        }



        textComponent.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new Text(text)));
        return textComponent;
    }

    /**
     * Called when a player want to unclaim a chunk
     * Will verify if the player is allowed to unclaim it, and if so, unclaim.
     * @param player the player trying to unclaim this chunk
     */
    public void unclaimChunk(Player player) {

        ITanPlayer playerStat = PlayerDataStorage.getInstance().get(player);
        LangType langType = playerStat.getLang();

        TerritoryData ownerTerritory = getOwner();

        //If owner territory contains the player, regular check
        if(ownerTerritory.isPlayerIn(player)){
            if (!ownerTerritory.doesPlayerHavePermission(playerStat, RolePermission.UNCLAIM_CHUNK)) {
                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(langType), SoundEnum.NOT_ALLOWED);
                return;
            }

            if(ownerTerritory instanceof TownData ownerTown){
                for (PropertyData propertyData : ownerTown.getProperties()) {
                    if (propertyData.isInChunk(this)) {
                        TanChatUtils.message(player, Lang.PROPERTY_IN_CHUNK.get(langType, propertyData.getName()));
                        return;
                    }
                }
            }

            if (ChunkUtil.chunkContainsBuildings(this, ownerTerritory)) {
                TanChatUtils.message(player, Lang.BUILDINGS_OR_CAPITAL_IN_CHUNK.get(langType));
                return;
            }

            if(isOccupied()){
                TanChatUtils.message(player, Lang.CHUNK_OCCUPIED_CANT_UNCLAIM.get(langType));
                return;
            }
            if (Constants.preventOrphanChunks() && !Constants.allowNonAdjacentChunksFor(ownerTerritory)) {
                if (ChunkUtil.doesUnclaimCauseOrphan(this)) {
                    TanChatUtils.message(player, Lang.CANNOT_UNCLAIM_BECAUSE_CREATE_ORPHAN.get(langType));
                    return;
                }
            }

            NewClaimedChunkStorage.getInstance().unclaimChunkAndUpdate(this);

            ChunkCap chunkCap = ownerTerritory.getNewLevel().getStat(ChunkCap.class);
            if(chunkCap.isUnlimited()){
                TanChatUtils.message(player, Lang.CHUNK_UNCLAIMED_SUCCESS_UNLIMITED.get(player, ownerTerritory.getColoredName()));
            }
            else {
                String currentChunks = Integer.toString(ownerTerritory.getNumberOfClaimedChunk());
                String maxChunks = Integer.toString(chunkCap.getMaxAmount());
                TanChatUtils.message(player, Lang.CHUNK_UNCLAIMED_SUCCESS_LIMITED.get(player, ownerTerritory.getColoredName(), currentChunks, maxChunks));
            }
        }
        else {
            // Special case: one of the player's territories can conquer chunks due to a past war.
            for(TerritoryData territoryData : playerStat.getAllTerritoriesPlayerIsIn()){
                if(territoryData.canConquerChunk(this)){

                    if(isOccupied()){
                        TanChatUtils.message(player, Lang.CHUNK_OCCUPIED_CANT_UNCLAIM.get(langType));
                        return;
                    }

                    if(
                            Constants.preventOrphanChunks() && !Constants.allowNonAdjacentChunksFor(ownerTerritory)
                    ) {
                        if (ChunkUtil.doesUnclaimCauseOrphan(this)) {
                            TanChatUtils.message(player, Lang.CANNOT_UNCLAIM_BECAUSE_CREATE_ORPHAN.get(langType));
                            return;
                        }
                    }

                    NewClaimedChunkStorage.getInstance().unclaimChunkAndUpdate(this);
                    TanChatUtils.message(player, Lang.CHUNK_UNCLAIMED_SUCCESS_UNLIMITED.get(langType, ownerTerritory.getColoredName()), SoundEnum.MINOR_GOOD);
                    return;
                }
            }
            // Player is not part of territory
            TanChatUtils.message(player, Lang.PLAYER_NOT_IN_TERRITORY.get(langType, ownerTerritory.getColoredName()));
        }
    }


    public Optional<Fort> getFortProtecting() {
        for (Fort fort : getOccupier().getAllControlledFort()) {
            if (fort.getPosition().getDistance(getMiddleVector2D()) <= Constants.getFortProtectionRadius()) {
                return Optional.of(fort);
            }
        }
        return Optional.empty();
    }


    public String getOccupierID() {
        if(occupierID == null) {
            occupierID = ownerID;
        }
        return occupierID;
    }

    @Override
    public boolean canExplosionGrief() {
        return Constants.getChunkSettings(GeneralChunkSetting.TNT_GRIEF).canGrief(getOwner(), GeneralChunkSetting.TNT_GRIEF);
    }

    @Override
    public boolean canFireGrief() {
        return Constants.getChunkSettings(GeneralChunkSetting.FIRE_GRIEF).canGrief(getOwner(), GeneralChunkSetting.FIRE_GRIEF);
    }

    @Override
    public boolean canPVPHappen() {
        return Constants.getChunkSettings(GeneralChunkSetting.ENABLE_PVP).canGrief(getOwner(), GeneralChunkSetting.ENABLE_PVP);
    }

    @Override
    public boolean canMobGrief() {
        return Constants.getChunkSettings(GeneralChunkSetting.MOB_GRIEF).canGrief(getOwner(), GeneralChunkSetting.MOB_GRIEF);
    }

    public TerritoryData getOccupier(){
        return TerritoryUtil.getTerritory(getOccupierID());
    }

    public void setOccupier(TerritoryData occupier) {
        setOccupierID(occupier.getID());
    }

    public void setOccupierID(String occupierID) {
        this.occupierID = occupierID;
    }

    public void liberate() {
        this.occupierID = getOwnerID();
    }

    public boolean isOccupied() {
        return !ownerID.equals(occupierID);
    }

    protected boolean commonTerritoryCanPlayerDo(Player player, ChunkPermissionType permissionType, ITanPlayer tanPlayer) {

        TerritoryData territoryOfChunk = getOwner();
        //Player is at war with the town
        for (CurrentAttack currentAttacks : territoryOfChunk.getCurrentAttacks()) {
            if (currentAttacks.containsPlayer(tanPlayer))
                return true;
        }

        //If the permission is locked by admins, only shows default value.
        var defaultPermission = (territoryOfChunk instanceof TownData)
                ? Constants.getChunkPermissionConfig().getTownPermission(permissionType)
                : Constants.getChunkPermissionConfig().getRegionPermission(permissionType);
        if(defaultPermission.isLocked()){
            return defaultPermission.defaultRelation().isAllowed(territoryOfChunk, tanPlayer);
        }

        ChunkPermission chunkPermission = territoryOfChunk.getChunkSettings().getChunkPermissions().get(permissionType);
        if (chunkPermission.isAllowed(territoryOfChunk, tanPlayer))
            return true;

        playerCantPerformAction(player);
        return false;
    }

    /**
     * Defines if this territory can bypass buffer zone restrictions and claim a chunk in the radius of the buffer zone
      * @param territoryToAllow The territory wishing to claim a chunk in the buffer zone
     * @return True if the territory can bypass buffer zone restrictions, false otherwise
     */
    public boolean canBypassBufferZone(TerritoryData territoryToAllow) {

        String ownerID = getOwnerID();
        // This chunks is held by the same territory
        if(ownerID.equals(territoryToAllow.getID())){
            return true;
        }

        //This chunk is held by a vassal of the territory
        return territoryToAllow.getVassalsID().contains(ownerID);
    }
}
