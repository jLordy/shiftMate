package com.example.shiftmate.Controllers.Employee;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SubjectFormController implements Initializable {
    @FXML
    private TextField fieldSubjectName;
    @FXML
    private TextField fieldSectionsHandle;
    @FXML
    private RadioButton radioMinor, radioMajor;
    @FXML
    private ImageView btnRemoveSubject;

    private final ToggleGroup courseTypeGroup = new ToggleGroup();
    public Runnable onRemoveSubjectCallback;
    private List<SubjectFormController> formControllers;

    public String getSubjectName(){return fieldSubjectName.getText();}
    public String getSectionsHandle(){return fieldSectionsHandle.getText();}
    public String getTimeAllocation(){return getCourseType();}
    public String getCourseType() {return radioMajor.isSelected() ? "major" : "minor";}

    public void setOnRemoveSubjectCallback(Runnable callback){this.onRemoveSubjectCallback = callback;}
    public void setFormControllers(List<SubjectFormController> formControllers) {this.formControllers = formControllers;}
    public boolean isValid() {
        return !getSubjectName().isEmpty() &&
                !getSectionsHandle().isEmpty() &&
                (radioMajor.isSelected() || radioMinor.isSelected());
    }

    public void removeSubject(){
        if(formControllers.size() == 1){
            showAlert("Employee must have at least one subject");
            return;
        }
        // Get the root VBox of this subject form
        VBox formRoot = (VBox) btnRemoveSubject.getParent().getParent();

        // Traverse up until we find the VBox that is the actual form container
        while (formRoot != null && !(formRoot.getParent() instanceof VBox)) {
            formRoot = (VBox) formRoot.getParent();
        }

        if (formRoot != null && formRoot.getParent() instanceof VBox parentContainer) {
            parentContainer.getChildren().remove(formRoot);
        }
        //Remove the subject from the list
        if (onRemoveSubjectCallback != null){
            onRemoveSubjectCallback.run();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btnRemoveSubject.setOnMouseClicked(event -> {
            removeSubject();
        });
        // Assign toggle group
        radioMajor.setToggleGroup(courseTypeGroup);
        radioMinor.setToggleGroup(courseTypeGroup);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
