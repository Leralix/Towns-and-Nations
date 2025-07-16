package org.leralix.tan.war.fort;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.position.Vector3D;
import org.leralix.lib.utils.CustomNBT;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.building.Building;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.FortStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public abstract class Fort extends Building {

    protected Fort() {

    }

    public void updateFlag() {
        Vector3D flagPosition = getFlagPosition();
        Block flagBlock = flagPosition.getLocation().add(0, 1, 0).getBlock();
        if (isOccupied()) {
            flagBlock.setType(Material.RED_BANNER);
        }
        if (!isOccupied()) {
            flagBlock.setType(Material.GREEN_BANNER);
        }
    }

    public void spawnFlag() {
        Vector3D flagPosition = getFlagPosition();
        Block baseBlock = flagPosition.getLocation().getBlock();
        Block flagBlock = flagPosition.getLocation().add(0, 1, 0).getBlock();
        flagBlock.setType(Material.GREEN_BANNER);

        CustomNBT.setBockMetaData(TownsAndNations.getPlugin(), baseBlock, "fortFlag", "fortFlag");
        CustomNBT.setBockMetaData(TownsAndNations.getPlugin(), flagBlock, "fortFlag", "fortFlag");
    }

    public abstract String getID();

    public abstract Vector3D getFlagPosition();

    public abstract TerritoryData getOwner();

    public abstract TerritoryData getOccupier();

    public abstract String getName();

    public abstract int getCaptureProgress();

    protected abstract void setOccupierInternal(TerritoryData newOwner);

    public void setOccupier(TerritoryData newOwner) {
        newOwner.addOccupiedFort(this);
        setOccupierInternal(newOwner);
    }

    public void liberate() {
        getOccupier().removeOccupiedFort(this);
        this.setOccupierInternal(getOwner());
    }

    public abstract void setCaptureProgress(int value);

    public boolean isOccupied() {
        return !getOwner().getID().equals(getOccupier().getID());
    }

    @Override
    public GuiItem getGuiItem(IconManager iconManager, Player player, TerritoryData territoryData, BasicGui basicGui) {

        LangType langType = PlayerDataStorage.getInstance().get(player).getLang();
        Vector3D position = getFlagPosition();
        return iconManager.get(IconKey.FORT_BUILDING_ICON)
                .setName(getName())
                .setDescription(
                        Lang.FORT_OCCUPIED_BY.get(langType, getOccupier().getColoredName()),
                        Lang.DISPLAY_COORDINATES.get(langType, position.getX(), position.getY(), position.getZ()),
                        Lang.GUI_GENERIC_RIGHT_CLICK_TO_DELETE.get(langType)
                )
                .setAction(
                        action -> {
                            if (action.isRightClick()) {
                                FortStorage.getInstance().delete(this);
                                SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
                                basicGui.open();
                            }
                        }
                )
                .asGuiItem(player);
    }


    private void deleteFlag() {
        Vector3D flagPosition = getFlagPosition();
        Block baseBlock = flagPosition.getLocation().getBlock();
        Block flagBlock = flagPosition.getLocation().add(0, 1, 0).getBlock();
        flagBlock.setType(Material.AIR);

        baseBlock.removeMetadata("fortFlag", TownsAndNations.getPlugin());
        flagBlock.removeMetadata("fortFlag", TownsAndNations.getPlugin());
    }

    public void delete() {
        deleteFlag();
    }

}
