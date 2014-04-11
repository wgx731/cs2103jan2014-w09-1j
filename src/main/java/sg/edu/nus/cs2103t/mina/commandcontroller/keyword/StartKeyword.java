package sg.edu.nus.cs2103t.mina.commandcontroller.keyword;

import hirondelle.date4j.DateTime;
import hirondelle.date4j.DateTime.DayOverflow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;

import sg.edu.nus.cs2103t.mina.utils.DateUtil;
import sg.edu.nus.cs2103t.mina.utils.LogHelper;

// @author A0099151B
public class StartKeyword extends Keyword {

    private LinkedHashMap<String, DateTime> _startValues;

    private static final int ONE_DAY = 1;
    private static final Integer ONE_YEAR = 1;
    private static final Integer ONE_MONTH = 1;
    private static final Integer ONE_WEEK = 7;

    private static final Integer NO_YEAR = 0;
    private static final Integer NO_MONTH = 0;
    private static final Integer NO_DAY = 0;
    private static final Integer NO_HOUR = 0;
    private static final Integer NO_MIN = 0;
    private static final Integer NO_SEC = 0;
    private static final Integer NO_NANO_SEC = 0;

    private static final int MONDAY = 0;
    private static final int TUESDAY = 1;
    private static final int WEDNESDAY = 2;
    private static final int THURSDAY = 3;
    private static final int FRIDAY = 4;
    private static final int SATURDAY = 5;
    private static final int SUNDAY = 6;

    static {
        StartKeyword startPrototype = new StartKeyword();
        KeywordFactory.addAliasEntry(SimpleKeyword.START.getFormattedKeyword(),
                startPrototype);
        KeywordFactory.addAliasEntry("-starting", startPrototype);
        KeywordFactory.addAliasEntry("-from", startPrototype);
    }

    protected static final String[] WEEK = { "mon", "tue", "wed", "thur",
            "fri", "sat", "sun" };

    protected String _defaultTime;

    private static final String CLASS_NAME = StartKeyword.class.getName();

    public StartKeyword(StandardKeyword type) {
        super(type);
    }

    public StartKeyword() {
        this(SimpleKeyword.START);
        _defaultTime = "000000";
    }

    @Override
    protected Keyword createKeyword() {
        return new StartKeyword();
    }

    @Override
    protected void initValues() {

        _startValues = new LinkedHashMap<String, DateTime>();
        DateTime today = DateTime.today(TimeZone.getDefault());

        // date-word
        _startValues.put("^today", today);
        _startValues.put("^tomorrow", today.plusDays(ONE_DAY));
        _startValues.put("^tmr", today.plusDays(ONE_DAY));
        _startValues.put("^tommorrow", today.plusDays(ONE_DAY));
        _startValues.put("^yesterday", today.minusDays(ONE_DAY));

        // this or next day
        _startValues.put("^(this\\s)?(monday|mon)", getWeekday(today, MONDAY));
        _startValues.put("^(this\\s)?(tuesday|tues|tue)",
                getWeekday(today, TUESDAY));
        _startValues.put("^(this\\s)?(wednesday|wed)",
                getWeekday(today, WEDNESDAY));
        _startValues.put("^(this\\s)?(thursday|thurs|thur)",
                getWeekday(today, THURSDAY));
        _startValues.put("^(this\\s)?(friday|fri)", getWeekday(today, FRIDAY));
        _startValues.put("^(this\\s)?(saturday|sat)",
                getWeekday(today, SATURDAY));
        _startValues.put("^(this\\s)?(sunday|sun)", getWeekday(today, SUNDAY));

        _startValues
                .put("^next\\s\\d*?\\s(days|weeks|months|years|day|week|month|year)",
                        null);

        _startValues.put("^next\\syear", today.plus(ONE_YEAR, NO_MONTH, NO_DAY,
                NO_HOUR, NO_MIN, NO_SEC, NO_NANO_SEC, DayOverflow.Spillover));
        _startValues.put("^next\\smonth", today.plus(NO_YEAR, ONE_MONTH,
                NO_DAY, NO_HOUR, NO_MIN, NO_SEC, NO_NANO_SEC,
                DayOverflow.Spillover));
        _startValues.put("^next\\sweek", today.plus(NO_YEAR, NO_MONTH,
                ONE_WEEK, NO_HOUR, NO_MIN, NO_SEC, NO_NANO_SEC,
                DayOverflow.Spillover));
        _startValues.put("^next\\sday", today.plus(NO_YEAR, NO_MONTH, ONE_DAY,
                NO_HOUR, NO_MIN, NO_SEC, NO_NANO_SEC, DayOverflow.Spillover));

        _startValues.put("^next\\s(monday|mon)", getNextWeekday(today, MONDAY));
        _startValues.put("^next\\s(tuesday|tue)",
                getNextWeekday(today, TUESDAY));
        _startValues.put("^next\\s(wednesday|wed)",
                getNextWeekday(today, WEDNESDAY));
        _startValues.put("^next\\s(thursday|thurs)",
                getNextWeekday(today, THURSDAY));
        _startValues.put("^next\\s(friday|fri)", getNextWeekday(today, FRIDAY));
        _startValues.put("^next\\s(saturday|sat)",
                getNextWeekday(today, SATURDAY));
        _startValues.put("^next\\s(sunday|sun)", getNextWeekday(today, SUNDAY));

    }

