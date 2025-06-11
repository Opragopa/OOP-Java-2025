package scraper;

import scraper.scrappers.WeatherAPI;
import scraper.scrappers.ApiScrapper;

public class ScrapperFactory {
    public static ApiScrapper createScrapper(String service, String outputFile, String format) {
        if (service.equalsIgnoreCase("weather")) {
            return new WeatherAPI(outputFile, format);
        }
        throw new IllegalArgumentException("Unknown service: " + service);
    }
}