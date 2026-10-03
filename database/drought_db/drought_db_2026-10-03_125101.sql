--
-- PostgreSQL database dump
--

\restrict VjbWVY2XRed3Oh1P08ymKpv1Ev8qELULdfsMLi5cjLBQ9p2zr57j8O0YwMcp1q0

-- Dumped from database version 18.6
-- Dumped by pg_dump version 18.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

DROP INDEX IF EXISTS public.idx_drought_ward;
DROP INDEX IF EXISTS public.idx_drought_status;
DROP INDEX IF EXISTS public.idx_drought_occurred;
DROP INDEX IF EXISTS public.idx_audit_incident;
ALTER TABLE IF EXISTS ONLY public.incident_audit DROP CONSTRAINT IF EXISTS incident_audit_pkey;
ALTER TABLE IF EXISTS ONLY public.drought_incidents DROP CONSTRAINT IF EXISTS drought_incidents_pkey;
DROP TABLE IF EXISTS public.incident_audit;
DROP TABLE IF EXISTS public.drought_incidents;
SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: drought_incidents; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.drought_incidents (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    district character varying(255) NOT NULL,
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    occurred_at timestamp(6) without time zone NOT NULL,
    province character varying(255) NOT NULL,
    rejection_reason character varying(1000),
    reporter_username character varying(255) NOT NULL,
    severity character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone,
    ward character varying(255) NOT NULL,
    consecutive_dry_days integer NOT NULL,
    crop_failure_pct double precision NOT NULL,
    livestock_mortality integer NOT NULL,
    people_water_shortage integer NOT NULL,
    rainfall_deficit_mm double precision NOT NULL,
    CONSTRAINT drought_incidents_severity_check CHECK (((severity)::text = ANY ((ARRAY['LOW'::character varying, 'MODERATE'::character varying, 'HIGH'::character varying, 'CRITICAL'::character varying])::text[]))),
    CONSTRAINT drought_incidents_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying, 'CORRECTION_REQUIRED'::character varying])::text[])))
);


--
-- Name: incident_audit; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.incident_audit (
    id uuid NOT NULL,
    action character varying(1000),
    actor_username character varying(255) NOT NULL,
    incident_id uuid NOT NULL,
    new_status character varying(255),
    previous_status character varying(255),
    "timestamp" timestamp(6) without time zone NOT NULL,
    CONSTRAINT incident_audit_new_status_check CHECK (((new_status)::text = ANY ((ARRAY['PENDING'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying, 'CORRECTION_REQUIRED'::character varying])::text[]))),
    CONSTRAINT incident_audit_previous_status_check CHECK (((previous_status)::text = ANY ((ARRAY['PENDING'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying, 'CORRECTION_REQUIRED'::character varying])::text[])))
);


--
-- Data for Name: drought_incidents; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.drought_incidents (id, created_at, district, latitude, longitude, occurred_at, province, rejection_reason, reporter_username, severity, status, updated_at, ward, consecutive_dry_days, crop_failure_pct, livestock_mortality, people_water_shortage, rainfall_deficit_mm) FROM stdin;
0a86d4fb-eb45-4735-895a-5e052a9e6078	2026-09-27 06:46:56.079497	Rushinga	-16.5	31.9	2025-01-15 10:00:00	Rushinga	\N	drought.recorder.wardA	HIGH	APPROVED	2026-09-27 06:48:46.225394	Ward A	45	60	30	800	120
\.


--
-- Data for Name: incident_audit; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.incident_audit (id, action, actor_username, incident_id, new_status, previous_status, "timestamp") FROM stdin;
32117c17-dcc0-45e0-bddb-b3892c27f493	Created by recorder	drought.recorder.wardA	0a86d4fb-eb45-4735-895a-5e052a9e6078	PENDING	\N	2026-09-27 06:46:56.148665
27b85fb6-6971-4daf-ac36-b0e4fa6496b3	Approved	drought.supervisor	0a86d4fb-eb45-4735-895a-5e052a9e6078	APPROVED	PENDING	2026-09-27 06:48:46.178264
\.


--
-- Name: drought_incidents drought_incidents_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.drought_incidents
    ADD CONSTRAINT drought_incidents_pkey PRIMARY KEY (id);


--
-- Name: incident_audit incident_audit_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.incident_audit
    ADD CONSTRAINT incident_audit_pkey PRIMARY KEY (id);


--
-- Name: idx_audit_incident; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_audit_incident ON public.incident_audit USING btree (incident_id);


--
-- Name: idx_drought_occurred; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_drought_occurred ON public.drought_incidents USING btree (occurred_at);


--
-- Name: idx_drought_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_drought_status ON public.drought_incidents USING btree (status);


--
-- Name: idx_drought_ward; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_drought_ward ON public.drought_incidents USING btree (ward);


--
-- PostgreSQL database dump complete
--

\unrestrict VjbWVY2XRed3Oh1P08ymKpv1Ev8qELULdfsMLi5cjLBQ9p2zr57j8O0YwMcp1q0

