package scraper.utils;

import scraper.models.ApiResponse;
import java.util.List;

public class FileDataWriter {
    public static void writeToFile(List<ApiResponse> data, String outputFile, String format) {
        for (ApiResponse resp : data) {
            // Теперь эти методы доступны
            String city = resp.getCityName();
            String condition = resp.getWeatherCondition();
            double temp = resp.getTemperature();
            double feelsLike = resp.getFeelsLike();

            // Ваша логика записи в файл
            System.out.printf("City: %s, Condition: %s, Temp: %.1f, Feels like: %.1f%n",
                    city, condition, temp, feelsLike);
        }
    }
}