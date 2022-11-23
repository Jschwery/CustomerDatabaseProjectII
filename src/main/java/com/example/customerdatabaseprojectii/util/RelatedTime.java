package com.example.customerdatabaseprojectii.util;

import java.io.File;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;
import java.util.*;

public class RelatedTime {

    private static final ZoneId userTimeZone = ZoneId.systemDefault();
    private static final String userLang = System.getProperty("user.language");
    private static final String userCountry = System.getProperty("user.country");

    public static void printZoneIDs() {
        Set<String> zoneIds = ZoneId.getAvailableZoneIds();
        List<String> zoneList = new ArrayList<>(zoneIds);
        Collections.sort(zoneList);
        for (String zoneId : zoneList) {
            System.out.println(zoneId);
        }
    }

    /**
     * @param formatter takes a formatter, example: DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
     * @param startTime this is a string representation of the localtime
     * @return returns a formatted localtime
     */
    public static LocalTime formattedTimeParser(DateTimeFormatter formatter, String startTime) {
        return LocalTime.parse(startTime, formatter);
    }

    public static ZoneId getUserTimeZone() {
        return userTimeZone;
    }

    public static LocalTime getLocalTime() {
        return LocalTime.now();
    }

    public static LocalDateTime getCurrentDateTime() {

        return LocalDateTime.of(getLocalDate(), getLocalTime());
    }

    public static LocalDate getLocalDate() {
        return LocalDate.now();
    }

    public static String getUserLang() {
        return userLang;
    }

    public static String getUserCountry() {
        return userCountry;
    }

    public static LocalDateTime changeTimeBusinessToUserLocal(String Zone, Timestamp timeFromDB) {
        LocalDateTime ofEasternTime = timeFromDB.toLocalDateTime();
        ZonedDateTime zdtOfUser = ZonedDateTime.of(ofEasternTime, ZoneId.of(Zone));
        return zdtOfUser.toLocalDateTime();
    }

    public static ZonedDateTime getTimeNowForSpecificZone(String zone) {
        return ZonedDateTime.of(getCurrentDateTime(), ZoneId.of(zone));
    }

    public enum TimePeriod {
        MONTH,
        WEEK,
        DAY,
        HOUR
    }
}