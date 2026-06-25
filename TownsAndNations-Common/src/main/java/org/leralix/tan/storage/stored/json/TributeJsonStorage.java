package org.leralix.tan.storage.stored.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.wargoals.Tribute;
import org.leralix.tan.storage.stored.TributeStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TributeJsonStorage extends JsonStorage<Tribute> implements TributeStorage {

    public TributeJsonStorage() {
        super("TAN - Tributes.json",
                new TypeToken<HashMap<String, Tribute>>() {
                }.getType(),
                new GsonBuilder()
                        .setPrettyPrinting()
                        .create()
        );
    }

    @Override
    public void registerTribute(Tribute tribute) {
        put(getId(tribute), tribute);
    }

    @Override
    public void deleteTribute(Tribute tribute) {
        delete(getId(tribute));
    }

    @Override
    public Set<Tribute> getTributeOfMaster(Territory territory) {
        Set<Tribute> res = new HashSet<>();
        for(Tribute tribute : new HashSet<>(getAll().values())){
            if(tribute.isFullyPaid()){
                delete(getId(tribute));
                continue;
            }
            if(tribute.getMasterID().equals(territory.getID())){
                res.add(tribute);
            }
        }
        return res;
    }

    @Override
    public Set<Tribute> getTributeOfTributary(Territory territory) {
        Set<Tribute> res = new HashSet<>();
        for(Tribute tribute : new HashSet<>(getAll().values())){
            if(tribute.isFullyPaid()){
                delete(getId(tribute));
                continue;
            }
            if(tribute.getTributaryID().equals(territory.getID())){
                res.add(tribute);
            }
        }
        return res;
    }

    public String getId(Tribute tribute) {
        return tribute.getMasterID() + " -> " + tribute.getTributaryID();
    }

}
