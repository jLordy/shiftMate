package com.example.shiftmate.Controllers.Program;

import com.example.shiftmate.Models.SectionModel;
import com.example.shiftmate.Models.SubjectModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.*;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import javafx.scene.layout.VBox;

public class ProgramFormController implements Initializable {
    @FXML
    private Button btnAddProgram, btnCancel;
    @FXML
    private VBox checkboxContainer;
    @FXML
    private TextField fieldProgramName, fieldYearLevel, fieldInitials, fieldSections;
    @FXML
    private CheckBox subjectBox;

    private List<SubjectModel> subjects;
    private List<CheckBox> subjectCheckboxes = new ArrayList<>();

    private Runnable onCancelCallback, onAddProgramCallback;
    public void setOnCancelCallback(Runnable onCancelCallback) {this.onCancelCallback = onCancelCallback;}
    public void setOnAddProgramCallback(Runnable onAddProgramCallback) {this.onAddProgramCallback = onAddProgramCallback;}

    public String getProgramName(){
        return fieldProgramName.getText();
    }
    public int getFieldYearLevel(){
        return Integer.parseInt(fieldYearLevel.getText());
    }
    public String getProgramInitials(){
        return fieldInitials.getText();
    }
    public List<SectionModel> getSections() {
        List<SectionModel> sections = new ArrayList<>();

        // Get input values
        String yearLevel = fieldYearLevel.getText().trim();
        String initials = fieldInitials.getText().trim();
        String numSectionsText = fieldSections.getText().trim();

        try {
            int numSections = Integer.parseInt(numSectionsText);

            // UI Validation 2: Check section limits (1-26)
            if (numSections < 1 || numSections > 26) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input",
                        "Number of sections must be between 1 and 26");
                return sections;
            }

            // Create sections
            for (int i = 0; i < numSections; i++) {
                char sectionLetter = (char) ('A' + i);
                String sectionName = String.format("%s-%s%s", initials, yearLevel, sectionLetter);
                sections.add(new SectionModel(sectionName));
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Number",
                    "Please enter a valid number for sections");
        }

        return sections;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Get selected subjects
    public List<SubjectModel> getSelectedSubjects() {
        List<SubjectModel> selected = new ArrayList<>();
        for (CheckBox cb : subjectCheckboxes) {
            if (cb.isSelected()) {
                selected.add((SubjectModel) cb.getUserData());
            }
        }
        return selected;
    }

    // Set subjects and create checkboxes
    public void setSubjects(List<SubjectModel> subjects) {
        if (subjects == null) {
            this.subjects = new ArrayList<>();
        } else {
            // Remove duplicates using LinkedHashSet to maintain order
            this.subjects = new ArrayList<>(new LinkedHashSet<>(subjects));
        }

        // Clear existing checkboxes
        checkboxContainer.getChildren().clear();
        subjectCheckboxes.clear();

        if (subjects != null) {
            // Get all style classes from the template checkbox
            List<String> templateStyles = subjectBox.getStyleClass();
            this.subjects.forEach(subject -> {
                CheckBox cb = new CheckBox(subject.getSubjectName());
                cb.setUserData(subject);
                subjectCheckboxes.add(cb);

                cb.getStyleClass().addAll(templateStyles);

                cb.setFont(subjectBox.getFont());
                cb.setTextFill(subjectBox.getTextFill());
                cb.setPadding(subjectBox.getPadding());
                cb.setGraphicTextGap(subjectBox.getGraphicTextGap());

                checkboxContainer.getChildren().add(cb);
            });
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnAddProgram.setOnAction(_ -> {
            // UI Validation 1: Check empty fields
            if (fieldYearLevel.getText().isEmpty() || fieldInitials.getText().isEmpty() || fieldSections.getText().isEmpty() || fieldProgramName.getText().isEmpty() || getSelectedSubjects().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Missing Information", "All fields must be filled");
            }
            else{onAddProgramCallback.run();}

        });
        btnCancel.setOnAction(_ -> {
            onCancelCallback.run();
        });
    }
}
