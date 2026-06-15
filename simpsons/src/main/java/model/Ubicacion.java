package main.java.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "ubicacion")
@Table(name = "ubicaciones")
public class Ubicacion {

    @Id
    private Long id;

    @Column
    private String name;

    @Column
    private String type;

    @Column
    private String town;

    @Column(length = 2000)
    private String description;

    @Column(name = "image_path")
    private String imagePath;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "residencias", joinColumns = @JoinColumn(name = "ubicacion_id"),
            inverseJoinColumns = @JoinColumn(name = "personaje_id"))
    
    private Set<Personaje> residentes = new HashSet<>();

    public Ubicacion() {
        super();
    }

    // Método helper: mantiene los dos lados sincronizados
    public void agregarResidente(Personaje personaje) {
        residentes.add(personaje);
        personaje.getUbicaciones().add(this);
    }


    public Long getId() { 
    	return id; }
    
    public void setId(Long id) { 
    	this.id = id; }

    public String getName() { 
    	return name; }
    
    public void setName(String name) { 
    	this.name = name; }

    public String getType() { 
    	return type; }
    
    public void setType(String type) { 
    	this.type = type; }

    public String getTown() { 
    	return town; }
    
    public void setTown(String town) { 
    	this.town = town; }

    public String getDescription() { 
    	return description; }
    
    public void setDescription(String description) { 
    	this.description = description; }

    public String getImagePath() { 
    	return imagePath; }
    
    public void setImagePath(String imagePath) { 
    	this.imagePath = imagePath; }

    public Set<Personaje> getResidentes() { 
    	return residentes; }

    @Override
    public String toString() {
        return "Ubicacion {id=" + id + ", name='" + name + "', type='" + type + "', residentes=" + residentes.size() + "}";
    }
}
