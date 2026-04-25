package dtu.projectplanner.domain;

import java.time.LocalDate;

public class TimeEntry {
    private LocalDate date;
    private int hours;

    // We reference the employee and activity to know who worked on what and when
    private Employee employee;
    private Activity activity;

    public TimeEntry(LocalDate date, int hours, Employee employee, Activity activity) {
        this.date = date;
        this.hours = hours;
        this.employee = employee;
        this.activity = activity;
    }

    public int getHours() {
        return hours;
    }
}
