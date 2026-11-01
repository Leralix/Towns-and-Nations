package org.leralix.tan.gui.service.requirements.model;

import org.bukkit.Material;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.Placeholder;

public class MaterialScope extends ItemScope{

    private final Material materialInScope;

    public MaterialScope(Material materialInScope){
        this.materialInScope = materialInScope;
    }


    @Override
    public boolean isInScope(Material material) {
        return this.materialInScope == material;
    }

    @Override
    public Placeholder getName() {
        String name = materialInScope.name().toLowerCase().replace("_", " ");
        return Lang.of(name.substring(0, 1).toUpperCase() + name.substring(1));
    }
}
