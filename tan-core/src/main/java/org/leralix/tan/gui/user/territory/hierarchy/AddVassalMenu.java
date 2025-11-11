package org.leralix.tan.gui.user.territory.hierarchy;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

public class AddVassalMenu extends IteratorGUI {

  private final TerritoryData overlordTerritory;

  private AddVassalMenu(Player player, ITanPlayer tanPlayer, TerritoryData overlordTerritory) {
    super(player, tanPlayer, Lang.GUI_INVITE_TOWN_TO_REGION.get(tanPlayer), 6);
    this.overlordTerritory = overlordTerritory;
  }

  public static void open(Player player, TerritoryData overlordTerritory) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new AddVassalMenu(player, tanPlayer, overlordTerritory).open();
            });
  }

  @Override
  public void open() {
    iterator(getAvailableTowns(), p -> VassalsMenu.open(player, overlordTerritory));
    gui.open(player);
  }

  private List<GuiItem> getAvailableTowns() {
    List<GuiItem> items = new ArrayList<>();
    List<TownData> allTowns = new ArrayList<>(TownDataStorage.getInstance().getAllSync().values());

    for (TownData town : allTowns) {
      // Skip if already a vassal or is the overlord itself
      if (overlordTerritory.getID().equals(town.getID())
          || overlordTerritory.getVassals().contains(town)) {
        continue;
      }

      // Skip if town already has an overlord
      if (town.haveOverlord()) {
        continue;
      }

      // Skip if proposal already sent
      if (town.containsVassalisationProposal(overlordTerritory)) {
        continue;
      }

      ItemStack townIcon =
          town.getIconWithInformationAndRelation(overlordTerritory, tanPlayer.getLang());

      GuiItem townButton =
          iconManager
              .get(town.getIcon())
              .setName(town.getColoredName())
              .setDescription(
                  Lang.GUI_TOWN_INFO_DESC0.get(town.getDescription()),
                  Lang.GUI_TOWN_INFO_DESC1.get(town.getLeaderNameSync()),
                  Lang.GUI_TOWN_INFO_DESC2.get(Integer.toString(town.getPlayerIDList().size())),
                  Lang.GUI_TOWN_INFO_DESC3.get(Integer.toString(town.getNumberOfClaimedChunk())))
              .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
              .setAction(
                  action -> {
                    town.addVassalisationProposal(overlordTerritory);
                    TanChatUtils.message(
                        player, Lang.VASSALISATION_PROPOSAL_SENT_SUCCESS.get(tanPlayer));
                    open();
                  })
              .asGuiItem(player, langType);

      items.add(townButton);
    }

    return items;
  }
}
