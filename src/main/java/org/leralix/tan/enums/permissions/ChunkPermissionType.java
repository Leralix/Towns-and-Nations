package org.leralix.tan.enums.permissions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.permission.RelationPermission;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.HeadUtils;

public enum ChunkPermissionType {
    INTERACT_CHEST(Material.CHEST, Lang.GUI_TOWN_CLAIM_SETTINGS_CHEST),
    INTERACT_DOOR(Material.OAK_DOOR, Lang.GUI_TOWN_CLAIM_SETTINGS_DOOR),
    BREAK_BLOCK(Material.IRON_PICKAXE, Lang.GUI_TOWN_CLAIM_SETTINGS_BREAK),
    PLACE_BLOCK(Material.BRICKS, Lang.GUI_TOWN_CLAIM_SETTINGS_BUILD),
    ATTACK_PASSIVE_MOB(Material.BEEF, Lang.GUI_TOWN_CLAIM_SETTINGS_ATTACK_PASSIVE_MOBS),
    INTERACT_BUTTON(Material.STONE_BUTTON, Lang.GUI_TOWN_CLAIM_SETTINGS_BUTTON),
    INTERACT_REDSTONE(Material.REDSTONE, Lang.GUI_TOWN_CLAIM_SETTINGS_REDSTONE),
    INTERACT_FURNACE(Material.FURNACE, Lang.GUI_TOWN_CLAIM_SETTINGS_FURNACE),
    INTERACT_ITEM_FRAME(Material.ITEM_FRAME, Lang.GUI_TOWN_CLAIM_SETTINGS_INTERACT_ITEM_FRAME),
    INTERACT_ARMOR_STAND(Material.ARMOR_STAND, Lang.GUI_TOWN_CLAIM_SETTINGS_INTERACT_ARMOR_STAND),
    INTERACT_DECORATIVE_BLOCK(Material.CAULDRON, Lang.GUI_TOWN_CLAIM_SETTINGS_DECORATIVE_BLOCK),
    INTERACT_MUSIC_BLOCK(Material.JUKEBOX, Lang.GUI_TOWN_CLAIM_SETTINGS_MUSIC_BLOCK),
    USE_LEAD(Material.LEAD, Lang.GUI_TOWN_CLAIM_SETTINGS_LEAD),
    USE_SHEARS(Material.SHEARS, Lang.GUI_TOWN_CLAIM_SETTINGS_SHEARS),
    INTERACT_BOAT(Material.OAK_BOAT, Lang.GUI_TOWN_CLAIM_SETTINGS_PLACE_BOAT),
    INTERACT_MINECART(Material.MINECART, Lang.GUI_TOWN_CLAIM_SETTINGS_PLACE_VEHICLE),
    USE_BONE_MEAL(Material.BONE_MEAL, Lang.GUI_TOWN_CLAIM_SETTINGS_USE_BONE_MEAL),
    INTERACT_BERRIES(Material.SWEET_BERRIES, Lang.GUI_TOWN_CLAIM_SETTINGS_GATHER_BERRIES);

    private final Material material;
    private final Lang label;

    ChunkPermissionType(Material material, Lang label) {
        this.material = material;
        this.label = label;
    }

    public String getLabel(LangType langType) {
        return label.get(langType);
    }

    public ItemStack getIcon(RelationPermission permission, LangType langType) {
        return HeadUtils.createCustomItemStack(
                material,
                label.get(langType),
                Lang.GUI_TOWN_CLAIM_SETTINGS_DESC1.get(langType, permission.getColoredName()),
                Lang.GUI_LEFT_CLICK_TO_INTERACT.get(langType),
                Lang.GUI_RIGHT_CLICK_TO_MANAGE_SPECIFIC_PLAYER.get(langType)
        );
    }
}
