package org.leralix.tan.api.wrappers;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.tan.api.interfaces.TanLandmark;
import org.tan.api.interfaces.TanTerritory;

import java.util.UUID;

public class LandmarkDataWrapper implements TanLandmark {

    Landmark landmark;

    private LandmarkDataWrapper(Landmark landmark) {
        this.landmark = landmark;
    }

    public static LandmarkDataWrapper of(Landmark landmark) {
        if (landmark == null) {
            return null;
        }
        return new LandmarkDataWrapper(landmark);
    }

    @Override
    public String getID() {
        return landmark.getID();
    }

    @Override
    public String getName() {
        return landmark.getName();
    }

    @Override
    public Location getLocation() {
        return landmark.getLocation();
    }
    @Override
    public void setName(String s) {
        landmark.setName(s);
    }

    @Override
    public void setQuantity(int i) {
        ItemStack reward = landmark.getResources();
        reward.setAmount(i);
        landmark.setReward(reward);
    }

    @Override
    public int getQuantity() {
        return landmark.getResources().getAmount();
    }

    @Override
    public void setItem(ItemStack itemStack) {
        itemStack.setAmount(landmark.getResources().getAmount());
        landmark.setReward(itemStack);
    }

    @Override
    public ItemStack getItem() {
        return landmark.getResources();
    }

    @Override
    public boolean isOwned() {
        return landmark.isOwned();
    }

    @Override
    public TanTerritory getOwner() {
        return TerritoryDataWrapper.of(TerritoryUtil.getTerritory(landmark.getOwnerID()));
    }

    @Override
    public void removeOwnership() {
        landmark.removeOwnership();
    }

    @Override
    public void setOwner(UUID uuid) {
        landmark.setOwner(TownDataStorage.getInstance().get(uuid.toString()));
    }

    @Override
    public void setOwner(TanTerritory tanTerritory) {
        if(tanTerritory instanceof TownData townData){
            landmark.setOwner(townData);
        }
    }
}
