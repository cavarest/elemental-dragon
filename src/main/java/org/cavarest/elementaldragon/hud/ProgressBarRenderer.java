package org.cavarest.elementaldragon.hud;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Flexible framework for rendering animated progress bars with custom frames.
 *
 * Define your own progress bar by providing frame symbols - the framework
 * handles mapping progress (0-100%) to the correct frame and animating between them.
 *
 * Built-in variants:
 * - TILES: Unicode tile progress bar with rainbow gradient (default)
 * - MOON: Moon phase emoji (new moon → full moon)
 * - CLOCK: Clock emoji (12 hour positions)
 * - BLOCK: Block shading (░ → ▒ → ▓ → █) with configurable width
 * - TRIANGLE: Triangle fill (▹ → ▸) with configurable width
 *
 * At 100% completion, displays "READY" in green.
 *
 * Width parameter determines the number of characters per frame:
 * - MOON(2) = 10 states (5 phases × 2 chars)
 * - CLOCK(2) = 24 states (12 faces × 2 chars)
 * - BLOCK(1) = 4 states, BLOCK(2) = 8 states, BLOCK(3) = 12 states
 * - TRIANGLE(1) = 6 states, TRIANGLE(2) = 12 states
 *
 * Usage:
 * <pre>
 * // Use built-in Tiles variant (default)
 * String bar = ProgressBarRenderer.render(progress);
 *
 * // Use Moon variant with 2 chars width
 * String moon = ProgressBarRenderer.render(progress, time, ProgressBarRenderer.MOON.withWidth(2));
 *
 * // Create custom variant
 * ProgressVariant custom = new ProgressVariant(
 *     new String[]{"▱", "▰", "▰▰", "▰▰▰"},
 *     100L  // animation interval in ms
 * );
 * String myBar = ProgressBarRenderer.render(progress, time, custom);
 * </pre>
 */
public class ProgressBarRenderer {

    /**
     * Empty tile character
     */
    private static final String EMPTY_TILE = "▱";

    /**
     * Filled tile character
     */
    private static final String FILLED_TILE = "▰";

    /**
     * Number of tiles in the TILES progress bar
     */
    private static final int TILE_COUNT = 7;

    /**
     * Tiles variant using Unicode block characters with rainbow gradient.
     * Dynamically generated based on progress. Width is fixed to 7 tiles.
     */
    public static final ProgressVariant TILES = new ProgressVariant(
        VariantType.TILES,
        1,  // fixed width for TILES
        80L
    );

    /**
     * Moon phase variant using emoji (new moon → full moon).
     * Default width: 1 char (5 states).
     * With width 2: 10 states (5 phases × 2).
     */
    public static final ProgressVariant MOON = new ProgressVariant(
        VariantType.MOON,
        1,  // default width
        80L
    );

    /**
     * Clock variant using clock emoji (12 hour positions).
     * Default width: 1 char (12 states).
     * With width 2: 24 states (12 faces × 2).
     */
    public static final ProgressVariant CLOCK = new ProgressVariant(
        VariantType.CLOCK,
        1,  // default width
        80L
    );

    /**
     * Shade variant (░ → ▒ → ▓ → █).
     * Width determines number of characters.
     * With width 1: 4 states.
     * With width 2: 8 states.
     * With width 3: 12 states.
     */
    public static final ProgressVariant SHADE = new ProgressVariant(
        VariantType.SHADE,
        1,  // default width (4 states)
        80L
    );

    /**
     * Block1 variant using ▏ character.
     * Width determines number of characters (default 6 = 7 states: binary on/off).
     */
    public static final ProgressVariant BLOCK1 = new ProgressVariant(
        VariantType.BLOCK1,
        6,  // default width (7 binary states)
        80L
    );

    /**
     * Block2 variant using ▍ character.
     * Width determines number of characters (default 6 = 7 states: binary on/off).
     */
    public static final ProgressVariant BLOCK2 = new ProgressVariant(
        VariantType.BLOCK2,
        6,  // default width (7 binary states)
        80L
    );

    /**
     * Block3 variant using ▋ character.
     * Width determines number of characters (default 6 = 7 states: binary on/off).
     */
    public static final ProgressVariant BLOCK3 = new ProgressVariant(
        VariantType.BLOCK3,
        6,  // default width (7 binary states)
        80L
    );