    private DateTime getNextWeekday(DateTime today, Integer nextWeekday) {
        today = today.plusDays(7);
        return getWeekday(today, nextWeekday);
    }

    private DateTime getWeekday(DateTime today, Integer nextWeekday) {

        int thisWeekday = today.getWeekDay() - 2;
        if (thisWeekday < 0) {
            thisWeekday = SUNDAY;
        }
        int daysInterval = nextWeekday - thisWeekday;
        return today.plusDays(daysInterval);
    }

    @Override
    public ArrayList<String> processKeyword(ArrayList<String> tokens,
            int currIndex, Argument argument) throws ParseException {

        String rawDate;
        String rawDateTime;
        String lookahead;
        String rawTime;

        // setup
        lookahead = getLookAhead(tokens, currIndex, LOOK_AHEAD_LIMIT);
        LogHelper.log(CLASS_NAME, Level.INFO, "Looking ahead: " + lookahead);

        if (hasJoinedDateTime(lookahead)) {
            String firstToken = getFirstToken(lookahead);
            String dateTime = getJointDateTime(firstToken);
            updateArgument(argument, dateTime);
            return nullifyTokens(tokens, currIndex, 1);
        } else {
            LogHelper.log(CLASS_NAME, Level.INFO,
                    lookahead + " is not joint datetime");
        }

        // time is the easiest to parse, so we shall try that
        rawTime = parseTimeFirst(lookahead);

        // time is not at first token, parse for the date
        if (rawTime == null) {
            LogHelper.log(CLASS_NAME, Level.INFO,
                    "No time at first token, Getting date first");
            rawDate = parseDateFirst(lookahead);
            rawTime = parseTimeFromRest(lookahead, rawDate);
            rawDateTime = getRawDateTime(rawDate, rawTime);
        } else {
            // parse the date from the rest
            LogHelper.log(CLASS_NAME, Level.INFO,
                    "Time found at first token, Getting date first");
            rawDate = parseDateFromRest(lookahead, rawTime);
            rawDateTime = getRawDateTime(rawTime, rawDate);
        }

        checkValidDateTime(rawDate, rawTime);

        int argLimit = getArgLimit(rawDateTime);
        String time = getMilitaryTime(rawTime);
        String date = getMilitaryDate(rawDate);
        updateArgument(argument, date, time);
        return nullifyTokens(tokens, currIndex, argLimit);
    }

    private String getJointDateTime(String dateTime) throws ParseException {
        dateTime = dateTime.trim();
        Date jointDateTime = DateUtil.parse(dateTime);
        SimpleDateFormat milDateTime = new SimpleDateFormat(
                DateUtil.MILITARY_DATE_FORMAT + DateUtil.MILITARY_TIME_FORMAT);
        return milDateTime.format(jointDateTime);
    }

    private boolean hasJoinedDateTime(String lookahead) {
        String firstToken = getFirstToken(lookahead);
        try {
            DateUtil.parse(firstToken, "ddMMyyyyHHmm");
            return true;
        } catch (ParseException e) {
            // FALLTHROUGH
        }
        try {
            DateUtil.parse(firstToken, "ddMMyyyyHHmmss");
            return true;
        } catch (ParseException e) {
            // FALLTHROUGH
        }
        return false;
    }

