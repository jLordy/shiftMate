package com.example.shiftmate.Models;

public class SlotModel {
    private int dayIndex;    // should be an int
    private int hourIndex;   // should be an int
    private double timeAllocation; // OK as double
    private String subjectName;

    public SlotModel(double timeAllocation) {
        this.timeAllocation = timeAllocation;
    }

    // Getters and Setters
    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    public int getHourIndex() {
        return hourIndex;
    }

    public void setHourIndex(int hourIndex) {
        this.hourIndex = hourIndex;
    }

    public double getTimeAllocation() {
        return timeAllocation;
    }

    // Optional: helper to print the slot info
    @Override
    public String toString() {
        return "Day: " + dayIndex + ", Start Hour Index: " + hourIndex + ", Duration: " + timeAllocation + "hr";
    }

    public String getSubjectName() {
        return subjectName;
    }
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
