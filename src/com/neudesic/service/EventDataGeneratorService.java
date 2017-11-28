package com.neudesic.service;

import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.neudesic.model.EventData;
import com.neudesic.util.Constants;
import com.neudesic.util.RandomDateGenerator;
import com.opencsv.CSVWriter;
import com.opencsv.bean.BeanToCsv;
import com.opencsv.bean.ColumnPositionMappingStrategy;

public class EventDataGeneratorService {

	public List<EventData> generateEventData() {
		List<EventData> eventList = new ArrayList<>();
		int eventDataRowsCount = Integer.parseInt(Constants.config.getProperty("EVENT_DATA_ROWS"));

		String EVENT_CATEGORY[] = Constants.eventColumnValueMapper.getProperty("EVENT_CATEGORY").split(",");
		String EVENT_ACTION[] = Constants.eventColumnValueMapper.getProperty("EVENT_ACTION").split(",");
		String EVENT_SESSIONID[] = Constants.eventColumnValueMapper.getProperty("EVENT_SESSIONID").split(",");
		String PUBLISH_DATE[] = Constants.eventColumnValueMapper.getProperty("PUBLISH_DATE").split(",");
		String EVENT_LINK[] = Constants.eventColumnValueMapper.getProperty("EVENT_LINK").split(",");
		String EVENT_USERID[] = Constants.eventColumnValueMapper.getProperty("EVENT_USERID").split(",");
		String EVENT_ACTION_DESC[] = Constants.eventColumnValueMapper.getProperty("EVENT_ACTION_DESC").split(",");
		String EVENT_PARENT_URL[] = Constants.eventColumnValueMapper.getProperty("EVENT_PARENT_URL").split(",");
		String PAGE_URL[] = Constants.eventColumnValueMapper.getProperty("PAGE_URL").split(",");
		String IS_HOMEPAGE[] = Constants.eventColumnValueMapper.getProperty("IS_HOMEPAGE").split(",");
		String LINK_TITLE[] = Constants.eventColumnValueMapper.getProperty("LINK_TITLE").split(",");
		String PAGE_TITLE[] = Constants.eventColumnValueMapper.getProperty("PAGE_TITLE").split(",");
		String IS_VIDEO[] = Constants.eventColumnValueMapper.getProperty("IS_VIDEO").split(",");
		String IS_PARTNER[] = Constants.eventColumnValueMapper.getProperty("IS_PARTNER").split(",");
		String PARTNER_NAME[] = Constants.eventColumnValueMapper.getProperty("PARTNER_NAME").split(",");
		String RECIPIE_NAME[] = Constants.eventColumnValueMapper.getProperty("RECIPIE_NAME").split(",");
		// String EVENT_IN_STOCK[] =
		// Constants.eventColumnValueMapper.getProperty("EVENT_IN_STOCK").split(",");

		// double num = Math.round(Math.random() * 5);

		for (int i = 0; i < eventDataRowsCount; i++) {
			EventData event = new EventData();
			event.setEVENT_CATEGORY(Constants.getRandomString(EVENT_CATEGORY));
			event.setEVENT_ACTION(Constants.getRandomString(EVENT_ACTION));
			event.setEVENT_LINK(Constants.getRandomString(EVENT_LINK));
			event.setEVENT_USERID(Constants.getRandomString(EVENT_USERID));
			event.setEVENT_ACTION_DESC(Constants.getRandomString(EVENT_ACTION_DESC));
			event.setEVENT_PARENT_URL(Constants.getRandomString(EVENT_PARENT_URL));
			event.setPAGE_URL(Constants.getRandomString(PAGE_URL));
			event.setIS_HOMEPAGE("N");
			event.setLINK_TITLE("");
			event.setPAGE_TITLE(Constants.getRandomString(PAGE_TITLE));
			event.setIS_VIDEO("N");
			event.setIS_PARTNER("N");
			event.setPARTNER_NAME(Constants.getRandomString(PARTNER_NAME));
			event.setRECIPIE_NAME("");
			event.setEVENT_IN_STOCK("");

			applyBusinessRules(event);

			eventList.add(event);
		}
		seteventSessionAndPublishDate(eventList, eventDataRowsCount);
		return eventList;
	}

	private void seteventSessionAndPublishDate(List<EventData> eventList, int eventDataRowsCount) {

		Map<String, List<Date>> sessionDateMapper = generateSessionDateMapper(eventDataRowsCount);
		int no_of_session = (int) (eventDataRowsCount * 0.2);
		int no_of_event_per_session = eventDataRowsCount / no_of_session;
		int sessionIndex = 0;
		int dateIndex = 0;
		List<String> sessionList = convertMapKeyIntoArray(sessionDateMapper);

		String session = sessionList.get(0);
		List<Date> dateList = sessionDateMapper.get(session);

		System.out.println("Sesion size:" + sessionList.size());
		System.out.println("date size :" + dateList.size());
		for (int i = 0; i < eventDataRowsCount; i++) {
			EventData event = eventList.get(i);
			System.out.println(i);

			if (i % no_of_session == 0) {
				session = sessionList.get(sessionIndex);
				dateList = sessionDateMapper.get(session);
				sessionIndex++;
			}
			if (i % no_of_event_per_session == 0) {
				dateIndex = 0;
			}

			event.setEVENT_SESSIONID(session);
			event.setPUBLISH_DATE(parseDate(dateList.get(dateIndex)));
			dateIndex++;
		}
	}

	private List<String> convertMapKeyIntoArray(Map<String, List<Date>> sessionDateMapper) {
		List<String> sessions = new ArrayList<>();
		for (String key : sessionDateMapper.keySet()) {
			sessions.add(key);
		}
		return sessions;
	}

