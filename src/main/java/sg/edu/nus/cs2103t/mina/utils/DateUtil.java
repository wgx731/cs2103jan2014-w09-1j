package sg.edu.nus.cs2103t.mina.utils;

import hirondelle.date4j.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;

/**
 * Useful Date utilities.
 * 
 * @author BalusC
 * @see CalendarUtil
 * @link http://balusc.blogspot.com/2007/09/dateutil.html
 */
public final class DateUtil {

    // Init
    // ---------------------------------------------------------------------------------------

    private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {
        /**
		 * 
		 */
        private static final long serialVersionUID = 1L;

        {
            put("^\\d{8}$", "ddMMyyyy");
            put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
            put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
            put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "dd/MM/yyyy");
            put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
            put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
            put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
            put("^\\d{12}$", "ddMMyyyyHHmm");
            put("^\\d{8}\\s\\d{4}$", "ddMMyyyy HHmm");
            put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$",
                    "dd-MM-yyyy hh:mm");
            put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$",
                    "yyyy-MM-dd hh:mm");
            put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$",
                    "dd/MM/yyyy hh:mm");
            put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$",
                    "yyyy/MM/dd hh:mm");
            put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$",
                    "dd MMM yyyy hh:mm");
            put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$",
                    "dd MMMM yyyy hh:mm");
            put("^\\d{14}$", "ddMMyyyyHHmmss");
            put("^\\d{8}\\s\\d{6}$", "ddMMyyyy HHmmss");
            put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",
                    "dd-MM-yyyy hh:mm:ss");
            put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$",
                    "yyyy-MM-dd hh:mm:ss");
            put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",
                    "dd/MM/yyyy hh:mm:ss");
            put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$",
                    "yyyy/MM/dd hh:mm:ss");
            put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",
                    "dd MMM yyyy hh:mm:ss");
            put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",
                    "dd MMMM yyyy hh:mm:ss");

            put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}$", "dd.MM.yyyy");
            put("^\\d{4}\\.\\d{1,2}\\.\\d{1,2}$", "yyyy.MM.dd");
            put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}\\s\\d{1,2}:\\d{2}$",
                    "dd.MM.yyyy hh:mm");
            put("^\\d{4}\\.\\d{1,2}\\.\\d{1,2}\\s\\d{1,2}:\\d{2}$",
                    "yyyy.MM.dd hh:mm");
            put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",
                    "dd.MM.yyyy hh:mm:ss");

            put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}.\\d{2}.\\d{2}$",
                    "dd-MM-yyyy HH.mm.ss");
            put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}.\\d{2}.\\d{2}$",
                    "yyyy-MM-dd HH.mm.ss");
            put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}.\\d{2}.\\d{2}$",
                    "dd/MM/yyyy HH.mm.ss");
            put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}.\\d{2}.\\d{2}$",
                    "yyyy/MM/dd HH.mm.ss");
            put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}.\\d{2}.\\d{2}$",
                    "dd MMM yyyy HH.mm.ss");
            put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}.\\d{2}.\\d{2}$",
                    "dd MMMM yyyy HH.mm.ss");
            put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}\\s\\d{1,2}.\\d{2}$",
                    "dd.MM.yyyy HH.mm");
            put("^\\d{4}\\.\\d{1,2}\\.\\d{1,2}\\s\\d{1,2}.\\d{2}$",
                    "yyyy.MM.dd HH.mm");
            put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}\\s\\d{1,2}.\\d{2}.\\d{2}$",
                    "dd.MM.yyyy HH.mm.ss");
            put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}.\\d{2}$",
                    "dd-MM-yyyy HH.mm");
            put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}.\\d{2}$",
                    "yyyy-MM-dd HH.mm");
            put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}.\\d{2}$",
                    "dd/MM/yyyy HH.mm");
            put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}.\\d{2}$",
                    "yyyy/MM/dd HH.mm");
            put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}.\\d{2}$",
                    "dd MMM yyyy HH.mm");
            put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}.\\d{2}$",
                    "dd MMMM yyyy HH.mm");

        }
    };
    
    //@author A0099151B
    private static final LinkedHashMap<String, String> TIME_FORMAT_REGEX = new LinkedHashMap<String, String>() {
        private static final long serialVersionUID = 3L;
        {
            // Military time
            put("^\\d{4}$", "HHmm");

            // HH AM/PM
            put("^\\d{1}(am|pm|AM|PM){1}$", "ha");
            put("^\\d{1,2}(am|pm|AM|PM){1}$", "hha");
            // HH no AM/PM
            put("^\\d{1}$", "H");
            put("^\\d{1,2}$", "HH");

            // HH:MM:SS AM/PM
            put("^\\d{1,2}:\\d{2}(am|pm|AM|PM){1}", "hh:mma");
            put("^\\d{1,2}:\\d{2}:\\{d2}(am|pm|AM|PM){1}", "hh:mm:ssa");
            // HH:MM:SS no AM/PM
            put("^\\d{1,2}:\\d{2}", "HH:mm");
            put("^\\d{1,2}:\\d{2}:\\{d2}", "HH:mm:ss");

            // HH.MM.SS AM/PM
            put("^\\d{1,2}\\.\\d{2}(am|pm|AM|PM){1}", "hh.mma");
            put("^\\d{1,2}\\.\\d{2}\\.\\{d2}(am|pm|AM|PM){1}", "hh.mm.ssa");
            // HH.MM.SS no AM/PM
            put("^\\d{1,2}\\.\\d{2}", "HH.mm");
            put("^\\d{1,2}\\.\\d{2}\\.\\{d2}", "HH.mm.ss");
        }
    };

    private static final LinkedHashMap<String, String> DATE_FORMAT_REGEX = new LinkedHashMap<String, String>() {
        private static final long serialVersionUID = 2L;
        {
            put("^\\d{8}$", "ddMMyyyy");
            put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
            put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
            put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "dd/MM/yyyy");
            put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");

            put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}$", "dd.MM.yyyy");
            put("^\\d{4}\\.\\d{1,2}\\.\\d{1,2}$", "yyyy.MM.dd");

            put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
            put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");

            // i.e 12th july 2014
            put("^\\d{1,2}th\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
            put("^\\d{1,2}th\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");

        }
    };
    
    private static final HashMap<String, String> PARTIAL_DATE_FORMAT = new HashMap<String, String>() {
        private static final long serialVersionUID = 4L;
        {
            put("^\\d{1,2}-\\d{1,2}$", "-");
            put("^\\d{1,2}/\\d{1,2}$", "/");
        }
    };
    
    //@author BalusC
    private static final String CLASS_NAME = DateUtil.class.getName();

    private static final String MOCK_DATE = "25081989";
    public static final String MILITARY_DATE_FORMAT = "ddMMyyyy";
    public static final String MILITARY_TIME_FORMAT = "HHmmss";

    private DateUtil() {
        // Utility class, hide the constructor.
    }

    // Converters
    // ---------------------------------------------------------------------------------

    /**
     * Convert the given date to a Calendar object. The TimeZone will be derived
     * from the local operating system's timezone.
     * 
     * @param date The date to be converted to Calendar.
     * @return The Calendar object set to the given date and using the local
     * timezone.
     */
    public static Calendar toCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * Convert the given date to a Calendar object with the given timezone.
     * 
     * @param date The date to be converted to Calendar.
     * @param timeZone The timezone to be set in the Calendar.
     * @return The Calendar object set to the given date and timezone.
     */
    public static Calendar toCalendar(Date date, TimeZone timeZone) {
        Calendar calendar = toCalendar(date);
        calendar.setTimeZone(timeZone);
        return calendar;
    }

    /**
     * Parse the given date string to date object and return a date instance
     * based on the given date string. This makes use of the
     * {@link DateUtil#determineDateFormat(String)} to determine the
     * SimpleDateFormat pattern to be used for parsing.
     * 
     * @param dateString The date string to be parsed to date object.
     * @return The parsed date object.
     * @throws ParseException If the date format pattern of the given date
     * string is unknown, or if the given date string or its actual date is
     * invalid based on the date format pattern.
     */
    public static Date parse(String dateString) throws ParseException {
        String dateFormat = determineDateFormat(dateString);
        if (dateFormat == null) {
            throw new ParseException("Unknown date format.", 0);
        }
        return parse(dateString, dateFormat);
    }

    /**
     * Validate the actual date of the given date string based on the given date
     * format pattern and return a date instance based on the given date string.
     * 
     * @param dateString The date string.
     * @param dateFormat The date format pattern which should respect the
     * SimpleDateFormat rules.
     * @return The parsed date object.
     * @throws ParseException If the given date string or its actual date is
     * invalid based on the given date format pattern.
     * @see SimpleDateFormat
     */
    public static Date parse(String dateString, String dateFormat)
            throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setLenient(false); // Don't automatically convert
                                            // invalid date.
        return simpleDateFormat.parse(dateString);
    }

    // Validators
    // ---------------------------------------------------------------------------------

    /**
     * Checks whether the actual date of the given date string is valid. This
     * makes use of the {@link DateUtil#determineDateFormat(String)} to
     * determine the SimpleDateFormat pattern to be used for parsing.
     * 
     * @param dateString The date string.
     * @return True if the actual date of the given date string is valid.
     */
    public static boolean isValidDate(String dateString) {
        try {
            parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Checks whether the actual date of the given date string is valid based on
     * the given date format pattern.
     * 
     * @param dateString The date string.
     * @param dateFormat The date format pattern which should respect the
     * SimpleDateFormat rules.
     * @return True if the actual date of the given date string is valid based
     * on the given date format pattern.
     * @see SimpleDateFormat
     */
    public static boolean isValidDate(String dateString, String dateFormat) {
        try {
            parse(dateString, dateFormat);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    //@author A0099151B
    public static boolean isTime(String token) {
        if (token == null) {
            return false;
        }
        return getTimeFormat(token) != null;
    }

    /**
     * Return the time format based on the token. If none is found, return a
     * null.
     * 
     * @param token
     * @return
     */
    public static String getTimeFormat(String token) {

        for (String regex : TIME_FORMAT_REGEX.keySet()) {
            LogHelper.log(CLASS_NAME, Level.INFO, "Matching " + token +
                    " with " +
                    regex);
            if (token.matches(regex)) {
                LogHelper.log(CLASS_NAME, Level.INFO, token + " matched with " +
                        regex);
                return TIME_FORMAT_REGEX.get(regex);
            }
        }
        return null;
    }

    /**
     * Convert to military time
     * 
     * @param token the time
     * @return the time converted to military format. If it's not parseable,
     * return null
     * @throws ParseException
     */
    public static String getMilitaryTime(String token) throws ParseException {
        if (!isTime(token)) {
            return null;
        }

        String timeFormat = getTimeFormat(token);
        String mockFormat = MILITARY_DATE_FORMAT + " " + timeFormat;
        String dateTimeStr = MOCK_DATE + " " + token;
        LogHelper.log(CLASS_NAME, Level.INFO, "Time format: " + timeFormat +
                "\n" +
                "Mock format: " +
                mockFormat +
                "\n" +
                "DateTime: " +
                dateTimeStr);

        SimpleDateFormat mockDateTimeFormat = new SimpleDateFormat(mockFormat);
        Date mockDate = mockDateTimeFormat.parse(dateTimeStr);

        SimpleDateFormat milTimeFormat = new SimpleDateFormat(
                MILITARY_TIME_FORMAT);
        return milTimeFormat.format(mockDate);
    }

    public static boolean isDate(String token) {
        if (token == null) {
            return false;
        }
        return getDateFormat(token) != null;
    }

    public static String getDateFormat(String token) {
        Pattern datePattern;
        Matcher dateMatcher;

        for (String regex : DATE_FORMAT_REGEX.keySet()) {
            datePattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            dateMatcher = datePattern.matcher(token);
            if (dateMatcher.find()) {
                return DATE_FORMAT_REGEX.get(regex);
            }
        }
        return null;
    }

    public static String getMilitaryDate(String token) throws ParseException {

        if (!isDate(token)) {
            return null;
        }

        Date date = parse(token);
        SimpleDateFormat milDateFormat = new SimpleDateFormat(
                MILITARY_DATE_FORMAT);
        return milDateFormat.format(date);
    }

    public static boolean isPartialDate(String token) {
        return getPartialToken(token) != null;
    }

    public static String getPartialToken(String token) {
        Pattern partialDatePattern;
        Matcher partialDateMatcher;

        if (token == null) {
            return null;
        }
        for (String regex : PARTIAL_DATE_FORMAT.keySet()) {

            partialDatePattern = Pattern.compile(regex,
                    Pattern.CASE_INSENSITIVE);
            partialDateMatcher = partialDatePattern.matcher(token);

            if (partialDateMatcher.find()) {
                return PARTIAL_DATE_FORMAT.get(regex);
            }
        }

        return null;
    }

    public static String getDateFromPartial(String token) throws ParseException {

        if (token == null || !isPartialDate(token)) {
            return null;
        }
        String partialDate = token;
        String partialToken = getPartialToken(partialDate);
        int year = getToday().getYear();
        Date convertedDate = parse(partialDate + partialToken + year);
        SimpleDateFormat milDateFormat = new SimpleDateFormat(
                MILITARY_DATE_FORMAT);
        return milDateFormat.format(convertedDate);
    }

    public static DateTime getToday() {
        return DateTime.today(TimeZone.getDefault());
    }
    
    //@author BalusC
    // Checkers
    // -----------------------------------------------------------------------------------
    /**
     * Determine SimpleDateFormat pattern matching with the given date string.
     * Returns null if format is unknown. You can simply extend DateUtil with
     * more formats if needed.
     * 
     * @param dateString The date string to determine the SimpleDateFormat
     * pattern for.
     * @return The matching SimpleDateFormat pattern, or null if format is
     * unknown.
     * @see SimpleDateFormat
     */
    public static String determineDateFormat(String dateString) {
        for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
            if (dateString.toLowerCase().matches(regexp)) {
                return DATE_FORMAT_REGEXPS.get(regexp);
            }
        }
        return null; // Unknown format.
    }
    
    //@author A0099324X
    // Comparator
    /**
     * Determine whether two Calendar instances are on the same date (time can
     * be different)
     * 
     * @param Calendar cal1, Calendar cal2
     * @return boolean _isSameDate
     */
    public static boolean isSameDateCalendar(Calendar cal1, Calendar cal2) {
        return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) && (cal1
                .get(Calendar.MONTH) == cal2.get(Calendar.MONTH)) &&
                (cal1.get(Calendar.DAY_OF_MONTH) == cal2
                        .get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Display date and time from a date object
     * 
     * @param Date date
     * @return String _date_time in user-friendly format
     */
    public static String displayDateTime(Calendar date) {
        return displayDateOnly(date).concat(" ".concat(displayTimeOnly(date)));
    }

    /**
     * Display date only from a date object
     * 
     * @param Date date
     * @return String _date in user-friendly format
     */
    public static String displayDateOnly(Calendar date) {
        Calendar todayCalendar = Calendar.getInstance(TimeZone.getDefault());
        DateTime todayDateTime = DateTime.today(TimeZone.getDefault());
        DateTime dateToDisplay = new DateTime(date.get(Calendar.YEAR) + "-" +
                String.format("%02d", (date.get(Calendar.MONTH) + 1)) +
                "-" +
                String.format("%02d", date.get(Calendar.DAY_OF_MONTH)) +
                " " +
                String.format("%02d", date.get(Calendar.HOUR_OF_DAY)) +
                ":" +
                String.format("%02d", date.get(Calendar.MINUTE)) +
                ":" +
                String.format("%02d", date.get(Calendar.SECOND)));
        DateTime thisSun = getThisSunday(todayCalendar);
        DateTime nextSun = getNextSunday(todayCalendar);
        DateTime lastSun = getLastSunday(todayCalendar);
        DateTime sunBeforeLast = getSundayBeforeLast(todayCalendar);
        if (nextSun.numDaysFrom(dateToDisplay) >= 1 || dateToDisplay
                .numDaysFrom(sunBeforeLast) >= 0) {
            return dateToDisplay.format("WWWW, DD MMM YYYY", Locale.US);
        } else if (thisSun.numDaysFrom(dateToDisplay) >= 1 && dateToDisplay
                .numDaysFrom(nextSun) >= 0) {
            return "Next ".concat(dateToDisplay.format("WWWW", Locale.US));
        } else if (lastSun.numDaysFrom(dateToDisplay) >= 1 && dateToDisplay
                .numDaysFrom(thisSun) >= 0) {
            if (todayDateTime.numDaysFrom(dateToDisplay) == 0) {
                return "Today";
            } else if (todayDateTime.numDaysFrom(dateToDisplay) == 1) {
                return "Tomorrow";
            } else {
                return dateToDisplay.format("WWWW", Locale.US);
            }
        } else {
            return "Last ".concat(dateToDisplay.format("WWWW", Locale.US));
        }
    }

    /**
     * Display time only from a date object
     * 
     * @param Date date
     * @return String _time in user-friendly format
     */
    public static String displayTimeOnly(Calendar date) {
        DateTime dateToDisplay = new DateTime(date.get(Calendar.YEAR) + "-" +
                String.format("%02d", (date.get(Calendar.MONTH) + 1)) +
                "-" +
                String.format("%02d", date.get(Calendar.DAY_OF_MONTH)) +
                " " +
                String.format("%02d", date.get(Calendar.HOUR_OF_DAY)) +
                ":" +
                String.format("%02d", date.get(Calendar.MINUTE)) +
                ":" +
                String.format("%02d", date.get(Calendar.SECOND)));
        return dateToDisplay.format("h12:mm a", Locale.US).toLowerCase();
    }

    /**
     * Get the calendar of current week's Sunday
     * 
     * @param Calendar currentDate
     * @return Calendar _thisSunday
     */
    private static DateTime getThisSunday(Calendar currentDate) {
        Calendar thisSun = Calendar.getInstance();
        thisSun.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        DateTime thisSunday = new DateTime(thisSun.get(Calendar.YEAR) + "-" +
                String.format("%02d", (thisSun.get(Calendar.MONTH) + 1)) +
                "-" +
                String.format("%02d", thisSun.get(Calendar.DAY_OF_MONTH)) +
                " " +
                String.format("%02d", thisSun.get(Calendar.HOUR_OF_DAY)) +
                ":" +
                String.format("%02d", thisSun.get(Calendar.MINUTE)) +
                ":" +
                String.format("%02d", thisSun.get(Calendar.SECOND)));
        thisSunday = thisSunday.plusDays(7);
        return thisSunday;
    }

    /**
     * Get the calendar of next week's Sunday
     * 
     * @param Calendar currentDate
     * @return Calendar _nextSunday
     */
    private static DateTime getNextSunday(Calendar currentDate) {
        DateTime thisSun = getThisSunday(currentDate);
        DateTime nextSun = thisSun.plusDays(7);
        return nextSun;
    }

    /**
     * Get the calendar of last week's Sunday
     * 
     * @param Calendar currentDate
     * @return Calendar _lastSunday
     */
    private static DateTime getLastSunday(Calendar currentDate) {
        DateTime thisSun = getThisSunday(currentDate);
        DateTime lastSun = thisSun.minusDays(7);
        return lastSun;
    }

    /**
     * Get the calendar of week before last week's Sunday
     * 
     * @param Calendar currentDate
     * @return Calendar _sundayBeforeLast
     */
    private static DateTime getSundayBeforeLast(Calendar currentDate) {
        DateTime thisSun = getThisSunday(currentDate);
        DateTime sunBeforeLast = thisSun.minusDays(14);
        return sunBeforeLast;
    }

}
