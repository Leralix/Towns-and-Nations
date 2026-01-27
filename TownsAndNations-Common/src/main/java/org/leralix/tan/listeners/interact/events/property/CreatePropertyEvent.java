package org.leralix.tan.listeners.interact.events.property;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.chunk.ClaimedChunk;
import org.leralix.tan.dataclass.chunk.TownClaimedChunk;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.listeners.interact.ListenerState;
import org.leralix.tan.listeners.interact.RightClickListenerEvent;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.NumberUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public abstract class CreatePropertyEvent extends RightClickListenerEvent {

    protected final Player player;
    protected final TownData townData;
    protected final ITanPlayer tanPlayer;
    protected Vector3D position1;
    protected Vector3D position2;
    protected double cost;

    protected CreatePropertyEvent(Player player) {
        this.player = player;
        this.townData = TownDataStorage.getInstance().get(player);
        this.tanPlayer = PlayerDataStorage.getInstance().get(player);
    }


    @Override
    public ListenerState execute(PlayerInteractEvent event) {

        LangType langType = tanPlayer.getLang();

        Block block = event.getClickedBlock();
        if (block == null)
            return ListenerState.CONTINUE;

        ClaimedChunk claimedChunk = NewClaimedChunkStorage.getInstance().get(block.getChunk());
        if (!(claimedChunk instanceof TownClaimedChunk townClaimedChunk && townClaimedChunk.getTown().isPlayerIn(player))) {
            TanChatUtils.message(player, Lang.POSITION_NOT_IN_CLAIMED_CHUNK.get(langType));
            return ListenerState.FAILURE;
        }

        if (!tanPlayer.hasTown()) {
            TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(langType));
            return ListenerState.FAILURE;
        }

        if (position1 == null) {
            position1 = new Vector3D(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID().toString());
            TanChatUtils.message(player, Lang.PLAYER_FIRST_POINT_SET.get(player, position1.toString()));
            return ListenerState.CONTINUE;
        }
        if (position2 == null) {
            int maxPropertySize = Constants.getMaxPropertySize();

            Vector3D vector3D = new Vector3D(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID().toString());

            if (Math.abs(position1.getX() - vector3D.getX()) * Math.abs(position1.getY() - vector3D.getY()) * Math.abs(position1.getZ() - vector3D.getZ()) > maxPropertySize) {
                TanChatUtils.message(player, Lang.PLAYER_PROPERTY_TOO_BIG.get(langType, Integer.toString(maxPropertySize)));
                return ListenerState.FAILURE;
            }
            position2 = vector3D;

            cost = NumberUtil.roundWithDigits(getTotalCost());
            if (EconomyUtil.getBalance(player) < cost) {
                TanChatUtils.message(player, Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(langType, Double.toString(cost - EconomyUtil.getBalance(player))));
                return ListenerState.SUCCESS;
            }
            EconomyUtil.removeFromBalance(player, cost);

            TanChatUtils.message(player, Lang.PLAYER_SECOND_POINT_SET.get(langType, vector3D.toString()));
            TanChatUtils.message(player, Lang.PLAYER_PLACE_SIGN.get(langType));
            return ListenerState.CONTINUE;
        }

        int margin = Constants.getMaxPropertySignMargin();
        if (!isNearProperty(block.getLocation(), position1, position2, margin)) {
            TanChatUtils.message(player, Lang.PLAYER_PROPERTY_SIGN_TOO_FAR.get(langType, Integer.toString(margin)), SoundEnum.NOT_ALLOWED);
            return ListenerState.CONTINUE;
        }

        PropertyData property = createProperty();
        property.createPropertySign(player, block, event.getBlockFace());
        property.updateSign();

        TanChatUtils.message(player, Lang.PLAYER_PROPERTY_CREATED.get(langType), SoundEnum.MINOR_GOOD);
        return ListenerState.SUCCESS;
    }

    protected abstract PropertyData createProperty();

    private double getTotalCost() {
        double costPerBlock = townData.getTaxOnCreatingProperty();
        return costPerBlock * position1.getArea(position2);
    }

    boolean isNearProperty(Location blockLocation, Vector3D p1, Vector3D p2, int margin) {
        int minX = Math.min(p1.getX(), p2.getX()) - margin;
        int minY = Math.min(p1.getY(), p2.getY()) - margin;
        int minZ = Math.min(p1.getZ(), p2.getZ()) - margin;
        int maxX = Math.max(p1.getX(), p2.getX()) + margin;
        int maxY = Math.max(p1.getY(), p2.getY()) + margin;
        int maxZ = Math.max(p1.getZ(), p2.getZ()) + margin;

        double blockX = blockLocation.getX();
        double blockY = blockLocation.getY();
        double blockZ = blockLocation.getZ();

        return blockX >= minX && blockX <= maxX &&
                blockY >= minY && blockY <= maxY &&
                blockZ >= minZ && blockZ <= maxZ;
    }


    public static BlockFace getTopDirection(Location blockLocation, Location playerLocation) {
        double dx = playerLocation.getX() - blockLocation.getX();
        double dz = playerLocation.getZ() - blockLocation.getZ();
        double angle = Math.toDegrees(Math.atan2(dz, dx)) + 180;
        return getClosestCardinalDirection(angle);
    }

    private static BlockFace getClosestCardinalDirection(double angle) {
        if (angle < 45 || angle >= 315) {
            return BlockFace.WEST;
        } else if (angle >= 45 && angle < 135) {
            return BlockFace.NORTH;
        } else if (angle >= 135 && angle < 225) {
            return BlockFace.EAST;
        } else {
            return BlockFace.SOUTH;
        }
    }
}
