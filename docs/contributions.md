# DPDMS — Individual Contributions

**Course:** HCS201 / HCC201 / HAI201 — Object Oriented Programming  
**Institution:** University of Zimbabwe  
**Year:** 2026

This document describes the specific responsibilities undertaken by each group member during the development of the Rushinga Provincial Disaster Monitoring and Management System (DPDMS).

---
## Tinodaishe Mapangela

**Role:** Lead Architect & Backend Engineer

**Email:** tinodamapangela@gmail.com

**Contributions:**

- Designed the overall microservices architecture
- Implemented discovery-service (Eureka) and api-gateway
- Implemented auth-service with JWT (RS256) and BCrypt password hashing
- Implemented the alert-service (RabbitMQ consumer, Email + WhatsApp integration)
- Configured IntelliJ run configurations and documented the deployment process
- Set up the GitHub repository and CI-ready project structure

---

## Sisilisiwe Ndhlovu

**Role:** Backend Engineer — Hazard Services

**Contributions:**

- Implemented flood-service (entity, repository, DTOs, service, controller, scope guard)
- Implemented drought-service (entity, repository, DTOs, service, controller, scope guard)
- Wrote the IncidentAudit trail logic and status-transition validation
- Wrote unit tests for scope guards and workflow state machine
- Contributed to the security test suite

---

## Nokutenda Zvenyika

**Role:** Backend Engineer — Hazard Services & Dashboard

**Contributions:**

- Implemented fire-service (entity, repository, DTOs, service, controller, scope guard)
- Implemented zoonotic-disease-service (entity, repository, DTOs, service, controller, scope guard)
- Implemented dashboard-service (Feign clients, aggregation, approved-only filter)
- Integrated Recharts into the frontend dashboard
- Designed the dashboard API contract

---

## Blessing Berejena

**Role:** Backend Engineer — Mining Service & Reporting

**Contributions:**

- Implemented mining-accident-service (entity, repository, DTOs, service, controller, scope guard)
- Implemented report-service with PDF (OpenPDF), DOCX/XLSX (Apache POI), and CSV writers
- Designed the report filter contract (hazard, ward, district, date range, severity)
- Enforced approved-only rule in reports
- Wrote integration tests for report generation

---

## Elshama Chivete

**Role:** Frontend Engineer & UX

**Contributions:**

- Designed the purple/teal UI theme and shared CSS design system
- Implemented the React frontend (Vite, React Router, Axios)
- Built the Leaflet interactive map for approved incidents
- Built the Incidents, Pending Approvals, Reports, and New Incident pages
- Wrote the user manual and screenshots for the submission

---

## Summary

| Member | Focus Area |
|--------|-----------|| Tinodaishe Mapangela | Lead Architect & Backend Engineer |
| Sisilisiwe Ndhlovu | Backend Engineer — Hazard Services |
| Nokutenda Zvenyika | Backend Engineer — Hazard Services & Dashboard |
| Blessing Berejena | Backend Engineer — Mining Service & Reporting |
| Elshama Chivete | Frontend Engineer & UX |

---

## Acknowledgements

All members participated in:

- Requirements analysis and feature planning
- Code reviews of each other's work
- Testing the integrated system end-to-end
- Preparing the final submission package

---

© 2026 DPDMS Group — University of Zimbabwe