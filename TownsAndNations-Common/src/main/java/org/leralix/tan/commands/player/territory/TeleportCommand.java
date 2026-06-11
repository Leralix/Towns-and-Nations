package org.leralix.tan.commands.player.territory;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.TeleportationRegister;
import org.leralix.tan.storage.stored.FortStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeleportCommand extends PlayerSubCommand {

    private final PlayerDataStorage playerDataStorage;
    private final FortStorage fortStorage;

    public TeleportCommand(
            PlayerDataStorage playerDataStorage,
            FortStorage fortStorage
    ){
        this.playerDataStorage = playerDataStorage;
        this.fortStorage = fortStorage;
    }

    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public String getDescription() {
        return Lang.TELEPORT_COMMAND_DESC.getDefault();
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tan teleport <name>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String currentMessage, String[] args) {
        if(args.length == 2){
            ITanPlayer tanPlayer = playerDataStorage.get(player);

            List<String> locationName = new ArrayList<>();

            for(Territory territory : TerritoryUtil.getTerritoriesAuthorizingTeleportation(tanPlayer)){
                locationName.addAll(fortStorage.getAllControlledFort(territory).stream().map(f -> f.getName().replace(" ", "-")).toList());
                locationName.add(territory.getName().replace(" ", "-"));
            }
            return locationName;
        }
        else {
            return List.of();
        }
    }

    @Override
    public void perform(Player player, String[] args) {

        if(args.length != 2){
            TanChatUtils.message(player, Lang.SYNTAX_ERROR);
            return;
        }

        String name = args[1];

        Optional<Territory> territory = TerritoryUtil.getTerritoryByName(name);
        ITanPlayer tanPlayer = playerDataStorage.get(player);
        if(territory.isPresent()){
            TeleportationRegister.teleportToTerritory(player, tanPlayer, territory.get());
            return;
        }

        if(Constants.allowFortTeleport()){
            Optional<Fort> fortOptional = fortStorage.getTerritoryByName(name);

            if(fortOptional.isPresent()){
                if(!Constants.allowFortTeleportDuringWar() && fortOptional.get().getOwner().isAtWar()){
                    TanChatUtils.message(player, Lang.CANNOT_TELEPORT_TO_FORT_WHILE_AT_WAR.get());
                    return;
                }
                TeleportationRegister.teleportToFort(player, tanPlayer, fortOptional.get());
                return;
            }
        }


        TanChatUtils.message(player, Lang.TERRITORY_NOT_FOUND);
    }
}
