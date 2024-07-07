package org.tan.TownsAndNations.DataClass.territoryData;

import org.tan.TownsAndNations.enums.SoundEnum;

public interface IBroadcast {

    void broadCastMessage(String message);

    void broadCastMessageWithSound(String message, SoundEnum soundEnum);

}
