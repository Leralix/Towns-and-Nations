package org.leralix.tan.storage.stored;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonStorageLoadTest extends BasicTest {

    private static class TestStorage extends JsonStorage<String> {

        private static final Type TYPE = new TypeToken<LinkedHashMap<String, String>>() {
        }.getType();

        protected TestStorage(String fileName) {
            super(fileName, TYPE, new Gson());
        }

        @Override
        public void reset() {
            dataMap = new LinkedHashMap<>();
            save();
        }
    }

    private static void writeToStorageJsonFolder(File pluginDataFolder, String fileName, String content) throws IOException {
        File jsonFolder = new File(pluginDataFolder, "storage/json");
        Files.createDirectories(jsonFolder.toPath());
        Files.writeString(new File(jsonFolder, fileName).toPath(), content, StandardCharsets.UTF_8);
    }

    @Test
    void load_shouldFallbackToEmptyMap_whenJsonIsNullLiteral() throws IOException {
        String fileName = "json-storage-null-literal-test.json";
        writeToStorageJsonFolder(townsAndNations.getDataFolder(), fileName, "null");

        TestStorage storage = new TestStorage(fileName);

        assertNotNull(storage.getAll());
        assertTrue(storage.getAll().isEmpty());
    }

    @Test
    void load_shouldFallbackToEmptyMap_whenJsonIsMalformed() throws IOException {
        String fileName = "json-storage-malformed-test.json";
        writeToStorageJsonFolder(townsAndNations.getDataFolder(), fileName, "{");

        TestStorage storage = new TestStorage(fileName);

        assertNotNull(storage.getAll());
        assertTrue(storage.getAll().isEmpty());
    }
}
