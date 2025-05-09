package com.example.shiftmate.Models;

import javafx.util.Pair;

import java.util.*;

public class ScheduleModel {
    private Map<Pair<String, String>, String> assignment = new HashMap<>();
    private String sectionName;
    private List<SlotModel> slots;
    private final int MAX_DAY = 5;

    public int getMAX_DAY() {
        return MAX_DAY;
    }

    public int getMAX_HOUR() {
        return MAX_HOUR;
    }

    private final int MAX_HOUR = 13;
    private int slotsCounter = 0;

    public ScheduleModel(String sectionName) {
        this.sectionName = sectionName;
        this.assignment = new HashMap<>();
        this.slots = new ArrayList<>();
    }

    // Set an assignment between subject and employee
    public void setAssignment(String subjectName, String employeeName) {
        assignment.put(new Pair<>(subjectName, employeeName), sectionName);
    }

    // Add a slot to the schedule
    public void addSlot(SlotModel slot) {
        slots.add(slot);
    }

    // Getter methods
    public String getSectionName() {
        return sectionName;
    }
    public List<SlotModel> getSlots() {
        return slots;
    }
    public Map<Pair<String, String>, String> getAssignments() {
        return assignment;
    }
}
