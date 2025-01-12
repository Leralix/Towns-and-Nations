package org.leralix.tan.enums;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.leralix.tan.lang.Lang;

public class MapSettings {

    ClaimAction claimAction;
    ClaimType claimType;


    public MapSettings(){
        this.claimType = ClaimType.TOWN;
        this.claimAction = ClaimAction.CLAIM;
    }
    public MapSettings(String claimActionTypeName, String mapTypeName){
        this.claimAction = ClaimAction.valueOf(mapTypeName.toUpperCase());
        this.claimType = ClaimType.valueOf(claimActionTypeName.toUpperCase());
    }

    private String getNextCommand(ClaimType mapType, ClaimAction claimAction){
        return "/tan map " + mapType.getTypeCommand() + " " + claimAction.getTypeCommand();
    }

    public ClaimType getClaimType() {
        return claimType;
    }

    public ClaimAction getClaimActionType() {
        return claimAction;
    }

    public TextComponent getMapTypeButton(){
        TextComponent claimButton = new TextComponent(claimType.getName());
        claimButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Lang.LEFT_CLICK_TO_MODIFY.get())));
        claimButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getNextCommand(claimType.getNextType(), claimAction)));
        return claimButton;
    }

    public TextComponent getClaimTypeButton(){
        TextComponent claimButton = new TextComponent(claimAction.getName());
        claimButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Lang.LEFT_CLICK_TO_MODIFY.get())));
        claimButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getNextCommand(claimType, claimAction.getNextType())));
        return claimButton;
    }
}
