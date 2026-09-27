package zw.ac.uz.dpdms.drought.service;

import java.io.Serializable;
import java.util.UUID;

public record IncidentApprovedEvent(
    UUID incidentId, String hazard, String ward, String district,
    String severity, Double latitude, Double longitude,
    boolean floodThresholdExceeded, boolean fireActive, boolean casualties
) implements Serializable {}