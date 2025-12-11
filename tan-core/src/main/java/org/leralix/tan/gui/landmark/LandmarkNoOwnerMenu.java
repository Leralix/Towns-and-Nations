package org.leralix.tan.gui.landmark;

import static org.leralix.lib.data.SoundEnum.GOOD;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.utils.ConfirmMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.upgrade.rewards.numeric.LandmarkCap;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.deprecated.HeadUtils;

public class LandmarkNoOwnerMenu extends BasicGui {

  private final Landmark landmark;
  @Nullable private final TownData playerTown;

  private LandmarkNoOwnerMenu(
      Player player, ITanPlayer tanPlayer, Landmark landmark, @Nullable TownData playerTown) {
    super(player, tanPlayer, Lang.HEADER_LANDMARK_UNCLAIMED.get(tanPlayer.getLang()), 3);
    this.landmark = landmark;
    this.playerTown = playerTown;
  }

  public static void open(Player player, Landmark landmark) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenCompose(
            tanPlayer -> {
              if (tanPlayer.hasTown()) {
                return tanPlayer.getTown().thenApply(town -> new Object[] {tanPlayer, town});
              } else {
                return java.util.concurrent.CompletableFuture.completedFuture(
                    new Object[] {tanPlayer, null});
              }
            })
        .thenAccept(
            data -> {
              ITanPlayer tanPlayer = (ITanPlayer) ((Object[]) data)[0];
              TownData playerTown = (TownData) ((Object[]) data)[1];
              new LandmarkNoOwnerMenu(player, tanPlayer, landmark, playerTown).open();
            });
  }

  @Override
  public void open() {

    gui.setItem(1, 5, getLandmarkIcon(landmark));

    gui.setItem(2, 5, getClaimButton());

    gui.setItem(3, 1, GuiUtil.createBackArrow(player, Player::closeInventory));

    GuiItem panelGui =
        ItemBuilder.from(HeadUtils.createCustomItemStack(Material.GRAY_STAINED_GLASS_PANE, ""))
            .asGuiItem(event -> event.setCancelled(true));
    gui.getFiller().fillBottom(panelGui);
    gui.open(player);
  }

  private @NotNull GuiItem getLandmarkIcon(Landmark landmark) {
    LangType lang = tanPlayer.getLang();
    return ItemBuilder.from(landmark.getIcon(lang)).asGuiItem(event -> event.setCancelled(true));
  }

  private @NotNull GuiItem getClaimButton() {
    double cost = Constants.getClaimLandmarkCost();
    List<FilledLang> description = new ArrayList<>();

    if (cost > 0.0) {
      description.add(Lang.GUI_LANDMARK_CLAIM_COST.get(String.valueOf(cost)));
    }

    boolean isRequirementsMet = playerTown != null;

    if (playerTown != null) {
      LandmarkCap landmarkCap = playerTown.getNewLevel().getStat(LandmarkCap.class);
      int currentLandmarkCount = LandmarkStorage.getInstance().getLandmarkOf(playerTown).size();
      if (!landmarkCap.canDoAction(currentLandmarkCount)) {
        isRequirementsMet = false;
        description.add(Lang.GUI_LANDMARK_TOWN_FULL.get());
      }

      if (Constants.isLandmarkClaimRequiresEncirclement() && !landmark.isEncircledBy(playerTown)) {
        isRequirementsMet = false;
        description.add(Lang.GUI_LANDMARK_NOT_ENCIRCLED.get());
      }

      if (cost > playerTown.getBalance()) {
        isRequirementsMet = false;
        description.add(
            Lang.GUI_LANDMARK_NOT_ENOUGH_MONEY.get(
                Double.toString(Constants.getClaimLandmarkCost())));
      }
    }

    if (isRequirementsMet) {
      description.add(Lang.GUI_LANDMARK_LEFT_CLICK_TO_CLAIM.get());
    }

    IconKey iconKey =
        isRequirementsMet
            ? IconKey.GUI_CONFIRM_CLAIM_LANDMARK_REQUIREMENTS_MET_ICON
            : IconKey.GUI_CONFIRM_CLAIM_LANDMARK_REQUIREMENTS_UNMET_ICON;

    final boolean requirementMet = isRequirementsMet;
    final TownData finalPlayerTown = playerTown;

    return iconManager
        .get(iconKey)
        .setName(Lang.GUI_TOWN_RELATION_ADD_TOWN.get(tanPlayer))
        .setDescription(description)
        .setAction(
            event -> {
              if (!requirementMet || finalPlayerTown == null) {
                SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                return;
              }

              ConfirmMenu.open(
                  player,
                  Lang.GUI_LANDMARK_LEFT_CLICK_TO_CLAIM.get(),
                  p -> {
                    finalPlayerTown.removeFromBalance(cost);
                    landmark.setOwner(finalPlayerTown);
                    finalPlayerTown.broadcastMessageWithSound(
                        Lang.GUI_LANDMARK_CLAIMED.get(), GOOD);
                    player.closeInventory();
                  },
                  p -> open());
            })
        .asGuiItem(player, langType);
  }
}
