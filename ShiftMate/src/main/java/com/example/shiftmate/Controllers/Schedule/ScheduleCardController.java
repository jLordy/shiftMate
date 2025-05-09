package com.example.shiftmate.Controllers.Schedule;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleCardController {
    @FXML
    private VBox scheduleCard;
    @FXML private Label labelSubjectName;
    @FXML private Label labelEmployeeName;
    @FXML private Label labelSectionName;

    // a static map from section → its assigned color
    private static final Map<String,String> sectionColorMap = new HashMap<>();
    // a simple palette to cycle through
    private static final List<String> COLOR_PALETTE = List.of(
            "#A8E0FF",
            "#8EE3F5",
            "#70CAD1",
            "#859ed4",
            "#F7ECE1",
            "#F2EFE9",
            "#9CFFD9",
            "#D5F2E3"
    );
    private static int nextColorIndex = 0;

    public void setData(String subject, String employee, String section, double duration) {
        labelSubjectName.setText(subject);
        labelEmployeeName.setText(employee);
        labelSectionName.setText(section);
        // adjust height: e.g. 50px per half-hour → duration*2*50
        double height = duration * 2 * 50;
        scheduleCard.setPrefHeight(height);

        // pick (or remember) a color for this section
        String bgColor = sectionColorMap.computeIfAbsent(
                section.toLowerCase(),
                s -> {
                    String c = COLOR_PALETTE.get(nextColorIndex % COLOR_PALETTE.size());
                    nextColorIndex++;
                    return c;
                }
        );

        scheduleCard.setStyle("-fx-background-color: " + bgColor + ";");
        labelSubjectName   .setStyle("-fx-font-size: 20px;");
        labelEmployeeName  .setStyle("-fx-font-size: 18px;");
        labelSectionName   .setStyle("-fx-font-size: 22px;");

    }
}

