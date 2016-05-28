package jp.thotta.ifinance.extractor;

import jp.thotta.ifinance.common.MyDate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * データ抽出の抽象クラス
 */
public abstract class AbstractDataExtractor {
    public String dataName;
    public List<String> header = new ArrayList<String>();
    public List<List<String>> data = new ArrayList<List<String>>();
    public String dirPath = "data/";

    public AbstractDataExtractor(String name) {
        this.dataName = name;
        this.dirPath += name;
        File dataDir = new File(dirPath);
        if (dataDir.mkdirs()) {
            System.out.println("make directory: " + dirPath);
        }
    }

    public void write() {
        try {
            String file_name = dirPath + "/" + MyDate.getToday() + ".txt";
            File file = new File(file_name);
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            writeList(pw, header);
            for (List<String> list : data) {
                writeList(pw, list);
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    abstract public void extract();

    public static void writeList(PrintWriter pw, List<String> list) {
        String t = "";
        for (String col : list) {
            pw.print(t + col);
            t = "\t";
        }
        pw.println();
    }
}
