package com.melonfishy.sleepycat.utils;

public class ClockTime {

    private int minutes;

    public ClockTime(int hr, int min) {
        minutes = hr * 60 + min;
        if (min < 0 || min >= 60 || minutes < 0 || minutes >= 1440) {
            throw new IllegalArgumentException("Invalid ClockTime constructor arguments: "
            + hr + " hours, " + min + " minutes");
        }
    }

    public ClockTime(String time) {
        if (time == null || !(time.matches("[0-9]{2}:[0-9]{2}")
            || time.matches("[0-9]{2}_[0-9]{2}"))) {
            throw new IllegalArgumentException("Invalid ClockTime constructor argument: "
            + time);
        }
        try {
            int hr = Integer.valueOf(time.substring(0, 2));
            int min = Integer.valueOf(time.substring(3, 5));
            minutes = hr * 60 + min;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("ClockTime constructor argument could not be parsed:"
                    + " " + time);
        }
    }

    public ClockTime(int totalMin) {
        minutes = totalMin;
    }

    public String queryString() {
        String hr = (minutes / 60 >= 10) ? String.valueOf(minutes / 60)
                : "0" + String.valueOf(minutes / 60);
        String min = (minutes % 60 >= 10) ? String.valueOf(minutes % 60)
                : "0" + String.valueOf(minutes % 60);
        return hr + "_" + min;
    }

    public String toString() {
        String hr = ((minutes / 60) % 12 == 0) ? "12"
                : ((minutes / 60) % 12 >= 10) ? String.valueOf((minutes / 60) % 12)
                : " " + String.valueOf((minutes / 60) % 12);
        String min = (minutes % 60 >= 10) ? String.valueOf(minutes % 60)
                : "0" + String.valueOf(minutes % 60);
        String meridian = (minutes / 60 >= 12) ? " PM" : " AM";
        return hr + ":" + min + meridian;
    }

    public int getMinutes() {
        return minutes;
    }

    public ClockTime add(ClockTime other) {
        if (minutes + other.getMinutes() < 1440) {
            return new ClockTime(minutes + other.getMinutes());
        } else {
            return new ClockTime(minutes + other.getMinutes() - 1440);
        }
    }

    public ClockTime subtract(ClockTime other) {
        if (minutes - other.getMinutes() >= 0) {
            return new ClockTime(minutes - other.getMinutes());
        } else {
            return subtract(new ClockTime(other.getMinutes() - 1440));
        }
    }
}
