package org.leralix.tan.gui.user;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RankEnum;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.RenameRank;
import org.leralix.tan.utils.GuiUtil;
import org.leralix.tan.utils.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.leralix.lib.data.SoundEnum.*;

public class RankManagerMenu extends BasicGui {

    private final TerritoryData territoryData;
    private final RankData rankData;

    public RankManagerMenu(Player player, TerritoryData territoryData, RankData rankData){
        super(player, Lang.HEADER_TERRITORY_RANKS.get(player, rankData.getName()), 4);
        this.territoryData = territoryData;
        this.rankData = rankData;
        open();
    }

    @Override
    public void open() {

        fillTopLayer(rankData.getRankEnum().getRankColorGuiIcon());

        gui.setItem(1, 5, getRankIcon());

        gui.setItem(2, 2, getRankLevel());
        gui.setItem(2, 3, getAddPlayerButton());
        gui.setItem(2, 4, getManagePermissionIcon());

        gui.setItem(2 ,6, lowerSalaryButton());
        gui.setItem(2, 7, getSalaryIcon());
        gui.setItem(2, 8, increaseSalaryButton());

        gui.setItem(3, 2, getRenameRankButton());
        gui.setItem(3, 3, getPayTaxRankButton());
        gui.setItem(3, 4, getDefaultRankButton());

        gui.setItem(3, 6, deleteRankIcon());

        gui.setItem(4, 1, GuiUtil.createBackArrow(player,p -> new TerritoryRanksMenu(player, territoryData).open()));
        gui.open(player);

    }

    private void fillTopLayer(GuiItem rankColorGuiIcon) {
        for(int i = 0; i <= 8; i++){
            gui.setItem(i, rankColorGuiIcon);
        }
    }

