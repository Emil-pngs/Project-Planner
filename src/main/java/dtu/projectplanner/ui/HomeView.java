package dtu.projectplanner.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class HomeView {

    private double w(double u) { return Style.w(u); }
    private double h(double u) { return Style.h(u); }

    public Pane build() {
        StackPane root = new StackPane();
        root.setStyle(Style.background());
        root.setPrefSize(w(100), h(100));

        VBox center = new VBox(h(2));
        center.setAlignment(Pos.CENTER);
        center.setMaxWidth(w(80));

        Label title = new Label("Project Planner");
        title.setFont(Style.bold(2.4));
        title.setTextFill(Color.web(Style.TEXT_DARK));

        Label subtitle = new Label("Select your role to continue");
        subtitle.setFont(Style.regular(1.3));
        subtitle.setTextFill(Color.web(Style.TEXT_MUTED));

        HBox cards = new HBox(w(5));
        cards.setAlignment(Pos.CENTER);
        cards.setPadding(new Insets(h(5), 0, 0, 0));

        cards.getChildren().addAll(
            buildCard("Employee",
                "Log time and view your\nassigned activities",
                Style.EMPLOYEE,
                () -> App.showEmployee()),
            buildCard("Project Leader",
                "Manage projects, assign\nemployees and track progress",
                Style.LEADER,
                () -> App.showProjectLeader())
        );

        center.getChildren().addAll(title, subtitle, cards);
        root.getChildren().add(center);
        return root;
    }

    private StackPane buildCard(String title, String desc, String color, Runnable action) {
        double cardW = w(28);
        double cardH = h(46);

        VBox card = new VBox(h(2));
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(cardW, cardH);
        card.setMaxSize(cardW, cardH);
        card.setPadding(new Insets(h(4), w(2), h(4), w(2)));
        card.setStyle(Style.card("white"));

        // Shadow lives on a wrapper so it doesn't expand the card's hover bounds
        StackPane wrapper = new StackPane(card);
        wrapper.setEffect(Style.cardShadow(color));

        Label dot = new Label("\u2B24");
        dot.setFont(Style.regular(2.2));
        dot.setTextFill(Color.web(color));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Style.bold(1.8));
        titleLabel.setTextFill(Color.web(Style.TEXT_DARK));
        titleLabel.setWrapText(true);
        titleLabel.setTextAlignment(TextAlignment.CENTER);

        Label descLabel = new Label(desc);
        descLabel.setFont(Style.regular(1.1));
        descLabel.setTextFill(Color.web(Style.TEXT_MUTED));
        descLabel.setWrapText(true);
        descLabel.setTextAlignment(TextAlignment.CENTER);

        Button btn = new Button("Enter \u2192");
        btn.setPrefWidth(w(15));
        btn.setPrefHeight(h(7));
        btn.setStyle(Style.solidButton(color, Style.u(1.1)));
        btn.setOnAction(e -> action.run());
        VBox.setMargin(btn, new Insets(h(2), 0, 0, 0));

        card.hoverProperty().addListener((obs, wasHovered, isHovered) -> {
            if (isHovered) {
                card.setStyle(Style.card(color));
                titleLabel.setTextFill(Color.WHITE);
                descLabel.setTextFill(Color.web(Style.TEXT_FAINT));
                dot.setTextFill(Color.WHITE);
                btn.setStyle(Style.ghostButton(color, Style.u(1.1)));
            } else {
                card.setStyle(Style.card("white"));
                titleLabel.setTextFill(Color.web(Style.TEXT_DARK));
                descLabel.setTextFill(Color.web(Style.TEXT_MUTED));
                dot.setTextFill(Color.web(color));
                btn.setStyle(Style.solidButton(color, Style.u(1.1)));
            }
        });

        card.getChildren().addAll(dot, titleLabel, descLabel, btn);
        return wrapper;
    }
}
