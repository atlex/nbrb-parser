package org.am.nbrb;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class downloads and parses various statistical information from http://www.nbrb.by/statistics/sref.asp
 *
 * DailyRates
 * http://www.nbrb.by/statistics/Rates/RatesDaily.asp
 * http://www.nbrb.by/statistics/Rates/XML/
 *
 *
 * @author  Alexander Maximenya (alex@maximenya.com)
 * @version 2012-06-21
 */
public class NbrbParser {
    public static final String URL_DAILY_RATES = "http://nbrb.by/Services/XmlExRates.aspx?ondate=";
    public static final String CODE = "NumCode";
    public static final String NAME = "Name";
    public static final String SHORT_NAME = "CharCode";
    public static final String AMOUNT = "Scale";
    public static final String RATE = "Rate";
    public static final String CURRENCY = "Currency";

    private DocumentBuilder docBuilder;

    public NbrbParser() {
    }

    /**
     * Gets currencies rates on a given date.
     *
     * @param date - MM/dd/yyyy - 07/29/2012
     *
     * @return currencies rates on a given date
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public List<Currency> getDailyRates(String date) throws ParserConfigurationException, IOException, SAXException {
        List<Currency> currencies = new ArrayList<Currency>();

        String url = URL_DAILY_RATES;
        if (date != null) {
            url += date;
        }

        DocumentBuilder db = getDocumentBuilder();
        Document doc = db.parse(url);

        NodeList list = doc.getElementsByTagName(CURRENCY);
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                Currency currency = new Currency();
                currency.setCode(getValue(CODE, element));
                currency.setName(getValue(NAME, element));
                currency.setShortName(getValue(SHORT_NAME, element));

                try {
                    currency.setAmount(Integer.parseInt(getValue(AMOUNT, element)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                try {
                    currency.setRate(Double.parseDouble(getValue(RATE, element)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                currencies.add(currency);
            }
        }

        return currencies;
    }

    public List<Currency> getDailyRates(String[] shortNames, String date) throws IOException, SAXException, ParserConfigurationException {
        List<Currency> currencies = getDailyRates(date);

        if (shortNames == null) {
            return currencies;
        }
        List<Currency> result = new ArrayList<Currency>();

        for (Currency currency : currencies) {
            for (int i = 0; i < shortNames.length; i++) {
                if (shortNames[i].equals(currency.getShortName())) {
                    result.add(currency);
                }
            }
        }
        return result;
    }

    private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        if (docBuilder == null) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            docBuilder = dbf.newDocumentBuilder();
        }
        return docBuilder;
    }

    private String getValue(String tagName, Element e) {
        String value = null;

        NodeList list = e.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList childList = list.item(0).getChildNodes();
            if (childList != null && childList.getLength() > 0) {
                value = childList.item(0).getNodeValue();
            }
        }

        return value;
    }

    public static void main(String[] args) {
        //Test usage
        NbrbParser parser = new NbrbParser();

        List<Currency> currencies;

        try {
            currencies = parser.getDailyRates(null, null);
            printCurrencies(currencies);

            currencies = parser.getDailyRates(new String[]{"USD"}, null);
            printCurrencies(currencies);

            currencies = parser.getDailyRates(new String[]{"USD"}, "01/25/2012");
            printCurrencies(currencies);

            currencies = parser.getDailyRates(new String[]{"USD", "EUR"}, null);
            printCurrencies(currencies);

            currencies = parser.getDailyRates(new String[]{"USD", "EUR"}, "01/25/2012");
            printCurrencies(currencies);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void printCurrencies(List<Currency> currencies) {
        for (Currency currency : currencies) {
            System.out.println(currency.toString());
        }
        System.out.println("------------------");
    }

    public class Currency {
        private String name; //Австралийский доллар
        private String shortName; //AUD
        private String code; //036
        private int amount; //1
        private double rate; //8540.12


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public double getRate() {
            return rate;
        }

        public void setRate(double rate) {
            this.rate = rate;
        }

        @Override
        public String toString() {
            return "Currency{" +
                    "name='" + name + '\'' +
                    ", shortName='" + shortName + '\'' +
                    ", code=" + code +
                    ", amount=" + amount +
                    ", rate=" + rate +
                    '}';
        }
    }
}
