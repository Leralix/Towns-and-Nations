package org.leralix.tan.data.territory.permission;

public enum RecruitingPolicy {

    CLOSED,
    APPLICATION_OPEN,
    AUTHORIZE_ALL;

    public RecruitingPolicy next(){
        return switch (this){
            case CLOSED -> APPLICATION_OPEN;
            case APPLICATION_OPEN -> AUTHORIZE_ALL;
            case AUTHORIZE_ALL -> CLOSED;
        };
    }

}
