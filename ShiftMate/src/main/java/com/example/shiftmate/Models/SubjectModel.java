package com.example.shiftmate.Models;

public class SubjectModel {
    private String subjectName;
    private String courseType;
    private String sectionsHandle;

    public SubjectModel(String subjectName, String sectionsHandle, String courseType) {
        this.subjectName = subjectName;
        this.sectionsHandle = sectionsHandle;
        this.courseType = courseType;
    }
    public String getSubjectName() { return subjectName; }
    public String getCourseType() {return courseType;}
    public String getSectionsHandle() { return sectionsHandle; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubjectModel that = (SubjectModel) o;
        return subjectName != null && subjectName.trim().equalsIgnoreCase(that.subjectName.trim());
    }

    @Override
    public int hashCode() {
        return subjectName != null ? subjectName.trim().toLowerCase().hashCode() : 0;
    }
}
