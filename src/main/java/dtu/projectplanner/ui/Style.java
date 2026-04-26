package dtu.projectplanner.ui;

public final class Style {

    private static final String COLOR_BG_ROOT = "#f7f7f7";
    private static final String COLOR_BG_LOGIN = "#f0f0f0";
    private static final String COLOR_SURFACE = "#ffffff";
    private static final String COLOR_BORDER_SOFT = "#cfcfcf";
    private static final String COLOR_BORDER_CARD = "#d0d0d0";
    private static final String COLOR_BADGE_BG = "#e8f0ff";
    private static final String COLOR_BRAND_DEEP = "#1f3a5f";
    private static final String COLOR_PRIMARY = "#2b4c7e";
    private static final String COLOR_SECONDARY_BORDER = "#8aa5cf";
    private static final String COLOR_BORDER_DEFAULT = "#bcbcbc";
    private static final String COLOR_TEXT_DARK = "#333333";
    private static final String COLOR_SELECTION = "#d9e8ff";
    private static final String COLOR_ACCENT = "#4d82c8";
    private static final String COLOR_TITLE = "#1f2937";
    private static final String COLOR_SUBTITLE = "#4b5563";
    private static final String COLOR_LABEL = "#374151";
    private static final String COLOR_ERROR = "#ef4444";
    private static final String COLOR_MUTED = "#6b7280";

    private Style() {
    }

    public static String rootBackground() {
        return "-fx-background-color: " + COLOR_BG_ROOT + ";";
    }

    public static String loginBackground() {
        return "-fx-background-color: " + COLOR_BG_LOGIN + ";";
    }

    public static String loginCard() {
        return "-fx-background-color: " + COLOR_SURFACE + ";" +
            "-fx-background-radius: 0;" +
            "-fx-border-radius: 0;" +
            "-fx-border-color: " + COLOR_BORDER_SOFT + ";" +
            "-fx-border-width: 1;";
    }

    public static String card() {
        return "-fx-background-color: " + COLOR_SURFACE + ";" +
            "-fx-background-radius: 0;" +
            "-fx-border-color: " + COLOR_BORDER_CARD + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 0;";
    }

    public static String topBadge() {
        return "-fx-background-color: " + COLOR_BADGE_BG + ";" +
            "-fx-background-radius: 0;" +
            "-fx-padding: 6 12 6 12;" +
            "-fx-text-fill: " + COLOR_BRAND_DEEP + ";" +
            "-fx-font-weight: 700;";
    }

    public static String primaryButton() {
        return "-fx-background-color: " + COLOR_PRIMARY + ";" +
            "-fx-text-fill: " + COLOR_SURFACE + ";" +
            "-fx-background-radius: 0;" +
            "-fx-border-radius: 0;" +
            "-fx-padding: 8 14 8 14;" +
            "-fx-font-size: 12;" +
            "-fx-font-weight: 700;";
    }

    public static String secondaryButton() {
        return "-fx-background-color: " + COLOR_SURFACE + ";" +
            "-fx-border-color: " + COLOR_SECONDARY_BORDER + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 0;" +
            "-fx-background-radius: 0;" +
            "-fx-text-fill: " + COLOR_BRAND_DEEP + ";" +
            "-fx-padding: 8 14 8 14;" +
            "-fx-font-size: 12;" +
            "-fx-font-weight: 700;";
    }

    public static String ghostButton() {
        return "-fx-background-color: " + COLOR_SURFACE + ";" +
            "-fx-border-color: " + COLOR_BORDER_DEFAULT + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 0;" +
            "-fx-background-radius: 0;" +
            "-fx-text-fill: " + COLOR_TEXT_DARK + ";" +
            "-fx-padding: 8 14 8 14;" +
            "-fx-font-size: 12;" +
            "-fx-font-weight: 600;";
    }

    public static String input() {
        return "-fx-background-color: " + COLOR_SURFACE + ";" +
            "-fx-border-color: " + COLOR_BORDER_DEFAULT + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 0;" +
            "-fx-background-radius: 0;" +
            "-fx-padding: 8 10 8 10;";
    }

    public static String list() {
        return "-fx-background-insets: 0;" +
            "-fx-border-color: " + COLOR_BORDER_DEFAULT + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 0;" +
            "-fx-background-radius: 0;";
    }

    public static String table() {
        return "-fx-border-color: " + COLOR_BORDER_DEFAULT + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 0;" +
            "-fx-background-radius: 0;" +
            "-fx-selection-bar: " + COLOR_SELECTION + ";" +
            "-fx-selection-bar-non-focused: " + COLOR_SELECTION + ";";
    }

    public static String tableColumn() {
        return "-fx-alignment: CENTER_LEFT;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + COLOR_TEXT_DARK + ";";
    }

    public static String tabPane() {
        return "-fx-background-color: transparent;" +
            "-fx-tab-min-height: 34;" +
            "-fx-tab-max-height: 34;" +
            "-fx-accent: " + COLOR_ACCENT + ";";
    }

    public static String dialog() {
        return "-fx-background-color: " + COLOR_BG_ROOT + ";";
    }

    public static String titleXL() {
        return "-fx-font-size: 26;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + COLOR_TITLE + ";";
    }

    public static String titleL() {
        return "-fx-font-size: 20;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + COLOR_TITLE + ";";
    }

    public static String titleM() {
        return "-fx-font-size: 16;" +
            "-fx-font-weight: 700;" +
            "-fx-text-fill: " + COLOR_TITLE + ";";
    }

    public static String subtitle() {
        return "-fx-text-fill: " + COLOR_SUBTITLE + ";" +
            "-fx-font-size: 13;";
    }

    public static String formLabel() {
        return "-fx-text-fill: " + COLOR_LABEL + ";" +
            "-fx-font-size: 12.5;" +
            "-fx-font-weight: 600;";
    }

    public static String errorText() {
        return "-fx-text-fill: " + COLOR_ERROR + ";" +
            "-fx-font-size: 12;";
    }

    public static String statusText() {
        return "-fx-text-fill: " + COLOR_SUBTITLE + ";" +
            "-fx-font-size: 12;";
    }

    public static String mutedText() {
        return "-fx-text-fill: " + COLOR_MUTED + ";" +
            "-fx-font-size: 12;";
    }

    public static String projectNameSmall() {
        return "-fx-text-fill: " + COLOR_TITLE + ";" +
            "-fx-font-size: 13;" +
            "-fx-font-weight: 700;";
    }

    public static String projectMetaSmall() {
        return "-fx-text-fill: " + COLOR_MUTED + ";" +
            "-fx-font-size: 11;";
    }

    public static String projectHeaderLabel() {
        return "-fx-text-fill: " + COLOR_SUBTITLE + ";" +
            "-fx-font-size: 11;" +
            "-fx-font-weight: 700;";
    }
}
