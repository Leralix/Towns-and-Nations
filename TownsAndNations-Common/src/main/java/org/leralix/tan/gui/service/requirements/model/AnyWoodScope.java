package org.leralix.tan.gui.service.requirements.model;

import org.bukkit.Material;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public class AnyWoodScope extends ItemScope {

    private static final Material CHERRY_WOOD_MATERIAL = Material.matchMaterial("CHERRY_WOOD");

    @Override
    public boolean isInScope(Material material) {
        return material == Material.ACACIA_WOOD ||
                material == Material.BIRCH_WOOD ||
                (CHERRY_WOOD_MATERIAL != null && material == CHERRY_WOOD_MATERIAL) ||
                material == Material.JUNGLE_WOOD ||
                material == Material.MANGROVE_WOOD ||
                material == Material.OAK_WOOD ||
                material == Material.DARK_OAK_WOOD||
                material == Material.SPRUCE_WOOD ||
                material == Material.CRIMSON_HYPHAE ||
                material == Material.WARPED_HYPHAE ;
    }

    @Override
    public String getName(LangType langType) {
        return Lang.ANY_WOOD_SCOPE_NAME.get(langType);
    }
}
