package org.leralix.tan.gui.user.war;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.user.territory.AttackMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.ChangeAttackName;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.legacy.WarRole;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.MINOR_GOOD;

public class PlannedAttackMenu extends BasicGui {

    private final TerritoryData territoryData;
    private final PlannedAttack plannedAttack;
    private final WarRole warRole;

    public PlannedAttackMenu(Player player, TerritoryData territoryData, PlannedAttack plannedAttack){
        super(player, Lang.HEADER_WAR_MANAGER.get(player), 3);
        this.territoryData = territoryData;
        this.plannedAttack = plannedAttack;
        this.warRole = plannedAttack.getTerritoryRole(territoryData);
        open();
    }

    @Override
    public void open() {
        gui.setItem(1, 5, getAttackIcon());

        gui.setItem(2, 2, getAttackingSideSidePanel());
        gui.setItem(2, 4, getDefendingSidePanel());


        if (warRole == WarRole.MAIN_ATTACKER) {
            ItemStack cancelAttack = HeadUtils.createCustomItemStack(Material.BARRIER, Lang.GUI_CANCEL_ATTACK.get(tanPlayer), Lang.GUI_GENERIC_CLICK_TO_DELETE.get(tanPlayer));
            ItemStack renameAttack = HeadUtils.createCustomItemStack(Material.NAME_TAG, Lang.GUI_RENAME_ATTACK.get(tanPlayer), Lang.GUI_GENERIC_CLICK_TO_RENAME.get(tanPlayer));
            GuiItem cancelButton = ItemBuilder.from(cancelAttack).asGuiItem(event -> {
                plannedAttack.end();
                territoryData.broadcastMessageWithSound(Lang.ATTACK_SUCCESSFULLY_CANCELLED.get(plannedAttack.getWar().getMainDefender().getName()), MINOR_GOOD);
                new AttackMenu(player, territoryData);
            });

            GuiItem renameButton = ItemBuilder.from(renameAttack).asGuiItem(event -> {
                event.setCancelled(true);
                TanChatUtils.message(player, Lang.ENTER_NEW_VALUE.get(tanPlayer));
                PlayerChatListenerStorage.register(player, new ChangeAttackName(plannedAttack, p -> open()));
            });

            gui.setItem(2, 6, renameButton);
            gui.setItem(2, 8, cancelButton);
        }
        else if (warRole == WarRole.MAIN_DEFENDER) {

            List<String> submitDescription = new ArrayList<>();
            submitDescription.add(Lang.SUBMIT_TO_REQUEST_DESC1.get(langType));
            submitDescription.addAll(plannedAttack.getWar().generateWarGoalsDesciption(warRole, langType));

            gui.setItem(2, 7, iconManager.get(Material.SOUL_LANTERN)
                    .setName(Lang.SUBMIT_TO_REQUESTS.get(langType))
                    .setDescription(submitDescription)
                    .setAction(
                            event -> {
                                plannedAttack.territorySurrendered();
                                new AttackMenu(player, territoryData);
                            })
                    .asGuiItem(player));
        }
        else if (warRole == WarRole.OTHER_ATTACKER || warRole == WarRole.OTHER_DEFENDER) {
            gui.setItem(2, 7, iconManager.get(Material.DARK_OAK_DOOR)
                    .setName(Lang.GUI_QUIT_WAR.get(langType))
                    .setDescription(Lang.GUI_QUIT_WAR_DESC1.get(langType))
                    .setAction(event -> {
                        plannedAttack.removeBelligerent(territoryData);
                        territoryData.broadcastMessageWithSound(Lang.TERRITORY_NO_LONGER_INVOLVED_IN_WAR_MESSAGE.get(plannedAttack.getWar().getMainDefender().getName()), MINOR_GOOD);
                        new AttackMenu(player, territoryData);
                    })
                    .asGuiItem(player));
        }
        else if (warRole == WarRole.NEUTRAL) {

            List<String> description = new ArrayList<>();
            description.add(Lang.GUI_JOIN_ATTACKING_SIDE_DESC1.get(langType, territoryData.getBaseColoredName()));
            description.addAll(plannedAttack.getWar().generateWarGoalsDesciption(warRole, langType));

            gui.setItem(2, 6, iconManager.get(Material.IRON_SWORD)
                    .setName(Lang.GUI_JOIN_ATTACKING_SIDE.get(langType))
                    .setDescription(description)
                    .setAction(event -> {
                        plannedAttack.addAttacker(territoryData);
                        open();
                    })
                    .asGuiItem(player)
            );

            gui.setItem(2, 8, iconManager.get(Material.SHIELD)
                    .setName(Lang.GUI_JOIN_DEFENDING_SIDE.get(tanPlayer))
                    .setDescription(Lang.GUI_JOIN_DEFENDING_SIDE_DESC1.get(tanPlayer, territoryData.getBaseColoredName()))
                    .setAction(event -> {
                        plannedAttack.addDefender(territoryData);
                        open();
                    })
                    .asGuiItem(player));
        }

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new AttackMenu(player, territoryData)));
        gui.open(player);
    }

    private @NotNull GuiItem getDefendingSidePanel() {
        return ItemBuilder.from(plannedAttack.getDefendingIcon(langType)).asGuiItem();
    }

    private @NotNull GuiItem getAttackingSideSidePanel() {
        return ItemBuilder.from(plannedAttack.getAttackingIcon(langType)).asGuiItem();
    }

    private @NotNull GuiItem getAttackIcon() {
        return  ItemBuilder.from(plannedAttack.getIcon(tanPlayer, territoryData)).asGuiItem();

    }


}
