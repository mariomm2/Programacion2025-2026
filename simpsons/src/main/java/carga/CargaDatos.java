package main.java.carga;

import com.fasterxml.jackson.databind.JsonNode;
import main.java.api.ApiClient;
import main.java.com.hibernate.ConnectionUtil;
import main.java.model.Episodio;
import main.java.model.Personaje;
import main.java.model.Ubicacion;
import org.hibernate.Session;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Descarga toda la información de https://thesimpsonsapi.com/api
 * y la persiste en la base de datos.
 *
 * Patrón de cada fase (igual que en los apuntes de Hibernate):
 *   Session session = ConnectionUtil.getSessionFactory().openSession();
 *   session.beginTransaction();
 *   ...  session.persist(entidad) ...
 *   session.getTransaction().commit();
 *   session.close();
 *
 * ⚠ EJECUTAR SOLO UNA VEZ. Si los datos ya están cargados y
 *   vuelves a ejecutarlo, fallará porque los @Id ya existen.
 *   Vacía las tablas en DBeaver antes de repetir la carga.
 */
public class CargaDatos {

    private final ApiClient api = new ApiClient();

    public void cargarTodo() throws Exception {
        Map<Long, Episodio> episodiosPorId = cargarEpisodios();
        cargarPersonajes(episodiosPorId);
        cargarUbicaciones();
        System.out.println("\n✅ Carga completa.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FASE 1: Episodios (768 en total)
    // ─────────────────────────────────────────────────────────────────────────
    private Map<Long, Episodio> cargarEpisodios() throws Exception {
        System.out.println("Descargando episodios...");
        List<JsonNode> nodos = api.getAll("episodes");
        Map<Long, Episodio> mapa = new HashMap<>();

        Session session = ConnectionUtil.getSessionFactory().openSession();
        session.beginTransaction();

        for (JsonNode n : nodos) {
            Episodio ep = new Episodio();
            ep.setId(ApiClient.longVal(n, "id"));
            ep.setName(ApiClient.texto(n, "name"));
            ep.setSeason(ApiClient.entero(n, "season"));
            ep.setEpisodeNumber(ApiClient.entero(n, "episode_number"));
            ep.setAirdate(parseFecha(ApiClient.texto(n, "airdate")));
            ep.setSynopsis(ApiClient.texto(n, "synopsis"));
            ep.setImagePath(ApiClient.texto(n, "image_path"));

            session.persist(ep);
            mapa.put(ep.getId(), ep);
        }

        session.getTransaction().commit();
        session.close();
        System.out.println("  → Episodios guardados: " + mapa.size());
        return mapa;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FASE 2: Personajes (1182 en total)
    // Primero guardamos los datos básicos y luego consultamos el detalle
    // de cada uno para obtener su primera aparición (1182 peticiones HTTP).
    // ─────────────────────────────────────────────────────────────────────────
    private void cargarPersonajes(Map<Long, Episodio> episodiosPorId) throws Exception {
        System.out.println("Descargando personajes...");
        List<JsonNode> nodos = api.getAll("characters");

        // --- Paso 2a: guardar datos básicos de todos los personajes ---
        Session session = ConnectionUtil.getSessionFactory().openSession();
        session.beginTransaction();

        for (JsonNode n : nodos) {
            Personaje p = new Personaje();
            p.setId(ApiClient.longVal(n, "id"));
            p.setName(ApiClient.texto(n, "name"));
            p.setAge(ApiClient.entero(n, "age"));
            p.setBirthdate(parseFecha(ApiClient.texto(n, "birthdate")));
            p.setGender(ApiClient.texto(n, "gender"));
            p.setOccupation(ApiClient.texto(n, "occupation"));
            p.setStatus(ApiClient.texto(n, "status"));
            p.setPortraitPath(ApiClient.texto(n, "portrait_path"));

            JsonNode frases = n.get("phrases");
            if (frases != null) {
                for (JsonNode f : frases) p.getPhrases().add(f.asText());
            }

            session.persist(p);
        }

        session.getTransaction().commit();
        session.close();
        System.out.println("  → Personajes (datos básicos) guardados: " + nodos.size());

        // --- Paso 2b: primera aparición (1 petición HTTP por personaje) ---
        System.out.println("  Consultando primera aparición (puede tardar varios minutos)...");

        session = ConnectionUtil.getSessionFactory().openSession();
        session.beginTransaction();

        int contador = 0;
        for (JsonNode n : nodos) {
            Long personajeId = ApiClient.longVal(n, "id");
            try {
                JsonNode detalle = api.getOne("characters", personajeId);
                Long epId = ApiClient.longVal(detalle, "first_appearance_ep_id");
                if (epId != null) {
                    Episodio ep = episodiosPorId.get(epId);
                    if (ep != null) {
                        Personaje managed = session.find(Personaje.class, personajeId);
                        managed.setPrimeraAparicion(ep);
                        managed.agregarEpisodio(ep); // también cuenta como aparición
                    }
                }
            } catch (Exception e) {
                System.out.println("    [AVISO] Personaje id=" + personajeId + ": " + e.getMessage());
            }

            contador++;
            // Hacemos commit cada 100 personajes para no acumular demasiado en memoria
            if (contador % 100 == 0) {
                session.getTransaction().commit();
                session.beginTransaction();
                System.out.println("    ... " + contador + "/" + nodos.size());
            }
        }

        session.getTransaction().commit();
        session.close();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FASE 3: Ubicaciones (477 en total)
    // La API no expone la lista de "residentes" de cada ubicación,
    // así que la relación N:M residencias queda vacía en la BD.
    // ─────────────────────────────────────────────────────────────────────────
    private void cargarUbicaciones() throws Exception {
        System.out.println("Descargando ubicaciones...");
        List<JsonNode> nodos = api.getAll("locations");

        Session session = ConnectionUtil.getSessionFactory().openSession();
        session.beginTransaction();

        for (JsonNode n : nodos) {
            Ubicacion u = new Ubicacion();
            u.setId(ApiClient.longVal(n, "id"));
            u.setName(ApiClient.texto(n, "name"));
            u.setType(ApiClient.texto(n, "type"));
            u.setTown(ApiClient.texto(n, "town"));
            u.setDescription(ApiClient.texto(n, "description"));
            u.setImagePath(ApiClient.texto(n, "image_path"));
            session.persist(u);
        }

        session.getTransaction().commit();
        session.close();
        System.out.println("  → Ubicaciones guardadas: " + nodos.size());
    }

    private LocalDate parseFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) return null;
        try { return LocalDate.parse(fecha); } catch (Exception e) { return null; }
    }
}
