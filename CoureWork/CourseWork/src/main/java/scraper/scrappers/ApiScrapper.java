package scraper.scrappers;

import java.io.IOException;

public interface ApiScrapper {
    void fetchAndSaveData() throws IOException;
    String getServiceName();
}