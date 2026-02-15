package org.leralix.tan.api.external.luckperms.context;

import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.data.chunk.ClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunk;
import org.leralix.tan.data.chunk.WildernessChunk;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.data.upgrade.Upgrade;
import org.leralix.tan.data.upgrade.rewards.StatsType;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.war.info.BoundaryType;
import org.tan.api.TanAPI;
import org.tan.api.enums.EDiplomacyState;
import org.tan.api.interfaces.buildings.TanProperty;
import org.tan.api.interfaces.territory.TanNation;
import org.tan.api.interfaces.territory.TanRegion;
import org.tan.api.interfaces.territory.TanTerritory;
import org.tan.api.interfaces.territory.TanTown;

public class TanContextCalculator implements ContextCalculator<Player> {

    private static final String IS_AT_WAR = "is at war";

    private static final String HAS_TOWN = "has town";
    private static final String HAS_REGION = "has region";
    private static final String HAS_NATION = "has nation";

    private static final String IS_TOWN_LEADER = "is town leader";
    private static final String IS_REGION_LEADER = "is region leader";
    private static final String IS_NATION_LEADER = "is nation leader";

    private static final String IS_IN_FRIENDLY_CLAIM = "is in friendly claims";
    private static final String IS_IN_NEUTRAL_CLAIM = "is in neutral claims";
    private static final String IS_IN_HOSTILE_CLAIM = "is in enemy claims";
    private static final String IS_IN_WILDERNESS_CLAIM = "is in wilderness claims";

    private static final String IS_IN_OWNED_PROPERTY = "is in owned property";
    private static final String IS_IN_RENTED_PROPERTY = "is in rented property";

    private static final String IS_PART_OF_TOWN = "is part of town";
    private static final String IS_PART_OF_REGION = "is part of region";
    private static final String IS_PART_OF_NATION = "is part of nation";
    private static final String TERRITORY_NAME = "territory name";

    private static final String TOWN_HAS_UNLOCKED_UPGRADE = "town has unlocked <upgrade_id>";
    private static final String REGION_HAS_UNLOCKED_UPGRADE = "region has unlocked <upgrade_id>";
    private static final String NATION_HAS_UNLOCKED_UPGRADE = "nation has unlocked <upgrade_id>";
    private static final String UPGRADE_NAME = "upgrade ID";


    private static final String TRUE = "true";
    private static final String FALSE = "false";

    private final PlayerDataStorage playerDataStorage;
    private final TownDataStorage townDataStorage;
    private final RegionDataStorage regionDataStorage;
    private final NationDataStorage nationDataStorage;
    private final NewClaimedChunkStorage chunkStorage;

    public TanContextCalculator(
            PlayerDataStorage playerDataStorage,
            TownDataStorage townDataStorage,
            RegionDataStorage regionDataStorage,
            NationDataStorage nationDataStorage,
            NewClaimedChunkStorage chunkStorage
    ){
        this.playerDataStorage = playerDataStorage;
        this.townDataStorage = townDataStorage;
        this.regionDataStorage = regionDataStorage;
        this.nationDataStorage = nationDataStorage;
        this.chunkStorage = chunkStorage;
    }

