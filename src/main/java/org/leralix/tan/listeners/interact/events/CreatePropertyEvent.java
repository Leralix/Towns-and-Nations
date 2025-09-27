package org.leralix.tan.listeners.interact.events;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.position.Vector3D;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TownClaimedChunk;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.user.property.PlayerPropertyManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.listeners.interact.ListenerState;
import org.leralix.tan.listeners.interact.RightClickListenerEvent;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.NumberUtil;

public class CreatePropertyEvent extends RightClickListenerEvent {

    private final Player player;
    private final TownData townData;
    private final ITanPlayer tanPlayer;
    private Vector3D position1;
    private Vector3D position2;
    private double cost;

    public CreatePropertyEvent(Player player){
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

        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(block.getChunk());
        if (!(claimedChunk instanceof TownClaimedChunk townClaimedChunk && townClaimedChunk.getTown().isPlayerIn(player))) {
            player.sendMessage(Lang.POSITION_NOT_IN_CLAIMED_CHUNK.get(langType));
            return ListenerState.FAILURE;
        }

        if (!tanPlayer.hasTown()){
            player.sendMessage(Lang.PLAYER_NO_TOWN.get(langType));
            return ListenerState.FAILURE;
        }

        if (position1 == null){
            position1 = new Vector3D(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID().toString());
            player.sendMessage(Lang.PLAYER_FIRST_POINT_SET.get(player, position1.toString()));
            return ListenerState.CONTINUE;
        }
        if(position2 == null){
            int maxPropertySize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("MaxPropertySize", 50000);

            Vector3D vector3D = new Vector3D(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID().toString());

            if (Math.abs(position1.getX() - vector3D.getX()) * Math.abs(position1.getY() - vector3D.getY()) * Math.abs(position1.getZ() - vector3D.getZ()) > maxPropertySize) {
                player.sendMessage(Lang.PLAYER_PROPERTY_TOO_BIG.get(langType, Integer.toString(maxPropertySize)));
                return ListenerState.FAILURE;
            }
            position2 = vector3D;

            cost = NumberUtil.roundWithDigits(getTotalCost());
            if(tanPlayer.getBalance() < cost){
                player.sendMessage(Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(langType, Double.toString(cost - tanPlayer.getBalance())));
                return ListenerState.SUCCESS;
            }

            player.sendMessage(Lang.PLAYER_SECOND_POINT_SET.get(langType, vector3D.toString()));
            player.sendMessage(Lang.PLAYER_PLACE_SIGN.get(langType));
            return ListenerState.CONTINUE;
        }

        int margin = Constants.getMaxPropertySignMargin();
        if (!isNearProperty(block.getLocation(), position1, position2, margin)) {
            player.sendMessage(Lang.PLAYER_PROPERTY_SIGN_TOO_FAR.get(langType, Integer.toString(margin)));
            return ListenerState.CONTINUE;
        }

        SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
        player.sendMessage(Lang.PLAYER_PROPERTY_CREATED.get(langType));
        tanPlayer.removeFromBalance(cost);
        townData.addToBalance(cost);

        PropertyData property = townData.registerNewProperty(position1,position2, tanPlayer);
        new PlayerPropertyManager(player, property, HumanEntity::closeInventory);

        property.createPropertySign(player, block, event.getBlockFace());
        property.updateSign();
        return ListenerState.SUCCESS;
    }

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