	private void applyBusinessRules(EventData event) {

		// Rule 1: EC :Pageload --> EA : pageload
		if (equals(event.getEVENT_CATEGORY(), "PageLoad")) {
			event.setEVENT_ACTION("PageLoad");
		}

		// Rule 2: EC: Click --> EA : click, addtocart
		if (equals(event.getEVENT_CATEGORY(), "Click")) {
			event.setEVENT_ACTION(Constants.getRandomString(new String[] { "Click", "AddToCart" }));
		}

		// Rule 3:PAGEURL: chefd/home --> IS_HOMEPAGE: Y
		if (equals(event.getPAGE_URL(), "chefd.com/home")) {
			event.setIS_HOMEPAGE("Y");
		}

		// Rule 4: EC: click --> event parenturl :contain /collection -->
		// IsPartner : Y
		if (equals(event.getEVENT_ACTION(), "Click") && startWith(event.getEVENT_PARENT_URL(), "/collections/")) {
			event.setIS_PARTNER("Y");
		}

		// Rule 5: EA: add to cart ||pageurl start with 'product/' || eventlink
		// start with 'product/'
		if (equals(event.getEVENT_ACTION(), "AddToCart") || startWith(event.getPAGE_URL(), "/products/")
				|| startWith(event.getEVENT_LINK(), "/products/")) {

			if (startWith(event.getPAGE_URL(), "/products/")) {
				/// products/bacon-cheeseburger-with-gruyere-and-maple-onion-confit
				String url[] = event.getPAGE_URL().split("/");
				String reciepname = url[url.length - 1].replaceAll("-", " ").toUpperCase();
				event.setRECIPIE_NAME(reciepname);
			} else if (startWith(event.getEVENT_LINK(), "/products/")) {
				String url[] = event.getEVENT_LINK().split("/");
				String reciepname = url[url.length - 1].replaceAll("-", " ").toUpperCase();
				event.setRECIPIE_NAME(reciepname);
			} else {
				event.setRECIPIE_NAME(
						Constants.getRandomString(Constants.eventColumnValueMapper.getProperty("RECIPIE_NAME").split(",")));
			}
		}

		// Rule 6:PArtner name :
		// if EVENT_PARENT_URL start with chefd.com --> partne name: chefd
		// else partner name : EVENT_PARENT_URL last word
		// eg:/collections/<the-coca-cola-company> last word
		
		//Rule 7: page title:
		//if EVENT_PARENT_URL start with chefd.com --> page title: The First and only Meal Store
		//else page title :EVENT_PARENT_URL  /collections/<the-coca-cola-company> last word

		if (startWith(event.getEVENT_PARENT_URL(), "chefd.com") || startWith(event.getEVENT_PARENT_URL(), "google.com")) {
			event.setPARTNER_NAME("Chefd");
			event.setPAGE_TITLE("The First and only Meal Store");
		} else {
			String url[] = event.getEVENT_PARENT_URL().split("/");
			event.setPARTNER_NAME(url[url.length - 1]);
			event.setPAGE_TITLE(url[url.length - 1]);
		}
	}

	public void writeToCSV(List<EventData> eventList) {
		CSVWriter csvWriter = null;
		try {
			csvWriter = new CSVWriter(new FileWriter(
					Constants.config.getProperty("FILE_PATH_EVENT_DATA") + System.currentTimeMillis() + ".csv"));
			BeanToCsv<EventData> bc = new BeanToCsv<>();
			bc.write(setColumMapping(), csvWriter, eventList);
			System.out.println("CSV File written successfully!!!");
		} catch (Exception ee) {
			ee.printStackTrace();
		} finally {
			try {
				// closing the writer
				csvWriter.close();
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static ColumnPositionMappingStrategy<EventData> setColumMapping() {
		ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
		strategy.setType(EventData.class);
		String[] columns = Constants.config.getProperty("EVENT_DATA").split(",");
		strategy.setColumnMapping(columns);
		return strategy;
	}

	public Map<String, List<Date>> generateSessionDateMapper(int no_of_rows) {
		int no_of_session = (int) (no_of_rows * 0.2);
		Map<String, List<Date>> sessionDataMapper = new HashMap<String, List<Date>>();
		for (int i = 0; i < no_of_session; i++) {
			String sessionid = UUID.randomUUID().toString();
			sessionDataMapper.put(sessionid, null);
		}
		String startDate = Constants.config.getProperty("DATE_RANGE_STARTDATE");
		String endDate = Constants.config.getProperty("DATE_RANGE_ENDDATE");

		int no_of_event_per_session = no_of_rows / no_of_session;
		sessionDataMapper.entrySet().stream().forEach(item -> {
			List<Date> dateList;
			try {
				dateList = RandomDateGenerator.generateDate(startDate, endDate, no_of_event_per_session);
				sessionDataMapper.put(item.getKey(), dateList);
			} catch (ParseException e) {
				e.printStackTrace();
			}

		});
		return sessionDataMapper;
	}

	private String parseDate(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
		return dateFormat.format(date).replaceAll("\\+0530", "");
	}
	
	private boolean equals(String s1, String s2) {
		return StringUtils.equalsIgnoreCase(s1, s2);
	}

	private boolean startWith(String src, String prefix) {
		return StringUtils.startsWith(src, prefix);
	}
	
	public static void main(String args[]) {
		EventDataGeneratorService dg = new EventDataGeneratorService();
		dg.writeToCSV(dg.generateEventData());
		//dg.generateSessionDateMapper(100);
	}

}
