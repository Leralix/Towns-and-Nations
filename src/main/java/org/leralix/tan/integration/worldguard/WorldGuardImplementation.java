package org.leralix.tan.integration.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.leralix.tan.enums.permissions.ChunkPermissionType;

import java.util.Set;


public class WorldGuardImplementation {



    public EWorldGuardResult canPlayerDo(Player player, Location location) {
        WorldGuardPlatform platform = WorldGuard.getInstance().getPlatform();

        World world = BukkitAdapter.adapt(location.getWorld());
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        RegionManager manager = platform.getRegionContainer().get(world);

        BlockVector3 vector = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        ApplicableRegionSet regionSet = manager.getApplicableRegions(vector);

        Set<ProtectedRegion> protectedRegions = regionSet.getRegions();

        if(protectedRegions.isEmpty()){
            return EWorldGuardResult.NO_REGION;
        }

        if(regionSet.isMemberOfAll(localPlayer)) {
            return EWorldGuardResult.PLAYER_CAN;
        }
        return EWorldGuardResult.PLAYER_CANNOT;
    }

    public boolean isActionAllowed(Player player, Location location, ChunkPermissionType actionType) {

        StateFlag flag = getFlagForAction(actionType);
        com.sk89q.worldedit.util.Location weLocation = BukkitAdapter.adapt(location);
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        return query.testState(weLocation, localPlayer, flag);
    }

    public boolean isHandledByWorldGuard(Location location) {
        com.sk89q.worldedit.util.Location weLocation = BukkitAdapter.adapt(location);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        ApplicableRegionSet regionSet = query.getApplicableRegions(weLocation);
        return regionSet.size() > 0;
    }

    private StateFlag getFlagForAction(ChunkPermissionType actionType) {
        return switch (actionType) {
            case BREAK_BLOCK -> Flags.BLOCK_BREAK;
            case PLACE_BLOCK -> Flags.BLOCK_PLACE;
            case ATTACK_PASSIVE_MOB -> Flags.DAMAGE_ANIMALS;
            case INTERACT_BERRIES,
                 INTERACT_BUTTON,
                 INTERACT_ARMOR_STAND,
                 INTERACT_DOOR,
                 INTERACT_DECORATIVE_BLOCK,
                 INTERACT_ITEM_FRAME,
                 INTERACT_MUSIC_BLOCK,
                 INTERACT_REDSTONE,
                 USE_LEAD,
                 USE_BONE_MEAL,
                 USE_SHEARS -> Flags.INTERACT;
            case INTERACT_CHEST,
                 INTERACT_FURNACE -> Flags.CHEST_ACCESS;
            case INTERACT_BOAT,
                 INTERACT_MINECART -> Flags.PLACE_VEHICLE;
        };
    }
}
