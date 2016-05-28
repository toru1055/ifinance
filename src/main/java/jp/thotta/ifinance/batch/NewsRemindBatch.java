package jp.thotta.ifinance.batch;

import jp.thotta.ifinance.common.MyDate;
import jp.thotta.ifinance.model.*;
import jp.thotta.ifinance.utilizer.JoinedStockInfo;
import jp.thotta.ifinance.utilizer.PredictedStockPrice;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewsRemindBatch {
    Connection batchConnection, webConnection;
    String tmpl = "text";
    Map<String, JoinedStockInfo> jsiMap;
    Map<String, PredictedStockPrice> pspMap;
    Map<String, DailyStockPrice> dspMap;
    Map<String, CompanyProfile> prMap;

    public NewsRemindBatch(Connection batchConnection,
                           Connection webConnection,
                           String tmpl)
            throws SQLException, ParseException {
        this.batchConnection = batchConnection;
        this.webConnection = webConnection;
        this.tmpl = tmpl;
        pspMap = PredictedStockPrice.selectLatestMap(batchConnection);
        dspMap = DailyStockPrice.selectLatests(batchConnection);
        jsiMap = JoinedStockInfo.selectAllMap(batchConnection);
        prMap = CompanyProfile.selectAll(batchConnection);
    }

    String getTitlePhrase() {
        return "登録イベント発生リマインド";
    }

    public void report() throws SQLException, ParseException {
        int daysList[] = {0, 4, 7, 14, 28};
        if (tmpl.equals("html")) {
            ReportPrinter.printHtmlHeader(getTitlePhrase());
        }
        for (int i = 1; i < daysList.length; i++) {
            printOne(daysList[i - 1], daysList[i]);
        }
        if (tmpl.equals("html")) {
            ReportPrinter.printHtmlFooter();
        }
    }

    public void printOne(int startDay, int endDay)
            throws SQLException, ParseException {
        MyDate startDate = MyDate.getFuture(startDay);
        MyDate endDate = MyDate.getFuture(endDay);
        Map<String, List<NewsReminderWeb>> reminderMap =
                NewsReminderWeb.categorizeByStockId(
                        NewsReminderWeb.selectReminderByDate(
                                startDate, endDate, webConnection));
        if (tmpl.equals("text")) {
            System.out.println("=== " + endDay +
                    "日以内の" + getTitlePhrase());
        } else if (tmpl.equals("html")) {
            System.out.println("<h2>" + endDay +
                    "日以内の" + getTitlePhrase() + "</h2>");
        }
        for (String k : reminderMap.keySet()) {
            JoinedStockInfo jsi = jsiMap.get(k);
            PredictedStockPrice psp = pspMap.get(k);
            CompanyProfile profile = prMap.get(k);
            DailyStockPrice dsp = dspMap.get(k);
            List<NewsReminderWeb> reminderList = reminderMap.get(k);
            List<CompanyNews> cnList = new ArrayList<CompanyNews>();
            for (NewsReminderWeb reminder : reminderList) {
                CompanyNews news =
                        CompanyNews.findById(batchConnection, reminder.newsId);
                cnList.add(news);
            }
            if (tmpl.equals("text")) {
                System.out.println("======= " + k + " =======");
                ReportPrinter.printStockDescriptions(
                        jsi, profile, null, dsp, psp, cnList, null);
                ReportPrinter.printReminderList(reminderList);
            } else if (tmpl.equals("html")) {
                StockInfoPrinter sip = new StockInfoPrinter(
                        jsi, profile, null, dsp, psp, cnList, null, null);
                sip.showChart = true;
                sip.reminderList = reminderList;
                sip.printStockElements();
            }
        }
    }

    public static void main(String[] args) {
        try {
            String tmpl = "text";
            if (args.length >= 1) {
                tmpl = args[0];
            }
            NewsRemindBatch reminder =
                    new NewsRemindBatch(Database.getConnection(),
                            DatabaseWeb.getConnection(),
                            tmpl);
            reminder.report();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                Database.closeConnection();
                DatabaseWeb.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
