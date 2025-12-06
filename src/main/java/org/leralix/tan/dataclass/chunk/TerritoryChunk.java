package org.leralix.tan.dataclass.chunk;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Chunk;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.permissions.GeneralChunkSetting;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.war.fort.Fort;

import java.util.Optional;

public abstract class TerritoryChunk extends ClaimedChunk2 {

    private String occupierID;

    protected TerritoryChunk(Chunk chunk, String owner) {
        super(chunk, owner);
        this.occupierID = owner;
    }

    protected TerritoryChunk(int x, int z, String worldUUID, String owner) {
        super(x, z, worldUUID, owner);
        this.occupierID = owner;
    }


    @Override
    public TextComponent getMapIcon(LangType langType) {

        TextComponent textComponent;
        String text;
        if(isOccupied()){
            textComponent = new TextComponent("🟧");
            textComponent.setColor(getOccupier().getChunkColor());
            text = "x : " + super.getMiddleX() + " z : " + super.getMiddleZ() + "\n" +
                    getOwner().getBaseColoredName() + "\n" +
                    getOccupier().getBaseColoredName() + "\n" +
                    Lang.LEFT_CLICK_TO_CLAIM.get(langType);
        }
        else {
            textComponent = new TextComponent("⬛");
            textComponent.setColor(getOwner().getChunkColor());
            text = "x : " + super.getMiddleX() + " z : " + super.getMiddleZ() + "\n" +
                    getOwner().getBaseColoredName() + "\n" +
                    Lang.LEFT_CLICK_TO_CLAIM.get(langType);
        }



        textComponent.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new Text(text)));
        return textComponent;
    }

    public Optional<Fort> getFortProtecting() {
        for (Fort fort : getOccupier().getAllControlledFort()) {
            if (fort.getPosition().getDistance(getMiddleVector2D()) <= Constants.getFortProtectionRadius()) {
                return Optional.of(fort);
            }
        }
        return Optional.empty();
    }


    public String getOccupierID() {
        if(occupierID == null) {
            occupierID = ownerID;
        }
        return occupierID;
    }

    @Override
    public boolean canExplosionGrief() {
        return Constants.getChunkSettings(GeneralChunkSetting.TNT_GRIEF).canGrief(getOwner(), GeneralChunkSetting.TNT_GRIEF);
    }

    @Override
    public boolean canFireGrief() {
        return Constants.getChunkSettings(GeneralChunkSetting.FIRE_GRIEF).canGrief(getOwner(), GeneralChunkSetting.FIRE_GRIEF);
    }

    @Override
    public boolean canPVPHappen() {
        return Constants.getChunkSettings(GeneralChunkSetting.ENABLE_PVP).canGrief(getOwner(), GeneralChunkSetting.ENABLE_PVP);
    }

    @Override
    public boolean canMobGrief() {
        return Constants.getChunkSettings(GeneralChunkSetting.MOB_GRIEF).canGrief(getOwner(), GeneralChunkSetting.MOB_GRIEF);
    }

    public TerritoryData getOccupier(){
        return TerritoryUtil.getTerritory(getOccupierID());
    }

    public void setOccupier(TerritoryData occupier) {
        setOccupierID(occupier.getID());
    }

    public void setOccupierID(String occupierID) {
        this.occupierID = occupierID;
    }

    public void liberate() {
        this.occupierID = getOwnerID();
    }

    public boolean isOccupied() {
        return !ownerID.equals(occupierID);
    }
}
