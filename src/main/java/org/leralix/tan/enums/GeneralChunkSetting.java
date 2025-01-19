package org.leralix.tan.enums;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public enum GeneralChunkSetting {
    ENABLE_PVP(Material.DIAMOND_SWORD, Lang.ENABLE_PVP_SETTING.get()),
    FIRE_GRIEF(Material.FLINT_AND_STEEL, Lang.ENABLE_FIRE_GRIEF_SETTING.get()),
    TNT_GRIEF(Material.TNT, Lang.ENABLE_TNT_GRIEF_SETTING.get()),;

    private final Material material;
    private final String name;

    GeneralChunkSetting(Material icon, String name) {
        this.material = icon;
        this.name = name;
    }

    public ItemStack getIcon(Boolean isEnabled) {

        String state = isEnabled ? Lang.ENABLED.get() : Lang.DISABLED.get();

        ItemStack icon = new ItemStack(this.material);
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName(name);
        List<String> description = new ArrayList<>();
        description.add(Lang.CURRENT_STATE.get(state));
        description.add(Lang.LEFT_CLICK_TO_MODIFY.get());
        meta.setLore(description);
        icon.setItemMeta(meta);
        return icon;
    }
}
