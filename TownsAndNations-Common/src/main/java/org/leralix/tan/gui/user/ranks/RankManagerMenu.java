package org.leralix.tan.gui.user.ranks;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.rank.RankData;
import org.leralix.tan.data.territory.rank.RankEnum;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.territory.TerritoryRanksMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.RenameRank;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.leralix.lib.data.SoundEnum.*;

public class RankManagerMenu extends BasicGui {

    private final TerritoryData territoryData;
    private final RankData rankData;

    public RankManagerMenu(Player player, TerritoryData territoryData, RankData rankData){
        super(player, Lang.HEADER_TERRITORY_RANKS.get(rankData.getName()), 4);
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

        gui.setItem(4, 1, GuiUtil.createBackArrow(player,p -> new TerritoryRanksMenu(player, territoryData).open(), langType));
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
                .setName(Lang.GUI_TOWN_MEMBERS_ROLE_DELETE.get(tanPlayer))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction(event -> {
                    if(!isEmpty){
                        TanChatUtils.message(player, Lang.GUI_TOWN_MEMBERS_ROLE_DELETE_ERROR_NOT_EMPTY.get(tanPlayer), NOT_ALLOWED);
                        return;
                    }
                    if(isDefaultRank){
                        TanChatUtils.message(player, Lang.GUI_TOWN_MEMBERS_ROLE_DELETE_ERROR_DEFAULT.get(tanPlayer), NOT_ALLOWED);
                        return;
                    }
                    territoryData.removeRank(rankData.getID());
                    new TerritoryRanksMenu(player, territoryData).open();
                    SoundUtil.playSound(player, MINOR_GOOD);
                })
                .asGuiItem(player, langType);
    }

    private GuiItem getDefaultRankButton() {
        boolean isDefaultRank = Objects.equals(rankData.getID(), territoryData.getDefaultRankID());

        List<FilledLang> description = new ArrayList<>();
        description.add(isDefaultRank ?
                Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_IS_DEFAULT.get() :
                Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_IS_NOT_DEFAULT.get()
        );
        description.add(Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT1.get());


        return iconManager.get(IconKey.SET_DEFAULT_ROLE_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT.get(langType))
                .setDescription(description)
                .setClickToAcceptMessage(Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT2)
                .setAction(event -> {
                    if(isDefaultRank){
                        TanChatUtils.message(player, Lang.GUI_TOWN_MEMBERS_ROLE_SET_DEFAULT_ALREADY_DEFAULT.get(tanPlayer), NOT_ALLOWED);
                    }
                    else{
                        territoryData.setDefaultRank(rankData.getID());
                        open();
                        SoundUtil.playSound(player, ADD);
                    }
                })
                .asGuiItem(player, langType);
    }

