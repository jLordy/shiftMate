package com.example.shiftmate.Controllers.Employee;

import com.example.shiftmate.Models.SubjectModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SubjectCardController{
    @FXML
    private Label empSubjectName, empSectionsHandle, empCourseType;

    public void setSubjectData(SubjectModel subject) {
        empSubjectName.setText(subject.getSubjectName());
        empSectionsHandle.setText(subject.getSectionsHandle());
        empCourseType.setText(displayCourseType(subject.getCourseType()));
    }

    private String displayCourseType(String courseType) {
        return (courseType.equals("major")) ? "Major (5hrs)" : "Minor (3hrs)";
    }
}


