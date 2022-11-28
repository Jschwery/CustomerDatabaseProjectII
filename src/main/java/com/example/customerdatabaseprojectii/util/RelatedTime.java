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

    /**
     * Method to print out all the ZoneIds if it is needed to
     * switch the time based off of Zones
     */
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

    //getters and setters
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

    /**
     *
     * @param Zone The zone to be switched to from eastern time zone
     * @param timeFromDB the time of the appointment from the database, which is stored in the business timezone est
     * @return
     */
    public static LocalDateTime changeTimeBusinessToUserLocal(String Zone, Timestamp timeFromDB) {
        LocalDateTime ofEasternTime = timeFromDB.toLocalDateTime();
        ZonedDateTime zdtOfUser = ZonedDateTime.of(ofEasternTime, ZoneId.of(Zone));
        return zdtOfUser.toLocalDateTime();
    }

    public enum TimePeriod {
        MONTH,
        WEEK,
        DAY,
        HOUR
    }
}