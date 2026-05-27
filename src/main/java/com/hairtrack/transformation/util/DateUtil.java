package com.hairtrack.transformation.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    public static Integer daysBetween(LocalDate transplantDate, LocalDateTime photoTaken) {
        if (transplantDate == null || photoTaken == null) return null;
        return (int) ChronoUnit.DAYS.between(transplantDate, photoTaken.toLocalDate());
    }

    public static Integer monthsBetween(LocalDate transplantDate, LocalDateTime photoTaken) {
        if (transplantDate == null || photoTaken == null) return null;
        return (int) ChronoUnit.MONTHS.between(transplantDate, photoTaken.toLocalDate());
    }
}