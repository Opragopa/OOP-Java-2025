package scraper.models;

public class ApiResponse {
    private final String cityName;
    private final String weatherCondition;
    private final double temperature;
    private final double feelsLike;

    public ApiResponse(String cityName, String weatherCondition,
                       double temperature, double feelsLike) {
        this.cityName = cityName;
        this.weatherCondition = weatherCondition;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
    }

    // Добавляем геттеры
    public String getCityName() {
        return cityName;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getFeelsLike() {
        return feelsLike;
    }
}