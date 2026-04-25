package dtu.projectplanner.ui;

import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.InputStream;

public final class Style {

    // Theme state
    public static boolean dark = false;

    // Colour tokens (mutable; swapped by toggleDark)
    public static String BG           = "#F5F3FF";
    public static String TEXT_DARK    = "#2A2040";
    public static String TEXT_MUTED   = "#6B6080";
    public static String TEXT_FAINT   = "#DDD8EE";
    public static String EMPLOYEE     = "#5A7EC0";
    public static String LEADER       = "#7B5A9E";
    public static String LEADER_BAR   = "#4E2D72";
    public static String SIDEBAR_BG   = "#EDE7F6";
    public static String SIDEBAR_TEXT = "#2E1F45";
    public static String SIDEBAR_MUTED = "#5C4478";
    public static String CARD_BG      = "white";
    public static String DIVIDER      = "#DDD8EE";
    public static String INPUT_BORDER = "#C9BBD9";
    public static String EMPLOYEE_BG      = "#F5F3FF";
    public static String EMPLOYEE_CARD_BG = "white";

    public static void toggleDark() {
        dark = !dark;
        BG            = dark ? "#1B1228" : "#F5F3FF";
        TEXT_DARK     = dark ? "#EDE8FA" : "#2A2040";
        TEXT_MUTED    = dark ? "#9D8DB8" : "#6B6080";
        TEXT_FAINT    = dark ? "#3A2C52" : "#DDD8EE";
        EMPLOYEE      = dark ? "#7A9DD4" : "#5A7EC0";
        LEADER        = dark ? "#9B6DC0" : "#7B5A9E";
        LEADER_BAR    = dark ? "#2A1845" : "#4E2D72";
        SIDEBAR_BG    = dark ? "#241838" : "#EDE7F6";
        SIDEBAR_TEXT  = dark ? "#D8CCEE" : "#2E1F45";
        SIDEBAR_MUTED = dark ? "#8870A8" : "#5C4478";
        CARD_BG       = dark ? "#2D2040" : "white";
        DIVIDER       = dark ? "#3A2C52" : "#DDD8EE";
        INPUT_BORDER  = dark ? "#5A4278" : "#C9BBD9";
        EMPLOYEE_BG      = dark ? "#0D1A2E" : "#F5F3FF";
        EMPLOYEE_CARD_BG = dark ? "#162336" : "white";
    }

    // Grid helpers
    public static double w(double u)  { return u * App.cellW; }
    public static double h(double u)  { return u * App.cellH; }
    public static double u(double g)  { return g * (App.cellW + App.cellH) / 2.0; }

    // Fonts
    public static Font bold(double gridUnits)    { return Font.font("System", FontWeight.BOLD, u(gridUnits)); }
    public static Font regular(double gridUnits) { return Font.font("System", u(gridUnits)); }

    // Dark mode toggle button
    private static String loadSvgPath(String name) {
        try (InputStream is = Style.class.getResourceAsStream(name)) {
            if (is == null) return "";
            String content = new String(is.readAllBytes());
            int start = content.indexOf("d=\"") + 3;
            int end = content.indexOf("\"", start);
            return content.substring(start, end);
        } catch (Exception e) {
            return "";
        }
    }

