package main.java.com.hibernate.persistencia;

import main.java.com.hibernate.ConnectionUtil;
import main.java.model.Personaje;
import org.hibernate.Session;

import java.util.List;

public class ServicioPersistenciaPersonaje {

    // CREATE
    public void persistir(Personaje personaje) {
        try {
            Session session = ConnectionUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.persist(personaje);
            session.getTransaction().commit();
            session.close();
            System.out.println("Personaje guardado: " + personaje);
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudo guardar el personaje: " + e.getMessage());
        }
    }

    // READ por id
    public Personaje obtener(long id) {
        try {
            Session session = ConnectionUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Personaje p = session.find(Personaje.class, id);
            session.close();
            if (p == null) System.out.println("[ERROR] No existe personaje con id=" + id);
            return p;
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudo obtener personaje id=" + id + ": " + e.getMessage());
            return null;
        }
    }

    // READ todos
    public List<Personaje> obtenerTodos() {
        try {
            Session session = ConnectionUtil.getSessionFactory().openSession();
            List<Personaje> lista = session.createQuery("from personaje", Personaje.class).list();
            session.close();
            return lista;
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudieron obtener los personajes: " + e.getMessage());
            return List.of();
        }
    }

    // UPDATE
    public void actualizar(Personaje personaje) {
        try {
            Session session = ConnectionUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.merge(personaje);
            session.getTransaction().commit();
            session.close();
            System.out.println("Personaje actualizado: " + personaje);
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudo actualizar el personaje: " + e.getMessage());
        }
    }

    // DELETE
    public void eliminar(long id) {
        try {
            Session session = ConnectionUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Personaje p = session.find(Personaje.class, id);
            if (p == null) {
                System.out.println("[ERROR] No existe personaje con id=" + id);
                session.close();
                return;
            }
            session.remove(p);
            session.getTransaction().commit();
            session.close();
            System.out.println("Personaje eliminado con id=" + id);
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudo eliminar personaje id=" + id + ": " + e.getMessage());
        }
    }
}