    private GuiItem getPayTaxRankButton() {
        return iconManager.get(IconKey.PAY_TAXES_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_ROLE_PAY_TAXES_NAME.get(tanPlayer))
                .setDescription(
                        rankData.isPayingTaxes() ?
                                Lang.GUI_TOWN_MEMBERS_ROLE_PAY_TAXES.get() :
                                Lang.GUI_TOWN_MEMBERS_ROLE_NOT_PAY_TAXES.get())
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_MODIFY)
                .setAction(event -> {
                    rankData.swapPayingTaxes();
                    SoundUtil.playSound(player, ADD);
                    open();
                })
                .asGuiItem(player, langType);
    }

    private GuiItem getRenameRankButton() {
        return iconManager.get(IconKey.RENAME_RANK_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_ROLE_CHANGE_NAME.get(tanPlayer))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction(event -> {
                    TanChatUtils.message(player, Lang.WRITE_IN_CHAT_NEW_ROLE_NAME.get(tanPlayer));
                    PlayerChatListenerStorage.register(player, langType, new RenameRank(territoryData , rankData));
                })
                .asGuiItem(player, langType);
    }

    private GuiItem increaseSalaryButton() {
        return iconManager.get(IconKey.INCREASE_SALARY_ICON)
                .setName(Lang.GUI_LOWER_SALARY.get(tanPlayer))
                .setDescription(
                        Lang.GUI_INCREASE_1_DESC.get(),
                        Lang.GUI_INCREASE_10_DESC.get()
                )
                .setAction(event -> {
                    int amountToAdd = event.isShiftClick() ? 10 : 1;

                    rankData.addFromSalary(amountToAdd);
                    SoundUtil.playSound(player, ADD);
                    open();
                })
                .asGuiItem(player, langType);
    }

    private GuiItem getSalaryIcon() {
        return iconManager.get(IconKey.CURRENT_SALARY_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_ROLE_SALARY.get(tanPlayer))
                .setDescription(Lang.GUI_TOWN_MEMBERS_ROLE_SALARY_DESC1.get(Integer.toString(rankData.getSalary())))
                .asGuiItem(player, langType);
    }

    private GuiItem lowerSalaryButton() {
        return iconManager.get(IconKey.DECREASE_SALARY_ICON)
                .setName(Lang.GUI_LOWER_SALARY.get(tanPlayer))
                .setDescription(
                        Lang.GUI_DECREASE_1_DESC.get(),
                        Lang.GUI_DECREASE_10_DESC.get()
                )
                .setAction(event -> {
                    int currentSalary = rankData.getSalary();
                    int amountToRemove = event.isShiftClick() && currentSalary >= 10 ? 10 : 1;

                    if (currentSalary <= 0) {
                        TanChatUtils.message(player, Lang.GUI_TOWN_MEMBERS_ROLE_SALARY_ERROR_LOWER.get(tanPlayer), NOT_ALLOWED);
                        return;
                    }

                    rankData.removeFromSalary(amountToRemove);
                    SoundUtil.playSound(player, REMOVE);
                    open();
                })
                .asGuiItem(player, langType);
    }



    private GuiItem getManagePermissionIcon() {
        return iconManager.get(IconKey.MANAGE_PERMISSION_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_ROLE_MANAGE_PERMISSION.get(tanPlayer))
                .setAction(event -> new ManageRankPermissionMenu(player, territoryData, rankData))
                .asGuiItem(player, langType);
    }


    private GuiItem getRankLevel() {
        RankEnum rankEnum = rankData.getRankEnum();
        return iconManager.get(rankData.getRankEnum().getBasicRankIcon())
                .setName(rankEnum.getColor() + Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_X.get(tanPlayer, Integer.toString(rankEnum.getLevel())))
                .setClickToAcceptMessage(
                        Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_DESC1,
                        Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_DESC2
                )
                .setAction(event -> {
                    RankData playerRank = territoryData.getRank(player);
                    boolean isLeader = territoryData.isLeader(player);
                    boolean isInferiorOrEquals = playerRank.getRankEnum().getLevel() <= (rankData.getRankEnum().getLevel() + 1);

                    if(isInferiorOrEquals && !isLeader){
                        TanChatUtils.message(player, Lang.GUI_TOWN_MEMBERS_ROLE_RANK_UP_INFERIOR_RANK.get(tanPlayer, playerRank.getColoredName()));
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
                .asGuiItem(player, langType);
    }

    private GuiItem getRankIcon() {

        return iconManager.get(rankData.getRankIcon())
                .setName(Lang.GUI_BASIC_NAME.get(tanPlayer, rankData.getColoredName()))
                .setClickToAcceptMessage(Lang.GUI_TOWN_MEMBERS_ROLE_NAME_DESC1)
                .setAction(event -> {
                    ItemStack itemMaterial = event.getCursor();
                    if(itemMaterial.getType() == Material.AIR){
                        TanChatUtils.message(player, Lang.GUI_TOWN_MEMBERS_ROLE_NO_ITEM_SHOWED.get(tanPlayer));
                        return;
                    }
                    rankData.setRankIcon(itemMaterial);
                    open();
                    TanChatUtils.message(player, Lang.GUI_TOWN_MEMBERS_ROLE_CHANGED_ICON_SUCCESS.get(tanPlayer));
                })
                .asGuiItem(player, langType);
    }


    private GuiItem getAddPlayerButton() {

        List<FilledLang> description = new ArrayList<>();

        for(ITanPlayer tanPlayer : rankData.getPlayers()){
            description.add(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO_DESC.get(tanPlayer.getNameStored()));
        }


        return iconManager.get(IconKey.PLAYER_LIST_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO.get(tanPlayer))
                .setDescription(description)
                .setClickToAcceptMessage(Lang.GUI_TOWN_MEMBERS_ROLE_MEMBER_LIST_INFO_DESC1)
                .setAction(p -> new AssignPlayerToRankMenu(player, territoryData, rankData).open())
                .asGuiItem(player, langType);
    }
}