    private GuiItem deleteRankIcon() {
        boolean isEmpty = rankData.getPlayersID().isEmpty();
        boolean isDefaultRank = Objects.equals(rankData.getID(), territoryData.getDefaultRankID());


        return iconManager.get(IconKey.DELETE_RANK_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_ROLE_DELETE.get(playerData))
                .setDescription(Lang.GUI_GENERIC_CLICK_TO_PROCEED.get(playerData))
                .setAction(event -> {
                    if(!isEmpty){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_DELETE_ERROR_NOT_EMPTY.get(playerData));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    if(isDefaultRank){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_DELETE_ERROR_DEFAULT.get(playerData));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }
                    territoryData.removeRank(rankData.getID());
                    new TerritoryRanksMenu(player, territoryData).open();
                    SoundUtil.playSound(player, MINOR_GOOD);
                })
                .asGuiItem(player);
    }

    private GuiItem getDefaultRankButton() {
        boolean isDefaultRank = Objects.equals(rankData.getID(), territoryData.getDefaultRankID());

        List<String> description = new ArrayList<>();
        description.add(Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT1.get(playerData));
        if(isDefaultRank)
            description.add(Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT2.get(playerData));

        return iconManager.get(IconKey.SET_DEFAULT_ROLE_ICON)
                .setName(isDefaultRank ? Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_IS_DEFAULT.get(playerData) : Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_IS_NOT_DEFAULT.get(playerData))
                .setDescription(description)
                .setAction(event -> {
                    if(isDefaultRank){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_ALREADY_DEFAULT.get(playerData));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                    }
                    else{
                        territoryData.setDefaultRank(rankData.getID());
                        open();
                        SoundUtil.playSound(player, ADD);
                    }
                })
                .asGuiItem(player);
    }

    private GuiItem getPayTaxRankButton() {
        return iconManager.get(IconKey.PAY_TAXES_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_ROLE_PAY_TAXES_NAME.get(playerData))
                .setDescription(
                        rankData.isPayingTaxes() ?
                                Lang.GUI_TOWN_MEMBERS_ROLE_PAY_TAXES.get(playerData) :
                                Lang.GUI_TOWN_MEMBERS_ROLE_NOT_PAY_TAXES.get(playerData),
                        Lang.GUI_GENERIC_CLICK_TO_MODIFY.get(playerData))
                .setAction(event -> {
                    rankData.swapPayingTaxes();
                    SoundUtil.playSound(player, ADD);
                    open();
                })
                .asGuiItem(player);
    }

    private GuiItem getRenameRankButton() {
        return iconManager.get(IconKey.RENAME_RANK_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_ROLE_CHANGE_NAME.get(playerData))
                .setDescription(Lang.GUI_GENERIC_CLICK_TO_PROCEED.get(playerData))
                .setAction(event -> {
                    player.sendMessage(TanChatUtils.getTANString() + Lang.WRITE_IN_CHAT_NEW_ROLE_NAME.get(playerData));
                    PlayerChatListenerStorage.register(player, new RenameRank(territoryData , rankData));
                    player.closeInventory();
                })
                .asGuiItem(player);
    }

    private GuiItem increaseSalaryButton() {
        return iconManager.get(IconKey.INCREASE_SALARY_ICON)
                .setName(Lang.GUI_LOWER_SALARY.get(playerData))
                .setDescription(
                        Lang.GUI_INCREASE_1_DESC.get(playerData),
                        Lang.GUI_INCREASE_10_DESC.get(playerData)
                )
                .setAction(event -> {
                    int amountToAdd = event.isShiftClick() ? 10 : 1;

                    rankData.addFromSalary(amountToAdd);
                    SoundUtil.playSound(player, ADD);
                    open();
                })
                .asGuiItem(player);
    }

    private GuiItem getSalaryIcon() {
        return iconManager.get(IconKey.CURRENT_SALARY_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_ROLE_SALARY.get(playerData))
                .setDescription(
                        Lang.GUI_TOWN_MEMBERS_ROLE_SALARY_DESC1.get(playerData, rankData.getSalary())
                )
                .asGuiItem(player);
    }

    private GuiItem lowerSalaryButton() {
        return iconManager.get(IconKey.DECREASE_SALARY_ICON)
                .setName(Lang.GUI_LOWER_SALARY.get(playerData))
                .setDescription(
                        Lang.GUI_DECREASE_1_DESC.get(playerData),
                        Lang.GUI_DECREASE_10_DESC.get(playerData)
                )
                .setAction(event -> {
                    int currentSalary = rankData.getSalary();
                    int amountToRemove = event.isShiftClick() && currentSalary >= 10 ? 10 : 1;

                    if (currentSalary <= 0) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_SALARY_ERROR_LOWER.get(playerData));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }

                    rankData.removeFromSalary(amountToRemove);
                    SoundUtil.playSound(player, REMOVE);
                    open();
                })
                .asGuiItem(player);
    }



    private GuiItem getManagePermissionIcon() {
        return iconManager.get(IconKey.MANAGE_PERMISSION_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_ROLE_MANAGE_PERMISSION.get(playerData))
                .setDescription(Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData))
                .setAction(event -> new ManageRankPermissionMenu(player, territoryData, rankData))
                .asGuiItem(player);
    }


    private GuiItem getRankLevel() {
        RankEnum rankEnum = rankData.getRankEnum();
        return iconManager.get(rankData.getRankEnum().getBasicRankIcon())
                .setName(rankEnum.getColor() + Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_X.get(playerData, rankEnum.getLevel()))
                .setDescription(
                        Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_DESC1.get(playerData),
                        Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_DESC2.get(playerData)
                )
                .setAction(event -> {
                    RankData playerRank = territoryData.getRank(player);
                    boolean isLeader = territoryData.isLeader(player);
                    boolean isInferiorOrEquals = playerRank.getRankEnum().getLevel() <= (rankData.getRankEnum().getLevel() + 1);

                    if(isInferiorOrEquals && !isLeader){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_RANK_UP_INFERIOR_RANK.get(playerData, playerRank.getColoredName()));
                        return;
                    }

                    if(event.isLeftClick()){
                        rankData.incrementLevel();
                        SoundUtil.playSound(player, ADD);
                    }
                    else if(event.isRightClick()){
                        rankData.decrementLevel();
                        SoundUtil.playSound(player, REMOVE);
                    }
                    open();
                })
                .asGuiItem(player);
    }

    private GuiItem getRankIcon() {

        return iconManager.get(rankData.getRankIcon())
                .setName(Lang.GUI_BASIC_NAME.get(playerData, rankData.getColoredName()))
                .setDescription(Lang.GUI_TOWN_MEMBERS_ROLE_NAME_DESC1.get(playerData))
                .setAction(event -> {
                    ItemStack itemMaterial = event.getCursor();
                    if(itemMaterial.getType() == Material.AIR){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_NO_ITEM_SHOWED.get(playerData));
                        return;
                    }
                    rankData.setRankIcon(itemMaterial);
                    open();
                    player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_CHANGED_ICON_SUCCESS.get(playerData));
                })
                .asGuiItem(player);
    }


    private GuiItem getAddPlayerButton() {

        List<String> description = new ArrayList<>();

        for(PlayerData playerData : rankData.getPlayers()){
            description.add(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO_DESC.get(this.playerData, playerData.getNameStored()));
        }
        description.add(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO_DESC1.get(playerData));


        return iconManager.get(IconKey.PLAYER_LIST_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO.get(playerData))
                .setDescription(description)
                .setAction(p -> new AssignPlayerToRankMenu(player, territoryData, rankData).open())
                .asGuiItem(player);
    }
}
