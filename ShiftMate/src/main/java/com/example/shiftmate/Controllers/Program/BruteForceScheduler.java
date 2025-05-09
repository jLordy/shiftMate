package com.example.shiftmate.Controllers.Program;

import com.example.shiftmate.Models.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.*;

public class BruteForceScheduler {
    /**
     * Generate schedules for all programs and employees.
     */
    public List<ScheduleModel> generateSchedule(List<ProgramModel> programs,
                                                List<EmployeeModel> employees) {
        List<ScheduleModel> allSchedules = new ArrayList<>();
        List<String> failures = new ArrayList<>();              // collect any scheduling failures

        Map<String, List<SlotModel>> employeeScheduleMap = new HashMap<>();
        Map<String, List<SlotModel>> sectionScheduleMap  = new HashMap<>();
        // Track how many sections of each subject an employee has been assigned
        Map<String, Map<String,Integer>> subjectCountMap = new HashMap<>();

        for (ProgramModel program : programs) {
            for (SectionModel section : program.getSections()) {
                ScheduleModel schedule = new ScheduleModel(section.getSectionName());
                sectionScheduleMap.putIfAbsent(section.getSectionName(), new ArrayList<>());
                List<SlotModel> sectionSlots = sectionScheduleMap.get(section.getSectionName());

                for (SubjectModel subject : program.getSubjectsRequired()) {
                    boolean isAssigned = false;
                    double workload = subject.getCourseType().equals("major") ? 5.0 : 3.0;
                    String subjectName = subject.getSubjectName();
                    // parse the max sections this employee can teach for this subject
                    int maxSections;
                    try {
                        maxSections = Integer.parseInt(subject.getSectionsHandle());
                    } catch (NumberFormatException e) {
                        // fallback: no limit
                        maxSections = Integer.MAX_VALUE;
                    }

                    for (EmployeeModel employee : employees) {
                        if (!employee.getSubjects().contains(subject)) continue;
                        String empName = employee.getEmployeeName();
                        // check current assignment count for this subject
                        int assignedCount = subjectCountMap
                                .getOrDefault(empName, Collections.emptyMap())
                                .getOrDefault(subjectName, 0);
                        if (assignedCount >= maxSections) {
                            // this employee reached their limit for this subject
                            continue;
                        }

                        employeeScheduleMap.putIfAbsent(empName, new ArrayList<>());
                        List<SlotModel> employeeSlots = employeeScheduleMap.get(empName);

                        // Try scheduling slots for this subject
                        List<SlotModel> candidateSlots = allocateSubjectSlots(
                                subjectName, workload, schedule,
                                sectionSlots, employeeSlots);
                        if (candidateSlots != null) {
                            // Book the slots
                            for (SlotModel slot : candidateSlots) {
                                slot.setSubjectName(subjectName);
                                schedule.addSlot(slot);
                                sectionSlots.add(slot);
                                employeeSlots.add(slot);
                            }
                            schedule.setAssignment(subjectName, empName);
                            // increment assignment count
                            subjectCountMap
                                    .computeIfAbsent(empName, k->new HashMap<>())
                                    .merge(subjectName, 1, Integer::sum);
                            isAssigned = true;
                            break;
                        }
                    }
                    if (!isAssigned) {
                        // record the failure instead of immediate alert
                        failures.add(
                                "Section '" + section.getSectionName() + "', Subject '" + subjectName + "'"
                        );
                        break; // give up on this section
                    }
                }
                allSchedules.add(schedule);
            }
        }

        // After attempting all sections, show one consolidated alert if there were failures
        if (!failures.isEmpty()) {
            StringBuilder content = new StringBuilder("The following assignments failed:\n");
            for (String f : failures) {
                content.append("• ").append(f).append("\n");
            }
            showAlert(
                    Alert.AlertType.ERROR,
                    "Scheduling Errors",
                    "Please adjust teacher limits or availability",
                    content.toString()
            );
        }

        return allSchedules;
    }

