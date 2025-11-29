package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public class OpenRankListForChunkPermission extends IteratorGUI {

    private final TerritoryData territoryData;
    private final ChunkPermission chunkPermission;
    private final BasicGui backMenu;

    public OpenRankListForChunkPermission(Player player, TerritoryData territoryData, ChunkPermissionType type, BasicGui backMenu) {
        super(player, type.getLabel(), 6);
        this.territoryData = territoryData;
        this.chunkPermission = territoryData.getChunkSettings().getChunkPermissions().get(type);
        this.backMenu = backMenu;
        open();
    }

    @Override
    public void open() {
        iterator(getRanksOfTerritory(), p -> backMenu.open());

        gui.open(player);
    }



    private List<GuiItem> getRanksOfTerritory() {
        List<GuiItem> guiItems = new ArrayList<>();

        for(RankData rankData : territoryData.getAllRanks()){
            boolean isAuthorized = chunkPermission.getAuthorizedRanks().contains(rankData.getID());

            guiItems.add(iconManager.get(rankData.getRankIcon())
                    .setName(rankData.getColoredName())
                    .setDescription(
                            Lang.GUI_ADD_RANK_CURRENT_STATUS.get(
                                    isAuthorized ?
                                            Lang.GUI_ADD_RANK_ALLOWED_PERMISSION.get(langType) :
                                            Lang.GUI_ADD_RANK_NEUTRAL_PERMISSION.get(langType))
                    )
                    .setClickToAcceptMessage(
                            Lang.GUI_GENERIC_CLICK_TO_SWITCH
                    )
                    .setAction(event -> {

                        if(isAuthorized){
                            chunkPermission.removeSpecificRankPermission(rankData.getID());
                        } else {
                            chunkPermission.addSpecificRankPermission(rankData.getID());
                        }
                        SoundUtil.playSound(player, SoundEnum.ADD);
                        open();
                    })
                    .asGuiItem(player, langType));
        }

        return guiItems;
    }
}
