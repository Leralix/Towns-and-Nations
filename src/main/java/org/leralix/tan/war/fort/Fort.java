package org.leralix.tan.war.fort;

import dev.triumphteam.gui.guis.GuiItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.position.Vector3D;
import org.leralix.lib.utils.CustomNBT;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.building.Building;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.dataclass.wars.WarRole;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.impl.FortDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.Constants;
import org.leralix.tan.utils.ProgressBar;

import java.util.List;

public abstract class Fort extends Building {

    public Fort(){

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

    public abstract void setOccupier(TerritoryData newOwner);

    public abstract void setCaptureProgress(int value);

    public boolean isOccupied() {
        return getOwner().getID().equals(getOccupier().getID());
    }

    /**
     * Check if a chunk is protected by this fort.
     * This method should only be called on claimed chunk owned by the same territory as the fort.
     *
     * @param chunk The chunk that needs to be checked
     * @return True if the chunk is protected, false otherwise.
     */
    boolean isProtecting(ClaimedChunk2 chunk) {

        TerritoryData fortOccupier = getOccupier();

        //If the chunk is not owned by the same territory as the fort, do not protect it.
        if (!chunk.getOwner().getID().equals(fortOccupier.getID())) {
            return false;
        }

        double distance = getFlagPosition().getDistance(chunk.getVector2D());
        return Constants.getFortProtectingDistance() > distance;
    }

    void updateCatpure(PlannedAttack attackData) {
        double captureRadius = Constants.getFortCaptureRadius();

        World world = getFlagPosition().getWorld();

        List<Entity> nearbyEntities = (List<Entity>) world.getNearbyEntities(getFlagPosition().getLocation(), captureRadius, captureRadius, captureRadius);

        // Keep only players
        List<Player> players = nearbyEntities.stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .toList();

        updateCapture(attackData, players);
    }

    void updateCapture(PlannedAttack attackData, List<Player> players) {
        int attackers = 0;
        int defenders = 0;

        for (Player player : players) {
            ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

            WarRole warRole = attackData.getRole(tanPlayer);

            if (warRole == WarRole.MAIN_ATTACKER || warRole == WarRole.OTHER_ATTACKER) {
                attackers++;
            }
            if (warRole == WarRole.MAIN_DEFENDER || warRole == WarRole.OTHER_DEFENDER) {
                defenders++;
            }
        }

        int total = defenders - attackers;

        int newValue = Math.max(0, Math.min(100, getCaptureProgress() + total));
        setCaptureProgress(newValue);
        if (newValue == 100) {
            setOccupier(attackData.getMainAttacker());
        }
        if (newValue == 0) {
            setOccupier(getOwner());
        }
    }

    private void displayCaptureProgress(List<Player> players) {
        String controlSide = getOccupier().getColoredName();
        String progressBar = ProgressBar.createProgressBar(getCaptureProgress(), 100, 20, ChatColor.RED, ChatColor.GREEN);

        for (Player player : players) {
            player.sendMessage(controlSide + ChatColor.WHITE + " | " + progressBar);
        }
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
                            if(action.isRightClick()){
                                FortDataStorage.getInstance().delete(this);
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

    public void liberate() {
        setOccupier(getOwner());
    }
}
