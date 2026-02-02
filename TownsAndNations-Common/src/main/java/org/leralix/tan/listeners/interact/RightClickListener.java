package org.leralix.tan.listeners.interact;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.HashMap;

public class RightClickListener implements Listener {

    private final PlayerDataStorage playerDataStorage;

    public RightClickListener(PlayerDataStorage playerDataStorage){
        this.playerDataStorage = playerDataStorage;
    }

    private static final HashMap<Player, RightClickListenerEvent> events = new HashMap<>();


    @EventHandler
    public void OnPlayerInteractEvent(PlayerInteractEvent event) {

        if(event.getHand() == EquipmentSlot.OFF_HAND)
            return;

        if(event.getItem() != null){
            return;
        }

        Player player = event.getPlayer();
        if(event.getAction().isRightClick() && events.containsKey(player)){
            event.setCancelled(true);
            ListenerState state = events.get(player).execute(event);
            if(state == ListenerState.SUCCESS){
                events.remove(player);
            }
            if(state == ListenerState.FAILURE){
                LangType langType = playerDataStorage.get(player).getLang();
                TanChatUtils.message(player, Lang.WRITE_CANCEL_TO_CANCEL.get(langType, Lang.CANCEL_WORD.get(langType)));
            }
        }
    }

    public static void removePlayer(Player player){
        events.remove(player);
    }

    public static void register(Player player, LangType langType, RightClickListenerEvent rightClickListenerEvent){
        player.closeInventory();
        TanChatUtils.message(player, Lang.WRITE_CANCEL_TO_CANCEL.get(langType, Lang.CANCEL_WORD.get(langType)), SoundEnum.MINOR_GOOD);
        events.put(player, rightClickListenerEvent);
    }

}
