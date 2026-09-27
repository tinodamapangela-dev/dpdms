package zw.ac.uz.dpdms.fire.domain;

import jakarta.persistence.*;
import zw.ac.uz.dpdms.fire.common.BaseIncident;

@Entity
@Table(name = "fire_incidents", indexes = {
    @Index(name = "idx_fire_ward", columnList = "ward"),
    @Index(name = "idx_fire_status", columnList = "status"),
    @Index(name = "idx_fire_occurred", columnList = "occurredAt")
})
public class FireIncident extends BaseIncident {

    @Column(nullable = false) private Double areaBurnedHa;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private SuspectedCause suspectedCause;

    @Column(nullable = false) private Integer injuries;
    @Column(nullable = false) private Integer fatalities;
    @Column(nullable = false) private Integer structuresDestroyed;
    @Column(nullable = false) private Boolean active;

    public Double getAreaBurnedHa() { return areaBurnedHa; }
    public void setAreaBurnedHa(Double v) { this.areaBurnedHa = v; }
    public SuspectedCause getSuspectedCause() { return suspectedCause; }
    public void setSuspectedCause(SuspectedCause v) { this.suspectedCause = v; }
    public Integer getInjuries() { return injuries; }
    public void setInjuries(Integer v) { this.injuries = v; }
    public Integer getFatalities() { return fatalities; }
    public void setFatalities(Integer v) { this.fatalities = v; }
    public Integer getStructuresDestroyed() { return structuresDestroyed; }
    public void setStructuresDestroyed(Integer v) { this.structuresDestroyed = v; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean v) { this.active = v; }
}