    private void checkValidDateTime(String rawDate, String rawTime)
            throws ParseException {
        if (rawDate == null && rawTime == null) {
            throw new ParseException("Please give a valid date and time", 0);
        }
    }

    protected ArrayList<String> nullifyTokens(ArrayList<String> tokens,
            int currIndex, int argLimit) {
        argLimit = currIndex + 1 + argLimit;
        for (int i = currIndex + 1; i < argLimit; i++) {
            tokens.set(i, null);
        }
        return tokens;
    }

    protected void updateArgument(Argument argument, String date, String time) {

        assert (argument != null);

        if (date == null) {
            DateTime today = DateTime.today(TimeZone.getDefault());
            date = today.format(DateUtil.MILITARY_DATE_FORMAT.toUpperCase());
        }
        if (time == null) {
            LogHelper.log(CLASS_NAME, Level.INFO, _defaultTime);
            time = _defaultTime;
        }

        String dateTime = date + time;
        updateArgument(argument, dateTime);
    }

    private void updateArgument(Argument argument, String dateTime) {
        argument.setKeywordValue(_type, dateTime.trim());
    }

    protected String getMilitaryDate(String rawDate) throws ParseException {
        LogHelper.log(CLASS_NAME, Level.INFO,
                "Getting military date: " + rawDate);

        if (rawDate == null) {
            return null;
        }

        if (hasDatePhrase(rawDate)) {
            rawDate = convertDatePharseToDate(rawDate);
        } else if (DateUtil.isPartialDate(rawDate)) {
            rawDate = DateUtil.getDateFromPartial(rawDate);
        }

        return DateUtil.getMilitaryDate(rawDate);
    }

    protected String convertDatePharseToDate(String rawDate) {

        Pattern phrasePattern;
        Matcher phraseMatch;

        DateTime converted = null;
        String properPhrase = null;
        boolean isParseable = false;

        for (String regex : _startValues.keySet()) {
            phrasePattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            phraseMatch = phrasePattern.matcher(rawDate);
            if (phraseMatch.find()) {
                isParseable = true;
                properPhrase = phraseMatch.group();
                converted = _startValues.get(regex);
                break;
            }
        }

        if (isParseable && converted == null) {
            return processDateWithValue(properPhrase);
        } else if (converted != null) {
            return converted
                    .format(DateUtil.MILITARY_DATE_FORMAT.toUpperCase());
        } else {
            return null;
        }
    }

    private String processDateWithValue(String properPhrase) {

        String valueRegex = "\\s\\d*\\s(day|days|month|months|year|years|week|weeks)";

        Pattern valuePattern = Pattern.compile(valueRegex,
                Pattern.CASE_INSENSITIVE);
        Matcher valueMatcher = valuePattern.matcher(properPhrase);

        String dateValue = null;

        if (valueMatcher.find()) {
            dateValue = valueMatcher.group();
        } else {
            return null;
        }
        LogHelper.log(CLASS_NAME, Level.INFO,
                "From date with value: " + dateValue);

        String[] dateValueTokens = dateValue.trim().split(" ");
        int advancedValue = Integer.parseInt(dateValueTokens[0]);
        String dateType = dateValueTokens[1].toLowerCase();

        DateTime advancedDate = DateTime.today(TimeZone.getDefault());

        if (dateType.startsWith("day")) {
            advancedDate = advancedDate.plus(NO_YEAR, NO_MONTH, advancedValue,
                    NO_HOUR, NO_MIN, NO_SEC, NO_NANO_SEC,
                    DateTime.DayOverflow.Spillover);
        } else if (dateType.startsWith("month")) {
            advancedDate = advancedDate.plus(NO_YEAR, advancedValue, NO_DAY,
                    NO_HOUR, NO_MIN, NO_SEC, NO_NANO_SEC,
                    DateTime.DayOverflow.Spillover);
        } else if (dateType.startsWith("week")) {
            advancedDate = advancedDate.plus(NO_YEAR, NO_MONTH,
                    advancedValue * 7, NO_HOUR, NO_MIN, NO_SEC, NO_NANO_SEC,
                    DateTime.DayOverflow.Spillover);
        } else if (dateType.startsWith("year")) {
            advancedDate = advancedDate.plus(advancedValue, NO_MONTH, NO_DAY,
                    NO_HOUR, NO_MIN, NO_SEC, NO_NANO_SEC,
                    DateTime.DayOverflow.Spillover);
        } else {
            return null;
        }

        return advancedDate.format(DateUtil.MILITARY_DATE_FORMAT.toUpperCase());
    }

