package org.leralix.tan.storage.typeadapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.leralix.tan.war.info.AttackResult;
import org.leralix.tan.war.info.AttackResultCompleted;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AttackResultAdapterTest {

    @Test
    void testSerializationRecursionFix() {
        // Setup Gson with the adapter
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(AttackResult.class, new AttackResultAdapter())
                .create();

        // Create an instance that was causing issues
        AttackResultCompleted result = new AttackResultCompleted(10, 5, 1, 2);

        // Try to serialize - this should not throw StackOverflowError
        String json = gson.toJson(result, AttackResult.class);

        // Verify output contains the type field and some data
        assertNotNull(json);
        // We expect the type field to be added by the adapter
        assert(json.contains("\"type\":\"AttackResultCompleted\""));
        // We expect the fields to be serialized
        assert(json.contains("\"nbDeathsAttacker\":10"));
        assert(json.contains("\"nbDeathsDefender\":5"));
    }
}
