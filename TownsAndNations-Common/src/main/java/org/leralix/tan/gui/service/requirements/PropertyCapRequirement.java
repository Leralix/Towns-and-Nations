package org.leralix.tan.gui.service.requirements;

import org.leralix.tan.data.territory.Town;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public class PropertyCapRequirement extends IndividualRequirement {

    private final int maxAmount;
    private final Town territoryData;
    private final boolean isUnlimited;

    public PropertyCapRequirement(Town territoryData, int maxAmount, boolean isUnlimited) {
        this.territoryData = territoryData;
        this.maxAmount = maxAmount;
        this.isUnlimited = isUnlimited;
    }

    @Override
    public String getLine(LangType langType) {
        int nbProperties = territoryData.getProperties().size();
        if (isUnlimited) {
            return Lang.GUI_PROPERTY_CAP.get(langType, Integer.toString(nbProperties), Lang.INFINITY.get(langType));
        }

        if (isInvalid()) {
            return Lang.GUI_PROPERTY_CAP_FULL.get(langType, Integer.toString(nbProperties), Integer.toString(maxAmount));
        } else {
            return Lang.GUI_PROPERTY_CAP.get(langType, Integer.toString(nbProperties), Integer.toString(maxAmount));
        }
    }

    @Override
    public boolean isInvalid() {
        if(isUnlimited){
            return false;
        }
        return territoryData.getProperties().size() >= maxAmount;
    }
}
