package scraper.scrappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import scraper.models.ApiResponse;
import scraper.utils.FileDataWriter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class WeatherAPI implements ApiScrapper {
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?lat=55.7558&lon=37.6176&appid=46279239ff1d1c0e54c09a3a9f3013f9&units=metric&lang=ru";
    private final String outputFile;
    private final String format;

    public WeatherAPI(String outputFile, String format) {
        this.outputFile = outputFile;
        this.format = format;
    }

    @Override
    public void fetchAndSaveData() throws IOException {
        List<ApiResponse> data = fetchData();
        FileDataWriter.writeToFile(data, outputFile, format);
    }

    @Override
    public String getServiceName() {
        return "weather";
    }

    private List<ApiResponse> fetchData() throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(API_URL);
            String response = client.execute(request, httpResponse ->
                    EntityUtils.toString(httpResponse.getEntity()));

            ObjectMapper mapper = new ObjectMapper();
            WeatherData weatherData = mapper.readValue(response, WeatherData.class);

            return Collections.singletonList(new ApiResponse(
                    weatherData.name,
                    weatherData.weather[0].main,
                    weatherData.main.temp,
                    weatherData.main.feels_like
            ));
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class WeatherData {
        public String name;
        public Weather[] weather;
        public Main main;

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Weather {
            public String main;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Main {
            public double temp;
            public double feels_like;
        }
    }
}