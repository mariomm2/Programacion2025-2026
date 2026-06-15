package main.java.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente HTTP para la API REST de Los Simpson (https://thesimpsonsapi.com/api).
 * La API devuelve los resultados paginados (20 por página):
 * { "count": N, "next": "url_siguiente", "results": [...] }
 */
public class ApiClient {

    private static final String BASE_URL = "https://thesimpsonsapi.com/api";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper    = new ObjectMapper();

    /**
     * Recorre TODAS las páginas de un endpoint y devuelve la lista completa.
     * Funciona con "characters", "episodes" y "locations".
     */
    public List<JsonNode> getAll(String endpoint) throws Exception {
        List<JsonNode> resultados = new ArrayList<>();
        String url = BASE_URL + "/" + endpoint;

        while (url != null) {
            JsonNode root = get(url);
            for (JsonNode item : root.get("results")) {
                resultados.add(item);
            }
            // "next" viene como null cuando es la última página
            JsonNode next = root.get("next");
            url = (next != null && !next.isNull()) ? next.asText() : null;
        }
        return resultados;
    }

    /** Obtiene el detalle de un elemento: /characters/{id} */
    public JsonNode getOne(String endpoint, long id) throws Exception {
        return get(BASE_URL + "/" + endpoint + "/" + id);
    }

    private JsonNode get(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readTree(resp.body());
    }

    // ── Helpers para leer campos del JSON sin NullPointerException ──────────

    public static String texto(JsonNode n, String campo) {
        JsonNode v = n.get(campo);
        if (v == null || v.isNull()) return null;
        String s = v.asText();
        return s.isBlank() ? null : s;
    }

    public static Integer entero(JsonNode n, String campo) {
        JsonNode v = n.get(campo);
        return (v == null || v.isNull()) ? null : v.asInt();
    }

    public static Long longVal(JsonNode n, String campo) {
        JsonNode v = n.get(campo);
        return (v == null || v.isNull()) ? null : v.asLong();
    }
}
