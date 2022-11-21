package com.example.customerdatabaseprojectii.util;

import java.io.File;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAmount;
import java.util.*;

public class RelatedTime {

    public enum TimePeriod {
        MONTH,
        WEEK,
        DAY,
        HOUR
    }

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
     *
     * @param formatter takes a formatter, example: DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
     * @param startTime this is a string representation of the localtime
     * @return returns a formatted localtime
     */
    public static LocalTime formattedTimeParser(DateTimeFormatter formatter, String startTime){
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

    //
    public static LocalDateTime changeTimeBusinessToUserLocal(String Zone, Timestamp timeFromDB){
        LocalDateTime ofEasternTime = timeFromDB.toLocalDateTime();
        ZonedDateTime zdtOfUser = ZonedDateTime.of(ofEasternTime, ZoneId.of(Zone));
        return zdtOfUser.toLocalDateTime();
    }


    public static ZonedDateTime getTimeNowForSpecificZone(String zone) {
        return ZonedDateTime.of(getCurrentDateTime(), ZoneId.of(zone));
    }

    public static ZonedDateTime adjustZoneTime(ZonedDateTime zdt, TimePeriod period, String addOrSubtract, Long timeMeasurementIncrement) {
        switch (period) {
            case MONTH:
                if (Objects.equals(addOrSubtract.toLowerCase(), "add")) {
                    return zdt.plusMonths(timeMeasurementIncrement);
                } else {
                    if (Objects.equals(addOrSubtract.toLowerCase(), "subtract")) {
                        return zdt.minusMonths(timeMeasurementIncrement);
                    }
                }
            case WEEK:
                if (Objects.equals(addOrSubtract.toLowerCase(), "add")) {
                    return zdt.plusWeeks(timeMeasurementIncrement);
                } else {
                    if (Objects.equals(addOrSubtract.toLowerCase(), "subtract")) {
                        return zdt.minusWeeks(timeMeasurementIncrement);
                    }
                }
            case DAY:
                if (Objects.equals(addOrSubtract.toLowerCase(), "add")) {
                   return zdt.plusDays(timeMeasurementIncrement);

                } else {
                    if (Objects.equals(addOrSubtract.toLowerCase(), "subtract")) {
                        return zdt.minusDays(timeMeasurementIncrement);
                    }
                }
            case HOUR:
                if(Objects.equals(addOrSubtract.toLowerCase(), "add")){
                    return zdt.plusHours(timeMeasurementIncrement);
                }else{
                    if(Objects.equals(addOrSubtract.toLowerCase(), "subtract")){
                        return zdt.minusHours(timeMeasurementIncrement);
                    }
                }
            }
        System.out.println("Returning passed in ZoneTime without Modifications");
            return zdt;
        }
    }