package zw.ac.uz.dpdms.zoonotic.domain;

import jakarta.persistence.*;
import zw.ac.uz.dpdms.zoonotic.common.BaseIncident;

@Entity
@Table(name = "zoonotic_incidents", indexes = {
    @Index(name = "idx_zoo_ward", columnList = "ward"),
    @Index(name = "idx_zoo_status", columnList = "status"),
    @Index(name = "idx_zoo_occurred", columnList = "occurredAt")
})
public class ZoonoticIncident extends BaseIncident {

    @Column(nullable = false) private String pathogen;
    @Column(nullable = false) private String animalSpecies;
    @Column(nullable = false) private Integer confirmedHumanCases;
    @Column(nullable = false) private Integer confirmedAnimalCases;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Classification classification;

    public String getPathogen() { return pathogen; }
    public void setPathogen(String v) { this.pathogen = v; }
    public String getAnimalSpecies() { return animalSpecies; }
    public void setAnimalSpecies(String v) { this.animalSpecies = v; }
    public Integer getConfirmedHumanCases() { return confirmedHumanCases; }
    public void setConfirmedHumanCases(Integer v) { this.confirmedHumanCases = v; }
    public Integer getConfirmedAnimalCases() { return confirmedAnimalCases; }
    public void setConfirmedAnimalCases(Integer v) { this.confirmedAnimalCases = v; }
    public Classification getClassification() { return classification; }
    public void setClassification(Classification v) { this.classification = v; }
}