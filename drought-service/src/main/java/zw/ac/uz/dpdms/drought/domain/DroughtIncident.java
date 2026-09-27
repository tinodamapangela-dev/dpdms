package zw.ac.uz.dpdms.drought.domain;

import jakarta.persistence.*;
import zw.ac.uz.dpdms.drought.common.BaseIncident;

@Entity
@Table(name = "drought_incidents", indexes = {
    @Index(name = "idx_drought_ward", columnList = "ward"),
    @Index(name = "idx_drought_status", columnList = "status"),
    @Index(name = "idx_drought_occurred", columnList = "occurredAt")
})
public class DroughtIncident extends BaseIncident {

    @Column(nullable = false) private Double rainfallDeficitMm;
    @Column(nullable = false) private Integer consecutiveDryDays;
    @Column(nullable = false) private Double cropFailurePct;
    @Column(nullable = false) private Integer peopleWaterShortage;
    @Column(nullable = false) private Integer livestockMortality;

    public Double getRainfallDeficitMm() { return rainfallDeficitMm; }
    public void setRainfallDeficitMm(Double v) { this.rainfallDeficitMm = v; }
    public Integer getConsecutiveDryDays() { return consecutiveDryDays; }
    public void setConsecutiveDryDays(Integer v) { this.consecutiveDryDays = v; }
    public Double getCropFailurePct() { return cropFailurePct; }
    public void setCropFailurePct(Double v) { this.cropFailurePct = v; }
    public Integer getPeopleWaterShortage() { return peopleWaterShortage; }
    public void setPeopleWaterShortage(Integer v) { this.peopleWaterShortage = v; }
    public Integer getLivestockMortality() { return livestockMortality; }
    public void setLivestockMortality(Integer v) { this.livestockMortality = v; }
}