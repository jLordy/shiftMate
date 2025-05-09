package com.example.shiftmate.Controllers.Employee;

import com.example.shiftmate.Models.EmployeeModel;
import com.example.shiftmate.Models.SubjectModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EmployeeCardController implements Initializable {
    @FXML
    private Label empName;
    @FXML
    private ImageView btnRemove;
    @FXML
    private VBox subjectContainer;

    private Runnable onRemoveCallback;

    public void setEmployeeData(EmployeeModel employee) {
        empName.setText(employee.getEmployeeName());
        subjectContainer.getChildren().clear();

        for (SubjectModel subject : employee.getSubjects()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/shiftmate/views/subject_card.fxml"));
                HBox card = loader.load();
                SubjectCardController controller = loader.getController();

                if (controller == null) {
                    System.out.println("Controller is null for subject card!");
                    return;
                }
                controller.setSubjectData(subject);
                subjectContainer.getChildren().add(card);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setOnRemoveCallback(Runnable callback) {this.onRemoveCallback = callback;}
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnRemove.setOnMouseClicked(event -> {
            if (onRemoveCallback != null) {
                onRemoveCallback.run();
            }
        });
    }
}
