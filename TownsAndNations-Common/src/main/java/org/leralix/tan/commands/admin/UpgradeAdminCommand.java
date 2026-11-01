package org.leralix.tan.commands.admin;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.upgrade.Upgrade;
import org.leralix.tan.gui.scope.BrowseScope;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class UpgradeAdminCommand extends PlayerSubCommand {

    private final String name;
    private final String description;
    private final String syntax;

    private final PlayerDataStorage playerDataStorage;

    protected UpgradeAdminCommand(
            PlayerDataStorage playerDataStorage,
            String name,
            String description,
            String syntax
    ) {
        this.playerDataStorage = playerDataStorage;
        this.name = name;
        this.description = description;
        this.syntax = syntax;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public int getArguments() {
        return 2;
    }

    @Override
    public String getSyntax() {
        return syntax;
    }

    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
        return switch (args.length){
            case 2 -> getTerritoryList();
            case 3 -> getUpgradeList(args[1]);
            default -> Collections.emptyList();
        };
    }

    private List<String> getUpgradeList(String territoryName) {
        Optional<Territory> territory = TerritoryUtil.getTerritoryByName(territoryName);
        return territory
                .map(value -> Constants.getUpgradeStorage().getUpgrades(value).stream().map(Upgrade::getID).toList())
                .orElseGet(() -> List.of("Unknown territory"));
    }

    private List<String> getTerritoryList() {
        return TerritoryUtil.getTerritories(BrowseScope.ALL).stream().map(territory -> territory.getName().replace(" ", "-")).toList();
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length != 3) {
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO);
            return;
        }

        Optional<Territory> optionalTerritory = TerritoryUtil.getTerritoryByName(args[1]);

        if(optionalTerritory.isEmpty()){
            TanChatUtils.message(player, Lang.TERRITORY_NOT_FOUND);
            return;
        }

        Upgrade upgrade = Constants.getUpgradeStorage().getUpgrade(optionalTerritory.get(), args[2]);
        allowTerritory(player, optionalTerritory.get(), upgrade);
    }

    protected abstract void allowTerritory(Player tanPlayer, Territory territory, Upgrade upgrade);
}
