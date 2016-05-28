package jp.thotta.ifinance.collector.json;

public class ItemV4Eir {
    public String format_date;
    public String link;
    public String title;

    public ItemV4Eir(String format_date, String link, String title) {
        this.format_date = format_date;
        this.link = link;
        this.title = title;
    }

    @Override
    public String toString() {
        return String.format(
                "\n[title : \"%s\", link : \"%s\", format_date : \"%s\"]",
                title, link, format_date);
    }
}
