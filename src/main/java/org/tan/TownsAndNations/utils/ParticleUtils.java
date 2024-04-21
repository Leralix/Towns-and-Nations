package org.tan.TownsAndNations.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.tan.TownsAndNations.DataClass.Vector3D;
import org.tan.TownsAndNations.TownsAndNations;

public class ParticleUtils {

    public static void showBox(Player player, Vector3D point1, Vector3D point2, int seconds){
        ParticleTask particleTask = new ParticleTask(player, point1, point2, seconds);

        particleTask.runTaskTimer(TownsAndNations.getPlugin(), 0, 20);

    }
    private static class ParticleTask extends BukkitRunnable {
        private final Player player;
        private final Vector3D p1;
        private final Vector3D p2;
        private int secondsLeft;


        public ParticleTask(Player player, Vector3D p1, Vector3D p2, int duration) {
            this.player = player;
            this.p1 = p1;
            this.p2 = p2;
            this.secondsLeft = duration;
        }
        @Override
        public void run() {
            if (player != null && secondsLeft > 0) {
                ParticleUtils.drawBox(player, this.p1, this.p2);
                secondsLeft--;
                if (secondsLeft == 0) {
                    cancel();
                }
            } else {
                cancel();
            }
        }
    }

    public static void drawBox(Player player, Vector3D point1, Vector3D point2) {
        double minX = Math.min(point1.getX(), point2.getX());
        double minY = Math.min(point1.getY(), point2.getY());
        double minZ = Math.min(point1.getZ(), point2.getZ());
        double maxX = Math.max(point1.getX(), point2.getX());
        double maxY = Math.max(point1.getY(), point2.getY());
        double maxZ = Math.max(point1.getZ(), point2.getZ());

        // Loop through all the edges of the box
        drawEdges(player, minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);
    }

    private static void drawEdges(Player player, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        // Draw bottom edges
        drawLine(player, minX, minY, minZ, maxX, minY, minZ);
        drawLine(player, maxX, minY, minZ, maxX, minY, maxZ);
        drawLine(player, maxX, minY, maxZ, minX, minY, maxZ);
        drawLine(player, minX, minY, maxZ, minX, minY, minZ);

        // Draw top edges
        drawLine(player, minX, maxY, minZ, maxX, maxY, minZ);
        drawLine(player, maxX, maxY, minZ, maxX, maxY, maxZ);
        drawLine(player, maxX, maxY, maxZ, minX, maxY, maxZ);
        drawLine(player, minX, maxY, maxZ, minX, maxY, minZ);

        // Draw vertical edges
        drawLine(player, minX, minY, minZ, minX, maxY, minZ);
        drawLine(player, maxX, minY, minZ, maxX, maxY, minZ);
        drawLine(player, maxX, minY, maxZ, maxX, maxY, maxZ);
        drawLine(player, minX, minY, maxZ, minX, maxY, maxZ);
    }

    private static void drawLine(Player player, double x1, double y1, double z1, double x2, double y2, double z2) {
        double length = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
        double amount = length * 2; // Adjust the density of particles based on the length of the line

        double dx = (x2 - x1) / amount;
        double dy = (y2 - y1) / amount;
        double dz = (z2 - z1) / amount;

        for (int i = 0; i < amount; i++) {
            Location loc = new Location(player.getWorld(), x1 + dx * i, y1 + dy * i, z1 + dz * i);
            player.spawnParticle(Particle.DRAGON_BREATH, loc, 0, 0, 0, 0, 1, null);
        }
    }
}
