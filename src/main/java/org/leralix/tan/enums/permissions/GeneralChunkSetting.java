package org.leralix.tan.enums.permissions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.war.legacy.GriefAllowed;

import java.util.ArrayList;
import java.util.List;

public enum GeneralChunkSetting {
    ENABLE_PVP(Material.DIAMOND_SWORD, Lang.ENABLE_PVP_SETTING, "pvpEnabledInClaimedChunks"),
    FIRE_GRIEF(Material.FLINT_AND_STEEL, Lang.ENABLE_FIRE_GRIEF_SETTING, "fireGrief"),
    TNT_GRIEF(Material.TNT, Lang.ENABLE_TNT_GRIEF_SETTING, "explosionGrief"),
    MOB_GRIEF(Material.ENDER_PEARL, Lang.ENABLE_MOB_GRIEF_SETTING, "mobGrief");

    private final Material material;
    private final Lang name;
    private final GriefAllowed setting;

    GeneralChunkSetting(Material icon, Lang name, String configPath) {
        this.material = icon;
        this.name = name;
        this.setting = GriefAllowed.valueOf(ConfigUtil.getCustomConfig(ConfigTag.MAIN).getString(configPath,"ALWAYS"));
    }

    public ItemStack getIcon(Boolean isEnabled, LangType lang) {
        ItemStack icon = new ItemStack(this.material);
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName(name.get(lang));
        meta.setLore(getDescription(isEnabled, lang));
        icon.setItemMeta(meta);
        return icon;
    }

    public List<String> getDescription(boolean isEnabled, LangType lang) {

        String state = isEnabled ? Lang.ENABLED.get(lang) : Lang.DISABLED.get(lang);

        boolean canBeModified = setting != GriefAllowed.ALWAYS && setting != GriefAllowed.NEVER && setting != GriefAllowed.WAR_ONLY;
        List<String> description = new ArrayList<>();

        if(canBeModified) {
            description.add(Lang.CURRENT_STATE.get(lang, state));
            description.add(Lang.LEFT_CLICK_TO_MODIFY.get(lang));
        }
        else {
            description.add(Lang.CANNOT_BE_MODIFIED.get(lang));
        }
        return description;


    }
}
