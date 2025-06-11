package scraper;

import scraper.scrappers.ApiScrapper;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScrapperManager {
    private ExecutorService executor;
    private volatile boolean running = true;

    public void startScrapping(int threadCount, int intervalSec, String outputFile,
                               String format, List<String> services) {
        executor = Executors.newFixedThreadPool(threadCount);

        for (String service : services) {
            executor.execute(() -> {
                while (running) {
                    try {
                        ApiScrapper scrapper = ScrapperFactory.createScrapper(service, outputFile, format);
                        scrapper.fetchAndSaveData();
                        Thread.sleep(intervalSec * 1000L); // Конвертируем секунды в миллисекунды
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        System.err.println("Error in " + service + " scrapper: " + e.getMessage());
                    }
                }
            });
        }
    }

    public void stopScrapping() {
        running = false;
        executor.shutdownNow();
    }
}