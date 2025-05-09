package com.example.shiftmate.Controllers.Schedule;

import com.example.shiftmate.Models.ScheduleModel;
import com.example.shiftmate.Models.SlotModel;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Exports all employees' schedules into a single CSV file,
 * with separate mini-tables per employee, including start and end times.
 */
public class ScheduleExporter {

    /**
     * Export all employees into one CSV, sections per employee.
     * @param schedules   List of generated schedules
     * @param outputPath  Path to the CSV file to write
     */
    public static void exportConsolidatedCsv(List<ScheduleModel> schedules,
                                             Path outputPath) throws IOException {
        // Group slots by employee
        Map<String, List<SlotRecord>> byEmployee = new LinkedHashMap<>();
        for (ScheduleModel sched : schedules) {
            String section = sched.getSectionName();
            // subject -> employee map for this section
            Map<String, String> teachMap = sched.getAssignments().keySet().stream()
                    .collect(Collectors.toMap(
                            kv -> kv.getKey(),
                            kv -> kv.getValue()
                    ));
            for (SlotModel slot : sched.getSlots()) {
                String subj = slot.getSubjectName();
                String emp = teachMap.get(subj);
                if (emp == null) continue;
                byEmployee
                        .computeIfAbsent(emp, k -> new ArrayList<>())
                        .add(new SlotRecord(
                                section,
                                subj,
                                dayLabel(slot.getDayIndex()),
                                timeLabel(slot.getHourIndex()),
                                slot.getTimeAllocation()
                        ));
            }
        }

        // Write out the single CSV
        try (BufferedWriter w = Files.newBufferedWriter(outputPath)) {
            for (Map.Entry<String, List<SlotRecord>> entry : byEmployee.entrySet()) {
                String emp = entry.getKey();
                List<SlotRecord> rows = entry.getValue();

                // Employee header
                w.write("Employee: " + emp);
                w.newLine();

                // Column header
                w.write("Section,Subject,Day,StartTime,EndTime,Duration");
                w.newLine();

                // Data rows
                for (SlotRecord r : rows) {
                    String endTime = computeEndTime(r.startTime, r.duration);
                    w.write(String.join(",",
                            r.section,
                            r.subject,
                            r.day,
                            r.startTime,
                            endTime,
                            String.format("%.1f", r.duration)
                    ));
                    w.newLine();
                }
                // Blank line between employees
                w.newLine();
            }
        }
    }

    /**
     * Compute end time given a start in "HH:00 AM/PM" and a fractional-hour duration.
     */
    private static String computeEndTime(String startTime, double duration) {
        // Parse "07:00 AM" => ["07","00","AM"]
        String[] parts = startTime.split("[: ]+");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        String ampm = parts[2];

        // Convert to 24h minutes
        if ("PM".equals(ampm) && hour < 12) hour += 12;
        if ("AM".equals(ampm) && hour == 12) hour = 0;
        int totalMin = hour * 60 + minute + (int) Math.round(duration * 60);

        // Back to 12h clock
        int h24 = (totalMin / 60) % 24;
        int m = totalMin % 60;
        String newAmPm = h24 < 12 ? "AM" : "PM";
        int h12 = h24 % 12;
        if (h12 == 0) h12 = 12;

        return String.format("%02d:%02d %s", h12, m, newAmPm);
    }

    private static String dayLabel(int idx) {
        return switch (idx) {
            case 0 -> "Monday";
            case 1 -> "Tuesday";
            case 2 -> "Wednesday";
            case 3 -> "Thursday";
            case 4 -> "Friday";
            default -> "Day" + idx;
        };
    }

    private static String timeLabel(double hourIdx) {
        int base = 7 + (int) hourIdx;
        int h12 = base % 12;
        if (h12 == 0) h12 = 12;
        String ap = base < 12 ? "AM" : "PM";
        return String.format("%02d:00 %s", h12, ap);
    }

    /** Simple container for CSV rows */
    private static class SlotRecord {
        final String section;
        final String subject;
        final String day;
        final String startTime;
        final double duration;

        SlotRecord(String section,
                   String subject,
                   String day,
                   String startTime,
                   double duration) {
            this.section = section;
            this.subject = subject;
            this.day = day;
            this.startTime = startTime;
            this.duration = duration;
        }
    }
}