    @Override
    public void calculate(@NotNull Player player, @NotNull ContextConsumer consumer) {

        ITanPlayer playerData = playerDataStorage.get(player);

        boolean hasTown = playerData.hasTown();
        boolean hasRegion = playerData.hasRegion();
        boolean hasNation = playerData.hasNation();
        consumer.accept(HAS_TOWN, Boolean.toString(hasTown));
        consumer.accept(HAS_REGION, Boolean.toString(hasRegion));
        consumer.accept(HAS_NATION, Boolean.toString(hasNation));

        consumer.accept(IS_AT_WAR, playerData.getWarsParticipatingIn().isEmpty() ? FALSE : TRUE);

        TownData playerTown = townDataStorage.get(playerData);
        consumer.accept(IS_TOWN_LEADER, isTerritoryLeader(player, playerTown));
        String townName = getNameOfTerritory(playerTown);
        if(townName != null){
            consumer.accept(IS_PART_OF_TOWN, townName);
        }
        registerUpgradesOfTerritory(playerTown, consumer);

        RegionData playerRegion = regionDataStorage.get(playerTown);
        consumer.accept(IS_REGION_LEADER, isTerritoryLeader(player, playerRegion));
        String regionName = getNameOfTerritory(playerRegion);
        if(regionName != null){
            consumer.accept(IS_PART_OF_REGION, regionName);
        }
        registerUpgradesOfTerritory(playerRegion, consumer);

        TanNation playerNation = nationDataStorage.get(playerRegion);
        consumer.accept(IS_NATION_LEADER, isTerritoryLeader(player, playerNation));
        String nationName = getNameOfTerritory(playerRegion);
        if(nationName != null){
            consumer.accept(IS_PART_OF_NATION, nationName);
        }
        registerUpgradesOfTerritory(playerNation, consumer);

        Location location = player.getLocation();

        // Using location.getChunk() will crash the plugin.
        ClaimedChunk chunk = chunkStorage.get(
                Math.floorDiv((int) location.getX(), 16),
                Math.floorDiv((int) location.getZ(), 16),
                location.getWorld().getUID().toString()
        );
        if(chunk instanceof WildernessChunk){
            consumer.accept(IS_IN_FRIENDLY_CLAIM, FALSE);
            consumer.accept(IS_IN_NEUTRAL_CLAIM, FALSE);
            consumer.accept(IS_IN_HOSTILE_CLAIM, FALSE);
            consumer.accept(IS_IN_WILDERNESS_CLAIM, TRUE);
            consumer.accept(IS_IN_OWNED_PROPERTY, FALSE);
            consumer.accept(IS_IN_RENTED_PROPERTY, FALSE);
        }
        else if (chunk instanceof TerritoryChunk territoryChunk){
            TerritoryData territory = territoryChunk.getOccupierInternal();
            TownRelation diplomacyState = territory.getWorstRelationWith(playerData);

            boolean isFriendlyClaim = diplomacyState.getBoundaryType() == BoundaryType.ALLY;
            boolean isHostileClaim = diplomacyState.getBoundaryType() == BoundaryType.ENEMY;
            boolean isNeutralClaim = diplomacyState.getBoundaryType() == BoundaryType.NEUTRAL;

            String isInOwnedProperty = FALSE;
            String isInRentedProperty = FALSE;
            if(territory instanceof TownData townData){
                for(PropertyData property : townData.getPropertiesInternal()){
                    if(property.isLocationInside(location)){
                        if(property.getOwner().canAccess(playerData)){
                            isInOwnedProperty = TRUE;
                        }
                        var optRenter = property.getRenter();
                        if(optRenter.isPresent() && optRenter.get().getID().equals(playerData.getID())){
                            isInRentedProperty = FALSE;
                        }
                    }
                }
            }


            consumer.accept(IS_IN_FRIENDLY_CLAIM, Boolean.toString(isFriendlyClaim));
            consumer.accept(IS_IN_NEUTRAL_CLAIM, Boolean.toString(isNeutralClaim));
            consumer.accept(IS_IN_HOSTILE_CLAIM, Boolean.toString(isHostileClaim));
            consumer.accept(IS_IN_WILDERNESS_CLAIM, FALSE);
            consumer.accept(IS_IN_OWNED_PROPERTY, isInOwnedProperty);
            consumer.accept(IS_IN_RENTED_PROPERTY, isInRentedProperty);
        }
    }

