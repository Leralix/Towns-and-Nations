package org.leralix.tan.enums;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.leralix.tan.Lang.Lang;

public enum MapType {

    TOWN(Lang.MAP_TOWN.get(), "/tan map REGION"),
    REGION(Lang.MAP_REGION.get(), "/tan map TOWN");


    private final String buttonName;
    private final String buttonCommand;

    MapType(String buttonName, String buttonCommand){
        this.buttonName = buttonName;
        this.buttonCommand = buttonCommand;
    }


    public TextComponent getButton() {
        TextComponent claimButton = new TextComponent(buttonName);
        claimButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Lang.LEFT_CLICK_TO_MODIFY.get())));
        claimButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, buttonCommand));
        return claimButton;
    }
}
