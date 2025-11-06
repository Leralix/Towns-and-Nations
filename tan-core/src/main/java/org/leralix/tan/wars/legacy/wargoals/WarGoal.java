package org.leralix.tan.wars.legacy.wargoals;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public abstract class WarGoal {

  /** Used for serialization */
  private final String type;

  protected WarGoal() {
    this.type = this.getClass().getSimpleName();
  }

  public abstract IconBuilder getIcon(LangType langType);

  public abstract String getDisplayName(LangType langType);

  public abstract void applyWarGoal(TerritoryData winner, TerritoryData loser);

  public abstract boolean isCompleted();

  protected IconBuilder buildIcon(
      Material material, List<FilledLang> description, LangType langType) {
    ItemStack itemStack = new ItemStack(material);

    return IconManager.getInstance()
        .get(itemStack)
        .setName(getDisplayName(langType))
        .setDescription(description)
        .setClickToAcceptMessage(Lang.GUI_GENERIC_RIGHT_CLICK_TO_DELETE);
  }

  public abstract String getCurrentDesc(LangType langType);
}
