package scraper;

import scraper.scrappers.WeatherAPI;
import scraper.scrappers.ApiScrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class App {
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Usage: java App <threads> <timeout_sec> <service1,service2,...> <format>");
            System.out.println("Example: java App 3 5 weather json");
            return;
        }

        try {
            int threadCount = Integer.parseInt(args[0]);
            int timeoutSec = Integer.parseInt(args[1]);
            String[] services = args[2].split(",");
            String format = args[3];

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<Future<?>> futures = new ArrayList<>();

            while (true) {
                for (String service : services) {
                    ApiScrapper scrapper = createScrapper(service, "output_" + service + "." + format, format);
                    if (scrapper != null) {
                        futures.add(executor.submit(() -> {
                            try {
                                scrapper.fetchAndSaveData();
                                System.out.println("Data fetched for " + service);
                            } catch (IOException e) {
                                System.err.println("Error fetching data for " + service + ": " + e.getMessage());
                            }
                        }));
                    }
                }

                // Ожидаем завершения всех задач текущей итерации
                for (Future<?> future : futures) {
                    future.get();
                }
                futures.clear();

                System.out.println("Waiting " + timeoutSec + " seconds before next iteration...");
                Thread.sleep(timeoutSec * 1000L); // Замена TimeUnit.SECONDS.sleep()
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in arguments");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Execution interrupted: " + e.getMessage());
        }
    }

    private static ApiScrapper createScrapper(String service, String outputFile, String format) {
        if (service.equalsIgnoreCase("weather")) {
            return new WeatherAPI(outputFile, format);
            // Удален ExchangeAPI, так как его нет в проекте
        }
        System.err.println("Unknown service: " + service);
        return null;
    }
}