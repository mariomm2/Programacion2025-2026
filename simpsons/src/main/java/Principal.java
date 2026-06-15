package main.java;

import main.java.carga.CargaDatos;
import main.java.com.hibernate.ConnectionUtil;
import main.java.com.hibernate.persistencia.ServicioPersistenciaEpisodio;
import main.java.com.hibernate.persistencia.ServicioPersistenciaPersonaje;
import main.java.com.hibernate.persistencia.ServicioPersistenciaUbicacion;
import main.java.consultas.ConsultasService;
import main.java.model.Episodio;
import main.java.model.Personaje;
import main.java.model.Ubicacion;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Principal {

    public static void main(String[] args) throws Exception {

        // ══════════════════════════════════════════════════════════════════════
        // PASO 1: CARGA DE DATOS DESDE LA API (solo la primera vez)
        //
        // Descomenta las dos líneas siguientes, ejecútalas UNA SOLA VEZ,
        // y vuelve a comentarlas. La carga tarda varios minutos porque
        // consulta el detalle de cada uno de los 1182 personajes.
        // ══════════════════════════════════════════════════════════════════════
        // new CargaDatos().cargarTodo();
        // System.out.println("Carga finalizada. Vuelve a comentar esta línea.");


        // ══════════════════════════════════════════════════════════════════════
        // PASO 2: CRUD de prueba — Personaje, Episodio y Ubicacion
        // ══════════════════════════════════════════════════════════════════════
        ServicioPersistenciaPersonaje svcPersonaje  = new ServicioPersistenciaPersonaje();
        ServicioPersistenciaEpisodio  svcEpisodio   = new ServicioPersistenciaEpisodio();
        ServicioPersistenciaUbicacion svcUbicacion  = new ServicioPersistenciaUbicacion();

        System.out.println("\n===== CRUD PERSONAJE =====");

        // READ
        Personaje homer = svcPersonaje.obtener(1L);
        System.out.println("Obtenido: " + homer);

        // UPDATE
        if (homer != null) {
            homer.setOccupation("Safety Inspector at Springfield Nuclear Power Plant");
            svcPersonaje.actualizar(homer);
        }

        // Prueba de error: id inexistente (no debe detener el programa)
        System.out.println("\n-- Intentar obtener personaje inexistente (id=9999) --");
        svcPersonaje.obtener(9999L);
        System.out.println("El programa continúa correctamente.\n");

        System.out.println("===== CRUD EPISODIO =====");
        Episodio ep = svcEpisodio.obtener(1L);
        System.out.println("Obtenido: " + ep);
        if (ep != null) {
            ep.setSynopsis("El episodio piloto de Los Simpson.");
            svcEpisodio.actualizar(ep);
        }

        System.out.println("\n===== CRUD UBICACION =====");
        Ubicacion u = svcUbicacion.obtener(1L);
        System.out.println("Obtenida: " + u);
        if (u != null) {
            u.setTown("Springfield");
            svcUbicacion.actualizar(u);
        }


        // ══════════════════════════════════════════════════════════════════════
        // PASO 3: LAS 7 CONSULTAS DEL BOLETÍN
        // ══════════════════════════════════════════════════════════════════════
        ConsultasService consultas = new ConsultasService();

        // ── 1. Personaje más recurrente de un género ──────────────────────────
        System.out.println("\n===== CONSULTA 1: Personaje más recurrente (Male) =====");
        Personaje masRecurrente = consultas.personajeMasRecurrentePorGenero("Male");
        if (masRecurrente != null)
            System.out.println("HQL:    " + masRecurrente.getName()
                    + " -> " + masRecurrente.getEpisodios().size() + " episodios");

        Personaje masRecurrenteLambda = consultas.personajeMasRecurrentePorGeneroLambda("Female");
        if (masRecurrenteLambda != null)
            System.out.println("Lambda: " + masRecurrenteLambda.getName()
                    + " -> " + masRecurrenteLambda.getEpisodios().size() + " episodios");

        // ── 2. Último episodio de un personaje ────────────────────────────────
        System.out.println("\n===== CONSULTA 2: Último episodio de Homer Simpson =====");
        Episodio ultimoHQL    = consultas.ultimoEpisodioDePersonaje("Homer Simpson");
        Episodio ultimoLambda = consultas.ultimoEpisodioDePersonajeLambda("Homer Simpson");
        System.out.println("HQL:    " + ultimoHQL);
        System.out.println("Lambda: " + ultimoLambda);

        // ── 3. Media de personajes vivos por episodio y temporada ─────────────
        System.out.println("\n===== CONSULTA 3: Media personajes vivos =====");
        double mediaGlobal = consultas.mediaPersonajesVivosPorEpisodio();
        System.out.printf("Media global: %.2f personajes vivos por episodio%n", mediaGlobal);

        Map<Integer, Double> mediaPorTemporada = consultas.mediaPersonajesVivosPorTemporada();
        mediaPorTemporada.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .limit(5)
                .forEach(e -> System.out.printf("  Temporada %2d -> %.2f%n", e.getKey(), e.getValue()));

        // ── 4. Temporadas ordenadas por nº de personajes distintos ────────────
        System.out.println("\n===== CONSULTA 4: Temporadas por personajes distintos =====");
        List<Object[]> temporadas = consultas.temporadasPorPersonajesDistintos();
        temporadas.stream().limit(5).forEach(fila ->
                System.out.println("  Temporada " + fila[0] + " -> " + fila[1] + " personajes distintos"));

        // ── 5. Personajes cuyo nombre empieza por "B" con debut tras 1995-01-01 ──
        System.out.println("\n===== CONSULTA 5: Personajes con 'B' y debut > 1995-01-01 =====");
        LocalDate fecha = LocalDate.of(1995, 1, 1);

        List<Personaje> hql5 = consultas.personajesPorInicialYFecha("B", fecha);
        System.out.println("HQL (" + hql5.size() + " resultados):");
        hql5.stream().limit(5).forEach(p ->
                System.out.println("  " + p.getName() + " -> debut: " + p.getPrimeraAparicion().getAirdate()));

        List<Personaje> lambda5 = consultas.personajesPorInicialYFechaLambda("B", fecha);
        System.out.println("Lambda (" + lambda5.size() + " resultados):");
        lambda5.stream().limit(5).forEach(p -> System.out.println("  " + p.getName()));

        // ── 6. Ubicaciones ordenadas por nº de residentes (desc) ─────────────
        System.out.println("\n===== CONSULTA 6: Ubicaciones por nº de residentes =====");
        List<Ubicacion> ubicaciones = consultas.ubicacionesPorResidentesDesc();
        System.out.println("Top 5:");
        ubicaciones.stream().limit(5).forEach(ub ->
                System.out.println("  " + ub.getName() + " -> " + ub.getResidentes().size() + " residentes"));

        // ── 7. Ubicaciones por tipo y especie de residentes ───────────────────
        System.out.println("\n===== CONSULTA 7: Ubicaciones 'House' con residentes 'Human' =====");
        List<Ubicacion> casas = consultas.ubicacionesPorTipoYEspecie("House", "Human");
        System.out.println("HQL (" + casas.size() + " resultados):");
        casas.stream().limit(5).forEach(ub -> System.out.println("  " + ub.getName()));

        List<Ubicacion> casasLambda = consultas.ubicacionesPorTipoYEspecieLambda("House", "Human");
        System.out.println("Lambda (" + casasLambda.size() + " resultados):");
        casasLambda.stream().limit(5).forEach(ub -> System.out.println("  " + ub.getName()));

        // Cerrar todo al finalizar
        consultas.cerrar();
        ConnectionUtil.shutdown();
        System.out.println("\n===== FIN =====");
    }
}
