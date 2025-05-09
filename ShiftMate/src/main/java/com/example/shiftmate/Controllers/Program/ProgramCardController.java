package com.example.shiftmate.Controllers.Program;

import com.example.shiftmate.Models.ProgramModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ProgramCardController implements Initializable {
    @FXML
    private ImageView btnRemove;
    @FXML
    private Label labelInitials, labelProgramName, labelSections, labelSubjectsNum, labelYear;
    @FXML
    private VBox subjectContainer;

    private Runnable onRemoveCallback;

    public void setProgramData(ProgramModel program) {
        labelInitials.setText(program.getProgramInitials());
        labelProgramName.setText(program.getName());
        labelSections.setText(String.valueOf(program.getSections().size()));
        labelSubjectsNum.setText(String.valueOf(program.getSubjectsRequired().size()));
        labelYear.setText(String.valueOf(program.getYearLevel()));

        // Clear existing subjects (keeps your template labelSubject if needed)
        subjectContainer.getChildren().clear();

        // Add each subject with consistent styling
        program.getSubjectsRequired().forEach(subject -> {
            Label subjectLabel = new Label(" •" + subject.getSubjectName());
            // Apply the CSS class
            subjectLabel.getStyleClass().addAll("text-body", "text-bold");

            subjectContainer.getChildren().add(subjectLabel);
        });
    }



    public void setOnRemoveCallback(Runnable callback) {
        this.onRemoveCallback = callback;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnRemove.setOnMouseClicked(e -> {
            onRemoveCallback.run();
        });
    }
}
