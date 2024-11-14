package org.leralix.tan.dataclass.territory.economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

import java.util.UUID;

public class PlayerTaxLine extends ProfitLine{

    double actualTaxes = 0;
    double missingTaxes = 0;

    public PlayerTaxLine(TownData townData){
        double flatTax = townData.getFlatTax();
        for (String playerID : townData.getPlayerIDList()){
            PlayerData otherPlayerData = PlayerDataStorage.get(playerID);
            OfflinePlayer otherPlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));
            if(!otherPlayerData.getTownRank().isPayingTaxes()){
                continue;
            }
            if(EconomyUtil.getBalance(otherPlayer) < flatTax){
                missingTaxes += flatTax;
            }
            actualTaxes += flatTax;
        }

    }
    @Override
    public String getLine() {
       if(missingTaxes > 0)
           return Lang.PLAYER_TAX_MISSING_LINE.get(getColoredMoney(actualTaxes), missingTaxes);
       else
           return Lang.PLAYER_TAX_LINE.get(getColoredMoney(actualTaxes));
    }
}
