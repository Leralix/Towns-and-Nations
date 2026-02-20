package org.leralix.tan.listeners.interact.events.property;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.listeners.interact.ListenerState;
import org.leralix.tan.listeners.interact.RightClickListenerEvent;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.territory.PropertyUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class RelocateSignEvent extends RightClickListenerEvent {

    private final PropertyData propertyData;
    private final LangType langType;
    private final BasicGui returnGui;

    public RelocateSignEvent(PropertyData propertyData, LangType langType, BasicGui returnGui) {
        this.propertyData = propertyData;
        this.langType = langType;
        this.returnGui = returnGui;
    }

    @Override
    public ListenerState execute(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        if(clickedBlock == null){
            return ListenerState.CONTINUE;
        }

        Location clickLocation = clickedBlock.getLocation();

        int margin = Constants.getMaxPropertySignMargin();
        if(!PropertyUtil.isNearProperty(clickLocation, propertyData, margin)){
            TanChatUtils.message(player, Lang.PLAYER_PROPERTY_SIGN_TOO_FAR.get(langType, Integer.toString(margin)), SoundEnum.NOT_ALLOWED);
            return ListenerState.CONTINUE;
        }

        propertyData.removeSign();
        propertyData.createPropertySign(player, clickedBlock, event.getBlockFace());
        TanChatUtils.message(player, Lang.PLAYER_PROPERTY_SIGN_RELOCATED.get(langType), SoundEnum.MINOR_GOOD);

        openGui(player1 -> returnGui.open(), player);


        return ListenerState.SUCCESS;
    }
}
