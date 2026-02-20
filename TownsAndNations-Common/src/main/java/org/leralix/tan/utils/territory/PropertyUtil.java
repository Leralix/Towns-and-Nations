package org.leralix.tan.utils.territory;

import org.bukkit.Location;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.data.building.property.PropertyData;

public class PropertyUtil {

    private PropertyUtil() {
        /* This utility class should not be instantiated */
    }

    public static boolean isNearProperty(Location blockLocation, PropertyData propertyData, int margin) {
        return isNearProperty(blockLocation, propertyData.getFirstCorner(), propertyData.getSecondCorner(), margin);
    }



        public static boolean isNearProperty(Location blockLocation, Vector3D p1, Vector3D p2, int margin) {
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
}