    /**
     * Block4 variant using ▉ character.
     * Width determines number of characters (default 6 = 7 states: binary on/off).
     */
    public static final ProgressVariant BLOCK4 = new ProgressVariant(
        VariantType.BLOCK4,
        6,  // default width (7 binary states)
        80L
    );

    /**
     * Triangle fill variant (▹ → ▸).
     * Binary states: each char is either ▹ (empty) or ▸ (filled).
     * Default width: 6 chars (7 states: all ▹ → all ▸).
     */
    public static final ProgressVariant TRIANGLE = new ProgressVariant(
        VariantType.TRIANGLE,
        6,  // default width (7 binary states)
        80L
    );

    /**
     * Current selected variant (default: TILES)
     */
    private static ProgressVariant currentVariant = TILES;

    private ProgressBarRenderer() {
        // Utility class
    }

    /**
     * Get the currently selected progress variant.
     *
     * @return Current progress variant
     */
    public static ProgressVariant getCurrentVariant() {
        return currentVariant;
    }

    /**
     * Set the current progress variant.
     *
     * @param variant The variant to use
     */
    public static void setCurrentVariant(ProgressVariant variant) {
        if (variant == null) {
            throw new IllegalArgumentException("Variant cannot be null");
        }
        currentVariant = variant;
    }

    /**
     * Render an animated progress bar using the current variant.
     *
     * @param progress Progress from 0.0 to 1.0
     * @return Rendered progress bar string with MiniMessage formatting
     */
    public static String render(float progress) {
        return render(progress, System.currentTimeMillis(), currentVariant);
    }

    /**
     * Render an animated progress bar using the current variant.
     *
     * @param progress Progress from 0.0 to 1.0
     * @param currentTimeMs Current time in milliseconds for animation
     * @return Rendered progress bar string with MiniMessage formatting
     */
    public static String render(float progress, long currentTimeMs) {
        return render(progress, currentTimeMs, currentVariant);
    }

    /**
     * Render an animated progress bar using the specified variant.
     *
     * At 100%, returns "READY" in green.
     * Below 100%, shows progress bar with optional rainbow gradient.
     *
     * @param progress Progress from 0.0 to 1.0
     * @param currentTimeMs Current time in milliseconds for animation
     * @param variant The progress variant to use
     * @return Rendered progress bar string with MiniMessage formatting
     */
    public static String render(float progress, long currentTimeMs, ProgressVariant variant) {
        // Clamp progress to valid range
        progress = Math.max(0.0f, Math.min(1.0f, progress));

        // At 100%, show READY in green
        if (progress >= 1.0f) {
            return "<green>READY";
        }

        // Render based on variant type
        return variant.render(progress, currentTimeMs);
    }

    /**
     * Get the current static progress bar (no animation) using the current variant.
     *
     * @param progress Progress from 0.0 to 1.0
     * @return Static progress bar string
     */
    public static String getStatic(float progress) {
        return getStatic(progress, currentVariant);
    }

    /**
     * Get the current static progress bar (no animation) using the specified variant.
     *
     * @param progress Progress from 0.0 to 1.0
     * @param variant The progress variant to use
     * @return Static progress bar string
     */
    public static String getStatic(float progress, ProgressVariant variant) {
        progress = Math.max(0.0f, Math.min(1.0f, progress));

        // At 100%, show READY
        if (progress >= 1.0f) {
            return "<green>READY";
        }

        return variant.renderStatic(progress);
    }

    /**
     * Variant type enumeration.
     */
    public enum VariantType {
        TILES,
        MOON,
        CLOCK,
        SHADE,
        BLOCK1,
        BLOCK2,
        BLOCK3,
        BLOCK4,
        TRIANGLE,
        CUSTOM
    }

    /**
     * Progress variant containing frame definitions, animation interval, and width.
     *
     * Each variant has a width parameter that affects the number of progress states:
     * - MOON(2) = 10 states (5 phases × 2 chars)
     * - CLOCK(2) = 24 states (12 faces × 2 chars)
     * - BLOCK(n) = 4n states (4 transitions × n chars)
     * - TRIANGLE(n) = 6n states (6 fills × n chars)
     */
    public static class ProgressVariant {
        private final VariantType type;
        private final int width;
        private final long interval;
        private final String[] customFrames;

        /**
         * Create a variant from type, width, and interval.
         *
         * @param type The variant type
         * @param width Number of characters (affects state count)
         * @param interval Animation interval in milliseconds
         */
        public ProgressVariant(VariantType type, int width, long interval) {
            this(type, width, interval, null);
        }

