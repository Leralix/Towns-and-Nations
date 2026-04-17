package org.leralix.tan.data.chunk;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.data.territory.permission.ChunkPermission;
import org.leralix.tan.data.territory.permission.ChunkPermissionType;
import org.leralix.tan.data.territory.permission.GeneralChunkSetting;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.data.upgrade.rewards.numeric.ChunkCap;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.territory.ChunkUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.war.attack.CurrentAttack;
import org.leralix.tan.war.info.SideStatus;
import org.tan.api.enums.TerritoryPermission;
import org.tan.api.interfaces.territory.TanTerritory;

/**
 * A ClaimedChunk that is owned by a Territory.
 * There are 3 types of TerritoryChunk:
 * <ul>
 *     <li>Town : {@link TownClaimedChunk}</li>
 *     <li>Region : {@link RegionClaimedChunk}</li>
 *     <li>Nation : {@link NationClaimedChunk}</li>
 * </ul>
 */
public abstract class TerritoryChunkData extends ChunkData implements TerritoryChunk {

    /**
     * The ID of the territory owning this chunk
     */
    private String ownerID;

    /**
     * The ID of the territory occupying this chunk
     * If the territory is captured by another territory, this will be different from ownerID
     */
    private String occupierID;

    protected TerritoryChunkData(Chunk chunk, String owner) {
        super(chunk);
        this.ownerID = owner;
        this.occupierID = owner;
    }

    protected TerritoryChunkData(int x, int z, String worldUUID, String owner) {
        super(x, z, worldUUID);
        this.ownerID = owner;
        this.occupierID = owner;
    }

    @Override
    public Territory getOwnerInternal() {
        return TerritoryUtil.getTerritory(ownerID);
    }


    @Override
    public String getOwnerID() {
        return ownerID;
    }

    @Override
    public Territory getOccupierInternal() {
        return TerritoryUtil.getTerritory(occupierID);
    }

    @Override
    public TanTerritory getOccupier() {
        return getOccupierInternal();
    }

    @Override
    protected void playerCantPerformAction(Player player, LangType langType) {
        TanChatUtils.message(player, Lang.PLAYER_ACTION_NO_PERMISSION.get());
        TanChatUtils.message(player, Lang.CHUNK_BELONGS_TO.get(getOwner().getName()));
    }

    @Override
    public boolean canTerritoryClaim(Player player, Territory territoryData, LangType langType) {
        if (canTerritoryClaim(territoryData)) {
            return true;
        }
        TanChatUtils.message(player, Lang.CHUNK_ALREADY_CLAIMED_WARNING.get(getOwner().getColoredName()));
        return false;
    }

    @Override
    public boolean isClaimed() {
        return true; // A TerritoryChunk is always claimed
    }

    @Override
    public void playerEnterClaimedArea(Player player, ITanPlayer tanPlayer, boolean displayTerritoryColor) {
        TanTerritory ownerTerritory = getOwner();

        Territory territoryData = TerritoryUtil.getTerritory(ownerTerritory.getID());

        TerritoryEnterMessageUtil.sendEnterTerritoryMessage(player, territoryData, displayTerritoryColor, tanPlayer.getLang());

        Town enteringPlayerTown = tanPlayer.getTown();
        if (enteringPlayerTown == null) {
            return;
        }
        TownRelation relation = enteringPlayerTown.getRelationWith(territoryData);

        if (relation == TownRelation.WAR && Constants.notifyWhenEnemyEnterTerritory()) {
            TanChatUtils.message(player, Lang.CHUNK_ENTER_TOWN_AT_WAR.get(tanPlayer.getLang()));
            territoryData.broadCastBarMessage(Lang.CHUNK_INTRUSION_ALERT.get(enteringPlayerTown.getName(), player.getName()));
        }
    }

