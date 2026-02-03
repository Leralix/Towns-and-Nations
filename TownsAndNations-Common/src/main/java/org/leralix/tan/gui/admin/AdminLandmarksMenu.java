package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.data.chunk.ClaimedChunk;
import org.leralix.tan.data.chunk.LandmarkClaimedChunk;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.GOOD;

public class AdminLandmarksMenu extends IteratorGUI {


    public AdminLandmarksMenu(Player player) {
        super(player, Lang.HEADER_ADMIN_LANDMARK_MENU, 6);
        open();
    }

    @Override
    public void open() {
        iterator(getLandmarks(), p -> new AdminMainMenu(player));

        gui.setItem(6, 4, getAddLandmarkButton());
        gui.open(player);
    }

    private GuiItem getAddLandmarkButton() {


        return iconManager.get(IconKey.CREATE_LANDMARK)
                .setName(Lang.ADMIN_GUI_CREATE_LANDMARK.get(langType))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction(action -> {
                    ClaimedChunk claimedChunk = NewClaimedChunkStorage.getInstance().get(player.getLocation().getBlock().getChunk());

                    if (claimedChunk instanceof LandmarkClaimedChunk) {
                        TanChatUtils.message(player, Lang.ADMIN_CHUNK_ALREADY_LANDMARK.get(langType));
                        return;
                    }
                    Landmark newLandmark = LandmarkStorage.getInstance().addLandmark(player.getLocation());
                    new AdminLandmarkMenu(player, newLandmark);
                })
                .asGuiItem(player, langType);
    }

    private List<GuiItem> getLandmarks() {
        ArrayList<GuiItem> guiItems = new ArrayList<>();

        for (Landmark landmark : LandmarkStorage.getInstance().getAll().values()) {
            ItemStack icon = landmark.getIcon(langType);
            HeadUtils.addLore(icon,
                    "",
                    Lang.CLICK_TO_OPEN_LANDMARK_MENU.get(langType),
                    Lang.GUI_GENERIC_SHIFT_CLICK_TO_TELEPORT.get(langType));

            GuiItem item = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);
                if (!event.isShiftClick()) {
                    new AdminLandmarkMenu(player, landmark);
                } else {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.closeInventory();
                            player.teleport(landmark.getLocation());
                        }
                    }.runTaskLater(TownsAndNations.getPlugin(), 1L);


                    SoundUtil.playSound(player, GOOD);
                }
            });
            guiItems.add(item);
        }
        return guiItems;
    }
}
