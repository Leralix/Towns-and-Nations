package org.leralix.tan.enums;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.Lang.Lang;
import org.leralix.tan.utils.HeadUtils;

public enum TownRankEnum {

    ONE(1, ChatColor.GOLD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODIxNGEyYmUzM2YwMzdiZmU2ZmEzZTI0YjFjMmZlMDRmMWU1ZmZkNzQ4ODA5NGQ0ZmY3YWJiMGIzNzBlZjViZSJ9fX0="),
    TWO(2, ChatColor.DARK_PURPLE,"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWEwZjQ2MDQ2YWUxM2QzMTkzZDQyNTcyZmRiY2I2MmVhMWQ2OWMzODA3ZjA2ZTQwYmQxMTc4MmY1MTQxNGM0NCJ9fX0="),
    THREE(3, ChatColor.BLUE,"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTlhMWMxOTFlMGViYWJlODlkZGYxOGE4YmFjOGY0MjgwZTNhYzZiYzY2MWMxM2NlMWRmZjY3NGRhZDI4ODVlMyJ9fX0="),
    FOUR(4, ChatColor.DARK_GREEN,"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTFmZGM4YTk1YzEzM2NlYTRlZDNlNGQ0Njg0MWNkMjM1YmRmYmJlZjYwN2I0MDAzYjM5ZjQ0NzQ1NzQ5OTQyMSJ9fX0="),
    FIVE(5, ChatColor.GREEN,"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmNmZTg2ODQ4MjdiMDUxM2UzMTBiNDVlODAyMzc2ZTEzM2YxYTI4MmZkYzEzNTBjZGQ0ZjdiZWExYmNjNzllZiJ9fX0=");

    private final int level;
    private final ChatColor color;
    private final String skullTexture;

    TownRankEnum(int level, ChatColor color, String skullTexture){
        this.level = level;
        this.color = color;
        this.skullTexture = skullTexture;
    }

    public int getLevel() {
        return level;
    }

    public ChatColor getColor() {
        return color;
    }
    public String getSkullTexture() {
        return skullTexture;
    }

    public TownRankEnum getRankByLevel(int level){
        return switch (level) {
            case 2 -> TWO;
            case 3 -> THREE;
            case 4 -> FOUR;
            case 5 -> FIVE;
            default -> ONE;
        };
    }

    public TownRankEnum nextRank(){
        return getRankByLevel((this.getLevel() % 5) + 1);
    }

    public ItemStack getRankGuiIcon(){
        return HeadUtils.makeSkullB64(
                this.getColor() + Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_X.get(getLevel()),
                getSkullTexture(),
                Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_DESC1.get(),
                Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_DESC2.get());
    }

}
