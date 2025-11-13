package org.leralix.tan.enums.permissions;

import org.bukkit.Material;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.war.legacy.InteractionStatus;

public enum GeneralChunkSetting {
    ENABLE_PVP(Material.DIAMOND_SWORD, Lang.ENABLE_PVP_SETTING),
    FIRE_GRIEF(Material.FLINT_AND_STEEL, Lang.ENABLE_FIRE_GRIEF_SETTING),
    TNT_GRIEF(Material.TNT, Lang.ENABLE_TNT_GRIEF_SETTING),
    MOB_GRIEF(Material.ENDER_PEARL, Lang.ENABLE_MOB_GRIEF_SETTING);

    private final Material material;
    private final Lang name;

    GeneralChunkSetting(Material icon, Lang name) {
        this.material = icon;
        this.name = name;
    }

    public IconBuilder getIcon(IconManager iconManager, boolean isEnabled, LangType lang) {

        IconBuilder iconBuilder = iconManager.get(this.material)
                .setName(name.get(lang));

        InteractionStatus state = Constants.getChunkSettings(this);
        boolean canBeModified = state != InteractionStatus.ALWAYS && state != InteractionStatus.NEVER && state != InteractionStatus.WAR_ONLY;

        if(canBeModified) {
            String status = isEnabled ? Lang.ENABLED.get(lang) : Lang.DISABLED.get(lang);
            iconBuilder.setDescription(Lang.CURRENT_STATE.get(status));
            iconBuilder.setClickToAcceptMessage(Lang.LEFT_CLICK_TO_MODIFY);
        }
        else {

            if(state == InteractionStatus.ALWAYS){
                iconBuilder.setDescription(Lang.CURRENT_STATE.get(Lang.ENABLED.get(lang)));
            }
            else if (state == InteractionStatus.NEVER) {
                iconBuilder.setDescription(Lang.CURRENT_STATE.get(Lang.DISABLED.get(lang)));
            }
            iconBuilder.setClickToAcceptMessage(Lang.CANNOT_BE_MODIFIED);
        }
        return iconBuilder;
    }
}