        /**
         * Create a custom variant with explicit frames.
         *
         * @param frames Array of frame strings (minimum 2)
         * @param interval Animation interval in milliseconds
         */
        public ProgressVariant(String[] frames, long interval) {
            this(VariantType.CUSTOM, frames.length, interval, frames);
            if (frames == null || frames.length < 2) {
                throw new IllegalArgumentException("Custom variant must have at least 2 frames");
            }
        }

        private ProgressVariant(VariantType type, int width, long interval, String[] customFrames) {
            if (type == null) {
                throw new IllegalArgumentException("Type cannot be null");
            }
            if (width < 1) {
                throw new IllegalArgumentException("Width must be at least 1");
            }
            if (interval < 1) {
                throw new IllegalArgumentException("Interval must be at least 1ms");
            }
            this.type = type;
            this.width = width;
            this.interval = interval;
            this.customFrames = customFrames;
        }

        /**
         * Create a new variant with different width.
         *
         * @param newWidth The new width
         * @return New variant with specified width
         */
        public ProgressVariant withWidth(int newWidth) {
            return new ProgressVariant(type, newWidth, interval, customFrames);
        }

        /**
         * Get the variant type.
         */
        public VariantType getType() {
            return type;
        }

        /**
         * Get the width (number of characters).
         */
        public int getWidth() {
            return width;
        }

        /**
         * Get the animation interval.
         */
        public long getInterval() {
            return interval;
        }

        /**
         * Get the total number of states for this variant.
         */
        public int getStateCount() {
            return switch (type) {
                case TILES -> TILE_COUNT;
                case MOON -> 5 * width;
                case CLOCK -> 12 * width;
                case SHADE -> 4 * width;
                case BLOCK1, BLOCK2, BLOCK3, BLOCK4 -> width + 1;  // binary: each char can be space or symbol
                case TRIANGLE -> width + 1;  // binary: each char is ▹ or ▸
                case CUSTOM -> customFrames != null ? customFrames.length : width;
            };
        }

        /**
         * Check if this variant uses rainbow gradient.
         */
        public boolean hasRainbowGradient() {
            return type == VariantType.TILES;
        }

        /**
         * Render the progress bar with animation.
         */
        String render(float progress, long currentTimeMs) {
            int stateCount = getStateCount();
            int state = (int) (progress * stateCount);
            state = Math.min(stateCount - 1, state);

            // Get next state for animation
            int nextState = Math.min(stateCount - 1, state + 1);

            // Calculate animation timing
            long animationTime = currentTimeMs % (interval * 2);
            boolean showNext = animationTime >= interval;

            int displayState = showNext ? nextState : state;

            return renderState(displayState);
        }

        /**
         * Render static progress bar (no animation).
         */
        String renderStatic(float progress) {
            int stateCount = getStateCount();
            int state = (int) (progress * stateCount);
            state = Math.min(stateCount - 1, state);
            return renderState(state);
        }

        /**
         * Render a specific state.
         */
        String renderState(int state) {
            return switch (type) {
                case TILES -> renderTiles(state);
                case MOON -> renderMoon(state);
                case CLOCK -> renderClock(state);
                case SHADE -> renderShade(state);
                case BLOCK1 -> renderBlock1(state);
                case BLOCK2 -> renderBlock2(state);
                case BLOCK3 -> renderBlock3(state);
                case BLOCK4 -> renderBlock4(state);
                case TRIANGLE -> renderTriangle(state);
                case CUSTOM -> customFrames != null ? customFrames[state] : "";
                default -> "";
            };
        }

        /**
         * Render TILES variant with rainbow gradient.
         */
        private String renderTiles(int state) {
            int filledCount = state;
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < TILE_COUNT; i++) {
                if (i < filledCount) {
                    sb.append(getRainbowColor(i, filledCount)).append(FILLED_TILE).append("</color>");
                } else {
                    sb.append("<gray>").append(EMPTY_TILE).append("</gray>");
                }
            }

            return sb.toString();
        }

