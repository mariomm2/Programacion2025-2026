package main.java.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "personaje")
@Table(name = "personajes")
public class Personaje {

    @Id
    private Long id;

    @Column
    private String name;

    @Column
    private Integer age;

    @Column
    private LocalDate birthdate;

    @Column
    private String gender;

    @Column(length = 1000)
    private String occupation;

    @Column
    private String status; 

    @Column(name = "portrait_path")
    private String portraitPath;

    @Column
    private String species = "Human";

    // Frases del personaje -> tabla separada personaje_frases
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "personaje_frases", joinColumns = @JoinColumn(name = "personaje_id"))
    
    @Column(name = "frase", length = 1000)
        private List<String> phrases = new ArrayList<>();

 
    @ManyToOne(optional = true)
    @JoinColumn(name = "primera_aparicion_id", foreignKey = @ForeignKey(name = "fk_primera_aparicion"))
    private Episodio primeraAparicion;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "apariciones", joinColumns = @JoinColumn(name = "personaje_id"),
            inverseJoinColumns = @JoinColumn(name = "episodio_id"))
    
    private Set<Episodio> episodios = new HashSet<>();

    @ManyToMany(mappedBy = "residentes", fetch = FetchType.LAZY)
    private Set<Ubicacion> ubicaciones = new HashSet<>();

    public Personaje() {
        super();
    }

    // Método helper: mantiene los dos lados de la relación sincronizados
    public void agregarEpisodio(Episodio episodio) {
        episodios.add(episodio);
        episodio.getPersonajes().add(this);
    }

    public Long getId() { 
    	return id; }
    
    public void setId(Long id) { 
    	this.id = id; }

    public String getName() { 
    	return name; }
    
    public void setName(String name) { 
    	this.name = name; }

    public Integer getAge() { 
    	return age; }
    
    public void setAge(Integer age) { 
    	this.age = age; }

    public LocalDate getBirthdate() { 
    	return birthdate; }
    
    public void setBirthdate(LocalDate birthdate) { 
    	this.birthdate = birthdate; }

    public String getGender() { 
    	return gender; }
    
    public void setGender(String gender) { 
    	this.gender = gender; }

    public String getOccupation() { 
    	return occupation; }
    
    public void setOccupation(String occupation) { 
    	this.occupation = occupation; }

    public String getStatus() { 
    	return status; }
    
    public void setStatus(String status) { 
    	this.status = status; }

    public String getPortraitPath() { 
    	return portraitPath; }
    
    public void setPortraitPath(String portraitPath) { 
    	this.portraitPath = portraitPath; }

    public String getSpecies() { 
    	return species; }
    
    public void setSpecies(String species) { 
    	this.species = species; }

    public List<String> getPhrases() { 
    	return phrases; }

    public Episodio getPrimeraAparicion() { 
    	return primeraAparicion; }
    
    public void setPrimeraAparicion(Episodio primeraAparicion) { 
    	this.primeraAparicion = primeraAparicion; }

    public Set<Episodio> getEpisodios() { 
    	return episodios; }

    public Set<Ubicacion> getUbicaciones() { 
    	return ubicaciones; }

    public boolean isVivo() { 
    	return "Alive".equalsIgnoreCase(status); }

    @Override
    public String toString() {
        return "Personaje{id=" + id + ", name='" + name + "', gender='" + gender + "', status='" + status + "', episodios=" + episodios.size() + "}";
    }
}
