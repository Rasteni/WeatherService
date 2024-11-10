import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {

    private static final String URL = "https://api.weather.yandex.ru/v2/forecast";

    public static void main(String[] args) {
        try {
            findLatLon();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void findLatLon() throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите координаты широты (lat) и долготы (lon), " +
                "где вы хотите узнать погоду:");
        System.out.print("lat: ");
        String lat = scanner.next();
        System.out.print("lon: ");
        String lon = scanner.next();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "?lat=" + lat + "&lon=" + lon))
                .header("X-Yandex-Weather-Key", "dba9eca8-bb43-447b-81bb-e0f1de525b8b")
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        String responseBody = response.body();
        System.out.println(responseBody);

        String tempAvg = findAvgInBody(responseBody);

        if (!tempAvg.isEmpty()) {
            System.out.println("Средняя температура: " + tempAvg + "°C");
        } else {
            System.out.println("Средняя температура не найдена в ответе.");
        }

        scanner.close();
    }

    private static String findAvgInBody(String body) {
        body = body.trim();
        if (body.startsWith("{") && body.endsWith("}")) {
            body = body.substring(1, body.length() - 1);
            String[] keyValuePairs = body.split(",");
            for (String keyValuePair : keyValuePairs) {
                String[] parts = keyValuePair.split(":");
                String key = parts[0].trim().replaceAll("[{\"}]", "");
                if (key.equals("temp_avg")) {
                    return parts[1].trim().replaceAll("[{\"}]", "");
                }
            }
        }
        return "";
    }
}