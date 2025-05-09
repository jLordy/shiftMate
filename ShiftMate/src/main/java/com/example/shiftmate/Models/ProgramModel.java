package com.example.shiftmate.Models;

import java.util.List;

public class ProgramModel {
    String name;
    String programInitials;
    int yearLevel;
    List<SectionModel> sections;
    List<SubjectModel> subjectsRequired;

    public ProgramModel(String name, String programInitials, int yearLevel, List<SectionModel> sections, List<SubjectModel> subjectsRequired) {
        this.name = name;
        this.programInitials = programInitials;
        this.yearLevel = yearLevel;
        this.sections = sections;
        this.subjectsRequired = subjectsRequired;
    }

    public String getName() {return name;}
    public String getProgramInitials() {
        return programInitials;
    }
    public int getYearLevel() {
        return yearLevel;
    }
    public List<SectionModel> getSections() {
        return sections;
    }
    public List<SubjectModel> getSubjectsRequired() {
        return subjectsRequired;
    }

}
