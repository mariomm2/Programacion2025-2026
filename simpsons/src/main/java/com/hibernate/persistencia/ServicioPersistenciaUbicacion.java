package main.java.com.hibernate.persistencia;

import main.java.com.hibernate.ConnectionUtil;
import main.java.model.Ubicacion;
import org.hibernate.Session;

import java.util.List;

public class ServicioPersistenciaUbicacion {

    // CREATE
    public void persistir(Ubicacion ubicacion) {
        try {
            Session session = ConnectionUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.persist(ubicacion);
            session.getTransaction().commit();
            session.close();
            System.out.println("Ubicacion guardada: " + ubicacion);
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudo guardar la ubicacion: " + e.getMessage());
        }
    }

    // READ 1
    public Ubicacion obtener(long id) {
        try {
            Session session = ConnectionUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Ubicacion u = session.find(Ubicacion.class, id);
            session.close();
            if (u == null) System.out.println("[ERROR] No existe ubicacion con id=" + id);
            return u;
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudo obtener ubicacion id=" + id + ": " + e.getMessage());
            return null;
        }
    }

    // READ 2
    public List<Ubicacion> obtenerTodas() {
        try {
            Session session = ConnectionUtil.getSessionFactory().openSession();
            List<Ubicacion> lista = session.createQuery("from ubicacion", Ubicacion.class).list();
            session.close();
            return lista;
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudieron obtener las ubicaciones: " + e.getMessage());
            return List.of();
        }
    }

    // UPDATE
    public void actualizar(Ubicacion ubicacion) {
        try {
            Session session = ConnectionUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.merge(ubicacion);
            session.getTransaction().commit();
            session.close();
            System.out.println("Ubicacion actualizada: " + ubicacion);
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudo actualizar la ubicacion: " + e.getMessage());
        }
    }

    // DELETE
    public void eliminar(long id) {
        try {
            Session session = ConnectionUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Ubicacion u = session.find(Ubicacion.class, id);
            if (u == null) {
                System.out.println("[ERROR] No existe ubicacion con id=" + id);
                session.close();
                return;
            }
            session.remove(u);
            session.getTransaction().commit();
            session.close();
            System.out.println("Ubicacion eliminada con id=" + id);
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudo eliminar ubicacion id=" + id + ": " + e.getMessage());
        }
    }
}
