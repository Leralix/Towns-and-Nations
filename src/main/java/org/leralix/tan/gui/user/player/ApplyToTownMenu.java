package org.leralix.tan.gui.user.player;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.user.territory.NoTownMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.GuiUtil;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class ApplyToTownMenu extends IteratorGUI {


    public ApplyToTownMenu(Player player){
        super(player, Lang.HEADER_TOWN_LIST.get(player), 6);
        open();
    }

    @Override
    public void open() {

        GuiUtil.createIterator(gui, getTowns(), page, player,
                p -> new NoTownMenu(player),
                p -> nextPage(),
                p -> previousPage());

        gui.open(player);
    }


    public List<GuiItem> getTowns(){
        ArrayList<GuiItem> towns = new ArrayList<>();

        for(TownData specificTownData : TownDataStorage.getInstance().getTownMap().values()){
            ItemStack townIcon = specificTownData.getIconWithInformations(tanPlayer.getLang());
            HeadUtils.addLore(townIcon,
                    "",
                    (specificTownData.isRecruiting()) ? Lang.GUI_TOWN_INFO_IS_RECRUITING.get(tanPlayer) : Lang.GUI_TOWN_INFO_IS_NOT_RECRUITING.get(tanPlayer),
                    (specificTownData.isPlayerAlreadyRequested(player)) ? Lang.GUI_TOWN_INFO_RIGHT_CLICK_TO_CANCEL.get(tanPlayer) : Lang.GUI_TOWN_INFO_LEFT_CLICK_TO_JOIN.get(tanPlayer)
            );
            GuiItem townButton = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);

                if(event.isLeftClick()){

                    if(!player.hasPermission("tan.base.town.join")){
                        player.sendMessage(Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    if(specificTownData.isPlayerAlreadyRequested(player)){
                        return;
                    }
                    if(!specificTownData.isRecruiting()){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_TOWN_NOT_RECRUITING.get(tanPlayer));
                        return;
                    }
                    specificTownData.addPlayerJoinRequest(player);
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_ASK_TO_JOIN_TOWN_PLAYER_SIDE.get(tanPlayer, specificTownData.getName()));
                    open();
                }
                if(event.isRightClick()){
                    if(!specificTownData.isPlayerAlreadyRequested(player)){
                        return;
                    }
                    specificTownData.removePlayerJoinRequest(player);
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_REMOVE_ASK_TO_JOIN_TOWN_PLAYER_SIDE.get(tanPlayer));
                    open();
                }
            });
            towns.add(townButton);
        }
        return towns;
    }
}
