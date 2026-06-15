package main.java.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "episodio")
@Table(name = "episodios")
public class Episodio {

    @Id
    private Long id;

    @Column
    private String name;

    @Column
    private Integer season;

    @Column(name = "episode_number")
    private Integer episodeNumber;

    @Column
    private LocalDate airdate;

    @Column(length = 2000)
    private String synopsis;

    @Column(name = "image_path")
    private String imagePath;

    @ManyToMany(mappedBy = "episodios", fetch = FetchType.LAZY)
    private Set<Personaje> personajes = new HashSet<>();

    public Episodio() {
        super();
    }

    
    public Long getId() { 
    	return id; }
    
    public void setId(Long id) { 
    	this.id = id; }

    public String getName() { 
    	return name; }
    
    public void setName(String name) { 
    	this.name = name; }

    public Integer getSeason() { 
    	return season; }
    
    public void setSeason(Integer season) { 
    	this.season = season; }

    public Integer getEpisodeNumber() { 
    	return episodeNumber; }
    
    public void setEpisodeNumber(Integer episodeNumber) { 
    	this.episodeNumber = episodeNumber; }

    public LocalDate getAirdate() { 
    	return airdate; }
    
    public void setAirdate(LocalDate airdate) { 
    	this.airdate = airdate; }

    public String getSynopsis() { 
    	return synopsis; }
    
    public void setSynopsis(String synopsis) { 
    	this.synopsis = synopsis; }

    public String getImagePath() { 
    	return imagePath; }
    
    public void setImagePath(String imagePath) { 
    	this.imagePath = imagePath; }

    public Set<Personaje> getPersonajes() { 
    	return personajes; }

    @Override
    public String toString() {
        return "Episodio{id=" + id + ", T" + season + "E" + episodeNumber + ", name='" + name + "', airdate=" + airdate + "}";
    }
}
