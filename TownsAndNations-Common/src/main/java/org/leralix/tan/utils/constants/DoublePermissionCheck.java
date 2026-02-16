package org.leralix.tan.utils.constants;

import org.bukkit.configuration.ConfigurationSection;

public class DoublePermissionCheck {

    private final boolean breackChestNeedinteractChestPermission;
    private final boolean breakFurnaceNeedinteractFurnacePermission;


    public DoublePermissionCheck(ConfigurationSection configurationSection){
        this.breackChestNeedinteractChestPermission = configurationSection.getBoolean("BREAK_CHEST_NEED_INTERACT_CHEST_PERMISSION", true);
        this.breakFurnaceNeedinteractFurnacePermission = configurationSection.getBoolean("BREAK_FURNACE_NEED_BREAK_FURNACE_PERMISSION", true);
    }
    
    public boolean isBreackChestNeedinteractChestPermission() {
        return breackChestNeedinteractChestPermission;
    }

    public boolean isInteractFurnaceNeedBreakBlockPermission() {
        return breakFurnaceNeedinteractFurnacePermission;
    }
}
