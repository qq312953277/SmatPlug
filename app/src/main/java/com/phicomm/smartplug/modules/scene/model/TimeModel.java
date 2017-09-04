package com.phicomm.smartplug.modules.scene.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TimeModel implements Serializable {
    int hour;
    int minute;
    List<Integer> repeatDays;

    public TimeModel() {
        hour = 0;
        minute = 0;
        repeatDays = new ArrayList<>();
    }

    public TimeModel(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        repeatDays = new ArrayList<>();
    }

    public TimeModel(String timeStream) {
        hour = 0;
        minute = 0;
        repeatDays = new ArrayList<>();
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public List<Integer> getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(List<Integer> repeatDays) {
        this.repeatDays = repeatDays;
    }

    public String getTimeStream() {
        StringBuilder sb = new StringBuilder();
        // append repeat days
        for (int i = 1; i < 8; i++) {
            if (repeatDays.contains(i)) {
                sb.append("1");
            } else {
                sb.append("0");
            }
        }

        // append time
        sb.append((hour >= 10) ? (hour) : ("0" + hour));
        sb.append((minute >= 10) ? (minute) : ("0" + minute));

        return sb.toString();
    }

    public boolean parseTimeStream(String timeStream) {
        if (timeStream == null || timeStream.length() != 11) {
            return false;
        }

        for (int i = 0; i < 7; i++) {
            char c = timeStream.charAt(i);
            if (c == '1') {
                repeatDays.add(i + 1);
            }
        }

        hour = Integer.parseInt(timeStream.substring(7, 9));
        minute = Integer.parseInt(timeStream.substring(9, 11));
        return true;
    }
}
