package org.leralix.tan.enums.permissions;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.wars.legacy.InteractionStatus;

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

  public ItemStack getIcon(Boolean isEnabled, LangType lang) {
    ItemStack icon = new ItemStack(this.material);
    ItemMeta meta = icon.getItemMeta();
    org.leralix.tan.utils.text.ComponentUtil.setDisplayName(meta, name.get(lang));
    org.leralix.tan.utils.text.ComponentUtil.setLore(meta, getDescription(isEnabled, lang));
    icon.setItemMeta(meta);
    return icon;
  }

  public List<String> getDescription(boolean isEnabled, LangType lang) {

    InteractionStatus state = Constants.getChunkSettings(this);
    boolean canBeModified =
        state != InteractionStatus.ALWAYS
            && state != InteractionStatus.NEVER
            && state != InteractionStatus.WAR_ONLY;

    List<String> description = new ArrayList<>();
    if (canBeModified) {
      String status = isEnabled ? Lang.ENABLED.get(lang) : Lang.DISABLED.get(lang);
      description.add(Lang.CURRENT_STATE.get(lang, status));
      description.add(Lang.LEFT_CLICK_TO_MODIFY.get(lang));
    } else {
      description.add(Lang.CANNOT_BE_MODIFIED.get(lang));
    }
    return description;
  }
}
