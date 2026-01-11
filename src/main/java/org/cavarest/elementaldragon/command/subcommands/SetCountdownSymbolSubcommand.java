package org.cavarest.elementaldragon.command.subcommands;

import org.bukkit.command.CommandSender;
import org.cavarest.elementaldragon.command.base.AbstractSubcommand;
import org.cavarest.elementaldragon.hud.ProgressBarRenderer;
import org.cavarest.elementaldragon.hud.ProgressBarRenderer.ProgressVariant;
import org.cavarest.elementaldragon.hud.ProgressBarRenderer.VariantType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Subcommand for setting the countdown progress bar style.
 * Handles switching between TILES, MOON, CLOCK, SHADE, BLOCK1-4, and TRIANGLE variants.
 * Also supports configurable width for some variants.
 *
 * <p>Usage:</p>
 * <ul>
 *   <li>{@code /ed setcountdownsym <style> [width]} - Set progress bar style</li>
 * </ul>
 *
 * <p>Available styles:</p>
 * <ul>
 *   <li>TILES - Unicode tile progress bar with rainbow gradient (default, width fixed at 7)</li>
 *   <li>MOON - Moon phase emoji (width affects states: 1=5 states, 2=10 states)</li>
 *   <li>CLOCK - Clock emoji (width affects states: 1=12 states, 2=24 states)</li>
 *   <li>SHADE - Block shading ░▒▓█ (width affects states: 1=4, 2=8, 3=12)</li>
 *   <li>BLOCK1 - Left eighth block ▏ (width = number of characters)</li>
 *   <li>BLOCK2 - One quarter block ▍ (width = number of characters)</li>
 *   <li>BLOCK3 - Three eighths block ▋ (width = number of characters)</li>
 *   <li>BLOCK4 - Five eighths block ▉ (width = number of characters)</li>
 *   <li>TRIANGLE - Triangle fill ▹▸ (width affects states: 1=6, 2=12)</li>
 * </ul>
 */
public class SetCountdownSymbolSubcommand extends AbstractSubcommand {

    private static final List<String> VALID_STYLES = Arrays.asList(
        "TILES", "MOON", "CLOCK", "SHADE", "BLOCK1", "BLOCK2", "BLOCK3", "BLOCK4", "TRIANGLE", "HELP"
    );

    /**
     * Creates a new set countdown symbol subcommand.
     */
    public SetCountdownSymbolSubcommand() {
        super(
            "setcountdownsym",
            "Set countdown progress bar style",
            "/ed setcountdownsym <style> [width]",
            "elementaldragon.admin"
        );
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendError(sender, "Usage: /ed setcountdownsym <style> [width]");
            sendStyleInfo(sender);
            return true;
        }

        String styleArg = args[0].toUpperCase();

        // Handle help explicitly
        if (styleArg.equals("HELP")) {
            showDetailedStyleHelp(sender);
            return true;
        }

        if (!VALID_STYLES.contains(styleArg)) {
            sendError(sender, "Invalid style: " + styleArg);
            sendStyleInfo(sender);
            return true;
        }

        VariantType type = VariantType.valueOf(styleArg);
        int width = 1; // default width

        // Parse optional width parameter
        if (args.length >= 2) {
            Integer widthArg = parseInteger(sender, args[1], "width");
            if (widthArg == null) {
                return true;
            }
            if (widthArg < 1 || widthArg > 10) {
                sendError(sender, "Width must be between 1 and 10");
                return true;
            }
            width = widthArg;
        }

        ProgressVariant variant;

