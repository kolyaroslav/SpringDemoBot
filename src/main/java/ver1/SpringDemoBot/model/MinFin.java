package ver1.SpringDemoBot.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class MinFin {
// Чому саме парсинг? Тому що API Мінфіна платне...
    public static String getCurrency (String messageText) throws Exception {
var document = Jsoup.connect("https://minfin.com.ua/ua/currency/").get(); // підключаємось до ВEБ сторінки
        Elements currency_tables = document.getElementsByTag("tbody"); // виділяемо таблицю з якої будемо брати інформацію.
        Element our_table = currency_tables.get(0); //беремо інформацію з таблиці
        Elements el_from_currency_tables = our_table.children(); //таблиця 0 елементу доллар
        Element USD = el_from_currency_tables.get(0);
        Elements USD_elements = USD.children(); // витягуємо елементи звписані в Долларі
        Element EURO = el_from_currency_tables.get(1);
        Elements EURO_elements = EURO.children(); // витягуємо елементи звписані в Евро
        Element PLN = el_from_currency_tables.get(2);
        Elements PLN_elements = PLN.children(); //витягуємо елементи звписані в Злотих



        return "Курс НБУ для розрахунку митних затрат: \n\n"
                + USD_elements.get(0).text() +": " + USD_elements.get(2).text().substring(0,7) + "\n\n"
                + EURO_elements.get(0).text() +": " + EURO_elements.get(2).text().substring(0,7) + "\n\n"
                + PLN_elements.get(0).text() +": " + PLN_elements.get(2).text().substring(0,7) + "\n\n";
    }
}
