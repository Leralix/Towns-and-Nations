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
import org.leralix.tan.listeners.interact.RightClickListener;
import org.leralix.tan.listeners.interact.RightClickListenerEvent;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.Constants;
import org.leralix.tan.utils.TanChatUtils;

import static org.leralix.tan.utils.TanChatUtils.getTANString;

public class CreatePropertyEvent extends RightClickListenerEvent {

    private final Player player;
    private Vector3D position1;
    private Vector3D position2;

    public CreatePropertyEvent(Player player){
        this.player = player;
    }


    @Override
    public void execute(PlayerInteractEvent event) {

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        LangType langType = tanPlayer.getLang();

        Block block = event.getClickedBlock();
        if (block == null)
            return;

        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(block.getChunk());
        if (claimedChunk instanceof TownClaimedChunk townClaimedChunk && townClaimedChunk.getTown().isPlayerIn(player)) {
            player.sendMessage(Lang.POSITION_NOT_IN_CLAIMED_CHUNK.get(langType));
        }

        if (!tanPlayer.hasTown()){
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_TOWN.get(langType));
            RightClickListener.removePlayer(player);
            return;
        }

        if (position1 == null){
            position1 = new Vector3D(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID().toString());
            player.sendMessage(getTANString() + Lang.PLAYER_FIRST_POINT_SET.get(player, position1));
            return;
        }
        if(position2 == null){
            int maxPropertySize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("maxPropertySize", 50000);

            Vector3D vector3D = new Vector3D(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID().toString());


            if (Math.abs(position1.getX() - vector3D.getX()) * Math.abs(position1.getY() - vector3D.getY()) * Math.abs(position1.getZ() - vector3D.getZ()) > maxPropertySize) {
                player.sendMessage(getTANString() + Lang.PLAYER_PROPERTY_TOO_BIG.get(maxPropertySize));
                return;
            }
            position2 = vector3D;
            player.sendMessage(getTANString() + Lang.PLAYER_SECOND_POINT_SET.get(vector3D));
            player.sendMessage(getTANString() + Lang.PLAYER_PLACE_SIGN.get());
            return;
        }

        int margin = Constants.getPropertySignMargin();
        if (!isNearProperty(block.getLocation(), position1, position2, margin)) {
            player.sendMessage(getTANString() + Lang.PLAYER_PROPERTY_SIGN_TOO_FAR.get(margin));
            return;
        }

        RightClickListener.removePlayer(player);

        SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
        player.sendMessage(getTANString() + Lang.PLAYER_PROPERTY_CREATED.get());

        TownData playerTown = tanPlayer.getTown();
        if(playerTown == null){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.get());
            return;
        }

        PropertyData property = playerTown.registerNewProperty(position1,position2, tanPlayer);
        new PlayerPropertyManager(player, property, HumanEntity::closeInventory);

        property.createPropertySign(player, property, block, event.getBlockFace());
        property.updateSign();
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
