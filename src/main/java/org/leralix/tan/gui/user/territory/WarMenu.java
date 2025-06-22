package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.territory.StrongholdData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlannedAttackStorage;
import org.leralix.tan.utils.GuiUtil;
import org.leralix.tan.utils.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class WarMenu extends IteratorGUI {

    private final TerritoryData territoryData;

    public WarMenu(Player player, TerritoryData territoryData){
        super(player, Lang.HEADER_WARS_MENU, 6);
        this.territoryData = territoryData;
        open();
    }

    @Override
    public void open() {

        GuiUtil.createIterator(gui, getWars(tanPlayer), page, player,
                p -> territoryData.openMainMenu(player),
                p -> nextPage(),
                p -> previousPage());

        gui.setItem(6, 5, getStrongholdButton());

        gui.open(player);

    }

    private GuiItem getStrongholdButton() {
        StrongholdData territoryStronghold = territoryData.getStronghold();

        List<String> description = new ArrayList<>();

        if(territoryStronghold == null){
            description.add(Lang.GUI_NO_STRONGHOLD.get(tanPlayer));
        }
        else {
            int x = territoryStronghold.getClaimedChunk().getX() * 16 + 8;
            int z = territoryStronghold.getClaimedChunk().getZ() * 16 + 8;
            description.add(Lang.GUI_STRONGHOLD_LOCATION.get(tanPlayer, x,z));
            description.add(Lang.GUI_GENERIC_CLICK_TO_MODIFY.get(tanPlayer));
        }


        return iconManager.get(IconKey.STRONGHOLD_INFO_ICON)
                .setName(Lang.GUI_STRONGHOLD.get(tanPlayer))
                .setDescription(description)
                .setAction(action -> {
                            if(territoryStronghold != null){
                                if(!territoryData.doesPlayerHavePermission(player, RolePermission.TOWN_ADMINISTRATOR)){
                                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                                    SoundUtil.playSound(player,NOT_ALLOWED);
                                }
                                else{
                                    Chunk playerChunk = player.getLocation().getChunk();
                                    ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(playerChunk);
                                    if(!claimedChunk.getOwnerID().equals(territoryData.getID())){
                                        player.sendMessage(Lang.CHUNK_DO_NOT_BELONG_TO_TERRITORY.get(tanPlayer));
                                        SoundUtil.playSound(player,NOT_ALLOWED);
                                        return;
                                    }
                                    territoryData.setStrongholdPosition(playerChunk);
                                    player.sendMessage("new Stronghold is at x : " + playerChunk.getX() *16 + " z : " + playerChunk.getZ() * 16 );
                                }
                            }
                        })
                .asGuiItem(player);
    }

    private List<GuiItem> getWars(ITanPlayer tanPlayer) {
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for(PlannedAttack plannedAttack : PlannedAttackStorage.getWars()){
            ItemStack attackIcon = plannedAttack.getIcon(tanPlayer, territoryData);
            GuiItem attackButton = ItemBuilder.from(attackIcon).asGuiItem(event -> {
                event.setCancelled(true);
                if(event.isLeftClick()){
                    PlayerGUI.openSpecificPlannedAttackMenu(player, territoryData, plannedAttack);
                }
            });
            guiItems.add(attackButton);
        }
        return guiItems;
    }
}
