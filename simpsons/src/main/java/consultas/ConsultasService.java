package main.java.consultas;

import jakarta.persistence.criteria.*;
import main.java.com.hibernate.ConnectionUtil;
import main.java.model.Episodio;
import main.java.model.Personaje;
import main.java.model.Ubicacion;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Las 7 consultas del boletín, usando los 3 estilos de los apuntes:
 *
 *  A) Selección SIMPLE      → HQL sin parámetros (como: "from Personaje")
 *  B) Selección PARAMETRIZADA → HQL con setParameter (como: "where p.gender = :g")
 *  C) Selección CONDICIONAL  → CriteriaBuilder con cb.and(condicion1, condicion2)
 *
 * Cada consulta tiene también su versión con expresiones lambda/Stream.
 * La sesión se abre una sola vez en el constructor y se cierra con cerrar().
 */
public class ConsultasService {

    private final Session session;

    public ConsultasService() {
        this.session = ConnectionUtil.getSessionFactory().openSession();
    }

    public void cerrar() {
        session.close();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CONSULTA 1: Personaje más recurrente de un género
    //             → Estilo B: PARAMETRIZADA (HQL + setParameter)
    // ══════════════════════════════════════════════════════════════════════════
    public Personaje personajeMasRecurrentePorGenero(String genero) {
        Query<Personaje> query = session.createQuery(
                "from personaje p " +
                "where p.gender = :genero " +
                "order by size(p.episodios) desc", Personaje.class);
        query.setParameter("genero", genero);
        query.setMaxResults(1);
        return query.uniqueResultOptional().orElse(null);
    }

    // Versión lambda: trae todos del género y elige el de mayor colección
    public Personaje personajeMasRecurrentePorGeneroLambda(String genero) {
        Query<Personaje> query = session.createQuery(
                "from personaje p where p.gender = :genero", Personaje.class);
        query.setParameter("genero", genero);

        return query.list().stream()
                .max(Comparator.comparingInt(p -> p.getEpisodios().size()))
                .orElse(null);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CONSULTA 2: Último episodio (por fecha) en que aparece un personaje
    //             → Estilo B: PARAMETRIZADA con JOIN
    // ══════════════════════════════════════════════════════════════════════════
    public Episodio ultimoEpisodioDePersonaje(String nombre) {
        Query<Episodio> query = session.createQuery(
                "select e from personaje p join p.episodios e " +
                "where p.name = :nombre and e.airdate is not null " +
                "order by e.airdate desc", Episodio.class);
        query.setParameter("nombre", nombre);
        query.setMaxResults(1);
        return query.uniqueResultOptional().orElse(null);
    }

    // Versión lambda: encuentra el personaje y filtra sus episodios en Java
    public Episodio ultimoEpisodioDePersonajeLambda(String nombre) {
        Query<Personaje> query = session.createQuery(
                "from personaje p where p.name = :nombre", Personaje.class);
        query.setParameter("nombre", nombre);
        Personaje p = query.uniqueResultOptional().orElse(null);
        if (p == null) return null;

        return p.getEpisodios().stream()
                .filter(e -> e.getAirdate() != null)
                .max(Comparator.comparing(Episodio::getAirdate))
                .orElse(null);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CONSULTA 3: Número medio de personajes vivos por episodio y por temporada
    //             → Estilo B (HQL con GROUP BY) + lambda para calcular la media
    // ══════════════════════════════════════════════════════════════════════════

    // Media global: la query da los conteos por episodio, la lambda calcula la media
    public double mediaPersonajesVivosPorEpisodio() {
        Query<Long> query = session.createQuery(
                "select count(p) from episodio e join e.personajes p " +
                "where p.status = :estado group by e.id", Long.class);
        query.setParameter("estado", "Alive");

        List<Long> conteos = query.list();
        return conteos.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
    }

    // Media por temporada: la lambda agrupa los conteos por número de temporada
    public Map<Integer, Double> mediaPersonajesVivosPorTemporada() {
        Query<Object[]> query = session.createQuery(
                "select e.season, count(p) from episodio e join e.personajes p " +
                "where p.status = :estado group by e.season, e.id", Object[].class);
        query.setParameter("estado", "Alive");

        return query.list().stream()
                .collect(Collectors.groupingBy(
                        fila -> (Integer) fila[0],
                        Collectors.averagingLong(fila -> (Long) fila[1])
                ));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CONSULTA 4: Temporadas ordenadas por nº de personajes DISTINTOS
    //             → Estilo A: SIMPLE (HQL con GROUP BY, sin parámetros)
    // ══════════════════════════════════════════════════════════════════════════
    public List<Object[]> temporadasPorPersonajesDistintos() {
        Query<Object[]> query = session.createQuery(
                "select e.season, count(distinct p) " +
                "from episodio e join e.personajes p " +
                "group by e.season " +
                "order by count(distinct p) desc", Object[].class);
        return query.list();
    }

    // Versión lambda: carga todo y lo ordena en Java
    public Map<Integer, Long> temporadasPorPersonajesDistintosLambda() {
        List<Episodio> episodios = session.createQuery("from episodio", Episodio.class).list();

        return episodios.stream()
                .collect(Collectors.groupingBy(
                        Episodio::getSeason,
                        Collectors.mapping(
                                e -> e.getPersonajes(),
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        listas -> listas.stream()
                                                .flatMap(java.util.Set::stream)
                                                .distinct()
                                                .count()
                                )
                        )
                ));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CONSULTA 5: Personajes cuyo nombre empieza por {x} y debut posterior a {fecha}
    //             → Estilo C: CONDICIONAL con CriteriaBuilder + cb.and(...)
    // ══════════════════════════════════════════════════════════════════════════
    public List<Personaje> personajesPorInicialYFecha(String x, LocalDate fecha) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Personaje> cquery = cb.createQuery(Personaje.class);
        Root<Personaje> root = cquery.from(Personaje.class);

        cquery.select(root);
        cquery.where(
                cb.and(
                        cb.like(root.get("name"), x + "%"),
                        cb.greaterThan(root.get("primeraAparicion").get("airdate"), fecha)
                )
        );

        return session.createQuery(cquery).getResultList();
    }

    // Versión lambda: usa Stream.filter() con dos condiciones encadenadas
    public List<Personaje> personajesPorInicialYFechaLambda(String x, LocalDate fecha) {
        List<Personaje> todos = session.createQuery("from personaje", Personaje.class).list();

        return todos.stream()
                .filter(p -> p.getName() != null && p.getName().startsWith(x))
                .filter(p -> p.getPrimeraAparicion() != null
                        && p.getPrimeraAparicion().getAirdate() != null
                        && p.getPrimeraAparicion().getAirdate().isAfter(fecha))
                .collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CONSULTA 6: Ubicaciones ordenadas por nº de residentes (descendente)
    //             → Estilo A: SIMPLE con CriteriaQuery + orderBy(cb.size(...))
    // ══════════════════════════════════════════════════════════════════════════
    public List<Ubicacion> ubicacionesPorResidentesDesc() {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Ubicacion> cquery = cb.createQuery(Ubicacion.class);
        Root<Ubicacion> root = cquery.from(Ubicacion.class);

        cquery.select(root);
        cquery.orderBy(cb.desc(cb.size(root.get("residentes"))));

        return session.createQuery(cquery).getResultList();
    }

    // Versión lambda
    public List<Ubicacion> ubicacionesPorResidentesDescLambda() {
        List<Ubicacion> todas = session.createQuery("from ubicacion", Ubicacion.class).list();

        return todas.stream()
                .sorted(Comparator.comparingInt((Ubicacion u) -> u.getResidentes().size()).reversed())
                .collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CONSULTA 7: Ubicaciones filtradas por tipo y especie de residentes
    //             → Estilo C: CONDICIONAL con CriteriaBuilder + Join + cb.and(...)
    // ══════════════════════════════════════════════════════════════════════════
    public List<Ubicacion> ubicacionesPorTipoYEspecie(String tipo, String especie) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Ubicacion> cquery = cb.createQuery(Ubicacion.class);
        Root<Ubicacion> root = cquery.from(Ubicacion.class);
        Join<Ubicacion, Personaje> residentes = root.join("residentes");

        cquery.select(root).distinct(true);
        cquery.where(
                cb.and(
                        cb.equal(root.get("type"), tipo),
                        cb.equal(residentes.get("species"), especie)
                )
        );

        return session.createQuery(cquery).getResultList();
    }

    // Versión lambda
    public List<Ubicacion> ubicacionesPorTipoYEspecieLambda(String tipo, String especie) {
        List<Ubicacion> todas = session.createQuery("from ubicacion", Ubicacion.class).list();

        return todas.stream()
                .filter(u -> tipo.equals(u.getType()))
                .filter(u -> u.getResidentes().stream()
                        .anyMatch(p -> especie.equals(p.getSpecies())))
                .collect(Collectors.toList());
    }
}
