package org.leralix.tan.enums;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.utils.HeadUtils;

public enum RolePermission {

    MANAGE_TAXES(false, Material.GOLD_INGOT, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_TAXES.get()),
    CLAIM_CHUNK(false, Material.EMERALD_BLOCK, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_CLAIM_CHUNK.get()),
    UNCLAIM_CHUNK(false, Material.REDSTONE_BLOCK, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_UNCLAIM_CHUNK.get()),
    UPGRADE_TOWN(true, Material.SPECTRAL_ARROW, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_UPGRADE_TOWN.get()),
    INVITE_PLAYER(true, Material.PLAYER_HEAD, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_INVITE_PLAYER.get()),
    KICK_PLAYER(true, Material.CREEPER_HEAD, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_KICK_PLAYER.get()),
    CREATE_RANK(false, Material.LADDER, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_CREATE_RANK.get()),
    DELETE_RANK(false, Material.CHAIN, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_DELETE_RANK.get()),
    MANAGE_RANKS(false, Material.PAPER, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MODIFY_RANK.get()),
    MANAGE_CLAIM_SETTINGS(true, Material.GRASS_BLOCK, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_CLAIM_SETTINGS.get()),
    MANAGE_TOWN_RELATION(false, Material.FLOWER_POT, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_TOWN_RELATION.get()),
    MANAGE_MOB_SPAWN(true, Material.CREEPER_HEAD, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_MOB_SPAWN.get()),
    CREATE_PROPERTY(false, Material.OAK_HANGING_SIGN, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_CREATE_PROPERTY.get()),
    MANAGE_PROPERTY(false, Material.WRITABLE_BOOK, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_PROPERTY.get()),
    TOWN_ADMINISTRATOR(true, Material.DIAMOND, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_TOWN_ADMINISTRATOR.get()),;

    private final boolean onlyTown;
    private final Material material;
    private final String description;


    RolePermission(boolean onlyTown, Material material, String description) {
        this.onlyTown = onlyTown;
        this.material = material;
        this.description = description;
    }

    public boolean isForTerritory(TerritoryData territoryData) {
        if(territoryData instanceof TownData) {
            return true;
        }
        return !onlyTown;
    }


    public GuiItem createGuiItem(Player player, TerritoryData territoryData, RankData rankData) {
        ItemStack itemStack = HeadUtils.createCustomItemStack(material, description,(rankData.hasPermission(this)) ? Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() : Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get());
        return ItemBuilder.from(itemStack).asGuiItem(event -> {
            event.setCancelled(true);
            if(!territoryData.getRank(player).hasPermission(this) && !territoryData.isLeader(player)) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.ERROR_CANNOT_CHANGE_PERMISSION_IF_PLAYER_RANK_DOES_NOT_HAVE_IT.get());
                SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                return;
            }
            rankData.switchPermission(this);
            PlayerGUI.openRankManagerPermissions(player, territoryData, rankData);
        });
    }
}