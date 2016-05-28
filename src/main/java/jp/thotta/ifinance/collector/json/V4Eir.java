package jp.thotta.ifinance.collector.json;

import java.util.ArrayList;
import java.util.List;

public class V4Eir {
    public int item_count;
    public List<ItemV4Eir> item;

    public V4Eir(int item_count) {
        this.item_count = item_count;
        this.item = new ArrayList<ItemV4Eir>();
    }

    public String toString() {
        String s = "item_count : " + item_count + ",\n";
        s += "item : " + item;
        return s;
    }
}
