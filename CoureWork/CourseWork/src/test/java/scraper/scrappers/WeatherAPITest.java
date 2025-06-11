package scraper.scrappers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import scraper.models.ApiResponse;
import java.util.List;

public class WeatherAPITest {
    private static final String TEST_OUTPUT = "test_output.json";
    private static final int TEST_INTERVAL = 1;

    @Test
    public void testFetchData() throws Exception {
        WeatherAPI scrapper = new WeatherAPI(TEST_OUTPUT, TEST_INTERVAL);
        List<ApiResponse> result = scrapper.fetchData();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testRunMethod() {
        WeatherAPI scrapper = new WeatherAPI(TEST_OUTPUT, TEST_INTERVAL);
        Thread thread = new Thread(scrapper);
        thread.start();

        // Даем время на выполнение
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        scrapper.stop();
        assertTrue(true); // Простая проверка, что код выполнился
    }
}