    /**
     * Allocate slots for a subject, enforcing at most 2 major sessions per day,
     * wrapping Monday->Friday. Returns null if no valid placement.
     */
    private List<SlotModel> allocateSubjectSlots(String subjectName,
                                                 double totalHours,
                                                 ScheduleModel schedule,
                                                 List<SlotModel> sectionSlots,
                                                 List<SlotModel> employeeSlots) {
        int maxDay = schedule.getMAX_DAY();   // typically 5
        int maxHour = schedule.getMAX_HOUR(); // e.g. 12

        // Precompute free windows per day
        Map<Integer, List<Window>> freeWindows = new HashMap<>();
        for (int d = 0; d < maxDay; d++) {
            freeWindows.put(d,
                    findFreeWindows(d, sectionSlots, employeeSlots, maxHour));
        }

        boolean isMajor = (totalHours == 5.0);
        double[] parts = isMajor
                ? new double[]{3.0, 2.0}
                : new double[]{3.0};

        // Minor subjects: try a single 3-hour block first
        if (!isMajor && totalHours == 3.0) {
            for (int d = 0; d < maxDay; d++) {
                for (Window w : freeWindows.get(d)) {
                    if (w.duration >= 3.0) {
                        SlotModel s = new SlotModel(3.0);
                        s.setSubjectName(subjectName);
                        s.setDayIndex(d);
                        s.setHourIndex(w.startHour);
                        return List.of(s);
                    }
                }
            }
            // fallback to two 1.5-hour parts
            parts = new double[]{1.5, 1.5};
        }

        // Track number of major sessions per day
        int[] majorCount = new int[maxDay];
        for (SlotModel ex : sectionSlots) {
            double dur = ex.getTimeAllocation();
            if (isMajor && (dur == 3.0 || dur == 2.0)) {
                majorCount[ex.getDayIndex()]++;
            }
        }

        List<SlotModel> result = new ArrayList<>();
        int dayPointer = 0;
        // Place each part in turn
        for (double part : parts) {
            boolean placed = false;
            // Try each day once, wrapping
            for (int attempt = 0; attempt < maxDay; attempt++) {
                int d = (dayPointer + attempt) % maxDay;
                if (isMajor && majorCount[d] >= 3) continue; // enforce max 3 major sessions per day

                for (Window w : freeWindows.get(d)) {
                    if (w.duration < part) continue;
                    SlotModel slot = new SlotModel(part);
                    slot.setSubjectName(subjectName);
                    slot.setDayIndex(d);
                    slot.setHourIndex(w.startHour);

                    if (!hasSlotConflict(slot, sectionSlots)
                            && !hasSlotConflict(slot, employeeSlots)) {
                        result.add(slot);
                        sectionSlots.add(slot);
                        employeeSlots.add(slot);
                        if (isMajor) majorCount[d]++;
                        dayPointer = (d + 1) % maxDay;
                        placed = true;
                        break;
                    }
                }
                if (placed) break;
            }
            if (!placed) {
                // no placement found
                return null;
            }
        }
        return result;
    }

    /**
     * Find all contiguous free windows on a given day.
     */
    private List<Window> findFreeWindows(int day,
                                         List<SlotModel> sectionSlots,
                                         List<SlotModel> employeeSlots,
                                         int maxHour) {
        List<Window> windows = new ArrayList<>();
        for (int h = 0; h < maxHour; h++) {
            double maxLen = 0;
            for (double len = 0; h + len <= maxHour; len += 0.5) {
                SlotModel test = new SlotModel(len);
                test.setSubjectName(null);
                test.setDayIndex(day);
                test.setHourIndex(h);
                if (!hasSlotConflict(test, sectionSlots)
                        && !hasSlotConflict(test, employeeSlots)) {
                    maxLen = len;
                } else {
                    break;
                }
            }
            if (maxLen >= 0.5) {
                windows.add(new Window(h, maxLen));
            }
        }
        return windows;
    }

    /**
     * Check for overlap between a slot and existing bookings.
     */
    private boolean hasSlotConflict(SlotModel slot, List<SlotModel> existingSlots) {
        int day = slot.getDayIndex();
        double start = slot.getHourIndex();
        double end   = start + slot.getTimeAllocation();
        for (SlotModel ex : existingSlots) {
            if (ex.getDayIndex() != day) continue;
            double exStart = ex.getHourIndex();
            double exEnd   = exStart + ex.getTimeAllocation();
            if (start < exEnd && exStart < end) return true;
        }
        return false;
    }

    /**
     * Represents a free time window starting at startHour for duration hours.
     */
    private static class Window {
        final int startHour;
        final double duration;
        Window(int startHour, double duration) {
            this.startHour = startHour;
            this.duration  = duration;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}
