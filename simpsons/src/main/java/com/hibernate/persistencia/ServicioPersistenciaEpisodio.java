package main.java.com.hibernate.persistencia;

import main.java.com.hibernate.ConnectionUtil;
import main.java.model.Episodio;
import org.hibernate.Session;

import java.util.List;

public class ServicioPersistenciaEpisodio {

    // CREATE
    public void persistir(Episodio episodio) {
        try {
            Session session = ConnectionUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.persist(episodio);
            session.getTransaction().commit();
            session.close();
            System.out.println("Episodio guardado: " + episodio);
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudo guardar el episodio: " + e.getMessage());
        }
    }

    // READ por id
    public Episodio obtener(long id) {
        try {
            Session session = ConnectionUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Episodio ep = session.find(Episodio.class, id);
            session.close();
            if (ep == null) System.out.println("[ERROR] No existe episodio con id=" + id);
            return ep;
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudo obtener episodio id=" + id + ": " + e.getMessage());
            return null;
        }
    }

    // READ todos
    public List<Episodio> obtenerTodos() {
        try {
            Session session = ConnectionUtil.getSessionFactory().openSession();
            List<Episodio> lista = session.createQuery("from episodio", Episodio.class).list();
            session.close();
            return lista;
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudieron obtener los episodios: " + e.getMessage());
            return List.of();
        }
    }

    // UPDATE
    public void actualizar(Episodio episodio) {
        try {
            Session session = ConnectionUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.merge(episodio);
            session.getTransaction().commit();
            session.close();
            System.out.println("Episodio actualizado: " + episodio);
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudo actualizar el episodio: " + e.getMessage());
        }
    }

    // DELETE
    public void eliminar(long id) {
        try {
            Session session = ConnectionUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Episodio ep = session.find(Episodio.class, id);
            if (ep == null) {
                System.out.println("[ERROR] No existe episodio con id=" + id);
                session.close();
                return;
            }
            session.remove(ep);
            session.getTransaction().commit();
            session.close();
            System.out.println("Episodio eliminado con id=" + id);
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudo eliminar episodio id=" + id + ": " + e.getMessage());
        }
    }
}
