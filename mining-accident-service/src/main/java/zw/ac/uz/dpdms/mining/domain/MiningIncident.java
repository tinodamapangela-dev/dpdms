package zw.ac.uz.dpdms.mining.domain;

import jakarta.persistence.*;
import zw.ac.uz.dpdms.mining.common.BaseIncident;

@Entity
@Table(name = "mining_incidents", indexes = {
    @Index(name = "idx_mining_ward", columnList = "ward"),
    @Index(name = "idx_mining_status", columnList = "status"),
    @Index(name = "idx_mining_occurred", columnList = "occurredAt")
})
public class MiningIncident extends BaseIncident {

    @Column(nullable = false) private String mineName;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private MineType mineType;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private AccidentType accidentType;

    @Column(nullable = false) private Integer trappedMiners;
    @Column(nullable = false) private Integer injuredMiners;
    @Column(nullable = false) private Integer fatalities;
    @Column(nullable = false) private Boolean rescueOngoing;

    public String getMineName() { return mineName; }
    public void setMineName(String v) { this.mineName = v; }
    public MineType getMineType() { return mineType; }
    public void setMineType(MineType v) { this.mineType = v; }
    public AccidentType getAccidentType() { return accidentType; }
    public void setAccidentType(AccidentType v) { this.accidentType = v; }
    public Integer getTrappedMiners() { return trappedMiners; }
    public void setTrappedMiners(Integer v) { this.trappedMiners = v; }
    public Integer getInjuredMiners() { return injuredMiners; }
    public void setInjuredMiners(Integer v) { this.injuredMiners = v; }
    public Integer getFatalities() { return fatalities; }
    public void setFatalities(Integer v) { this.fatalities = v; }
    public Boolean getRescueOngoing() { return rescueOngoing; }
    public void setRescueOngoing(Boolean v) { this.rescueOngoing = v; }
}