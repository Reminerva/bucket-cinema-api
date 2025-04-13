package com.flix.flix.constant.customEnum;

import lombok.Getter;

@Getter
public enum ESchedule {

    SCHEDULE_9_00("9:00"),
    SCHEDULE_9_10("9:10"),
    SCHEDULE_9_20("9:20"),
    SCHEDULE_9_30("9:30"),
    SCHEDULE_9_40("9:40"),
    SCHEDULE_9_50("9:50"),
    SCHEDULE_10_00("10:00"),
    SCHEDULE_10_10("10:10"),
    SCHEDULE_10_20("10:20"),
    SCHEDULE_10_30("10:30"),
    SCHEDULE_10_40("10:40"),
    SCHEDULE_10_50("10:50"),
    SCHEDULE_11_00("11:00"),
    SCHEDULE_11_10("11:10"),
    SCHEDULE_11_20("11:20"),
    SCHEDULE_11_30("11:30"),
    SCHEDULE_11_40("11:40"),
    SCHEDULE_11_50("11:50"),
    SCHEDULE_12_00("12:00"),
    SCHEDULE_12_10("12:10"),
    SCHEDULE_12_20("12:20"),
    SCHEDULE_12_30("12:30"),
    SCHEDULE_12_40("12:40"),
    SCHEDULE_12_50("12:50"),
    SCHEDULE_13_00("13:00"),
    SCHEDULE_13_10("13:10"),
    SCHEDULE_13_20("13:20"),
    SCHEDULE_13_30("13:30"),
    SCHEDULE_13_40("13:40"),
    SCHEDULE_13_50("13:50"),
    SCHEDULE_14_00("14:00"),
    SCHEDULE_14_10("14:10"),
    SCHEDULE_14_20("14:20"),
    SCHEDULE_14_30("14:30"),
    SCHEDULE_14_40("14:40"),
    SCHEDULE_14_50("14:50"),    
    SCHEDULE_15_00("15:00"),
    SCHEDULE_15_10("15:10"),
    SCHEDULE_15_20("15:20"),
    SCHEDULE_15_30("15:30"),
    SCHEDULE_15_40("15:40"),
    SCHEDULE_16_00("16:00"),
    SCHEDULE_16_10("16:10"),
    SCHEDULE_16_20("16:20"),
    SCHEDULE_16_30("16:30"),
    SCHEDULE_16_40("16:40"),
    SCHEDULE_16_50("16:50"),
    SCHEDULE_17_00("17:00"),
    SCHEDULE_17_10("17:10"),
    SCHEDULE_17_20("17:20"),
    SCHEDULE_17_30("17:30"),
    SCHEDULE_17_40("17:40"),
    SCHEDULE_17_50("17:50"),
    SCHEDULE_18_00("18:00"),
    SCHEDULE_18_10("18:10"),
    SCHEDULE_18_20("18:20"),
    SCHEDULE_18_30("18:30"),
    SCHEDULE_18_40("18:40"),
    SCHEDULE_18_50("18:50"),
    SCHEDULE_19_00("19:00"),
    SCHEDULE_19_10("19:10"),
    SCHEDULE_19_20("19:20"),
    SCHEDULE_19_30("19:30"),
    SCHEDULE_19_40("19:40"),
    SCHEDULE_19_50("19:50"),
    SCHEDULE_20_00("20:00");

    private final String description;

    ESchedule (String description){
        this.description = description;
    }

    public static ESchedule findByDescription(String description) {
        for (ESchedule schedule : values()) {
            if (schedule.description.equalsIgnoreCase(description)) {
                return schedule;
            }
        }
        throw new IllegalArgumentException("Invalid schedule description: " + description);
    }
}
