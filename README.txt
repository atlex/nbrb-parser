nbrb-parser
E-Mail: 4alex@mail.ru

Downloads and parses currency rates from www.nbrb.by

Usage:

NbrbParser parser = new NbrbParser();
List<Currency> currencies = parser.getDailyRates(null, null);
currencies = parser.getDailyRates(new String[]{"USD"}, null);
currencies = parser.getDailyRates(new String[]{"USD"}, "01/25/2012");
currencies = parser.getDailyRates(new String[]{"USD", "EUR"}, null);
currencies = parser.getDailyRates(new String[]{"USD", "EUR"}, "01/25/2012");

Copyright (c) 2005-2012 Alexander Maximenya
All rights reserved