package org.leralix.tan.gui.service.requirements.model;

import org.bukkit.Material;
import org.leralix.tan.lang.LangType;

public class MaterialScope extends ItemScope {

  private final Material materialInScope;

  public MaterialScope(Material materialInScope) {
    this.materialInScope = materialInScope;
  }

  @Override
  public boolean isInScope(Material material) {
    return this.materialInScope == material;
  }

  @Override
  public String getName(LangType langType) {
    String name = materialInScope.name().toLowerCase().replace("_", " ");
    return name.substring(0, 1).toUpperCase() + name.substring(1);
  }
}
