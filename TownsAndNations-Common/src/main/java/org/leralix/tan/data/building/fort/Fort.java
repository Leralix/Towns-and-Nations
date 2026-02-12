package org.leralix.tan.data.building.fort;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.position.Vector3D;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.data.building.Building;
import org.leralix.tan.data.chunk.ClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunk;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.cosmetic.BannerBuilder;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.FortStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TANCustomNBT;
import org.leralix.tan.utils.territory.ChunkUtil;
import org.tan.api.interfaces.buildings.TanFort;

public abstract class Fort extends Building implements TanFort {

    protected Fort() {

    }

    public void updateFlag() {
        Vector3D flagPosition = getPosition();
        Block flagBlock = flagPosition.getLocation().add(0, 1, 0).getBlock();

        BannerBuilder bannerBuilder = getOccupier().getBanner();

        flagBlock.setType(bannerBuilder.getBannerType());
        if(flagBlock.getState() instanceof Banner banner){
            banner.setPatterns(bannerBuilder.getPatterns());
            banner.update();
        }
    }

    public void spawnFlag() {
        updateFlag();
        TANCustomNBT.setProtectedBlockData(this);
    }

    public abstract Vector3D getPosition();

    public abstract TerritoryData getOwner();

    public abstract TerritoryData getOccupier();

    protected abstract void setOccupierInternal(TerritoryData newOwner);

    public void setOccupier(TerritoryData newOwner) {
        newOwner.addOccupiedFort(this);
        setOccupierInternal(newOwner);
        // If "claim all" is enabled, all claims in the Fort radius not protected by others fort will be occupied
        if(!Constants.isClaimAllIfCaptured()){
            return;
        }

        var chunks = ChunkUtil.getChunksInRadius(
                getPosition().getLocation().getWorld().getChunkAt(getPosition().getX() / 16, getPosition().getZ() / 16),
                Constants.getFortProtectionRadius() / 16,
                claimedChunk2 -> claimedChunk2 instanceof TerritoryChunk territoryChunk &&
                        !territoryChunk.isOccupied() &&
                        territoryChunk.getFortProtecting().isEmpty()
        );

        for (ClaimedChunk claimedChunk : chunks) {
            if (claimedChunk instanceof TerritoryChunk territoryChunk) {
                territoryChunk.setOccupier(newOwner);
            }
        }
        getOwner().checkIfShouldSurrender();
        updateFlag();
    }

    public void liberate() {
        getOccupier().removeOccupiedFort(this);
        this.setOccupierInternal(getOwner());
        // If "claim all" is enabled, all claims in the Fort radius not protected by others fort will be occupied
        if(!Constants.isClaimAllIfCaptured()){
            return;
        }

        var chunks = ChunkUtil.getChunksInRadius(
                getPosition().getLocation().getWorld().getChunkAt(getPosition().getX() / 16, getPosition().getZ() / 16),
                Constants.getFortProtectionRadius() / 16,
                claimedChunk2 -> claimedChunk2 instanceof TerritoryChunk territoryChunk &&
                            territoryChunk.isOccupied() &&
                            territoryChunk.getFortProtecting().isEmpty()
        );

        for (ClaimedChunk claimedChunk : chunks) {
            if (claimedChunk instanceof TerritoryChunk territoryChunk) {
                territoryChunk.liberate();
            }
        }
    }

    public boolean isOccupied() {
        return !getOwner().getID().equals(getOccupier().getID());
    }

    @Override
    public GuiItem getGuiItem(IconManager iconManager, Player player, BasicGui basicGui, LangType langType) {

        Vector3D position = getPosition();
        return iconManager.get(IconKey.FORT_BUILDING_ICON)
                .setName(getName())
                .setDescription(
                        Lang.FORT_OCCUPIED_BY.get(getOccupier().getColoredName()),
                        Lang.DISPLAY_COORDINATES.get(Integer.toString(position.getX()), Integer.toString(position.getY()), Integer.toString(position.getZ()))
                )
                .setClickToAcceptMessage(Lang.GUI_GENERIC_RIGHT_CLICK_TO_DELETE)
                .setAction(
                        action -> {
                            if (action.isRightClick()) {
                                FortStorage.getInstance().delete(this);
                                SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
                                basicGui.open();
                            }
                        }
                )
                .asGuiItem(player, langType);
    }


    private void deleteFlag() {
        Vector3D flagPosition = getPosition();

        if (flagPosition.getWorld() == null) {
            return;
        }

        Block baseBlock = flagPosition.getLocation().getBlock();
        Block flagBlock = flagPosition.getLocation().add(0, 1, 0).getBlock();
        flagBlock.setType(Material.AIR);


        TANCustomNBT.removeBockMetaData(baseBlock, "fortFlag");
        TANCustomNBT.removeBockMetaData(flagBlock, "fortFlag");
    }

    @Override
    public void delete() {
        if(isOccupied()){
            getOccupier().removeOwnedFort(this);
        }
        deleteFlag();

    }

    public abstract void setOwner(TerritoryData newOwner);
}
