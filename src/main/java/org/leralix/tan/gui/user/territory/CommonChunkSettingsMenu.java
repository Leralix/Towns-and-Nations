package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.PermissionManager;
import org.leralix.tan.utils.constants.SpecificChunkConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class CommonChunkSettingsMenu extends IteratorGUI {

    private final BasicGui returnMenu;
    private final PermissionManager permissionManager;

    protected CommonChunkSettingsMenu(Player player, PermissionManager permissionManager, BasicGui returnGui) {
        super(player, Lang.HEADER_CHUNK_PERMISSION, 4);
        this.permissionManager = permissionManager;
        this.returnMenu = returnGui;
        open();
    }


    @Override
    public void open() {
        iterator(getChunkPermission(), p -> returnMenu.open(), Material.LIME_STAINED_GLASS_PANE);
        gui.open(player);
    }

    private List<GuiItem> getChunkPermission() {
        List<GuiItem> guiItems = new ArrayList<>();

        for (ChunkPermissionType type : ChunkPermissionType.values()) {
            ChunkPermission permission = permissionManager.get(type);

            SpecificChunkConfig specificChunkConfig = getSpecificChunkConfig(type);

            IconBuilder item = iconManager.get(type.getIconKey())
                    .setName(type.getName().get(tanPlayer));

            if(specificChunkConfig.isLocked()){
                item.setDescription(
                        Lang.GUI_TOWN_CLAIM_SETTINGS_DESC1.get(specificChunkConfig.defaultRelation().getColoredName(langType)),
                        Lang.GUI_TOWN_CLAIM_SETTINGS_LOCKED.get()
                );
            }
            else {
                item.setDescription(
                            Lang.GUI_TOWN_CLAIM_SETTINGS_DESC1.get(permission.getOverallPermission().getColoredName(langType)),
                            Lang.GUI_TOWN_CLAIM_SETTINGS_DESC_ADDITIONAL_PLAYERS.get(Integer.toString(permission.getAuthorizedPlayers().size())),
                            Lang.GUI_TOWN_CLAIM_SETTINGS_DESC_ADDITIONAL_RANKS.get(Integer.toString(permission.getAuthorizedRanks().size()))
                    )
                    .setClickToAcceptMessage(
                            Lang.GUI_GENERIC_CLICK_TO_MODIFY,
                            Lang.GUI_RIGHT_CLICK_TO_ADD_SPECIFIC_PLAYER,
                            Lang.GUI_SHIFT_RIGHT_CLICK_TO_ADD_SPECIFIC_RANK
                    )
                    .setAction(getAction(type, permissionManager));
            }

            guiItems.add(item.asGuiItem(player, langType));
        }
        return guiItems;
    }

    protected abstract SpecificChunkConfig getSpecificChunkConfig( ChunkPermissionType type);

    protected abstract Consumer<InventoryClickEvent> getAction(ChunkPermissionType type, PermissionManager permissionManager);
}
