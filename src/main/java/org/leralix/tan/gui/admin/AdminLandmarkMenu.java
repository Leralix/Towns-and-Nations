package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.ChangeLandmarkName;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import static org.leralix.lib.data.SoundEnum.GOOD;

public class AdminLandmarkMenu extends BasicGui {

    private final Landmark landmark;

    public AdminLandmarkMenu(Player player, Landmark landmark) {
        super(player, Lang.HEADER_ADMIN_SPECIFIC_LANDMARK_MENU.get(player, landmark.getName()), 3);
        this.landmark = landmark;
        open();
    }

    @Override
    public void open() {

        gui.setItem(2, 3, getRenameLandmarkButton());
        gui.setItem(2, 5, getManageProductionButton());
        gui.setItem(2, 7, getDeleteLandmarkButton());

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new AdminLandmarksMenu(player)));
        gui.open(player);
    }

    private @NotNull GuiItem getDeleteLandmarkButton() {

        return iconManager.get(IconKey.DELETE_LANDMARK_ICON)
                .setName(Lang.ADMIN_GUI_DELETE_LANDMARK.get(langType))
                .setDescription(Lang.ADMIN_GUI_DELETE_LANDMARK_DESC1.get())
                .setAction(action -> {
                    landmark.deleteLandmark();
                    SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
                    new AdminLandmarksMenu(player);
                })
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getManageProductionButton() {

        ItemStack ressources = landmark.getResources();

        return iconManager.get(ressources)
                .setName(Lang.LANDMARK_PRODUCTION_NAME.get(langType))
                .setDescription(Lang.SPECIFIC_LANDMARK_ICON_DESC1.get(Integer.toString(ressources.getAmount()), ressources.getType().name().toLowerCase()))
                .setClickToAcceptMessage(Lang.SPECIFIC_LANDMARK_ICON_SWITCH_REWARD)
                .setAction(action -> {
                    ItemStack itemOnCursor = player.getItemOnCursor();
                    if (itemOnCursor.getType() == Material.AIR) {
                        return;
                    }
                    TanChatUtils.message(player, Lang.ADMIN_GUI_LANDMARK_REWARD_SET.get(player, Integer.toString(itemOnCursor.getAmount()), itemOnCursor.getType().name()), GOOD);
                    landmark.setReward(itemOnCursor);
                    open();
                })
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getRenameLandmarkButton() {
        return iconManager.get(IconKey.LANDMARK_RENAME_ICON)
                .setName(Lang.ADMIN_GUI_CHANGE_LANDMARK_NAME.get(langType))
                .setDescription(Lang.ADMIN_GUI_CHANGE_LANDMARK_NAME_DESC1.get())
                .setAction(action -> {
                    TanChatUtils.message(player, Lang.ENTER_NEW_VALUE.get(player));
                    PlayerChatListenerStorage.register(player, new ChangeLandmarkName(landmark, Constants.getLandmarkMaxNameSize(), p -> open()));
                })
                .asGuiItem(player, langType);
    }

}
