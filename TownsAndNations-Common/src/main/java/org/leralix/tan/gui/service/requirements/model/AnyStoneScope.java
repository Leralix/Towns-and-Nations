package org.leralix.tan.gui.service.requirements.model;

import org.bukkit.Material;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public class AnyStoneScope extends ItemScope {


    @Override
    public boolean isInScope(Material material) {
        return material == Material.STONE ||
                material == Material.COBBLESTONE ||
                material == Material.COBBLED_DEEPSLATE ||
                material == Material.DEEPSLATE ||
                material == Material.ANDESITE ||
                material == Material.DIORITE ||
                material == Material.GRANITE;
    }

    @Override
    public String getName(LangType langType) {
        return Lang.ANY_STONE_SCOPE_NAME.get(langType);
    }
}
