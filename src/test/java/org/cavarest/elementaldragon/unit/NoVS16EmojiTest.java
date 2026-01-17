package org.cavarest.elementaldragon.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to ensure no emoji with VS16 variation selector (U+FE0F) are used in the codebase.
 *
 * VS16 (Variation Selector 16) tells systems to display a character as an emoji instead
 * of text. Minecraft clients don't handle VS16 properly and will show "snowflake[VS16]"
 * instead of "❄".
 *
 * This test scans all Java source files for the VS16 character (U+FE0F).
 */
@DisplayName("VS16 Variation Selector Detection Test")
public class NoVS16EmojiTest {

    /**
     * VS16 variation selector character (U+FE0F).
     * This character follows emoji to force emoji-style rendering.
     * Minecraft clients display this as literal text like "[VS16]".
     */
    private static final String VS16 = "\uFE0F";

    @Test
    @DisplayName("Source code should not contain VS16 variation selector")
    public void testNoVS16InSourceFiles(@TempDir Path tempDir) throws IOException {
        // Find all Java source files
        Path srcDir = Path.of("src/main/java");
        if (!Files.exists(srcDir)) {
            // Running from different directory
            srcDir = Path.of("../src/main/java");
        }

        if (!Files.exists(srcDir)) {
            fail("Could not find src/main/java directory");
            return;
        }

        List<String> violations = new ArrayList<>();

        // Walk through all Java files
        Files.walk(srcDir)
            .filter(p -> p.toString().endsWith(".java"))
            .forEach(file -> {
                try {
                    List<String> lines = Files.readAllLines(file);
                    for (int i = 0; i < lines.size(); i++) {
                        String line = lines.get(i);
                        if (line.indexOf(VS16) >= 0) {
                            // Find all VS16 positions in this line
                            StringBuilder sb = new StringBuilder();
                            for (int j = 0; j < line.length(); j++) {
                                if (line.substring(j).startsWith(VS16)) {
                                    // Show context around the VS16
                                    int start = Math.max(0, j - 5);
                                    int end = Math.min(line.length(), j + 6);
                                    sb.append(String.format("  Position %d: ...%s...\n",
                                        j, line.substring(start, end).replace(VS16, "[VS16]")));
                                }
                            }
                            violations.add(String.format("%s:%d\n%s",
                                file.getFileName(), i + 1, sb.toString()));
                        }
                    }
                } catch (IOException e) {
                    fail("Could not read file: " + file);
                }
            });

        if (!violations.isEmpty()) {
            StringBuilder failureMessage = new StringBuilder();
            failureMessage.append("Found VS16 variation selector (U+FE0F) in source files!\n");
            failureMessage.append("This causes Minecraft clients to display \"[VS16]\" instead of the emoji.\n\n");
            failureMessage.append("Violations:\n");
            for (String violation : violations) {
                failureMessage.append(violation).append("\n");
            }
            failureMessage.append("\nFix: Remove the VS16 character from the emoji.\n");
            failureMessage.append("  - Replace '❄' (snowflake + VS16) with '❄' (snowflake only)\n");
            failureMessage.append("  - Replace '🛡' (shield + VS16) with '🛡' (shield only)\n");
            failureMessage.append("  - etc.\n");
            fail(failureMessage.toString());
        }
    }

    @Test
    @DisplayName("Common emojis should not contain VS16")
    public void testCommonEmojisNoVS16() {
        // List of emoji characters that should NOT have VS16
        String[] emojisWithoutVS16 = {
            "❄",  // snowflake (U+2744)
            "🛡",  // shield (U+1F6A1)
            "🔥",  // fire (U+1F525)
            "💨",  // wind (U+1F4A8)
            "👁",  // eye (U+1F441)
            "🩸",  // drop of blood (U+1FA78)
            "💫",  // dizzy (U+1F4AB)
            "❤",   // heart (U+2764)
            "⚡",  // high voltage (U+26A1)
            "🌊",  // wave (U+1F30A)
            "⭕",  // heavy large circle (U+2B55)
            "🎯",  // bullseye (U+1F3AF)
            "☄",  // comet (U+2604)
        };

        StringBuilder violations = new StringBuilder();

        for (String emoji : emojisWithoutVS16) {
            if (emoji.indexOf(VS16) >= 0) {
                violations.append("Emoji contains VS16: ")
                    .append(emoji.replace(VS16, "[VS16]"))
                    .append("\n");
            }
        }

        if (violations.length() > 0) {
            fail("Test configuration error - emoji list contains VS16:\n" + violations);
        }
    }

    @Test
    @DisplayName("Should detect VS16 in test strings")
    public void testDetectsVS16() {
        // Construct badString with VS16 using Unicode escape
        String badString = "Freeze now! \u2744\uFE0F";  // snowflake + VS16
        String goodString = "Freeze now! \u2744";   // snowflake without VS16

        assertTrue(badString.indexOf(VS16) >= 0,
            "Should detect VS16 in bad string");
        assertEquals(-1, goodString.indexOf(VS16),
            "Should not detect VS16 in good string");
    }
}
