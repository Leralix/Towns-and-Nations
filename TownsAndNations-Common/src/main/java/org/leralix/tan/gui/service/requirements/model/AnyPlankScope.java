package org.leralix.tan.gui.service.requirements.model;

import org.bukkit.Material;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public class AnyPlankScope extends ItemScope {

    private static final Material CHERRY_PLANKS_MATERIAL = Material.matchMaterial("CHERRY_PLANKS");

    @Override
    public boolean isInScope(Material material) {
        return material == Material.ACACIA_PLANKS ||
                material == Material.BIRCH_PLANKS ||
                (CHERRY_PLANKS_MATERIAL != null && material == CHERRY_PLANKS_MATERIAL) ||
                material == Material.JUNGLE_PLANKS ||
                material == Material.MANGROVE_PLANKS ||
                material == Material.OAK_PLANKS ||
                material == Material.DARK_OAK_PLANKS||
                material == Material.SPRUCE_PLANKS ||
                material == Material.CRIMSON_PLANKS ||
                material == Material.WARPED_PLANKS ;
    }

    @Override
    public String getName(LangType langType) {
        return Lang.ANY_PLANK_SCOPE_NAME.get(langType);
    }
}
