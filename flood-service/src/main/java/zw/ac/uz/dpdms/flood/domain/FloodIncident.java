package zw.ac.uz.dpdms.flood.domain;

import jakarta.persistence.*;
import zw.ac.uz.dpdms.flood.common.BaseIncident;

@Entity
@Table(name = "flood_incidents", indexes = {
    @Index(name = "idx_flood_ward", columnList = "ward"),
    @Index(name = "idx_flood_status", columnList = "status"),
    @Index(name = "idx_flood_occurred", columnList = "occurredAt")
})
public class FloodIncident extends BaseIncident {

    @Column(nullable = false) private Double peakWaterLevelM;
    @Column(nullable = false) private String riverBasin;
    @Column(nullable = false) private Integer householdsDisplaced;
    @Column(nullable = false) private Double areaFloodedHectares;
    @Column(nullable = false) private Integer inundationDays;

    public Double getPeakWaterLevelM() { return peakWaterLevelM; }
    public void setPeakWaterLevelM(Double v) { this.peakWaterLevelM = v; }
    public String getRiverBasin() { return riverBasin; }
    public void setRiverBasin(String v) { this.riverBasin = v; }
    public Integer getHouseholdsDisplaced() { return householdsDisplaced; }
    public void setHouseholdsDisplaced(Integer v) { this.householdsDisplaced = v; }
    public Double getAreaFloodedHectares() { return areaFloodedHectares; }
    public void setAreaFloodedHectares(Double v) { this.areaFloodedHectares = v; }
    public Integer getInundationDays() { return inundationDays; }
    public void setInundationDays(Integer v) { this.inundationDays = v; }
}