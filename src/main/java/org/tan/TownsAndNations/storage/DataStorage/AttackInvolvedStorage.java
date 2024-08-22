package org.tan.TownsAndNations.storage.DataStorage;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.tan.TownsAndNations.DataClass.wars.AttackInvolved;
import org.tan.TownsAndNations.DataClass.wars.CreateAttackData;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.DataClass.wars.wargoals.WarGoal;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.TypeAdapter.WargoalTypeAdapter;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AttackInvolvedStorage {
    private static Map<String, AttackInvolved> warDataMapWithWarKey = new HashMap<>();

    public static void newWar(CreateAttackData createAttackData){
        newWar(Lang.BASIC_ATTACK_NAME.get(createAttackData.getMainAttacker().getName(), createAttackData.getMainDefender().getName()), createAttackData);
    }

    public static void newWar(String warName, CreateAttackData createAttackData){

        String newID = getNewID();
        long deltaDateTime = createAttackData.getDeltaDateTime();
        AttackInvolved attackInvolved = new AttackInvolved(newID,warName, createAttackData, deltaDateTime);
        add(attackInvolved);
        save();
    }

    private static void add(AttackInvolved attackInvolved) {
        warDataMapWithWarKey.put(attackInvolved.getID(), attackInvolved);
    }

    public static void remove(AttackInvolved attackInvolved) {
        warDataMapWithWarKey.remove(attackInvolved.getID());
    }

    private static void setupAllAttacks(){
        for(AttackInvolved attackInvolved : warDataMapWithWarKey.values()){
            attackInvolved.setUpStartOfAttack();
        }
    }

    private static String getNewID(){
        int ID = 0;
        while(warDataMapWithWarKey.containsKey("W"+ID)){
            ID++;
        }
        return "W"+ID;
    }

    public static Collection<AttackInvolved> getWars() {
        return warDataMapWithWarKey.values();
    }

    public static AttackInvolved get(String warID) {
        return warDataMapWithWarKey.get(warID);
    }

    public static void load(){

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(WarGoal.class, new WargoalTypeAdapter())
                .setPrettyPrinting().
                create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Planned_wars.json");
        if (file.exists()){
            Reader reader;
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Type type = new TypeToken<HashMap<String, AttackInvolved>>() {}.getType();
            warDataMapWithWarKey = gson.fromJson(reader, type);
            for(AttackInvolved attackInvolved : warDataMapWithWarKey.values()){
                warDataMapWithWarKey.put(attackInvolved.getID(), attackInvolved);
            }
        }
        setupAllAttacks();
    }

    public static void save() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(WarGoal.class, new WargoalTypeAdapter())
                .setPrettyPrinting()
                .create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Planned_wars.json");
        file.getParentFile().mkdir();

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Writer writer;
        try {
            writer = new FileWriter(file, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gson.toJson(warDataMapWithWarKey, writer);
        try {
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
