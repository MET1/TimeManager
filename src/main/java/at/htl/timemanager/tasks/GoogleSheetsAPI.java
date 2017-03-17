package at.htl.timemanager.tasks;


import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @timeline .
 * 14.11.2016: MET 001  created class
 * 14.11.2016: MET 150  Google-Sheet-API
 * 14.11.2016: MET 040  Google-Sheet-API: Fetch the cell feed of the worksheet.
 */
public class GoogleSheetsAPI {

    public static final String GOOGLE_ACCOUNT_USERNAME = "registrierkassaapp@gmail.com";
    public static final String GOOGLE_ACCOUNT_PASSWORD = "nsadd132e";
    public static final String SPREADSHEET_KEY = "1L9c6Hl2HLLlkz5z-klJo5TwtopTILqKXtc0FGiK1ZPg";

    public static final String SPREADSHEET_URL
            = "https://spreadsheets.google.com/feeds/list/" + SPREADSHEET_KEY + "/default/public/values";

    private static GoogleSheetsAPI instance;
    private ListFeed feed;

    /**
     * Our view of Google Spreadsheets as an authenticated Google user.
     * Login and prompt the user to pick a sheet to use.
     * Load sheet
     * Use this String as url
     * Get Feed of Spreadsheet url
     */
    private GoogleSheetsAPI() {
        SpreadsheetService service = new SpreadsheetService("Sheet");
        //service.setUserCredentials(GOOGLE_ACCOUNT_USERNAME, GOOGLE_ACCOUNT_PASSWORD);
        try {
            URL url = new URL(SPREADSHEET_URL);
            feed = service.getFeed(url, ListFeed.class);
        } catch (ServiceException | IOException e) {
            e.printStackTrace();
        }
    }

    public static GoogleSheetsAPI getInstance() {
        if (instance == null) {
            instance = new GoogleSheetsAPI();
        }
        return instance;
    }

    public ListFeed getFeed() {
        return feed;
    }

    public List<String[]> getData() {
        List<String[]> data = new LinkedList<>();
        List<ListEntry> rows = feed.getEntries();
        Set<String> columnHeadings = rows.get(0).getCustomElements().getTags();
        data.add(columnHeadings.toArray(new String[columnHeadings.size()]));
        for (ListEntry entry : rows) {
            List<String> row = new LinkedList<>();
            for (String columnHeading : columnHeadings) {
                String cell = entry.getCustomElements().getValue(columnHeading);
                row.add(cell == null ? "" : cell);
            }
            data.add(row.toArray(new String[row.size()]));
        }
        return data;
    }


    public static void main(String[] args) throws IOException, ServiceException {
        List<ListEntry> rows = getInstance().getFeed().getEntries();
        for (ListEntry entry : rows) {
            Set<String> columnHeadings = entry.getCustomElements().getTags();
            for (String columnHeading : columnHeadings) {
                System.out.print(entry.getCustomElements().getValue(columnHeading) + "\t\t\t");
                entry.getCustomElements().setValueLocal(columnHeading, entry.getCustomElements().getValue(columnHeading) + "xxxx");
                //service.insert(listFeedUrl, entry);
            }
            System.out.print("\n");
        }
        try {
            write();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //feed.setEntries(rows);

                    /*//Iterate over feed to get cell value
            for (ListEntry le : lf.getEntries()) {
                CustomElementCollection cec = le.getCustomElements();
                //Pass column name to access it's cell values
                String val = cec.getValue("Nr");
                System.out.println(val);
                String val2 = cec.getValue("Status");
                System.out.println(val2);
                System.out.println(le.get);
            }*/
    }

    public static void write() throws Exception {
        SpreadsheetService service1 = new SpreadsheetService("google-spreadsheet");

        FeedURLFactory urlFactory = FeedURLFactory.getDefault();
        WorksheetFeed worksheetFeed1 = service1.getFeed(urlFactory.getWorksheetFeedUrl(SPREADSHEET_KEY, "private", "full"), WorksheetFeed.class);
        List<WorksheetEntry> worksheets1 = worksheetFeed1.getEntries();

        WorksheetEntry worksheet1 = worksheets1.get(0);
        System.out.println(worksheet1.getTitle().getPlainText());

        // Fetch the cell feed of the worksheet.
        URL cellFeedUrl1 = worksheet1.getCellFeedUrl();
        CellFeed cellFeed1 = service1.getFeed(cellFeedUrl1, CellFeed.class);

        for (CellEntry cell : cellFeed1.getEntries()) {

            if (cell.getTitle().getPlainText().equals("A1")) {
                cell.changeInputValueLocal("200");
                cell.update();
            } else if (cell.getTitle().getPlainText().equals("B1")) {
                cell.changeInputValueLocal("=SUM(A1, 200)");
                cell.update();
            }
        }
    }

}