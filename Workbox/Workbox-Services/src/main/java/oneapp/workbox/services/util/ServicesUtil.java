package oneapp.workbox.services.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Contains utility functions to be used by Services
 * 
 * @version R1
 */
public class ServicesUtil {

	public static final String NOT_APPLICABLE = "N/A";
	public static final String SPECIAL_CHAR = "∅";
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	public static boolean isEmpty(Object[] objs) {
		if (objs == null || objs.length == 0) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(Object o) {
		if (o == null) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(Collection<?> o) {
		if (o == null || o.isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(String str) {
		if (str == null || str.trim().isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(StringBuffer sb) {
		if (sb == null || sb.length() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(StringBuilder sb) {
		if (sb == null || sb.length() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(Element nd) {
		if (nd == null) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(NamedNodeMap nd) {
		if (nd == null || nd.getLength() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(Node nd) {
		if (nd == null) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(NodeList nd) {
		if (nd == null || nd.getLength() == 0) {
			return true;
		}
		return false;
	}

	public static String getCSV(Object... objs) {
		if (!isEmpty(objs)) {
			if (objs[0] instanceof Collection<?>) {
				return getCSVArr(((Collection<?>) objs[0]).toArray());
			} else {
				return getCSVArr(objs);
			}

		} else {
			return "";
		}
	}

	private static String getCSVArr(Object[] objs) {
		if (!isEmpty(objs)) {
			StringBuffer sb = new StringBuffer();
			for (Object obj : objs) {
				sb.append(',');
				if (obj instanceof Field) {
					sb.append(extractFieldName((Field) obj));
				} else {
					sb.append(extractStr(obj));
				}
			}
			sb.deleteCharAt(0);
			return sb.toString();
		} else {
			return "";
		}
	}

	public static String extractStr(Object o) {
		return o == null ? "" : o.toString();
	}

	public static String extractFieldName(Field o) {
		return o == null ? "" : o.getName();
	}

	public static String buildNoRecordMessage(String queryName, Object... parameters) {
		StringBuffer sb = new StringBuffer("No Record found for query: ");
		sb.append(queryName);
		if (!isEmpty(parameters)) {
			sb.append(" for params:");
			sb.append(getCSV(parameters));
		}
		return sb.toString();
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static Date resultAsDate(Object o) {
		// System.err.println("[PMC][WorkBoxFacade][resultAsDate][o]" + o);
		String template = "";
		if (o instanceof Object[]) {
			template = Arrays.asList((Object[]) o).toString();
		} else {
			template = String.valueOf(o);
		}
		Date date = null;
		try {
			if (!isEmpty(template)) {
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
				date = formatter.parse(template);
			}
			// System.err.println("[PMC][WorkBoxFacade][resultAsDate][o]" + o +
			// "[template]" + template + "[date]" + date + "yyyy-MM-dd
			// hh:mm:ss.SSS");
		} catch (ParseException e) {
			System.err.println("resultAsString ParseException" + e.getMessage());
		}
		return date;
	}

	/*
	 * public static Date getFirstDateOfCurrentMonth() { Calendar cal =
	 * Calendar.getInstance(); cal.set(Calendar.DAY_OF_MONTH,
	 * cal.getActualMinimum(Calendar.DAY_OF_MONTH)); return cal.getTime(); }
	 */

	public static Date setInitialTime(Date currentDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Date setEndTime(Date currentDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	/*
	 * public static List<Date> getWeekDates() { List<Date> dateList = new
	 * ArrayList<Date>(); Calendar cal = Calendar.getInstance(); int num =
	 * cal.get(Calendar.DAY_OF_WEEK); for (int i = Calendar.SUNDAY; i <= num;
	 * i++) { cal.set(Calendar.DAY_OF_WEEK, i); dateList.add(cal.getTime()); }
	 * return dateList; }
	 */

	/*
	 * public static List<String> getWeekDatesInString() { DateFormat formatter
	 * = new SimpleDateFormat("dd-MM-yy"); List<String> dateList = new
	 * ArrayList<String>(); Calendar cal = Calendar.getInstance(); int num =
	 * cal.get(Calendar.DAY_OF_WEEK); for (int i = Calendar.SUNDAY; i <= num;
	 * i++) { cal.set(Calendar.DAY_OF_WEEK, i);
	 * dateList.add(formatter.format(cal.getTime())); } return dateList;
	 * 
	 * }
	 */

	public static List<String> getWeekDateRangeInString(int noOfDays) {
		DateFormat formatter = new SimpleDateFormat(PMCConstant.PMC_DATE_FORMATE);
		List<String> dateList = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -(noOfDays - 1));
		dateList.add(formatter.format(cal.getTime()));
		for (int i = 0; i < (noOfDays - 1); i++) {
			cal.add(Calendar.DAY_OF_YEAR, 1);
			dateList.add(formatter.format(cal.getTime()));
		}
		return dateList;
	}

	public static List<Date> getWeekDateRange(int noOfDays) {
		List<Date> dateList = new ArrayList<Date>();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -(noOfDays - 1));
		dateList.add(cal.getTime());
		for (int i = 0; i < (noOfDays - 1); i++) {
			cal.add(Calendar.DAY_OF_YEAR, 1);
			dateList.add(cal.getTime());
		}
		return dateList;
	}

	/*
	 * public static List<String> getWeeks() { List<String> dateList = new
	 * ArrayList<String>(); int num =
	 * Calendar.getInstance().get(Calendar.WEEK_OF_MONTH); for (int i = 1; i <=
	 * num; i++) { dateList.add(PMCConstant.WEEK_PREFIX + i); } return dateList;
	 * }
	 */

	public static Map<String, List<Date>> dateSegmentMap(List<String> segList) {
		System.err.println("segList : " + segList);
		Map<String, List<Date>> segmentMap = new LinkedHashMap<String, List<Date>>();
		Iterator<String> itr = segList.iterator();
		while (itr.hasNext()) {
			String seg = itr.next().toString();
			List<Date> tempDateList = new ArrayList<Date>();
			String[] serArr = seg.split("-");
			try {
				tempDateList.add(setEndTime(getDate(Integer.parseInt(serArr[0].trim()))));
				tempDateList.add(setInitialTime(getDate(Integer.parseInt(serArr[1].trim()))));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			segmentMap.put(seg, tempDateList);
		}
		return segmentMap;
	}

	public static Date getDate(int i) throws ParseException {
		int x = -i;
		SimpleDateFormat format = new SimpleDateFormat(PMCConstant.PMC_DATE_FORMATE);
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, x);
		Date tempdate = cal.getTime();
		String formattedDate = format.format(tempdate);
		Date date = format.parse(formattedDate);
		return date;
	}

	public static List<Date> getMonthIntervalDates() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(PMCConstant.PMC_DATE_FORMATE);
		List<Date> dateInterval = new ArrayList<Date>();
		int count = PMCConstant.MONTH_RANGE;
		while (count >= 0) {
			Calendar calendar = Calendar.getInstance();
			Date date = null;
			if (count == 0) {
				calendar.add(Calendar.DAY_OF_MONTH, -(count));
				date = calendar.getTime();
			} else {
				calendar.add(Calendar.DAY_OF_MONTH, -(count - 1));
				date = sdf.parse(sdf.format(calendar.getTime()));
			}
			count = count - PMCConstant.MONTH_INTERVAL;
			dateInterval.add(date);
		}
		return dateInterval;
	}

	public static Map<String, List<Date>> getDateIntervalRangeMap(int rangeofDays, int intervalOfDays)
			throws ParseException {
		Map<String, List<Date>> map = new LinkedHashMap<String, List<Date>>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
		int count = rangeofDays;
		Date intialDate = null;
		Date finalDate = null;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -(rangeofDays - 1));
		intialDate = sdf.parse(sdf.format(calendar.getTime()));
		while (count > 0) {
			String key = null;
			List<Date> dateInterval = new ArrayList<Date>();
			int initialCount = count;
			key = "Last " + initialCount + " days";
			if (count == intervalOfDays) {
				finalDate = new Date();
				count = 0;
			} else {
				count = count - intervalOfDays;
				if (initialCount == rangeofDays) {
					calendar.add(Calendar.DAY_OF_MONTH, +(intervalOfDays - 1));
				} else {
					calendar.add(Calendar.DAY_OF_MONTH, +intervalOfDays);
				}
				finalDate = setEndTime(sdf.parse(sdf.format(calendar.getTime())));
			}
			dateInterval.add(intialDate);
			dateInterval.add(finalDate);
			map.put(key, dateInterval);
		}
		return map;
	}

	public static List<String> getMonthInterval(List<Date> dates) {
		SimpleDateFormat formatter = new SimpleDateFormat(PMCConstant.PMC_DATE_FORMATE);
		List<String> dateInterval = null;
		if (dates.size() >= 2) {
			dateInterval = new ArrayList<String>();
			int count = 0;
			while (count < dates.size() - 1) {
				dateInterval.add(formatter.format(dates.get(count++)) + " - " + formatter.format(dates.get(count)));
			}
		}
		return dateInterval;
	}

	public static String getDateDifferenceInHours(Date date) {
		long t1 = new Date().getTime();
		long t2 = date.getTime();
		long diffinHrs = (t1 - t2) / (60 * 60 * 1000) % 60;
		return String.valueOf(diffinHrs);

	}

	public static Calendar timeStampToCal(Object obj) {
		Calendar cal = Calendar.getInstance();
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			// System.err.println("[PMC][ServicesUtil][timeStampToCal][obj]" +
			// obj + "obj.toString()" + obj.toString());
			cal.setTime(formatter.parse(obj.toString()));
			return cal;
		} catch (Exception e) {
			System.err.println("[PMC][ServicesUtil][timeStampToCal][getMessage]" + e.getMessage());
		}
		return null;
	}

	public static Calendar timeStampToCalAdmin(Object obj) {
		Calendar cal = Calendar.getInstance();
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			// System.err.println("[PMC][ServicesUtil][timeStampToCal][obj]" +
			// obj + "obj.toString()" + obj.toString());
			cal.setTime(formatter.parse(obj.toString()));
			return cal;
		} catch (Exception e) {
			System.err.println("[PMC][ServicesUtil][timeStampToCal][getMessage]" + e.getMessage());
		}
		return null;
	}

	public static Date dateResultAsDate(Object o) {
		String template = "";
		if (o instanceof Object[]) {
			template = Arrays.asList((Object[]) o).toString();
		} else {
			template = String.valueOf(o);
		}
		Date date = null;
		try {
			if (!isEmpty(template)) {
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-DD");
				date = formatter.parse(template);
			}
			// System.err.println("[PMC][WorkBoxFacade][resultAsDate][o]" + o +
			// "[template]" + template + "[date]" + date + "yyyy-MM-dd
			// hh:mm:ss.SSS");
		} catch (ParseException e) {
			System.err.println("resultAsString ParseException" + e.getMessage());
		}
		return date;
	}

	public static Calendar getSLADueDate(Calendar created, String slaString) {
		String[] sla = ((String) slaString).split("\\s+");
		int slaCount = Integer.parseInt(sla[0]);
		if (PMCConstant.DAYS.equalsIgnoreCase(sla[1])) {
			created.add(Calendar.DATE, slaCount);
		} else if (PMCConstant.HOURS.equalsIgnoreCase(sla[1])) {
			created.add(Calendar.HOUR, slaCount);
		} else if (PMCConstant.MINUTES.equalsIgnoreCase(sla[1])) {
			created.add(Calendar.MINUTE, slaCount);
		}
		return created;
	}

	public static Calendar getNotifyByDate(Calendar created, String threshold) {
		String[] sla = ((String) threshold).split("\\s+");
		int thresCount = Integer.parseInt(sla[0]);
		if (PMCConstant.DAYS.equalsIgnoreCase(sla[1])) {
			created.add(Calendar.DATE, -thresCount);
		} else if (PMCConstant.HOURS.equalsIgnoreCase(sla[1])) {
			created.add(Calendar.HOUR, -thresCount);
		} else if (PMCConstant.MINUTES.equalsIgnoreCase(sla[1])) {
			created.add(Calendar.MINUTE, -thresCount);
		}
		return created;
	}

	public static String getSLATimeLeft(Calendar sla) {

		Calendar cal = Calendar.getInstance();
		long today = cal.getTimeInMillis();
		long slaTime = sla.getTimeInMillis();
		long timeLeft = slaTime - today;
		if (timeLeft > 0) {
			String timeLeftString = "";
			int seconds = (int) (timeLeft / 1000) % 60;
			int minutes = (int) ((timeLeft / (1000 * 60)) % 60);
			int hours = (int) ((timeLeft / (1000 * 60 * 60)) % 24);
			int days = (int) (timeLeft / (1000 * 60 * 60 * 24));
			timeLeftString = "" + days + " days :" + hours + " hrs :" + minutes + " min :" + seconds + "sec";
			return timeLeftString;
		} else {
			return "Breach";
		}

	}

	public static String getCompletedTimePassed(Calendar sla, Calendar completedAt) {

		Calendar cal = Calendar.getInstance();
		long completed = cal.getTimeInMillis();
		long slaTime = sla.getTimeInMillis();
		long timePassed = completed - slaTime;
		String timePassesString = "";
		System.err.println("date_time sla: " + sla);
		System.err.println("date_time completedAt: " + completedAt);
		System.err.println("date_time timePassed: " + timePassed);

		int seconds = (int) (timePassed / 1000) % 60;
		int minutes = (int) ((timePassed / (1000 * 60)) % 60);
		int hours = (int) ((timePassed / (1000 * 60 * 60)) % 24);
		System.err.println("date_time hours: " + hours);
		int days = (int) (timePassed / (1000 * 60 * 60 * 24));
		System.err.println("date_time days: " + days);
		timePassesString = "" + days + " days :" + hours + " hrs :" + minutes + " min :" + seconds + "sec";
		return timePassesString;
	}

	public static float getPercntTimeCompleted(Calendar createdDate, Calendar slaDate) {
		Calendar cal = Calendar.getInstance();
		long today = cal.getTimeInMillis();
		long created = createdDate.getTimeInMillis();
		System.err.println("[PMC][ServicesUtil][timeStampToCal][today]" + today + "created" + created
				+ "slaDate.getTimeInMillis()" + slaDate.getTimeInMillis());
		return (((float) (today - created) / (slaDate.getTimeInMillis() - created)) * 100);
	}

	public static Date getEarlierDate(int noOfDays) {
		Calendar calendar = Calendar.getInstance();
		System.out.println(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, -noOfDays);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static String getDecryptedText(String encryptedText) {
		if (!isEmpty(encryptedText)) {
			byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
			return new String(decodedBytes);
		}
		return null;
	}

	public static String getBasicAuth(String userName, String password) {
		String userpass = userName + ":" + password;
		return "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
	}

	public static String convertInputStreamToString(InputStream inputStream) {
		try {

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[10000];
			while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}

			buffer.flush();
			byte[] byteArray = buffer.toByteArray();
			return new String(byteArray);

		} catch (Exception e) {
			System.err.println("[PMC][ServicesUtil][convertInputStreamToString][error]" + e.getMessage());
		}
		return null;
	}

	public static Date resultTAsDate(Object o) {
		Date date = null;
		if (!isEmpty(o) && o.toString() != "") {
			String template = "";
			if (o instanceof Object[]) {
				template = Arrays.asList((Object[]) o).toString();
			} else {
				template = String.valueOf(o);
			}
			try {
				DateFormat formatterT = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");
				DateFormat newDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				date = newDf.parse(newDf.format(formatterT.parse(template)));
				System.err.println("formatterT.parse(template)" + formatterT.parse(template)
						+ "newDf.format(formatterT.parse(template)" + newDf.format(formatterT.parse(template))
						+ "newDf.parse(newDf.format(formatterT.parse(template)))"
						+ newDf.parse(newDf.format(formatterT.parse(template))));

				System.err.println("[PMC][WorkBoxFacade][resultAsDate][o]" + o + "[template]" + template + "[date]"
						+ date + "yyyy-MM-dd hh:mm:ss.SSS");
			} catch (Exception e) {
				System.err.println("resultTAsDate " + e.getMessage());
			}
		}
		return date;
	}

	public static BigDecimal getBigDecimal(Object value) {
		BigDecimal ret = null;
		if (value != null) {
			if (value instanceof BigDecimal) {
				ret = (BigDecimal) value;
			} else if (value instanceof String) {
				ret = new BigDecimal((String) value);
			} else if (value instanceof BigInteger) {
				ret = new BigDecimal((BigInteger) value);
			} else if (value instanceof Number) {
				ret = new BigDecimal(((Number) value).doubleValue());
			} else {
				throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass()
						+ " into a BigDecimal.");
			}
		}
		return ret;

	}

	public static String convertFromZoneToZoneString(Date fromDate, Object fromDateObject, String fromZone,
			String toZone, String fromFormat, String toFormat) {
		try {
			if (!ServicesUtil.isEmpty(fromDateObject)) {
				DateFormat fromDateFormat = new SimpleDateFormat(fromFormat);
				fromDateFormat.setTimeZone(TimeZone.getTimeZone(fromZone));
				fromDate = fromDateFormat.parse(fromDateObject.toString());
			} else if (ServicesUtil.isEmpty(fromDate)) {
				fromDate = new Date();
			}
			// System.err.println("[Murphy][ServicesUtil][convertFromZoneToZoneString]\n
			// fromDate :"+fromDate + "fromDateObject :"+fromDateObject
			// +"fromZone :"+fromZone +" toZone : "+toZone+ "fromFormat :
			// "+fromFormat + " toFormat :" +toFormat);
			DateFormat toDateFormat = new SimpleDateFormat(toFormat);
			if (!ServicesUtil.isEmpty(toZone)) {
				toDateFormat.setTimeZone(TimeZone.getTimeZone(toZone));
			}
			return toDateFormat.format(fromDate.getTime());

		} catch (Exception e) {
			System.err.println("[PMC][ServicesUtil][convertFromZoneToZoneString][error] : " + e.getMessage());
		}
		return null;
	}

	public static String convertFromDateToString(Date fromDate) {
		String toString = null;
		try {
			Date date = new Date(fromDate.getTime());
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			toString = df.format(date);

		} catch (Exception e) {
			System.err.println("[PMC][ServicesUtil][convertFromDateToString][error] : " + e.getMessage());
		}
		return toString;
	}

	public static Date convertFromStringToDate(String fromString) {
		Date date = null;
		try {
			date = new SimpleDateFormat("dd-MMM-yy hh:mm:ss a").parse(fromString);

		} catch (Exception e) {
			System.err.println("[PMC][ServicesUtil][convertFromStringToDate][error] : " + e.getMessage());
		}
		return date;
	}

	public static Date convertFromStringToDateSubstitution(String fromString) {
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(fromString);

		} catch (Exception e) {
			System.err.println(
					"[PMC][ServicesUtil][convertFromStringToDate][for Substitution][error] : " + e.getMessage());
		}
		return date;
	}

	public static Date convertAdminFromStringToDate(String fromString) {
		Date date = null;
		try {

			date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(fromString);

		} catch (Exception e) {
			System.err.println("[PMC][ServicesUtil][convertFromStringToDate][error] : " + e.getMessage());
		}
		return date;
	}

	public static Date convertSuccessFactorsDate(String date) {
		date = date.substring(date.indexOf('(') + 1, date.indexOf('+'));
		Long dateLong = Long.valueOf(date);
		return new Date(dateLong);
	}

	

	public static String getAuthorization(String accessToken, String tokenType) {
		return tokenType + " " + accessToken;
	}

	// public static void main(String[] args) {
	//
	// System.currentTimeMillis();
	// Date today1 = new Date(System.currentTimeMillis());
	// System.out.println("Milli seconds to date using Date class: " + today1);
	// DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssXXX");
	// String reportDate = df.format(today1);
	// System.out.println("Report Date: " + reportDate);
	//
	// }

	public static String convertFromDateStringToDateString(String inDate, String inDateFormat, String outDateFormat) {
		DateFormat df1 = null;
		DateFormat df2 = null;
		try {
			df1 = new SimpleDateFormat(inDateFormat);
			df2 = new SimpleDateFormat(outDateFormat);
			return df2.format(df1.parse(inDate));
		} catch (ParseException e) {
			System.err.println("Exception parsing date : " + e.getMessage());
		}
		return null;
	}

	public static int asInteger(Object object) {
		if (!ServicesUtil.isEmpty(object)) {
			if(object instanceof java.math.BigInteger) {
				return ((BigInteger) object).intValue();
			} else if (object instanceof java.lang.Integer) {
				return ((Integer) object);
			}
		}
		return 0;
	}

	public static Boolean asBoolean(Object object) {
		if(!ServicesUtil.isEmpty(object)) {
			if (object instanceof java.lang.Short) {
				if((Short) object == 0) return Boolean.FALSE; else if((Short) object == 1) return Boolean.TRUE;
			} else if (object instanceof java.lang.Byte) {
				if((Byte) object == 0) return Boolean.FALSE; else if((Byte) object == 1) return Boolean.TRUE;
			} else if (object instanceof java.lang.Boolean) {
				if((Boolean) object == true) return Boolean.TRUE; else if ((Boolean) object == false) return Boolean.FALSE;
			}
		}
		return null;
	}

	
	public static String asString(Object object) {
		if (!ServicesUtil.isEmpty(object))
			return (String) object;
		return null;
	}

	public static List<String> getPreviousDates() {

		List<String> days = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		for (int i = 0; i <= 7; i++) {
			date = cal.getTime();
			cal.add(Calendar.DAY_OF_MONTH, -1);
			days.add(sdf.format(date));
		}
		return days;

	}

	public static List<String> getPreviousMonths() {

		List<String> months = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		for (int i = 0; i < 3; i++) {
			date = cal.getTime();
			cal.add(Calendar.MONTH, -1);
			months.add(sdf.format(date));
		}
		return months;

	}

	public static List<String> getPreviousMonthsName() {

		List<String> months = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM");
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		for (int i = 0; i < 3; i++) {
			date = cal.getTime();
			cal.add(Calendar.MONTH, -1);
			months.add(sdf.format(date));
		}
		return months;

	}

	public static String monthStartDate(String month) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY");
		Date date = cal.getTime();
		return sdf.format(date) + "-" + month + "-01";
		// return "01-" + month + "-" + sdf.format(date);
	}

	public static String monthEndDate(String month) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY");
		Date date = cal.getTime();
		YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(sdf.format(date)), Integer.parseInt(month));
		int daysInMonth = yearMonthObject.lengthOfMonth();
		return sdf.format(date) + "-" + month + "-" + daysInMonth;
		// return daysInMonth + "-" + month + "-" + sdf.format(date);
	}

	public static String getPreviousDay(String date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date myDate = null;
		try {
			myDate = dateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date oneDayBefore = new Date(myDate.getTime() - 2);
		return dateFormat.format(oneDayBefore);

	}
	
	public static String getMonthInNumber(String month)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM");
		Date varDate =null;
		try {
			varDate= dateFormat.parse(month);
			dateFormat = new SimpleDateFormat("MM");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateFormat.format(varDate);
	}
	
	
	//newly added from agco demo
	public static String getSchedularTimeDifference() {
		 SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, -1);
		Date twoMinBack = cal.getTime();
		String time = format1.format(twoMinBack);

		return time;
	}
	
	public static String getStringFromList(List<String> strList) {
		String searchInString = "";
		if (!isEmpty(strList)) {
			for (String str : strList) {
				searchInString += "'" + str + "',";
			}
			searchInString = searchInString.substring(0, searchInString.length() - 1);
		}
		return searchInString;
	}

	public static String getIstTime() {
		 SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		format1.setTimeZone(TimeZone.getTimeZone("IST"));
		String currentTime = format1.format(new Date());
		return currentTime;
	}
	public static String getIstTimeWithoutT() {
		 SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		format1.setTimeZone(TimeZone.getTimeZone("IST"));
		String currentTime = format1.format(new Date());
		return currentTime;
	}
	public static String getIstTimeTwoMinuteAfter() {
		 SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, 2);
		Date twoMinAfter = cal.getTime();
		String time = format1.format(twoMinAfter);

		return time;
	}
	public static String getIstTimeTwoMinuteBefore() {
		 SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, -2);
		Date twoMinBack = cal.getTime();
		String time = format1.format(twoMinBack);

		return time;
	}


//	public static void main(String args[]) {
//		String strDate = "Mar";
//		
//		System.err.println(monthEndDate(getMonthInNumber(strDate)));
//		System.err.println(monthStartDate(getMonthInNumber(strDate)));
//	}
	
}