    private String getMilitaryTime(String rawTime) throws ParseException {
        LogHelper.log(CLASS_NAME, Level.INFO,
                "Getting military time: " + rawTime);

        if (rawTime == null) {
            return null;
        }
        return DateUtil.getMilitaryTime(rawTime);
    }

    protected int getArgLimit(String rawDateTime) {
        String[] rawDateTimeTokens = rawDateTime.split(" ");
        return rawDateTimeTokens.length;
    }

    private String parseDateFromRest(String lookahead, String rawTime)
            throws ParseException {
        String lookaheadRest = getLookaheadRest(lookahead, rawTime);
        return parseDateFirst(lookaheadRest);
    }

    private String parseTimeFromRest(String lookahead, String rawDate)
            throws ParseException {
        LogHelper.log(CLASS_NAME, Level.INFO, "Lookahead: " + lookahead +
                " rawDate: " +
                rawDate);
        String lookaheadRest = getLookaheadRest(lookahead, rawDate);
        LogHelper.log(CLASS_NAME, Level.INFO,
                "Trimmed Lookahead: " + lookaheadRest);
        return parseTimeFirst(lookaheadRest);
    }

    private String getLookaheadRest(String lookahead, String rawDateOrTime) {

        if (rawDateOrTime == null) {
            return lookahead;
        }

        int rawLength = rawDateOrTime.length();
        return lookahead.substring(rawLength).trim();
    }

    protected String parseDateFirst(String lookahead) throws ParseException {
        LogHelper.log(CLASS_NAME, Level.INFO,
                "Getting date first: " + lookahead + "|");
        String dateCandidate = getFirstToken(lookahead);
        LogHelper.log(CLASS_NAME, Level.INFO, "Candidate: " + dateCandidate +
                "|");

        if (DateUtil.isDate(dateCandidate)) {
            return dateCandidate;
        } else if (hasDatePhrase(lookahead)) {
            return getDatePhrase(lookahead);
        } else if (DateUtil.isPartialDate(dateCandidate)) {
            return dateCandidate;
        }

        return null;
    }

    protected boolean hasDatePhrase(String lookahead) {
        return getDatePhrase(lookahead) != null;
    }

    protected String getDatePhrase(String lookahead) {
        Pattern phraseRegex;
        Matcher phraseMatcher;

        for (String regex : _startValues.keySet()) {
            phraseRegex = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            phraseMatcher = phraseRegex.matcher(lookahead);
            if (phraseMatcher.find()) {
                return phraseMatcher.group();
            }
        }

        return null;
    }

    private String parseTimeFirst(String lookahead) throws ParseException {
        LogHelper.log(CLASS_NAME, Level.INFO,
                "parseTimeFirst lookahead: " + lookahead + "|");
        String firstToken = getFirstToken(lookahead);
        LogHelper.log(CLASS_NAME, Level.INFO,
                "Getting time first token: " + firstToken + "|");

        if (DateUtil.isTime(firstToken)) {
            return firstToken;
        } else {
            return null;
        }
    }

    private String getRawDateTime(String firstSeg, String secSeg) {
        if (firstSeg == null) {
            firstSeg = "";
        }
        if (secSeg == null) {
            secSeg = "";
        }
        String dateTimePhrase = firstSeg + " " + secSeg;
        return dateTimePhrase.trim();
    }

    private String getFirstToken(String lookahead) {
        int firstDelimit = lookahead.trim().indexOf(" ");
        if (firstDelimit == -1) {
            return lookahead;
        }
        String firstToken = lookahead.substring(0, firstDelimit);
        return firstToken;
    }

    @Override
    public Map<String, String> getKeywordValues() {
        LinkedHashMap<String, String> dateStrings = new LinkedHashMap<String, String>();

        for (String key : _startValues.keySet()) {
            String value = _startValues.get(key).format("DDMMYYYYHHMMSS");
            dateStrings.put(key, value);
        }

        return dateStrings;
    }
}
