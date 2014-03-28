package sg.edu.nus.cs2103t.mina.utils;

import hirondelle.date4j.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Useful Date utilities.
 *
 * @author BalusC
 * @see CalendarUtil
 * @link http://balusc.blogspot.com/2007/09/dateutil.html
 */
public final class DateUtil {

    // Init ---------------------------------------------------------------------------------------

    private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {/**
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
        put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy hh12:mm");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd hh12:mm");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "dd/MM/yyyy hh12:mm");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd hh12:mm");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy hh12:mm");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy hh12:mm");
        put("^\\d{14}$", "ddMMyyyyHHmmss");
        put("^\\d{8}\\s\\d{6}$", "ddMMyyyy HHmmss");
        put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy hh12:mm:ss");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd hh12:mm:ss");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd/MM/yyyy hh12:mm:ss");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd hh12:mm:ss");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy hh12:mm:ss");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy hh12:mm:ss");
        
        put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}$", "dd.MM.yyyy");
        put("^\\d{4}\\.\\d{1,2}\\.\\d{1,2}$", "yyyy.MM.dd");
        put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}\\s\\d{1,2}:\\d{2}$", "dd.MM.yyyy hh12:mm");
        put("^\\d{4}\\.\\d{1,2}\\.\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy.MM.dd hh12:mm");
        put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd.MM.yyyy hh12:mm:ss");
        
        put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}.\\d{2}.\\d{2}$", "dd-MM-yyyy HH.mm.ss");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}.\\d{2}.\\d{2}$", "yyyy-MM-dd HH.mm.ss");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}.\\d{2}.\\d{2}$", "dd/MM/yyyy HH.mm.ss");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}.\\d{2}.\\d{2}$", "yyyy/MM/dd HH.mm.ss");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}.\\d{2}.\\d{2}$", "dd MMM yyyy HH.mm.ss");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}.\\d{2}.\\d{2}$", "dd MMMM yyyy HH.mm.ss");
        put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}\\s\\d{1,2}.\\d{2}$", "dd.MM.yyyy HH.mm");
        put("^\\d{4}\\.\\d{1,2}\\.\\d{1,2}\\s\\d{1,2}.\\d{2}$", "yyyy.MM.dd HH.mm");
        put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}\\s\\d{1,2}.\\d{2}.\\d{2}$", "dd.MM.yyyy HH.mm.ss");
        put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}.\\d{2}$", "dd-MM-yyyy HH.mm");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}.\\d{2}$", "yyyy-MM-dd HH.mm");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}.\\d{2}$", "dd/MM/yyyy HH.mm");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}.\\d{2}$", "yyyy/MM/dd HH.mm");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}.\\d{2}$", "dd MMM yyyy HH.mm");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}.\\d{2}$", "dd MMMM yyyy HH.mm");
        
	}};

    private DateUtil() {
        // Utility class, hide the constructor.
    }

    // Converters ---------------------------------------------------------------------------------

    /**
     * Convert the given date to a Calendar object. The TimeZone will be derived from the local
     * operating system's timezone.
     * @param date The date to be converted to Calendar.
     * @return The Calendar object set to the given date and using the local timezone.
     */
    public static Calendar toCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * Convert the given date to a Calendar object with the given timezone.
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
     * Parse the given date string to date object and return a date instance based on the given
     * date string. This makes use of the {@link DateUtil#determineDateFormat(String)} to determine
     * the SimpleDateFormat pattern to be used for parsing.
     * @param dateString The date string to be parsed to date object.
     * @return The parsed date object.
     * @throws ParseException If the date format pattern of the given date string is unknown, or if
     * the given date string or its actual date is invalid based on the date format pattern.
     */
    public static Date parse(String dateString) throws ParseException {
        String dateFormat = determineDateFormat(dateString);
        if (dateFormat == null) {
            throw new ParseException("Unknown date format.", 0);
        }
        return parse(dateString, dateFormat);
    }

    /**
     * Validate the actual date of the given date string based on the given date format pattern and
     * return a date instance based on the given date string.
     * @param dateString The date string.
     * @param dateFormat The date format pattern which should respect the SimpleDateFormat rules.
     * @return The parsed date object.
     * @throws ParseException If the given date string or its actual date is invalid based on the
     * given date format pattern.
     * @see SimpleDateFormat
     */
    public static Date parse(String dateString, String dateFormat) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setLenient(false); // Don't automatically convert invalid date.
        return simpleDateFormat.parse(dateString);
    }

    // Validators ---------------------------------------------------------------------------------

    /**
     * Checks whether the actual date of the given date string is valid. This makes use of the
     * {@link DateUtil#determineDateFormat(String)} to determine the SimpleDateFormat pattern to be
     * used for parsing.
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
     * Checks whether the actual date of the given date string is valid based on the given date
     * format pattern.
     * @param dateString The date string.
     * @param dateFormat The date format pattern which should respect the SimpleDateFormat rules.
     * @return True if the actual date of the given date string is valid based on the given date
     * format pattern.
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

    // Checkers -----------------------------------------------------------------------------------

    /**
     * Determine SimpleDateFormat pattern matching with the given date string. Returns null if
     * format is unknown. You can simply extend DateUtil with more formats if needed.
     * @param dateString The date string to determine the SimpleDateFormat pattern for.
     * @return The matching SimpleDateFormat pattern, or null if format is unknown.
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
    
    // Comparator
    
    /**
     * Determine whether two Calendar instances are on the same date (time can be different)
     * @param Calendar cal1, Calendar cal2
     * @return boolean _isSameDate
     */
    public static boolean isSameDateCalendar(Calendar cal1, Calendar cal2){
    	return (cal1.get(Calendar.YEAR)==cal2.get(Calendar.YEAR))
				&&(cal1.get(Calendar.MONTH)==cal2.get(Calendar.MONTH))
				&&(cal1.get(Calendar.DAY_OF_MONTH)==cal2.get(Calendar.DAY_OF_MONTH));
    }
    
    /**
     * Display date and time from a date object
     * @param Date date
     * @return String _date_time in user-friendly format
     */
    public static String displayDateTime(Calendar date){
    	return displayDateOnly(date).concat(" ".concat(displayTimeOnly(date)));
    }
    
    /**
     * Display date only from a date object
     * @param Date date
     * @return String _date in user-friendly format
     */
    public static String displayDateOnly(Calendar date){
    	Calendar todayCalendar = Calendar.getInstance(TimeZone.getDefault());
    	DateTime todayDateTime = DateTime.today(TimeZone.getDefault());
    	DateTime dateToDisplay = new DateTime(date.get(Calendar.YEAR)+"-"+
    			String.format("%02d" ,(date.get(Calendar.MONTH)+1))+"-"+String.format("%02d" ,date.get(Calendar.DAY_OF_MONTH))+" "+
    			String.format("%02d" ,date.get(Calendar.HOUR_OF_DAY))+":"+String.format("%02d" ,date.get(Calendar.MINUTE))+":"+
    			String.format("%02d" ,date.get(Calendar.SECOND)));
    	DateTime thisSun = getThisSunday(todayCalendar);
    	DateTime nextSun = getNextSunday(todayCalendar);
    	DateTime lastSun = getLastSunday(todayCalendar);
    	DateTime sunBeforeLast = getSundayBeforeLast(todayCalendar);
    	if (nextSun.numDaysFrom(dateToDisplay)>=1||dateToDisplay.numDaysFrom(sunBeforeLast)>=0){
    		return dateToDisplay.format("WWWW, DD MMM YYYY", Locale.US);
    	} else if (thisSun.numDaysFrom(dateToDisplay)>=1&&dateToDisplay.numDaysFrom(nextSun)>=0){
    		return "Next ".concat(dateToDisplay.format("WWWW", Locale.US));
    	} else if (lastSun.numDaysFrom(dateToDisplay)>=1&&dateToDisplay.numDaysFrom(thisSun)>=0){
    		if (todayDateTime.numDaysFrom(dateToDisplay)==0){
    			return "Today";
    		} else if (todayDateTime.numDaysFrom(dateToDisplay)==1){
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
     * @param Date date
     * @return String _time in user-friendly format
     */
    public static String displayTimeOnly(Calendar date){
    	DateTime dateToDisplay = new DateTime(date.get(Calendar.YEAR)+"-"+
    			String.format("%02d" ,(date.get(Calendar.MONTH)+1))+"-"+String.format("%02d" ,date.get(Calendar.DAY_OF_MONTH))+" "+
    			String.format("%02d" ,date.get(Calendar.HOUR_OF_DAY))+":"+String.format("%02d" ,date.get(Calendar.MINUTE))+":"+
    			String.format("%02d" ,date.get(Calendar.SECOND)));
    	return dateToDisplay.format("h12:mm a", Locale.US).toLowerCase();
    }
    
    /**
     * Get the calendar of current week's Sunday
     * @param Calendar currentDate
     * @return Calendar _thisSunday
     */
    private static DateTime getThisSunday(Calendar currentDate){
    	Calendar thisSun = Calendar.getInstance();
    	thisSun.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
    	DateTime thisSunday = new DateTime(thisSun.get(Calendar.YEAR)+"-"+
    			String.format("%02d" ,(thisSun.get(Calendar.MONTH)+1))+"-"+String.format("%02d" ,thisSun.get(Calendar.DAY_OF_MONTH))+" "+
    			String.format("%02d" ,thisSun.get(Calendar.HOUR_OF_DAY))+":"+String.format("%02d" ,thisSun.get(Calendar.MINUTE))+":"+
    			String.format("%02d" ,thisSun.get(Calendar.SECOND)));
    	thisSunday = thisSunday.plusDays(7);
    	return thisSunday;
    }
    
    
    /**
     * Get the calendar of next week's Sunday
     * @param Calendar currentDate
     * @return Calendar _nextSunday
     */
    private static DateTime getNextSunday(Calendar currentDate){
    	DateTime thisSun = getThisSunday(currentDate);
    	DateTime nextSun = thisSun.plusDays(7);
    	return nextSun;
    }
    
    /**
     * Get the calendar of last week's Sunday
     * @param Calendar currentDate
     * @return Calendar _lastSunday
     */
    private static DateTime getLastSunday(Calendar currentDate){
    	DateTime thisSun = getThisSunday(currentDate);
    	DateTime lastSun = thisSun.minusDays(7);
    	return lastSun;
    }
    
    /**
     * Get the calendar of week before last week's Sunday
     * @param Calendar currentDate
     * @return Calendar _sundayBeforeLast
     */
    private static DateTime getSundayBeforeLast(Calendar currentDate){
    	DateTime thisSun = getThisSunday(currentDate);
    	DateTime sunBeforeLast = thisSun.minusDays(14);
    	return sunBeforeLast;
    }
}
