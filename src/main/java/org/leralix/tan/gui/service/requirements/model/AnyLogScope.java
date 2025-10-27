package org.leralix.tan.gui.service.requirements.model;

import org.bukkit.Material;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public class AnyLogScope extends ItemScope {


    @Override
    public boolean isInScope(Material material) {
        return material == Material.ACACIA_LOG ||
                material == Material.BIRCH_LOG ||
                material == Material.CHERRY_LOG ||
                material == Material.JUNGLE_LOG ||
                material == Material.MANGROVE_LOG ||
                material == Material.OAK_LOG ||
                material == Material.DARK_OAK_LOG||
                material == Material.SPRUCE_LOG ||
                material == Material.CRIMSON_STEM ||
                material == Material.WARPED_STEM ;
    }

    @Override
    public String getName(LangType langType) {
        return Lang.ANY_LOG_SCOPE_NAME.get(langType);
    }
}
