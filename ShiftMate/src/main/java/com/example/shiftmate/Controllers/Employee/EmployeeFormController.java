package com.example.shiftmate.Controllers.Employee;

import com.example.shiftmate.Models.SubjectModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;

import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EmployeeFormController implements Initializable {
    @FXML
    private Button btnAddEmployee, btnAddSubject;
    @FXML
    private TextField fieldName;
    @FXML
    private VBox mainContainer;

    public List<SubjectModel> subjects = new ArrayList<>();
    private List<SubjectFormController> formControllers = new ArrayList<>();
    public Runnable onAddEmployeeCallback;

    public List<SubjectModel> getSubjects(){return subjects;}

    public void updateAllSubjects() {
        subjects.clear(); // Clear existing subjects

        for (SubjectFormController controller : formControllers) {
            if (controller.isValid()) {
                SubjectModel subject = new SubjectModel(
                        controller.getSubjectName(),
                        controller.getSectionsHandle(),
                        controller.getTimeAllocation()
                );
                subjects.add(subject);
            }
        }
    }

    public boolean validateForm() {
        if (fieldName.getText().isEmpty()) {
            showAlert("Employee name is required");
            return false;
        }

        updateAllSubjects(); // Refresh all subjects

        if (subjects.isEmpty()) {
            showAlert("At least one valid subject is required");
            return false;
        }

        return true;
    }

    public String getName(){return fieldName.getText();}
    public void setOnAddEmployeeCallback(Runnable callback) {this.onAddEmployeeCallback = callback;}

    private void generateSubjectForm() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/shiftmate/views/subject_form.fxml"));

        SubjectFormController controller = new SubjectFormController(); // create the controller
        loader.setController(controller); // explicitly set it



        VBox card;
        try {
            card = loader.load(); //links the controller properly
        } catch (IOException e) {
            throw new RuntimeException("Error loading FXML file", e);
        }
        // Set up the remove callback
        controller.setOnRemoveSubjectCallback(() -> {

            int index = formControllers.indexOf(controller);
            if (index >= 0 && index < subjects.size()) {
                subjects.remove(index);
            }
            // Also remove the controller from the list
            formControllers.remove(controller);
        });
        formControllers.add(controller);
        mainContainer.getChildren().add(card);

        controller.setFormControllers(formControllers);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnAddEmployee.setOnAction(event -> {
            if (validateForm() && onAddEmployeeCallback != null) {
                onAddEmployeeCallback.run();
            }
        });

        btnAddSubject.setOnAction(event -> {
            if (formControllers.size() < 5) {
                generateSubjectForm();
            } else {
                showAlert("Maximum subject reached");
            }
        });

        generateSubjectForm();
    }


}
