package org.leralix.tan.storage.typeadapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.JsonParseException;
import org.leralix.tan.newsletter.DiplomacyProposalNL;
import org.leralix.tan.newsletter.Newsletter;
import org.leralix.tan.newsletter.PlayerJoinRequestNL;

import java.io.IOException;

public class NewsletterAdapter extends TypeAdapter<Newsletter> {
    @Override
    public void write(JsonWriter out, Newsletter value) throws IOException {
        System.out.println("Enregistrement de la newsletter " + value.getClass().getSimpleName());
        out.beginObject();
        out.name("type").value(value.getClass().getSimpleName()); // Écrit le type de sous-classe
        out.name("data");
        new Gson().toJson(value, value.getClass(), out);

        out.endObject();
    }


    @Override
    public Newsletter read(JsonReader in) throws IOException {
        in.beginObject();
        String type = null;
        Newsletter newsletter = null;
        while (in.hasNext()) {
            String name = in.nextName();
            if (name.equals("type")) {
                type = in.nextString(); // Récupère le type de la sous-classe
            } else if (name.equals("data")) {
                Class<? extends Newsletter> clazz = getClassForType(type);
                if (clazz != null) {
                    newsletter = new Gson().fromJson(in, clazz); // Désérialise le champ "data" en utilisant la sous-classe
                }
            }
        }
        in.endObject();
        return newsletter;
    }

    private Class<? extends Newsletter> getClassForType(String type) {
        switch (type) {
            case "PlayerJoinRequestNL":
                return PlayerJoinRequestNL.class;
            case "DiplomacyProposalNL":
                return DiplomacyProposalNL.class;
            default:
                throw new JsonParseException("Unknown type: " + type);
        }
    }

}
