package scr0ols.potionsbelt;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Missing keys in a non-English lang file are fine -- vanilla falls back to
 * English for those (see the wiki's Translations page), so completeness is
 * intentionally not enforced here. What's actually a bug is a key that
 * doesn't exist in en_us.json at all: a typo or stale entry that vanilla
 * would just as silently swallow, but for the wrong reason.
 */
class LangFileConsistencyTest {

    private static final File LANG_DIR =
            new File("src/main/resources/assets/potions-belt/lang");
    private static final Type LANG_MAP_TYPE = new TypeToken<Map<String, String>>() {}.getType();

    private static Map<String, String> readLang(File file) throws IOException {
        try (var reader = Files.newBufferedReader(file.toPath())) {
            return new Gson().fromJson(reader, LANG_MAP_TYPE);
        }
    }

    @Test
    void nonEnglishLangFilesHaveNoUnknownKeys() throws IOException {
        File[] langFiles = LANG_DIR.listFiles((dir, name) -> name.endsWith(".json"));
        assertTrue(langFiles != null && langFiles.length > 1,
                "expected multiple lang files under " + LANG_DIR);

        Set<String> enUsKeys = readLang(new File(LANG_DIR, "en_us.json")).keySet();

        for (File langFile : langFiles) {
            if (langFile.getName().equals("en_us.json")) {
                continue;
            }
            for (String key : readLang(langFile).keySet()) {
                assertTrue(enUsKeys.contains(key),
                        langFile.getName() + " has key \"" + key + "\" that doesn't exist in en_us.json");
            }
        }
    }
}
