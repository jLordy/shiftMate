package com.example.shiftmate.Models;

import java.util.List;

public class EmployeeModel {
    private String employeeName;
    private List<SubjectModel> subjects;

    public EmployeeModel(String name, List<SubjectModel> subjects) {
        this.employeeName = name;
        this.subjects = subjects;
    }

    public String getEmployeeName() { return employeeName; }
    public List<SubjectModel> getSubjects() {return subjects;}
}
