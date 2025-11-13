package org.leralix.tan.gui.common;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;

import java.util.Collections;
import java.util.List;

public class ConfirmMenu extends BasicGui {

    private final List<FilledLang> confirmDescription;
    private final Runnable  confirmAction;
    private final Runnable  cancelAction;

    public ConfirmMenu(Player player, FilledLang confirmDescription, Runnable confirmAction, Runnable  cancelAction){
        this(player, Collections.singletonList(confirmDescription), confirmAction, cancelAction);
    }

    public ConfirmMenu(Player player, List<FilledLang> confirmDescription, Runnable confirmAction, Runnable  cancelAction){
        super(player, Lang.HEADER_CONFIRMATION, 3);
        this.confirmDescription = confirmDescription;
        this.confirmAction = confirmAction;
        this.cancelAction = cancelAction;
        open();
    }

    @Override
    public void open() {
        gui.setItem(2, 4, getConfirmButton());
        gui.setItem(2, 6, getCancelButton());

        gui.open(player);
    }

    private @NotNull GuiItem getCancelButton() {
        return iconManager.get(IconKey.CANCEL_ICON)
                .setName(Lang.GENERIC_CANCEL_ACTION.get(tanPlayer))
                .setAction(action -> cancelAction.run())
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getConfirmButton() {
        return iconManager.get(IconKey.CONFIRM_ICON)
                .setName(Lang.GENERIC_CONFIRM_ACTION.get(tanPlayer))
                .setDescription(confirmDescription)
                .setAction(action -> confirmAction.run())
                .asGuiItem(player, langType);
    }
}
