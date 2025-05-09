package com.example.shiftmate.Controllers.Program;

import com.example.shiftmate.Controllers.Main.GeneratePageController;
import com.example.shiftmate.Models.*;
import com.example.shiftmate.MainController;

import com.example.shiftmate.Controllers.Schedule.SchedulePageController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ProgramPageController implements Initializable {
    @FXML
    private Pane btnAddProgram;
    @FXML
    private VBox mainContainer;
    @FXML
    private HBox programContainer;
    @FXML
    private ImageView btnGenerate, btnBack;
    public MainController mainController;
    private List<EmployeeModel> employees;
    private List<ProgramModel> programs = new ArrayList<>();
    public List<HBox> hBoxList = new ArrayList<>();

    private int cardCount = 0;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    public void setData(List<EmployeeModel> employees, List<ProgramModel> programs) {
        this.employees = employees;
        this.programs = programs;

        // 2. clear out old UI
        mainContainer.getChildren().clear();
        hBoxList.clear();

        // 3. re-initialize the very first row with the Add button
        programContainer.getChildren().clear();
        programContainer.getChildren().add(btnAddProgram);
        mainContainer.getChildren().add(programContainer);
        hBoxList.add(programContainer);

        // 4. re-draw one card per employee
        for (ProgramModel program : programs) {
            fillProgramCard(program);
        }
    }

    public List<SubjectModel> getAllSubjects() {
        return employees.stream()
                .flatMap(employee -> employee.getSubjects().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public void addProgram() {
        try {
            // 1. Verify resource exists
            String fxmlPath = "/com/example/shiftmate/views/program_form.fxml";
            URL resourceUrl = getClass().getResource(fxmlPath);
            if (resourceUrl == null) {
                throw new RuntimeException("FXML file not found at: " + fxmlPath);
            }
            //2. Load the FXMLLoader
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();

            // 3. Get controller with debug
            ProgramFormController controller = loader.getController();
            controller.setSubjects(getAllSubjects());
            // 4. Setup dialog
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Add New Program");
            dialog.setResizable(false);
            dialog.setScene(new Scene(root));

            controller.setOnAddProgramCallback(()-> {
                ProgramModel program = new ProgramModel(
                        controller.getProgramName(),
                        controller.getProgramInitials(),
                        controller.getFieldYearLevel(),
                        controller.getSections(),
                        controller.getSelectedSubjects()
                );
                programs.add(program);
                fillProgramCard(program);
                dialog.close();
            });

            controller.setOnCancelCallback(dialog::close);
            // 6. Show the form
            dialog.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private HBox createNewHBox() {
        HBox newHBox = new HBox();
        newHBox.setSpacing(16);
        newHBox.setAlignment(Pos.CENTER_LEFT);

        if (btnAddProgram.getParent() != null) {
            System.out.println("→ Removing btnAddEmployee from old parent");
            ((Pane) btnAddProgram.getParent()).getChildren().remove(btnAddProgram);
        }

        newHBox.getChildren().add(btnAddProgram);
        mainContainer.getChildren().add(newHBox);
        hBoxList.add(newHBox);
        System.out.println("→ New HBox created and added. HBox count: " + hBoxList.size());
        return newHBox;
    }
    public void fillProgramCard(ProgramModel program){
        try {
            cardCount++;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/shiftmate/views/program_card.fxml"));
            Parent card = loader.load();
            ProgramCardController cardController = loader.getController();
            cardController.setProgramData(program);
            cardController.setOnRemoveCallback(() -> {
                Platform.runLater(() -> {
                    removeProgramCard(card, program);
                });
            });

            HBox currentHBox = hBoxList.getLast();
            currentHBox.getChildren().add(currentHBox.getChildren().size() - 1, card);

            if (currentHBox.getChildren().size() - 1 == 6) {
                currentHBox.getChildren().remove(btnAddProgram);
                HBox newHBox = createNewHBox();  // make sure this only adds to list ONCE
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void removeProgramCard(Node card, ProgramModel program) {
        for (HBox hBox : hBoxList) {
            int count = hBox.getChildren().contains(btnAddProgram)
                    ? hBox.getChildren().size() - 1
                    : hBox.getChildren().size();
        }

        cardCount--;
        // 1. Remove from data list
        programs.remove(program);

        // 2. Remove from UI and find its position
        int hboxIndex = -1;
        int cardIndex = -1;

        for (int i = 0; i < hBoxList.size(); i++) {
            HBox hbox = hBoxList.get(i);
            if (hbox.getChildren().contains(card)) {
                cardIndex = hbox.getChildren().indexOf(card);
                hbox.getChildren().remove(card);
                hboxIndex = i;
                break;
            }
        }

        if (hboxIndex == -1) return; // Card not found

        // 3. Reorganize remaining cards
        reorganizeCardsAfterRemoval();

        // 4. Clean up empty HBoxes (keep at least one)
        cleanUpEmptyHBoxes();
    }
    private void reorganizeCardsAfterRemoval() {
        // Step 1: Gather all cards (excluding the button)
        List<Node> allCards = new ArrayList<>();
        for (HBox hbox : hBoxList) {
            for (Node node : hbox.getChildren()) {
                if (node != btnAddProgram) {
                    allCards.add(node);
                }
            }
            hbox.getChildren().clear(); // Clear all
        }

        // Step 2: Redistribute cards with max 6 per row
        int index = 0;
        for (HBox hbox : hBoxList) {
            while (hbox.getChildren().size() < 6 && index < allCards.size()) {
                hbox.getChildren().add(allCards.get(index++));
            }
        }

        // Step 3: Remove empty HBoxes except the first
        for (int i = hBoxList.size() - 1; i >= 1; i--) {
            HBox hbox = hBoxList.get(i);
            if (hbox.getChildren().isEmpty()) {
                hBoxList.remove(i);
                mainContainer.getChildren().remove(hbox);
            }
        }

        // Step 4: Place the add button in the first HBox with space
        for (HBox hbox : hBoxList) {
            if (hbox.getChildren().size() < 6) {
                hbox.getChildren().add(btnAddProgram);
                return;
            }
        }

        // Step 5: If no space, create new HBox
        HBox newHBox = new HBox(16);
        hBoxList.add(newHBox);
        mainContainer.getChildren().add(newHBox);
    }
    private void cleanUpEmptyHBoxes() {
        // Keep at least one HBox
        for (int i = hBoxList.size() - 1; i > 0; i--) {
            HBox hbox = hBoxList.get(i);
            // Check if only contains add button
            if (hbox.getChildren().size() == 1 &&
                    hbox.getChildren().getFirst() == btnAddProgram) {
                mainContainer.getChildren().remove(hbox);
                hBoxList.remove(i);
            }
        }
        // Ensure add button is in the last HBox
        if (!hBoxList.isEmpty()) {
            HBox lastHBox = hBoxList.getLast();
            if (!lastHBox.getChildren().contains(btnAddProgram)) {
                lastHBox.getChildren().add(btnAddProgram);
            }
        }
    }

    private void generateSchedules() {
        if(programs.isEmpty()){
            showAlert("Invalid Action", "Add a program first");
            return;
        }
        // 1) Generate all schedules
        BruteForceScheduler scheduler = new BruteForceScheduler();
        List<ScheduleModel> results = scheduler.generateSchedule(programs, employees);

        // 2) Load your modern calendar‐pane UI
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/shiftmate/views/schedule_page.fxml")
            );
            Parent root = loader.load();

            // 3) Initialize the new controller with all schedules and employees
            SchedulePageController controller = loader.getController();
            controller.setSchedules(results, employees);

            // 4) Display in a modal window
            Stage stage = new Stage();
            stage.setTitle("Shift Schedule");
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/shiftmate/images/shiftMateLogo.png")));
            stage.getIcons().add(icon);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(true);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            // Optionally show an alert here
        }
    }
    private void backToEmployee(){
        // Load the page and get its controller
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/shiftmate/views/generate_page.fxml"));
        Parent page = null;
        try {
            page = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        GeneratePageController generatePageController = loader.getController();
        generatePageController.setData(employees, programs);
        generatePageController.setMainController(mainController);
        // Show the page
        mainController.getRoot().setCenter(page);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // This will be called when data is actually available
        btnGenerate.setOnMouseClicked(event ->  generateSchedules());
        btnAddProgram.setOnMouseClicked(event -> addProgram());
        btnBack.setOnMouseClicked(event -> backToEmployee());
        // Ensure the first HBox has the Add button
        if (!programContainer.getChildren().contains(btnAddProgram)) {
            programContainer.getChildren().add(btnAddProgram);
        }
        hBoxList.add(programContainer);
    }

}