    private void registerUpgradesOfTerritory(TanTerritory territory, ContextConsumer consumer) {

        if(territory == null){
            return;
        }

        StatsType statsType;
        String consumerKey;
        switch (territory) {
            case TanTown ignored -> {
                statsType = StatsType.TOWN;
                consumerKey = TOWN_HAS_UNLOCKED_UPGRADE;
            }
            case TanRegion ignored -> {
                statsType = StatsType.REGION;
                consumerKey = REGION_HAS_UNLOCKED_UPGRADE;
            }
            case TanNation ignored -> {
                statsType = StatsType.NATION;
                consumerKey = NATION_HAS_UNLOCKED_UPGRADE;
            }
            default -> throw new IllegalStateException("Unexpected value: " + territory);
        };

        TerritoryData territoryData = TerritoryUtil.getTerritory(territory.getID());
        if(territoryData == null){
            return;
        }
        for(Upgrade upgrade : Constants.getUpgradeStorage().getUpgrades(statsType)){
            String key = consumerKey.replace("<upgrade_id>", upgrade.getID());
            System.out.println(key);
            if(territoryData.getNewLevel().getLevel(upgrade) > 0){
                consumer.accept(key, TRUE);
            }
            else {
                consumer.accept(key, FALSE);
            }
        }
    }

    private @Nullable String getNameOfTerritory(TanTerritory territory) {
        if(territory == null){
            return null;
        }
        return territory.getName();
    }

    @Override
    public @NotNull ContextSet estimatePotentialContexts() {
        return ImmutableContextSet.builder()
                .add(IS_AT_WAR, TRUE)
                .add(IS_AT_WAR, FALSE)
                .add(HAS_TOWN, TRUE)
                .add(HAS_TOWN, FALSE)
                .add(HAS_REGION, TRUE)
                .add(HAS_REGION, FALSE)
                .add(HAS_NATION, TRUE)
                .add(HAS_NATION, FALSE)
                .add(IS_TOWN_LEADER, TRUE)
                .add(IS_TOWN_LEADER, FALSE)
                .add(IS_REGION_LEADER, TRUE)
                .add(IS_REGION_LEADER, FALSE)
                .add(IS_NATION_LEADER, TRUE)
                .add(IS_NATION_LEADER, FALSE)
                .add(IS_IN_FRIENDLY_CLAIM, TRUE)
                .add(IS_IN_FRIENDLY_CLAIM, FALSE)
                .add(IS_IN_NEUTRAL_CLAIM, TRUE)
                .add(IS_IN_NEUTRAL_CLAIM, FALSE)
                .add(IS_IN_HOSTILE_CLAIM, TRUE)
                .add(IS_IN_HOSTILE_CLAIM, FALSE)
                .add(IS_IN_WILDERNESS_CLAIM, TRUE)
                .add(IS_IN_WILDERNESS_CLAIM, FALSE)
                .add(IS_IN_OWNED_PROPERTY, TRUE)
                .add(IS_IN_OWNED_PROPERTY, FALSE)
                .add(IS_IN_RENTED_PROPERTY, TRUE)
                .add(IS_IN_RENTED_PROPERTY, FALSE)
                .add(IS_PART_OF_TOWN, TERRITORY_NAME)
                .add(IS_PART_OF_REGION, TERRITORY_NAME)
                .add(IS_PART_OF_NATION, TERRITORY_NAME)
                .add(TOWN_HAS_UNLOCKED_UPGRADE, UPGRADE_NAME)
                .add(REGION_HAS_UNLOCKED_UPGRADE, UPGRADE_NAME)
                .add(NATION_HAS_UNLOCKED_UPGRADE, UPGRADE_NAME)
                .build();
    }

    /**
     *
     * @param player    the player to check
     * @param territory the territory to check
     * @return "true" String if the territory exist, the player is a part of it and is the leader. "false" otherwise.
     */
    private String isTerritoryLeader(@NotNull Player player, TanTerritory territory) {
        if(territory == null){
            return FALSE;
        }
        if(!territory.isPlayerIn(player)){
            return FALSE;
        }
        if(player.getUniqueId().equals(territory.getLeaderID())){
            return TRUE;
        }
        return FALSE;
    }
}