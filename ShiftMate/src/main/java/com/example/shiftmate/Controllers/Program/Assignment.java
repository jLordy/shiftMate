package com.example.shiftmate.Controllers.Program;

public class Assignment {
    private String employeeName;
    private String subjectName;

    public Assignment(String employeeName, String subjectName) {
        this.employeeName = employeeName;
        this.subjectName = subjectName;
    }

    public String getSubjectName() {
        return subjectName;
    }
}