        /**
         * Render MOON variant (reversed: full moon → new moon).
         * 0% = full moon 🌕, 100% = new moon 🌑
         */
        private String renderMoon(int state) {
            // Reversed: full moon to new moon
            String[] moonPhases = {"🌕", "🌔", "🌓", "🌒", "🌑"};

            // Map state (0 to 5*width-1) to moon phases
            // Each phase is repeated 'width' times for smooth progression
            int phase = state / width;
            phase = Math.min(4, phase);

            // If width > 1, show the current phase 'width' times or progress through phase
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < width; i++) {
                sb.append(moonPhases[Math.min(phase + (i < (state % width) ? 1 : 0), 4)]);
            }
            return sb.toString();
        }

        /**
         * Render CLOCK variant.
         */
        private String renderClock(int state) {
            String[] clocks = {"🕐", "🕑", "🕒", "🕓", "🕔", "🕕", "🕖", "🕗", "🕘", "🕙", "🕚", "🕛"};

            // Map state (0 to 12*width-1) to clock faces
            int clock = state / width;
            clock = Math.min(11, clock);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < width; i++) {
                sb.append(clocks[Math.min(clock + (i < (state % width) ? 1 : 0), 11)]);
            }
            return sb.toString();
        }

        /**
         * Render SHADE variant (░ → ▒ → ▓ → █).
         * All chars start at ░, then fill left-to-right through all states.
         * Width 2 example: ░░ → ▒░ → ▓░ → █░ → █▒ → █▓ → ██ (7 states shown as 8 for animation)
         */
        private String renderShade(int state) {
            String[] blocks = {"░", "▒", "▓", "█"};
            StringBuilder sb = new StringBuilder();

            // State determines how many "fill units" to display
            // Each char has 4 states, filling left to right
            for (int i = 0; i < width; i++) {
                // Calculate which state this char should be in
                int charState = Math.min(3, Math.max(0, state - i * 4));
                sb.append(blocks[charState]);
            }
            return sb.toString();
        }

        /**
         * Render BLOCK1 variant (▏).
         * Binary: each char is either space " " or symbol "▏".
         * Width 6: "      " → "▏     " → "▏▏    " → ... → "▏▏▏▏▏▏" (7 states)
         */
        private String renderBlock1(int state) {
            StringBuilder sb = new StringBuilder();
            String symbol = "▏";
            for (int i = 0; i < width; i++) {
                // First 'state' chars get the symbol, rest get space
                sb.append(i < state ? symbol : " ");
            }
            return sb.toString();
        }

        /**
         * Render BLOCK2 variant (▍).
         * Binary: each char is either space " " or symbol "▍".
         */
        private String renderBlock2(int state) {
            StringBuilder sb = new StringBuilder();
            String symbol = "▍";
            for (int i = 0; i < width; i++) {
                sb.append(i < state ? symbol : " ");
            }
            return sb.toString();
        }

        /**
         * Render BLOCK3 variant (▋).
         * Binary: each char is either space " " or symbol "▋".
         */
        private String renderBlock3(int state) {
            StringBuilder sb = new StringBuilder();
            String symbol = "▋";
            for (int i = 0; i < width; i++) {
                sb.append(i < state ? symbol : " ");
            }
            return sb.toString();
        }

        /**
         * Render BLOCK4 variant (▉).
         * Binary: each char is either space " " or symbol "▉".
         */
        private String renderBlock4(int state) {
            StringBuilder sb = new StringBuilder();
            String symbol = "▉";
            for (int i = 0; i < width; i++) {
                sb.append(i < state ? symbol : " ");
            }
            return sb.toString();
        }

        /**
         * Render TRIANGLE variant (▹ → ▸).
         * Binary: each char is either ▹ (empty) or ▸ (filled).
         * Width 6: "▹▹▹▹▹▹" → "▸▹▹▹▹▹" → "▸▸▹▹▹▹" → ... → "▸▸▸▸▸▸" (7 states)
         */
        private String renderTriangle(int state) {
            String empty = "▹";
            String filled = "▸";

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < width; i++) {
                // First 'state' chars get filled, rest get empty
                sb.append(i < state ? filled : empty);
            }
            return sb.toString();
        }

        /**
         * Get rainbow color for a tile.
         */
        private String getRainbowColor(int index, int filledCount) {
            String[] rainbowColors = {
                "<color:#ff0000>",  // red
                "<color:#ffff00>",  // yellow
                "<color:#00ff00>",  // green
                "<color:#00ffff>",  // cyan
                "<color:#0000ff>",  // blue
                "<color:#ff00ff>",  // magenta
            };

            int colorIndex = (index * 2) % rainbowColors.length;
            return rainbowColors[colorIndex];
        }
    }
}