        switch (type) {
            case TILES:
                variant = ProgressBarRenderer.TILES;
                sendSuccess(sender, "Set countdown style to TILES (rainbow tiles, 7 tiles)");
                break;

            case MOON:
                variant = ProgressBarRenderer.MOON.withWidth(width);
                sendSuccess(sender, "Set countdown style to MOON (moon phases, width=" + width + ", " + (5 * width) + " states)");
                break;

            case CLOCK:
                variant = ProgressBarRenderer.CLOCK.withWidth(width);
                sendSuccess(sender, "Set countdown style to CLOCK (clock faces, width=" + width + ", " + (12 * width) + " states)");
                break;

            case SHADE:
                variant = ProgressBarRenderer.SHADE.withWidth(width);
                sendSuccess(sender, "Set countdown style to SHADE (░▒▓█, width=" + width + ", " + (4 * width) + " states)");
                break;

            case BLOCK1:
                variant = ProgressBarRenderer.BLOCK1.withWidth(width);
                sendSuccess(sender, "Set countdown style to BLOCK1 (▏, width=" + width + ", " + (width + 1) + " states)");
                break;

            case BLOCK2:
                variant = ProgressBarRenderer.BLOCK2.withWidth(width);
                sendSuccess(sender, "Set countdown style to BLOCK2 (▍, width=" + width + ", " + (width + 1) + " states)");
                break;

            case BLOCK3:
                variant = ProgressBarRenderer.BLOCK3.withWidth(width);
                sendSuccess(sender, "Set countdown style to BLOCK3 (▋, width=" + width + ", " + (width + 1) + " states)");
                break;

            case BLOCK4:
                variant = ProgressBarRenderer.BLOCK4.withWidth(width);
                sendSuccess(sender, "Set countdown style to BLOCK4 (▉, width=" + width + ", " + (width + 1) + " states)");
                break;

            case TRIANGLE:
                variant = ProgressBarRenderer.TRIANGLE.withWidth(width);
                sendSuccess(sender, "Set countdown style to TRIANGLE (▹▸, width=" + width + ", " + (width + 1) + " states)");
                break;

            default:
                sendError(sender, "Invalid style: " + styleArg);
                return true;
        }

        ProgressBarRenderer.setCurrentVariant(variant);
        sendInfo(sender, "Progress bar will now use " + styleArg + " variant.");

