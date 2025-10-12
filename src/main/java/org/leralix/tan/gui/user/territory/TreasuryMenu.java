package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.economy.Budget;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.DonateToTerritory;
import org.leralix.tan.listeners.chat.events.RetrieveMoney;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public class TreasuryMenu extends BasicGui {

    protected final TerritoryData territoryData;
    protected final Budget budget;

    public TreasuryMenu(Player player, TerritoryData territoryData){
        super(player, Lang.HEADER_ECONOMY, 5);
        this.territoryData = territoryData;
        this.budget = territoryData.getBudget();
        open();
    }

    @Override
    public void open() {
        budget.createGui(gui, player, langType);

        gui.setItem(1, 5, getBudgetIcon());
        gui.getFiller().fillTop(GuiUtil.getUnnamedItem(Material.YELLOW_STAINED_GLASS_PANE));

        gui.setItem(2,8, getMiscSpendingsIcon());

        gui.setItem(3,2, getDonationButton());
        gui.setItem(3,3, getDonationHistoryButton());
        gui.setItem(3,4, getRetrieveButton());

        gui.setItem(5,1, GuiUtil.createBackArrow(player, p -> territoryData.openMainMenu(player)));

        gui.open(player);
    }

    protected GuiItem getBudgetIcon(){
        List<String> description = new ArrayList<>();
        description.add(Lang.GUI_TREASURY_STORAGE_DESC1.get(tanPlayer, Double.toString(territoryData.getBalance())));
        description.addAll(budget.createLore(tanPlayer.getLang()));

        return iconManager.get(IconKey.BUDGET_ICON)
                .setName(Lang.GUI_TREASURY_STORAGE.get(tanPlayer))
                .setDescription(description)
                .asGuiItem(player);
    }

    protected GuiItem getMiscSpendingsIcon(){
        return iconManager.get(IconKey.MISCELLANEOUS_SPENDING_ICON)
                .setName(Lang.GUI_TREASURY_MISCELLANEOUS_SPENDING.get(tanPlayer))
                .setDescription(Lang.GUI_GENERIC_CLICK_TO_OPEN_HISTORY.get(tanPlayer))
                .setAction(action ->
                        new EconomicHistoryMenu(player, territoryData, TransactionHistoryEnum.MISCELLANEOUS)
                )
                .asGuiItem(player);
    }

    protected GuiItem getDonationButton(){
        return iconManager.get(IconKey.DONATION_ICON)
                .setName(Lang.GUI_TREASURY_DONATION.get(tanPlayer))
                .setDescription(Lang.GUI_TOWN_TREASURY_DONATION_DESC1.get(tanPlayer))
                .setAction(action -> {
                    TanChatUtils.message(player, Lang.WRITE_IN_CHAT_AMOUNT_OF_MONEY_FOR_DONATION.get(tanPlayer));
                    PlayerChatListenerStorage.register(player, new DonateToTerritory(territoryData));
                })
                .asGuiItem(player);
    }

    protected GuiItem getDonationHistoryButton(){
        return iconManager.get(IconKey.DONATION_HISTORY_ICON)
                .setName(Lang.GUI_TREASURY_DONATION_HISTORY.get(tanPlayer))
                .setDescription(Lang.GUI_GENERIC_CLICK_TO_OPEN_HISTORY.get(tanPlayer))
                .setAction(action ->
                        new EconomicHistoryMenu(player, territoryData, TransactionHistoryEnum.DONATION))
                .asGuiItem(player);
    }

    protected GuiItem getRetrieveButton(){
        return iconManager.get(IconKey.RETRIEVE_MONEY_ICON)
                .setName(Lang.GUI_TREASURY_RETRIEVE_GOLD.get(tanPlayer))
                .setDescription(Lang.GUI_TREASURY_RETRIEVE_GOLD_DESC1.get(tanPlayer))
                .setAction(action -> {
                    if(!territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.MANAGE_TAXES)){
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                        return;
                    }
                    TanChatUtils.message(player, Lang.PLAYER_WRITE_QUANTITY_IN_CHAT.get(tanPlayer));
                    PlayerChatListenerStorage.register(player,new RetrieveMoney(territoryData));
                })
                .asGuiItem(player);
    }
}
