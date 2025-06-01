package org.leralix.tan.enums;

import dev.triumphteam.gui
.builder.item.ItemBuilder;
import dev.triumphteam.gui
.guis.GuiItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.HeadUtils;

public enum RankEnum {

    ONE(5, ChatColor.GOLD, Material.NETHERITE_HELMET, Material.YELLOW_STAINED_GLASS_PANE),
    TWO(4, ChatColor.DARK_PURPLE, Material.DIAMOND_HELMET, Material.PURPLE_STAINED_GLASS_PANE),
    THREE(3, ChatColor.BLUE,Material.GOLDEN_HELMET, Material.BLUE_STAINED_GLASS_PANE),
    FOUR(2, ChatColor.DARK_GREEN,Material.IRON_HELMET, Material.GREEN_STAINED_GLASS_PANE),
    FIVE(1, ChatColor.GREEN, Material.LEATHER_HELMET, Material.LIME_STAINED_GLASS_PANE);

    private final int level;
    private final ChatColor color;
    private final Material paneColorMaterial;
    private final Material helmetMaterial;

    RankEnum(int level, ChatColor color, Material helmetMaterial, Material paneColorMaterial) {
        this.level = level;
        this.color = color;
        this.paneColorMaterial = paneColorMaterial;
        this.helmetMaterial = helmetMaterial;
    }

    public int getLevel() {
        return level;
    }

    public ChatColor getColor() {
        return color;
    }

    public RankEnum getRankByLevel(int level){
        return switch (level) {
            case 2 -> FOUR;
            case 3 -> THREE;
            case 4 -> TWO;
            case 5 -> ONE;
            default -> FIVE;
        };
    }

    public RankEnum nextRank(){
        if(this.getLevel() == 5) return ONE;
        return getRankByLevel((this.getLevel() + 1));
    }
    public RankEnum previousRank(){
        if(this.getLevel() == 1) return FIVE;
        return getRankByLevel((this.getLevel() - 1));
    }

    public ItemStack getBasicRankIcon(){
        return new ItemStack(helmetMaterial);
    }

    public GuiItem getRankColorGuiIcon(){
        ItemStack itemStack = HeadUtils.createCustomItemStack(paneColorMaterial, "");
        return ItemBuilder.from(itemStack).asGuiItem(e -> e.setCancelled(true));
    }

}
