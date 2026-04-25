package dtu.projectplanner.ui;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public final class Style {

    // Colours
    public static final String BG           = "#F0F4FF";
    public static final String TEXT_DARK    = "#1A1A2E";
    public static final String TEXT_MUTED   = "#6B7280";
    public static final String TEXT_FAINT   = "#E0E0E0";
    public static final String EMPLOYEE     = "#4361EE";
    public static final String LEADER       = "#7B2D8B";

    // Grid helpers (delegate to App cell sizes)
    public static double w(double u)  { return u * App.cellW; }
    public static double h(double u)  { return u * App.cellH; }
    // Uniform scale unit: average of one column-cell and one row-cell
    public static double u(double g)  { return g * (App.cellW + App.cellH) / 2.0; }

    // Fonts
    public static Font bold(double gridUnits)   { return Font.font("System", FontWeight.BOLD, u(gridUnits)); }
    public static Font regular(double gridUnits){ return Font.font("System", u(gridUnits)); }

    // Inline CSS strings
    public static String background() {
        return "-fx-background-color: " + BG + ";";
    }

    public static String topBar(String accent) {
        return "-fx-background-color: " + accent + ";";
    }

    public static String card(String bgColor) {
        return "-fx-background-color: " + bgColor + ";" +
               "-fx-background-radius: 18;" +
               "-fx-cursor: hand;";
    }

    public static String roleTag() {
        return "-fx-background-color: rgba(255,255,255,0.15);" +
               "-fx-background-radius: 20;" +
               "-fx-padding: 4 12 4 12;";
    }

    public static String backButton(double fontPx) {
        return "-fx-background-color: transparent;" +
               "-fx-text-fill: rgba(255,255,255,0.75);" +
               "-fx-font-size: " + fontPx + "px;" +
               "-fx-cursor: hand;" +
               "-fx-padding: 4 12 4 0;";
    }

    public static String solidButton(String accent, double fontPx) {
        return "-fx-background-color: " + accent + ";" +
               "-fx-text-fill: white;" +
               "-fx-font-size: " + fontPx + "px;" +
               "-fx-font-weight: bold;" +
               "-fx-background-radius: 8;" +
               "-fx-cursor: hand;";
    }

    public static String ghostButton(String accent, double fontPx) {
        return "-fx-background-color: white;" +
               "-fx-text-fill: " + accent + ";" +
               "-fx-font-size: " + fontPx + "px;" +
               "-fx-font-weight: bold;" +
               "-fx-background-radius: 8;" +
               "-fx-cursor: hand;";
    }

    // Effects
    public static DropShadow cardShadow(String hexColor) {
        DropShadow s = new DropShadow();
        s.setColor(Color.web(hexColor, 0.2));
        s.setRadius(28);
        s.setOffsetY(8);
        return s;
    }

    private Style() {}
}
