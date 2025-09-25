package org.leralix.tan.enums;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public class MapSettings {

    ClaimAction claimAction;
    ClaimType claimType;


    public MapSettings(){
        this.claimType = ClaimType.TOWN;
        this.claimAction = ClaimAction.CLAIM;
    }
    public MapSettings(String claimActionTypeName, String mapTypeName){
        this.claimAction = ClaimAction.valueOf(claimActionTypeName.toUpperCase());
        this.claimType = ClaimType.valueOf(mapTypeName.toUpperCase());
    }

    private String getNextCommand(ClaimAction claimAction , ClaimType mapType){
        return "/tan map " + claimAction.getTypeCommand() + " " + mapType.getTypeCommand();
    }

    public ClaimType getClaimType() {
        return claimType;
    }

    public ClaimAction getClaimActionType() {
        return claimAction;
    }

    public TextComponent getMapTypeButton(LangType langType){
        TextComponent claimButton = new TextComponent(claimType.getName(langType));
        claimButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Lang.LEFT_CLICK_TO_MODIFY.get(langType))));
        claimButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getNextCommand(claimAction, claimType.getNextType())));
        return claimButton;
    }

    public TextComponent getClaimTypeButton(LangType langType){
        TextComponent claimButton = new TextComponent(claimAction.getName(langType));
        claimButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Lang.LEFT_CLICK_TO_MODIFY.get(langType))));
        claimButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getNextCommand(claimAction.getNextType(), claimType)));
        return claimButton;
    }
}
