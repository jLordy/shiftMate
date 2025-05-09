package com.example.shiftmate.Controllers.Schedule;

import com.example.shiftmate.Models.EmployeeModel;
import com.example.shiftmate.Models.ScheduleModel;
import com.example.shiftmate.Models.SlotModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SchedulePageController implements Initializable {
    @FXML private ComboBox<String> comboEmployee;
    @FXML private VBox monContainer, tueContainer, wedContainer, thuContainer, friContainer;
    @FXML private ImageView btnExport;

    // NEW: keep a reference to the raw schedules, so we can export
    private List<ScheduleModel> allSchedules;

    // Map of employee name -> list of their slots
    private Map<String, List<SlotModel>> slotsByEmployee;
    // Map of each slot -> its section name
    private Map<SlotModel, String> sectionBySlot;

    // Constants for visual layout: 1 half-hour == 50px height
    private static final double HALF_HOUR_HEIGHT = 50.0;

    /**
     * Inject schedules and employees after FXML load.
     * Build the slotsByEmployee and sectionBySlot maps and populate the ComboBox.
     */
    public void setSchedules(List<ScheduleModel> schedules,
                             List<EmployeeModel> employees) {
        this.allSchedules = schedules;

        slotsByEmployee = new HashMap<>();
        sectionBySlot   = new HashMap<>();

        // Build maps: for each schedule and each slot, map employee -> slots and slot -> section
        for (ScheduleModel sched : schedules) {
            String sectionName = sched.getSectionName();
            for (SlotModel slot : sched.getSlots()) {
                String subject = slot.getSubjectName();
                // Lookup employee for this subject in this schedule
                String empName = sched.getAssignments().entrySet().stream()
                        .filter(e -> e.getKey().getKey().equals(subject))
                        .map(e -> e.getKey().getValue())
                        .findFirst()
                        .orElse(null);
                if (empName != null) {
                    slotsByEmployee
                            .computeIfAbsent(empName, k -> new ArrayList<>())
                            .add(slot);
                    sectionBySlot.put(slot, sectionName);
                }
            }
        }

        // Populate employee selector
        comboEmployee.setItems(FXCollections.observableArrayList(slotsByEmployee.keySet()));
        comboEmployee.setOnAction(e -> refreshCalendar());
        // Auto-select first employee if available
        if (!comboEmployee.getItems().isEmpty()) {
            comboEmployee.getSelectionModel().selectFirst();
            refreshCalendar();
        }
    }

    /**
     * Clear and re-draw calendar when employee selection changes, including gap-fillers.
     */
    private void refreshCalendar() {
        // Clear previous cards and fillers
        monContainer.getChildren().clear();
        tueContainer.getChildren().clear();
        wedContainer.getChildren().clear();
        thuContainer.getChildren().clear();
        friContainer.getChildren().clear();

        String emp = comboEmployee.getValue();
        if (emp == null) return;

        // Retrieve and group slots by day
        List<SlotModel> allSlots = slotsByEmployee.getOrDefault(emp, Collections.emptyList());
        Map<Integer, List<SlotModel>> slotsByDay = new HashMap<>();
        for (SlotModel slot : allSlots) {
            slotsByDay.computeIfAbsent((int) slot.getDayIndex(), d -> new ArrayList<>()).add(slot);
        }

        // For each day column, insert half-hour fillers and slot cards as grid cells
        for (int day = 0; day <= 4; day++) {
            VBox container;
            switch (day) {
                case 0:
                    container = monContainer;
                    break;
                case 1:
                    container = tueContainer;
                    break;
                case 2:
                    container = wedContainer;
                    break;
                case 3:
                    container = thuContainer;
                    break;
                case 4:
                    container = friContainer;
                    break;
                default:
                    continue;
            }
            List<SlotModel> daySlots = slotsByDay.getOrDefault(day, Collections.emptyList());
            daySlots.sort(Comparator.comparingDouble(SlotModel::getHourIndex));

            int currentHalfUnit = 0; // counts half-hour blocks
            // Place each slot and preceding fillers
            for (SlotModel slot : daySlots) {
                int slotStartHalf = (int) (slot.getHourIndex() * 2);
                // Insert half-hour fillers up to the slot start
                int gapUnits = slotStartHalf - currentHalfUnit;
                for (int i = 0; i < gapUnits; i++) {
                    Pane filler = new Pane();
                    filler.setPrefHeight(HALF_HOUR_HEIGHT);
                    // add border style for grid look
                    filler.getStyleClass().add("cell-border");
                    container.getChildren().add(filler);
                }
                // Now insert the slot card
                String section = sectionBySlot.getOrDefault(slot, "Unknown Section");
                Pane card = createSlotCard(slot, emp, section);
                // Set card height in half-hour units
                int slotUnits = (int) Math.ceil(slot.getTimeAllocation() * 2);
                card.setPrefHeight(slotUnits * HALF_HOUR_HEIGHT);
                // ensure grid border on card
                card.getStyleClass().add("cell-border");
                container.getChildren().add(card);
                // advance current position
                currentHalfUnit = slotStartHalf + slotUnits;
            }
            // Fill the remaining day after the last slot with half-hour fillers to complete the grid
            int TOTAL_HALF_UNITS = 26; // e.g. 13 hours * 2 half-hours per hour
            int remainingUnits = TOTAL_HALF_UNITS - currentHalfUnit;
            for (int i = 0; i < remainingUnits; i++) {
                Pane filler = new Pane();
                filler.setPrefHeight(HALF_HOUR_HEIGHT);
                filler.getStyleClass().add("cell-border");
                container.getChildren().add(filler);
            }
        }
    }

    /**
     * Load a schedule card FXML and populate it with subject, employee, section, and duration.
     */
    private Pane createSlotCard(SlotModel slot, String employeeName, String sectionName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/shiftmate/views/schedule_card.fxml")
            );
            Pane card = loader.load();
            ScheduleCardController controller = loader.getController();
            // Populate the card
            controller.setData(
                    slot.getSubjectName(),
                    employeeName,
                    sectionName,
                    slot.getTimeAllocation()
            );
            return card;
        } catch (IOException ex) {
            ex.printStackTrace();
            return new Pane();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // hook the export click
        btnExport.setOnMouseClicked(evt -> {
            try {
                Path out = Paths.get("all_shifts.csv");
                ScheduleExporter.exportConsolidatedCsv(allSchedules, out);
                new Alert(Alert.AlertType.INFORMATION,
                        "Written consolidated CSV to:\n" + out.toAbsolutePath())
                        .showAndWait();
            } catch(IOException ex) {
                new Alert(Alert.AlertType.ERROR,
                        "Export failed:\n" + ex.getMessage())
                        .showAndWait();
                ex.printStackTrace();
            }
        });
    }
}
