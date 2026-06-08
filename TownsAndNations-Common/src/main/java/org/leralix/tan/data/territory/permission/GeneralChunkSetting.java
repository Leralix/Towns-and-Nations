package org.leralix.tan.data.territory.permission;

import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.constants.InteractionStatus;

import java.util.ArrayList;
import java.util.List;

public enum GeneralChunkSetting {
    ENABLE_PVP(IconKey.ENABLE_PVP_ICON, Lang.ENABLE_PVP_SETTING),
    FIRE_GRIEF(IconKey.ENABLE_FIRE_GRIEF_ICON, Lang.ENABLE_FIRE_GRIEF_SETTING),
    TNT_GRIEF(IconKey.ENABLE_TNT_GRIEF_ICON, Lang.ENABLE_TNT_GRIEF_SETTING),
    VILLAGER_GRIEF(IconKey.ENABLE_VILLAGER_GRIEF_ICON, Lang.ENABLE_VILLAGER_GRIEF_SETTING),
    PASSIVE_MOB_GRIEF(IconKey.ENABLE_PASSIVE_MOB_GRIEF_ICON, Lang.ENABLE_PASSIVE_MOB_GRIEF_SETTING),
    HOSTILE_MOB_GRIEF(IconKey.ENABLE_HOSTILE_MOB_GRIEF_ICON, Lang.ENABLE_HOSTILE_MOB_GRIEF_SETTING),
    ALLOW_UNAUTHORIZED_PLAYER_MOUNTS(IconKey.ALLOW_UNAUTHORIZED_PLAYER_MOUNTS_ICON, Lang.ALLOW_UNAUTHORIZED_PLAYER_MOUNTS_SETTING);

    private final IconKey iconKey;
    private final Lang name;

    GeneralChunkSetting(IconKey iconKey, Lang name) {
        this.iconKey = iconKey;
        this.name = name;
    }

    public IconBuilder getIcon(IconManager iconManager, boolean isEnabled, LangType lang) {

        IconBuilder iconBuilder = iconManager.get(this.iconKey)
                .setName(name.get(lang));

        InteractionStatus state = Constants.getChunkSettings(this);
        boolean canBeModified = state != InteractionStatus.ALWAYS && state != InteractionStatus.NEVER && state != InteractionStatus.WAR_ONLY;
        boolean autoTrueIfWar = state == InteractionStatus.PLAYER_CHOICE_AND_WAR;



        if(canBeModified) {
            String status = isEnabled ? Lang.ENABLED.get(lang) : Lang.DISABLED.get(lang);

            List<FilledLang> desc = new ArrayList<>();
            desc.add(Lang.CURRENT_STATE.get(status));
            if(autoTrueIfWar){
                desc.add(Lang.SET_TRUE_IF_AT_WAR.get());
            }
            iconBuilder.setDescription(desc);
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
