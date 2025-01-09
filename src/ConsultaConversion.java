// Actualizando el código para usar la nueva API de ExchangeRate-API.

// ConsultaConversion.java
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// Clase para manejar las consultas a la API de tasas de cambio.
public class ConsultaConversion {
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/b572bd13e3e05cca0a69b58e/latest/";

    public String buscaConversion(String monedaBase, String monedaObjetivo, double cantidad) {
        try {
            // URL construida para obtener tasas de cambio basadas en la moneda base.
            URI direccion = URI.create(API_URL + monedaBase);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(direccion)
                    .build();
            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());

            // Procesar la respuesta JSON.
            var json = response.body();
            JsonElement jsonElement = JsonParser.parseString(json);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            // Validar el estado de la respuesta.
            if (!"success".equals(jsonObject.get("result").getAsString())) {
                throw new RuntimeException("Error al obtener tasas de cambio: " + jsonObject.get("error-type").getAsString());
            }

            // Obtener la tasa de cambio específica.
            JsonObject rates = jsonObject.getAsJsonObject("conversion_rates");
            double tasaCambio = rates.get(monedaObjetivo).getAsDouble();

            // Calcular el monto convertido.
            double resultado = cantidad * tasaCambio;

            return String.format("%.2f", resultado);

        } catch (NumberFormatException | IOException | InterruptedException e) {
            System.out.println("Ocurrió un error: " + e.getMessage());
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
