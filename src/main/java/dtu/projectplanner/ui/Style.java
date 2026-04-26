package dtu.projectplanner.ui;

public final class Style {

    private Style() {
    }

    public static String rootBackground() {
        return "-fx-background-color: #f7f7f7;";
    }

    public static String loginBackground() {
        return "-fx-background-color: #f0f0f0;";
    }

    public static String loginCard() {
        return "-fx-background-color: #ffffff;" +
            "-fx-background-radius: 0;" +
            "-fx-border-radius: 0;" +
            "-fx-border-color: #cfcfcf;" +
            "-fx-border-width: 1;";
    }

    public static String card() {
        return "-fx-background-color: #ffffff;" +
            "-fx-background-radius: 0;" +
            "-fx-border-color: #d0d0d0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 0;";
    }

    public static String topBadge() {
        return "-fx-background-color: #e8f0ff;" +
            "-fx-background-radius: 0;" +
            "-fx-padding: 6 12 6 12;" +
            "-fx-text-fill: #1f3a5f;" +
            "-fx-font-weight: 700;";
    }

    public static String primaryButton() {
        return "-fx-background-color: #2b4c7e;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 0;" +
            "-fx-border-radius: 0;" +
            "-fx-padding: 8 14 8 14;" +
            "-fx-font-size: 12;" +
            "-fx-font-weight: 700;";
    }

    public static String secondaryButton() {
        return "-fx-background-color: #ffffff;" +
            "-fx-border-color: #8aa5cf;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 0;" +
            "-fx-background-radius: 0;" +
            "-fx-text-fill: #1f3a5f;" +
            "-fx-padding: 8 14 8 14;" +
            "-fx-font-size: 12;" +
            "-fx-font-weight: 700;";
    }

    public static String ghostButton() {
        return "-fx-background-color: #ffffff;" +
            "-fx-border-color: #bcbcbc;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 0;" +
            "-fx-background-radius: 0;" +
            "-fx-text-fill: #333333;" +
            "-fx-padding: 8 14 8 14;" +
            "-fx-font-size: 12;" +
            "-fx-font-weight: 600;";
    }

    public static String input() {
        return "-fx-background-color: #ffffff;" +
            "-fx-border-color: #bcbcbc;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 0;" +
            "-fx-background-radius: 0;" +
            "-fx-padding: 8 10 8 10;";
    }

    public static String list() {
        return "-fx-background-insets: 0;" +
            "-fx-border-color: #bcbcbc;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 0;" +
            "-fx-background-radius: 0;";
    }

    public static String table() {
        return "-fx-border-color: #bcbcbc;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 0;" +
            "-fx-background-radius: 0;" +
            "-fx-selection-bar: #d9e8ff;" +
            "-fx-selection-bar-non-focused: #d9e8ff;";
    }

    public static String tableColumn() {
        return "-fx-alignment: CENTER_LEFT; -fx-font-weight: 700; -fx-text-fill: #333333;";
    }

    public static String tabPane() {
        return "-fx-background-color: transparent;" +
            "-fx-tab-min-height: 34;" +
            "-fx-tab-max-height: 34;" +
            "-fx-accent: #4d82c8;";
    }

    public static String dialog() {
        return "-fx-background-color: #f7f7f7;";
    }

    public static String titleXL() {
        return "-fx-font-size: 26; -fx-font-weight: 700; -fx-text-fill: #1f2937;";
    }

    public static String titleL() {
        return "-fx-font-size: 20; -fx-font-weight: 700; -fx-text-fill: #1f2937;";
    }

    public static String titleM() {
        return "-fx-font-size: 16; -fx-font-weight: 700; -fx-text-fill: #1f2937;";
    }

    public static String subtitle() {
        return "-fx-text-fill: #4b5563; -fx-font-size: 13;";
    }

    public static String formLabel() {
        return "-fx-text-fill: #374151; -fx-font-size: 12.5; -fx-font-weight: 600;";
    }

    public static String errorText() {
        return "-fx-text-fill: #ef4444; -fx-font-size: 12;";
    }

    public static String statusText() {
        return "-fx-text-fill: #4b5563; -fx-font-size: 12;";
    }

    public static String mutedText() {
        return "-fx-text-fill: #6b7280; -fx-font-size: 12;";
    }
}
