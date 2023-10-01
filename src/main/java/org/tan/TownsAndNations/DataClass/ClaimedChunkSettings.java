package org.tan.TownsAndNations.DataClass;

import org.tan.TownsAndNations.enums.TownChunkPermission;

public class ClaimedChunkSettings {
    private int NumberOfClaimedChunk;
    private TownChunkPermission chestAuth;
    private TownChunkPermission doorAuth;
    private TownChunkPermission breakAuth;
    private TownChunkPermission placeAuth;
    private TownChunkPermission attackPassiveMobAuth;
    private TownChunkPermission useButtonsAuth;
    private TownChunkPermission useLeverAuth;
    private TownChunkPermission useFurnaceAuth;
    private TownChunkPermission interactItemFrameAuth;
    private TownChunkPermission interactArmorStandAuth;
    private TownChunkPermission decorativeBlockAuth;
    private TownChunkPermission musicBlockAuth;
    private TownChunkPermission leadAuth;


    public ClaimedChunkSettings(){
        this.NumberOfClaimedChunk = 0;
        this.chestAuth = TownChunkPermission.TOWN;
        this.doorAuth = TownChunkPermission.TOWN;
        this.breakAuth = TownChunkPermission.TOWN;
        this.placeAuth = TownChunkPermission.TOWN;
        this.attackPassiveMobAuth = TownChunkPermission.TOWN;
        this.useButtonsAuth = TownChunkPermission.TOWN;
        this.useLeverAuth = TownChunkPermission.TOWN;
        this.useFurnaceAuth = TownChunkPermission.TOWN;
        this.interactItemFrameAuth = TownChunkPermission.TOWN;
        this.interactArmorStandAuth = TownChunkPermission.TOWN;
        this.decorativeBlockAuth = TownChunkPermission.TOWN;
        this.musicBlockAuth = TownChunkPermission.TOWN;
        this.leadAuth = TownChunkPermission.TOWN;

    }

    public TownChunkPermission getChestAuth() {
        return this.chestAuth;
    }
    public TownChunkPermission getDoorAuth() {
        return this.doorAuth;
    }
    public TownChunkPermission getBreakAuth() {
        return this.breakAuth;
    }
    public TownChunkPermission getPlaceAuth() {
        return this.placeAuth;
    }
    public TownChunkPermission getAttackPassiveMobAuth() {
        return this.attackPassiveMobAuth;
    }
    public TownChunkPermission getUseButtonsAuth() {
        return this.useButtonsAuth;
    }
    public TownChunkPermission getUseLeverAuth() {
        return this.useLeverAuth;
    }
    public TownChunkPermission getUseFurnaceAuth() {
        return this.useFurnaceAuth;
    }
    public TownChunkPermission getInteractItemFrameAuth() {
        return this.interactItemFrameAuth;
    }
    public TownChunkPermission getInteractArmorStandAuth() {
        return this.interactArmorStandAuth;
    }
    public TownChunkPermission getDecorativeBlockAuth() {
        return this.decorativeBlockAuth;
    }
    public TownChunkPermission getMusicBlockAuth() {
        return this.musicBlockAuth;
    }
    public TownChunkPermission getLeadAuth() {
        return this.leadAuth;
    }


    public void nextChestAuth() {
        this.chestAuth = this.chestAuth.getNext();
    }

    public void nextDoorAuth() {
        this.doorAuth = this.doorAuth.getNext();
    }

    public void nextBreakAuth() {
        this.breakAuth = this.breakAuth.getNext();
    }

    public void nextPlaceAuth() {
        this.placeAuth = this.placeAuth.getNext();
    }

    public void nextAttackPassiveMobAuth() {
        this.attackPassiveMobAuth = this.attackPassiveMobAuth.getNext();
    }

    public void nextUseButtonsAuth() {
        this.useButtonsAuth = this.useButtonsAuth.getNext();
    }

    public void nextUseLeverAuth() {
        this.useLeverAuth = this.useLeverAuth.getNext();
    }

    public void nextUseFurnaceAuth() {
        this.useFurnaceAuth = this.useFurnaceAuth.getNext();
    }

    public void nextInteractItemFrameAuth() {
        this.interactItemFrameAuth = this.interactItemFrameAuth.getNext();
    }

    public void nextInteractArmorStandAuth() {
        this.interactArmorStandAuth = this.interactArmorStandAuth.getNext();
    }
    public void nextDecorativeBlockAuth() {
        this.decorativeBlockAuth = this.decorativeBlockAuth.getNext();
    }
    public void nextMusicBlockAuth() {
        this.musicBlockAuth = this.musicBlockAuth.getNext();
    }
    public void nextLeadAuth() {
        this.leadAuth = this.leadAuth.getNext();
    }



    public int getNumberOfClaimedChunk() {
        return this.NumberOfClaimedChunk;
    }
    public void incrementNumberOfClaimedChunk() {
        this.NumberOfClaimedChunk = this.NumberOfClaimedChunk + 1;
    }
    public void decreaseNumberOfClaimedChunk() {
        this.NumberOfClaimedChunk = this.NumberOfClaimedChunk - 1;
    }

}
