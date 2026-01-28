package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.war.War;
import org.leralix.tan.war.info.WarRole;

import java.util.ArrayList;
import java.util.List;

public class AdminManageWarMenu extends BasicGui {

    private final War war;
    private final BasicGui returnGui;

    public AdminManageWarMenu(Player player, War war, BasicGui returnGui) {
        super(player, Lang.HEADER_ADMIN_WAR_MENU, 3);
        this.war = war;
        this.returnGui = returnGui;
        open();
    }

    @Override
    public void open() {
        gui.setItem(1, 5, war.getIcon().asGuiItem(player, langType));
        gui.setItem(2, 2, getForceSurrenderButton(
                WarRole.MAIN_ATTACKER,
                Lang.ADMIN_FORCE_SURRENDER_ATTACK_SIDE,
                IconKey.WAR_ATTACKER_SIDE_ICON
        ));
        gui.setItem(2, 4, getForceSurrenderButton(
                WarRole.MAIN_DEFENDER,
                Lang.ADMIN_FORCE_SURRENDER_DEFENSIVE_SIDE,
                IconKey.WAR_DEFENDER_SIDE_ICON
        ));
        gui.setItem(2, 7, getAuthorizeAttacksButton());
        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> returnGui.open()));
        gui.open(player);
    }

    private @NotNull GuiItem getForceSurrenderButton(WarRole warRole, Lang name, IconKey icon) {
        List<FilledLang> description = new ArrayList<>();
        description.add(Lang.WAR_SURRENDER_DESC1.get());
        description.add(Lang.WAR_SURRENDER_DESC2.get());

        description.addAll(war.generateWarGoalsDesciption(warRole, langType));

        return iconManager.get(icon)
                .setName(name.get(langType))
                .setDescription(description)
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction(action -> {
                    war.territorySurrender(warRole);
                    returnGui.open();
                })
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getAuthorizeAttacksButton() {
        return iconManager.get(IconKey.TERRITORY_WAR_ICON)
                .setName(Lang.ADMIN_GUI_MANAGE_ATTACKS.get(langType))
                .setAction(action -> {
                    new AdminManageAttacks(player, war, this);
                })
                .asGuiItem(player, langType);
    }
}
