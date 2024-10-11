package org.leralix.tan.enums;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.dataclass.TownRank;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.utils.HeadUtils;

public enum TownRolePermission {

    MANAGE_TAXES(Material.GOLD_INGOT, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_TAXES.get()),
    PROMOTE_RANK_PLAYER(Material.EMERALD, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_PROMOTE_RANK_PLAYER.get()),
    DERANK_RANK_PLAYER(Material.REDSTONE, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_DERANK_RANK_PLAYER.get()),
    CLAIM_CHUNK(Material.EMERALD_BLOCK, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_CLAIM_CHUNK.get()),
    UNCLAIM_CHUNK(Material.REDSTONE_BLOCK, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_UNCLAIM_CHUNK.get()),
    UPGRADE_TOWN(Material.SPECTRAL_ARROW, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_UPGRADE_TOWN.get()),
    INVITE_PLAYER(Material.PLAYER_HEAD, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_INVITE_PLAYER.get()),
    KICK_PLAYER(Material.CREEPER_HEAD, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_KICK_PLAYER.get()),
    CREATE_RANK(Material.LADDER, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_CREATE_RANK.get()),
    DELETE_RANK(Material.CHAIN, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_DELETE_RANK.get()),
    MANAGE_RANKS(Material.PAPER, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MODIFY_RANK.get()),
    MANAGE_CLAIM_SETTINGS(Material.GRASS_BLOCK, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_CLAIM_SETTINGS.get()),
    MANAGE_TOWN_RELATION(Material.FLOWER_POT, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_TOWN_RELATION.get()),
    MANAGE_MOB_SPAWN(Material.CREEPER_HEAD, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_MOB_SPAWN.get()),
    CREATE_PROPERTY(Material.OAK_HANGING_SIGN, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_CREATE_PROPERTY.get()),
    MANAGE_PROPERTY(Material.WRITABLE_BOOK, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_PROPERTY.get()),
    TOWN_ADMINISTRATOR(Material.DIAMOND, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_TOWN_ADMINISTRATOR.get()),;

    public final Material material;
    public final String description;


    TownRolePermission(Material material, String description) {
        this.material = material;
        this.description = description;
    }


    public GuiItem createGuiItem(Player player, TownRank townRank) {
        ItemStack itemStack = HeadUtils.createCustomItemStack(material, description,(townRank.hasPermission(this)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        return ItemBuilder.from(itemStack).asGuiItem(event -> {
            townRank.switchPermission(this);
            PlayerGUI.openTownRankManagerPermissions(player, townRank.getID());
            event.setCancelled(true);
        });
    }
}