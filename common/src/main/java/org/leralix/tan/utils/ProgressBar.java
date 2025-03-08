package org.leralix.tan.utils;

import net.md_5.bungee.api.ChatColor;

public class ProgressBar {

    /**
     * Génère une barre de progression bicolore.
     *
     * @param current     La progression actuelle.
     * @param max         La valeur maximale de la progression.
     * @param length      Longueur totale de la barre.
     * @param completedColor Couleur pour la partie complétée (format Minecraft : §a, §b, etc.).
     * @param remainingColor Couleur pour la partie restante.
     * @return Une chaîne représentant la barre de progression.
     */
    public static String createProgressBar(int current, int max, int length, ChatColor completedColor, ChatColor remainingColor) {
        if (current < 0) current = 0;
        if (current > max) current = max;

        int completedBars = (int) ((double) current / max * length);
        int remainingBars = length - completedBars;

        return completedColor +
                "|".repeat(Math.max(0, completedBars)) +
                remainingColor +
                "|".repeat(Math.max(0, remainingBars)) +
                ChatColor.WHITE;
    }
}
