package org.leralix.tan.enums;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public class MapSettings {

  ClaimAction claimAction;
  ClaimType claimType;

  public MapSettings() {
    this.claimType = ClaimType.TOWN;
    this.claimAction = ClaimAction.CLAIM;
  }

  public MapSettings(String claimActionTypeName, String mapTypeName) {
    this.claimAction = ClaimAction.valueOf(claimActionTypeName.toUpperCase());
    this.claimType = ClaimType.valueOf(mapTypeName.toUpperCase());
  }

  private String getNextCommand(ClaimAction claimAction, ClaimType mapType) {
    return "/tan map " + claimAction.getTypeCommand() + " " + mapType.getTypeCommand();
  }

  public ClaimType getClaimType() {
    return claimType;
  }

  public ClaimAction getClaimActionType() {
    return claimAction;
  }

  public Component getMapTypeButton(LangType langType) {
    return Component.text(claimType.getName(langType))
        .hoverEvent(HoverEvent.showText(Component.text(Lang.LEFT_CLICK_TO_MODIFY.get(langType))))
        .clickEvent(ClickEvent.runCommand(getNextCommand(claimAction, claimType.getNextType())));
  }

  public Component getClaimTypeButton(LangType langType) {
    return Component.text(claimAction.getName(langType))
        .hoverEvent(HoverEvent.showText(Component.text(Lang.LEFT_CLICK_TO_MODIFY.get(langType))))
        .clickEvent(ClickEvent.runCommand(getNextCommand(claimAction.getNextType(), claimType)));
  }
}