    @Override
    public TextComponent getMapIcon(LangType langType) {

        TextComponent textComponent;
        String text;
        if (isOccupied()) {
            textComponent = new TextComponent("🟧");
            textComponent.setColor(getOccupierInternal().getChunkColor());
            text = "x : " + super.getMiddleX() + " z : " + super.getMiddleZ() + "\n" +
                    getOwner().getColoredName() + "\n" +
                    getOccupier().getColoredName() + "\n" +
                    Lang.LEFT_CLICK_TO_CLAIM.get(langType);
        } else {
            textComponent = new TextComponent("⬛");
            textComponent.setColor(getOccupierInternal().getChunkColor());
            text = "x : " + super.getMiddleX() + " z : " + super.getMiddleZ() + "\n" +
                    getOwner().getColoredName() + "\n" +
                    Lang.LEFT_CLICK_TO_CLAIM.get(langType);
        }

        textComponent.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new Text(text)));
        return textComponent;
    }

    /**
     * Called when a player wants to unclaim a chunk
     * Will verify if the player is allowed to unclaim it, and if so, unclaim.
     *
     * @param player   the player trying to unclaim this chunk
     * @param langType the display language for all messages sent to the player
     */
    public void unclaimChunk(Player player, ITanPlayer tanPlayer, LangType langType) {

        Territory ownerTerritory = getOwnerInternal();

        //If owner territory contains the player, regular check
        if (ownerTerritory.isPlayerIn(player)) {
            if (!ownerTerritory.checkPlayerPermission(tanPlayer, TerritoryPermission.UNCLAIM_CHUNK)) {
                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(langType), SoundEnum.NOT_ALLOWED);
                return;
            }

            if (ownerTerritory instanceof Town ownerTown) {
                for (PropertyData propertyData : ownerTown.getPropertiesInternal()) {
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

            if (isOccupied()) {
                TanChatUtils.message(player, Lang.CHUNK_OCCUPIED_CANT_UNCLAIM.get(langType));
                return;
            }
            if (Constants.preventOrphanChunks() &&
                    !Constants.allowNonAdjacentChunksFor(getType()) &&
                    ChunkUtil.doesUnclaimCauseOrphan(this)
            ) {
                TanChatUtils.message(player, Lang.CANNOT_UNCLAIM_BECAUSE_CREATE_ORPHAN.get(langType));
                return;
            }


            TownsAndNations.getPlugin().getClaimStorage().unclaimChunkAndUpdate(this);

            ChunkCap chunkCap = ownerTerritory.getNewLevel().getStat(ChunkCap.class);
            if (chunkCap.isUnlimited()) {
                TanChatUtils.message(player, Lang.CHUNK_UNCLAIMED_SUCCESS_UNLIMITED.get(ownerTerritory.getColoredName()));
            } else {
                String currentChunks = Integer.toString(ownerTerritory.getNumberOfClaimedChunk());
                String maxChunks = Integer.toString(chunkCap.getMaxAmount());
                TanChatUtils.message(player, Lang.CHUNK_UNCLAIMED_SUCCESS_LIMITED.get(ownerTerritory.getColoredName(), currentChunks, maxChunks));
            }
        } else {
            // Special case: one of the player's territories can conquer chunks due to a past war.
            for (Territory territoryData : tanPlayer.getAllTerritoriesPlayerIsIn()) {
                if (territoryData.canConquerChunk(this)) {

                    if (isOccupied()) {
                        TanChatUtils.message(player, Lang.CHUNK_OCCUPIED_CANT_UNCLAIM.get(langType));
                        return;
                    }

                    if (Constants.preventOrphanChunks() &&
                            !Constants.allowNonAdjacentChunksFor(ownerTerritory) &&
                            ChunkUtil.doesUnclaimCauseOrphan(this)
                    ) {
                        TanChatUtils.message(player, Lang.CANNOT_UNCLAIM_BECAUSE_CREATE_ORPHAN.get(langType));
                        return;
                    }


                    TownsAndNations.getPlugin().getClaimStorage().unclaimChunkAndUpdate(this);
                    TanChatUtils.message(player, Lang.CHUNK_UNCLAIMED_SUCCESS_UNLIMITED.get(langType, ownerTerritory.getColoredName()), SoundEnum.MINOR_GOOD);
                    return;
                }
            }
            // Player is not part of territory
            TanChatUtils.message(player, Lang.PLAYER_NOT_IN_TERRITORY.get(langType, ownerTerritory.getColoredName()));
        }
    }

    @Override
    public String getOccupierID() {
        if (occupierID == null) {
            occupierID = ownerID;
        }
        return occupierID;
    }

    @Override
    public boolean canExplosionGrief() {
        return Constants.getChunkSettings(GeneralChunkSetting.TNT_GRIEF).canGrief(getOwnerInternal(), GeneralChunkSetting.TNT_GRIEF);
    }

    @Override
    public boolean canFireGrief() {
        return Constants.getChunkSettings(GeneralChunkSetting.FIRE_GRIEF).canGrief(getOwnerInternal(), GeneralChunkSetting.FIRE_GRIEF);
    }

    @Override
    public boolean canPVPHappen() {
        return Constants.getChunkSettings(GeneralChunkSetting.ENABLE_PVP).canGrief(getOwnerInternal(), GeneralChunkSetting.ENABLE_PVP);
    }

    @Override
    public boolean canHostileGrief() {
        return Constants.getChunkSettings(GeneralChunkSetting.HOSTILE_MOB_GRIEF).canGrief(getOwnerInternal(), GeneralChunkSetting.HOSTILE_MOB_GRIEF);
    }

    @Override
    public boolean canVillagerGrief() {
        return Constants.getChunkSettings(GeneralChunkSetting.VILLAGER_GRIEF).canGrief(getOwnerInternal(), GeneralChunkSetting.VILLAGER_GRIEF);
    }

    @Override
    public boolean canPassiveGrief() {
        return Constants.getChunkSettings(GeneralChunkSetting.PASSIVE_MOB_GRIEF).canGrief(getOwnerInternal(), GeneralChunkSetting.PASSIVE_MOB_GRIEF);
    }

    @Override
    public void setOccupierID(String occupierID) {
        this.occupierID = occupierID;
    }

    @Override
    public void liberate() {
        this.occupierID = getOwnerID();
    }

    @Override
    public boolean isOccupied() {
        return !ownerID.equals(occupierID);
    }

    @Override
    protected boolean canPlayerDoInternal(Player player, ITanPlayer tanPlayer, ChunkPermissionType permissionType, Location location) {
        SideStatus side = tanPlayer.getWarSideWith(getOwnerInternal());
        if (side == SideStatus.ALLY && Constants.getPermissionAtWars().canAllyDoAction(permissionType) ||
                side == SideStatus.ENEMY && Constants.getPermissionAtWars().canEnemyDoAction(permissionType)) {
            return true;
        }

        if(this instanceof TownClaimedChunk townClaimedChunk){
            Town ownerTown = townClaimedChunk.getTown();
            PropertyData property = ownerTown.getProperty(location);
            if (property != null) {
                //Location is in a property
                if (property.isPlayerAllowed(permissionType, tanPlayer)) {
                    return true;
                } else {
                    TanChatUtils.message(player, property.getDenyMessage(tanPlayer.getLang()));
                    return false;
                }
            }
        }

        Territory territoryOfChunk = getOwnerInternal();
        //Player is at war with the town
        for (CurrentAttack currentAttacks : territoryOfChunk.getCurrentAttacks()) {
            if (currentAttacks.containsPlayer(tanPlayer))
                return true;
        }

        //If the permission is locked by admins, only shows default value.
        var defaultPermission = (territoryOfChunk instanceof Town)
                ? Constants.getChunkPermissionConfig().getTownPermission(permissionType)
                : Constants.getChunkPermissionConfig().getRegionPermission(permissionType);
        if (defaultPermission.isLocked()) {
            return defaultPermission.defaultRelation().isAllowed(territoryOfChunk, tanPlayer);
        }

        ChunkPermission chunkPermission = territoryOfChunk.getChunkSettings().getChunkPermissions().get(permissionType);
        if (chunkPermission.isAllowed(territoryOfChunk, tanPlayer))
            return true;

        playerCantPerformAction(player, tanPlayer.getLang());
        return false;
    }

    @Override
    public void notifyUpdate() {
        if (!Constants.allowNonAdjacentChunksFor(getType())) {
            ChunkUtil.unclaimIfNoLongerSupplied(this);
        }
    }
}