    private static Button buildToggleBtn(String bg, Color fgColor) {
        SVGPath icon = new SVGPath();
        icon.setContent(dark ? loadSvgPath("sun.svg") : loadSvgPath("moon.svg"));
        icon.setFill(fgColor);
        double scale = u(1.3) / 24.0;
        icon.setScaleX(scale);
        icon.setScaleY(scale);

        double sz = u(2.6);
        Button btn = new Button();
        btn.setGraphic(icon);
        btn.setPrefSize(sz, sz);
        btn.setMinSize(sz, sz);
        btn.setMaxSize(sz, sz);
        btn.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-background-radius: " + sz + ";" +
            "-fx-cursor: hand;" +
            "-fx-padding: 0;" +
            "-fx-alignment: center;"
        );
        btn.setOnAction(e -> { toggleDark(); App.refresh(); });
        return btn;
    }

    /** Toggle button for use in coloured top bars. */
    public static Button buildDarkToggle() {
        Color fg = dark ? Color.web(TEXT_DARK) : Color.web("#FFFFFF", 0.85);
        String bg = dark ? BG : "rgba(255,255,255,0.15)";
        return buildToggleBtn(bg, fg);
    }

    /** Toggle button for use on light/neutral backgrounds (e.g. HomeView). */
    public static Button buildDarkToggleLight() {
        return buildToggleBtn(BG, Color.web(TEXT_DARK));
    }

    // Component CSS
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
        return "-fx-background-color: " + CARD_BG + ";" +
               "-fx-text-fill: " + accent + ";" +
               "-fx-font-size: " + fontPx + "px;" +
               "-fx-font-weight: bold;" +
               "-fx-background-radius: 8;" +
               "-fx-cursor: hand;";
    }

    public static DropShadow cardShadow(String hexColor) {
        DropShadow s = new DropShadow();
        s.setColor(dark ? Color.web("#FFFFFF", 0.04) : Color.web(hexColor, 0.2));
        s.setRadius(dark ? 20 : 28);
        s.setOffsetY(8);
        return s;
    }

    public static String sidebar() {
        return "-fx-background-color: " + SIDEBAR_BG + ";";
    }

    public static String sidebarTextField(double fontPx) {
        return "-fx-font-size: " + fontPx + "px;" +
               "-fx-background-color: " + CARD_BG + ";" +
               "-fx-background-radius: 6;" +
               "-fx-text-fill: " + SIDEBAR_TEXT + ";" +
               "-fx-prompt-text-fill: rgba(100,80,130,0.45);" +
               "-fx-border-color: " + INPUT_BORDER + ";" +
               "-fx-border-radius: 6;" +
               "-fx-padding: 3 8 3 8;";
    }

    public static String sidebarAddButton(double fontPx) {
        return "-fx-background-color: " + LEADER + ";" +
               "-fx-text-fill: white;" +
               "-fx-font-size: " + fontPx + "px;" +
               "-fx-font-weight: bold;" +
               "-fx-background-radius: 6;" +
               "-fx-cursor: hand;";
    }

    public static String sidebarProjectRow() {
        return "-fx-cursor: hand; -fx-background-color: transparent;";
    }

    public static String sidebarProjectRowSelected() {
        return dark
            ? "-fx-background-color: rgba(122,157,212,0.25); -fx-cursor: hand;"
            : "-fx-background-color: rgba(90,126,192,0.18); -fx-cursor: hand;";
    }

    public static String sidebarProjectRowHover() {
        return dark
            ? "-fx-background-color: rgba(255,255,255,0.08); -fx-cursor: hand;"
            : "-fx-background-color: rgba(139,107,168,0.12); -fx-cursor: hand;";
    }

    public static String scrollPaneTransparent() {
        return "-fx-background: transparent;" +
               "-fx-background-color: transparent;" +
               "-fx-border-color: transparent;";
    }

    public static String projectViewHeader() {
        return "-fx-border-color: transparent transparent " + DIVIDER + " transparent;" +
               "-fx-border-width: 0 0 1 0;";
    }

    public static String inputField(double fontPx) {
        return "-fx-font-size: " + fontPx + "px;" +
               "-fx-background-color: " + CARD_BG + ";" +
               "-fx-background-radius: 8;" +
               "-fx-text-fill: " + TEXT_DARK + ";" +
               "-fx-border-color: " + INPUT_BORDER + ";" +
               "-fx-border-radius: 8;" +
               "-fx-padding: 6 12 6 12;";
    }

    public static String idBadge() {
        return "-fx-background-color: " + SIDEBAR_BG + ";" +
               "-fx-background-radius: 20;";
    }

    private Style() {}
}
