package org.leralix.tan.api.external.luckperms.context;

import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.tan.api.TanAPI;
import org.tan.api.interfaces.territory.TanNation;
import org.tan.api.interfaces.territory.TanRegion;
import org.tan.api.interfaces.territory.TanTerritory;
import org.tan.api.interfaces.territory.TanTown;

public class HasTownCalculator implements ContextCalculator<Player> {

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

    private static final String TRUE = "true";
    private static final String FALSE = "false";


    @Override
    public void calculate(@NotNull Player player, @NotNull ContextConsumer consumer) {

        var playerData = TanAPI.getInstance()
                .getPlayerManager()
                .get(player);

        boolean hasTown = playerData.hasTown();
        boolean hasRegion = playerData.hasRegion();
        boolean hasNation = playerData.hasNation();
        consumer.accept(HAS_TOWN, Boolean.toString(hasTown));
        consumer.accept(HAS_REGION, Boolean.toString(hasRegion));
        consumer.accept(HAS_NATION, Boolean.toString(hasNation));


        TanTown playerTown = playerData.getTown();
        consumer.accept(IS_TOWN_LEADER, isTerritoryLeader(player, playerTown));
        consumer.accept(IS_PART_OF_TOWN, getNameOfTerritory(playerTown));

        TanRegion playerRegion = playerData.getRegion();
        consumer.accept(IS_REGION_LEADER, isTerritoryLeader(player, playerRegion));
        consumer.accept(IS_PART_OF_REGION, getNameOfTerritory(playerRegion));

        TanNation playerNation = playerData.getNation();
        consumer.accept(IS_NATION_LEADER, isTerritoryLeader(player, playerNation));
        consumer.accept(IS_PART_OF_NATION, getNameOfTerritory(playerNation));

        var optionalTerritoryOwningChunk = TanAPI.getInstance().getClaimManager().getTerritoryOfChunk(player.getChunk());

        if(optionalTerritoryOwningChunk.isEmpty()){
            consumer.accept(IS_IN_FRIENDLY_CLAIM, FALSE);
            consumer.accept(IS_IN_NEUTRAL_CLAIM, FALSE);
            consumer.accept(IS_IN_HOSTILE_CLAIM, FALSE);
            consumer.accept(IS_IN_WILDERNESS_CLAIM, TRUE);
            consumer.accept(IS_IN_OWNED_PROPERTY, FALSE);
            consumer.accept(IS_IN_RENTED_PROPERTY, FALSE);
        }
        else {
            TanTerritory territory = optionalTerritoryOwningChunk.get();
            consumer.accept(IS_IN_FRIENDLY_CLAIM, ""); //TODO : finish
            consumer.accept(IS_IN_NEUTRAL_CLAIM, ""); //TODO : finish
            consumer.accept(IS_IN_HOSTILE_CLAIM, ""); //TODO : finish
            consumer.accept(IS_IN_WILDERNESS_CLAIM, FALSE);
            consumer.accept(IS_IN_OWNED_PROPERTY, ""); //TODO : finish
            consumer.accept(IS_IN_RENTED_PROPERTY, ""); //TODO : finish
        }
    }

    private @NonNull String getNameOfTerritory(TanTerritory territory) {
        if(territory == null){
            return "";
        }
        return territory.getName();
    }

    @Override
    public @NotNull ContextSet estimatePotentialContexts() {
        var contextBuilder =  ImmutableContextSet.builder()
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
                .add(IS_IN_RENTED_PROPERTY, FALSE);

        for(TanTown tanTown : TanAPI.getInstance().getTerritoryManager().getTowns()){
            contextBuilder.add(IS_PART_OF_TOWN, tanTown.getName());
        }
        for(TanRegion tanTown : TanAPI.getInstance().getTerritoryManager().getRegions()){
            contextBuilder.add(IS_PART_OF_REGION, tanTown.getName());
        }
        for(TanNation tanTown : TanAPI.getInstance().getTerritoryManager().getNations()){
            contextBuilder.add(IS_PART_OF_NATION, tanTown.getName());
        }
        return contextBuilder.build();
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