        return true;
    }

    /**
     * Shows detailed style help with all transition states.
     */
    private void showDetailedStyleHelp(CommandSender sender) {
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "═══════════════════════════════════════════════════════",
            net.kyori.adventure.text.format.NamedTextColor.GOLD));
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "   Countdown Progress Bar Styles",
            net.kyori.adventure.text.format.NamedTextColor.GOLD));
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "═══════════════════════════════════════════════════════",
            net.kyori.adventure.text.format.NamedTextColor.GOLD));

        // TILES
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "TILES - Rainbow tiles (fixed 7 tiles, rainbow gradient)",
            net.kyori.adventure.text.format.NamedTextColor.AQUA));
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "  ▱▱▱▱▱▱▱ > ▰▱▱▱▱▱▱ > ▰▰▰▱▱▱▱ > ▰▰▰▰▰▰▱ > ▰▰▰▰▰▰▰ [7 states]",
            net.kyori.adventure.text.format.NamedTextColor.GRAY));

        // MOON
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "MOON - Moon phases (width 1-10, 5×width states, countdown)",
            net.kyori.adventure.text.format.NamedTextColor.YELLOW));
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "  🌕 > 🌔 > 🌓 > 🌒 > 🌑 [5 states per char]",
            net.kyori.adventure.text.format.NamedTextColor.GRAY));

        // CLOCK
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "CLOCK - Clock faces (width 1-10, 12×width states)",
            net.kyori.adventure.text.format.NamedTextColor.GREEN));
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "  🕐 > 🕑 > 🕒 > 🕓 > 🕔 > 🕕 > 🕖 > 🕗 > 🕘 > 🕙 > 🕚 > 🕛 [12 states per char]",
            net.kyori.adventure.text.format.NamedTextColor.GRAY));

        // SHADE
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "SHADE - Block shading ░▒▓█ (width 1-10, 4×width states, fills left→right)",
            net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY));
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "  ░ > ▒ > ▓ > █ [4 states per char, width 2: ░░→▒░→▓░→█░→█▒→█▓→██]",
            net.kyori.adventure.text.format.NamedTextColor.GRAY));

        // BLOCK1
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "BLOCK1 - Left eighth block ▏ (width 1-10, binary: width+1 states)",
            net.kyori.adventure.text.format.NamedTextColor.WHITE));
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "     > ▏     > ▏▏    > ... > ▏▏▏▏▏▏ [width 6 = 7 states]",
            net.kyori.adventure.text.format.NamedTextColor.GRAY));

        // BLOCK2
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "BLOCK2 - One quarter block ▍ (width 1-10, binary: width+1 states)",
            net.kyori.adventure.text.format.NamedTextColor.WHITE));
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "     > ▍     > ▍▍    > ... > ▍▍▍▍▍▍ [width 6 = 7 states]",
            net.kyori.adventure.text.format.NamedTextColor.GRAY));

        // BLOCK3
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "BLOCK3 - Three eighths block ▋ (width 1-10, binary: width+1 states)",
            net.kyori.adventure.text.format.NamedTextColor.WHITE));
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "     > ▋     > ▋▋    > ... > ▋▋▋▋▋▋ [width 6 = 7 states]",
            net.kyori.adventure.text.format.NamedTextColor.GRAY));

        // BLOCK4
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "BLOCK4 - Five eighths block ▉ (width 1-10, binary: width+1 states)",
            net.kyori.adventure.text.format.NamedTextColor.WHITE));
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "     > ▉     > ▉▉    > ... > ▉▉▉▉▉▉ [width 6 = 7 states]",
            net.kyori.adventure.text.format.NamedTextColor.GRAY));

        // TRIANGLE
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "TRIANGLE - Triangle fill ▹▸ (width 1-10, binary: width+1 states)",
            net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE));
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "  ▹▹▹▹▹▹ > ▸▹▹▹▹▹ > ▸▸▹▹▹▹ > ... > ▸▸▸▸▸▸ [width 6 = 7 states]",
            net.kyori.adventure.text.format.NamedTextColor.GRAY));

        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "",
            net.kyori.adventure.text.format.NamedTextColor.WHITE));
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "Usage: /ed setcountdownsym <style> [width]",
            net.kyori.adventure.text.format.NamedTextColor.YELLOW));
        sender.sendMessage(net.kyori.adventure.text.Component.text(
            "Example: /ed setcountdownsym MOON 2 (10 states)",
            net.kyori.adventure.text.format.NamedTextColor.GRAY));
    }

    private void sendStyleInfo(CommandSender sender) {
        sendInfo(sender, "Valid styles:");
        sendInfo(sender, "  TILES - Rainbow tiles (fixed 7 tiles)");
        sendInfo(sender, "  MOON - Moon phases (width 1-10, 5×width states)");
        sendInfo(sender, "  CLOCK - Clock faces (width 1-10, 12×width states)");
        sendInfo(sender, "  SHADE - Block shading ░▒▓█ (width 1-10, 4×width states)");
        sendInfo(sender, "  BLOCK1 - Left eighth block ▏ (width 1-10, binary: width+1 states)");
        sendInfo(sender, "  BLOCK2 - One quarter block ▍ (width 1-10, binary: width+1 states)");
        sendInfo(sender, "  BLOCK3 - Three eighths block ▋ (width 1-10, binary: width+1 states)");
        sendInfo(sender, "  BLOCK4 - Five eighths block ▉ (width 1-10, binary: width+1 states)");
        sendInfo(sender, "  TRIANGLE - Triangle fill ▹▸ (width 1-10, binary: width+1 states)");
        sendInfo(sender, "  HELP - Show detailed style information with transitions");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(VALID_STYLES);
        } else if (args.length == 2) {
            // Width suggestions (1-10)
            for (int i = 1; i <= 10; i++) {
                completions.add(String.valueOf(i));
            }
        }

        // Filter by partial input
        if (args.length > 0) {
            String partial = args[args.length - 1].toUpperCase();
            completions.removeIf(c -> !c.startsWith(partial));
        }

        return completions;
    }
}
