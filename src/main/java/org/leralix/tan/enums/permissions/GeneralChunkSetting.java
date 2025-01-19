package org.leralix.tan.enums.permissions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.wars.GriefAllowed;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public enum GeneralChunkSetting {
    ENABLE_PVP(Material.DIAMOND_SWORD, Lang.ENABLE_PVP_SETTING.get(), "pvpEnabledInClaimedChunks"),
    FIRE_GRIEF(Material.FLINT_AND_STEEL, Lang.ENABLE_FIRE_GRIEF_SETTING.get(), "fireGrief"),
    TNT_GRIEF(Material.TNT, Lang.ENABLE_TNT_GRIEF_SETTING.get(), "explosionGrief"),;

    private final Material material;
    private final String name;
    private final GriefAllowed setting;

    GeneralChunkSetting(Material icon, String name, String configPath) {
        this.material = icon;
        this.name = name;
        this.setting = GriefAllowed.valueOf(ConfigUtil.getCustomConfig(ConfigTag.TAN).getString(configPath,"ALWAYS"));
    }

    public ItemStack getIcon(Boolean isEnabled) {

        String state = isEnabled ? Lang.ENABLED.get() : Lang.DISABLED.get();

        ItemStack icon = new ItemStack(this.material);
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(getDescription(state));
        icon.setItemMeta(meta);
        return icon;
    }

    public List<String> getDescription(String isEnabled) {

        boolean canBeModified = setting != GriefAllowed.ALWAYS && setting != GriefAllowed.NEVER && setting != GriefAllowed.WAR_ONLY;
        List<String> description = new ArrayList<>();

        if(canBeModified) {
            description.add(Lang.CURRENT_STATE.get(isEnabled));
            description.add(Lang.LEFT_CLICK_TO_MODIFY.get());
        }
        else {
            description.add(Lang.CANNOT_BE_MODIFIED.get());
        }
        return description;


    }
}
