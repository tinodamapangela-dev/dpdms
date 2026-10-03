--
-- PostgreSQL database dump
--

\restrict qrG8s7FtNMyoqN0q4PR13WNGGcgA7kLTziMpqcKgGB9UIj2M1lenwjfCBBgraDZ

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

DROP INDEX IF EXISTS public.idx_flood_ward;
DROP INDEX IF EXISTS public.idx_flood_status;
DROP INDEX IF EXISTS public.idx_flood_occurred;
DROP INDEX IF EXISTS public.idx_audit_incident;
ALTER TABLE IF EXISTS ONLY public.incident_audit DROP CONSTRAINT IF EXISTS incident_audit_pkey;
ALTER TABLE IF EXISTS ONLY public.flood_incidents DROP CONSTRAINT IF EXISTS flood_incidents_pkey;
DROP TABLE IF EXISTS public.incident_audit;
DROP TABLE IF EXISTS public.flood_incidents;
SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: flood_incidents; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.flood_incidents (
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
    area_flooded_hectares double precision NOT NULL,
    households_displaced integer NOT NULL,
    inundation_days integer NOT NULL,
    peak_water_levelm double precision NOT NULL,
    river_basin character varying(255) NOT NULL,
    CONSTRAINT flood_incidents_severity_check CHECK (((severity)::text = ANY ((ARRAY['LOW'::character varying, 'MODERATE'::character varying, 'HIGH'::character varying, 'CRITICAL'::character varying])::text[]))),
    CONSTRAINT flood_incidents_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying, 'CORRECTION_REQUIRED'::character varying])::text[])))
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
-- Data for Name: flood_incidents; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.flood_incidents (id, created_at, district, latitude, longitude, occurred_at, province, rejection_reason, reporter_username, severity, status, updated_at, ward, area_flooded_hectares, households_displaced, inundation_days, peak_water_levelm, river_basin) FROM stdin;
5b81ac75-2c9a-46f0-8bb2-91a2322d056c	2026-09-27 16:57:24.197386	Rushinga	-16.6	31.9	2026-09-27 14:57:00	Rushinga	\N	flood.recorder.wardA	HIGH	APPROVED	2026-09-27 17:30:58.250233	Ward A	3	10	3	10000	10000
e3b2d42e-fa83-454b-a3e5-1b2eb383832c	2026-09-27 17:19:43.479965	Rushinga	-16.6	31.9	2026-09-27 15:19:00	Rushinga	\N	flood.recorder.wardA	HIGH	APPROVED	2026-09-27 17:31:01.87442	Ward A	1	1	1	1	1
aa32ee4c-c80d-40da-8a57-fd37f17ba30d	2026-09-28 10:39:53.962311	Rushinga	-16.6	31.9	2026-09-28 08:39:00	Rushinga	\N	flood.recorder.wardA	HIGH	PENDING	2026-09-28 10:39:53.962311	Ward A	1	1	1	2	e
\.


--
-- Data for Name: incident_audit; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.incident_audit (id, action, actor_username, incident_id, new_status, previous_status, "timestamp") FROM stdin;
e2cca5f4-21dc-4a1e-b93a-3bee63031dac	Created by recorder	flood.recorder.wardA	5b81ac75-2c9a-46f0-8bb2-91a2322d056c	PENDING	\N	2026-09-27 16:57:24.489371
e76c9b8a-3c85-4b04-a292-be6686fce0b5	Created by recorder	flood.recorder.wardA	e3b2d42e-fa83-454b-a3e5-1b2eb383832c	PENDING	\N	2026-09-27 17:19:43.509988
101ad4af-62f3-4fc6-b23e-65ce07570a0c	Approved	flood.supervisor	5b81ac75-2c9a-46f0-8bb2-91a2322d056c	APPROVED	PENDING	2026-09-27 17:30:55.984672
2dcde9a8-17ed-472f-9cce-e6090d464960	Approved	flood.supervisor	e3b2d42e-fa83-454b-a3e5-1b2eb383832c	APPROVED	PENDING	2026-09-27 17:31:01.865644
d2af8cb4-7a46-4fca-b475-818ec7335f76	Created by recorder	flood.recorder.wardA	aa32ee4c-c80d-40da-8a57-fd37f17ba30d	PENDING	\N	2026-09-28 10:39:54.027993
\.


--
-- Name: flood_incidents flood_incidents_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.flood_incidents
    ADD CONSTRAINT flood_incidents_pkey PRIMARY KEY (id);


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
-- Name: idx_flood_occurred; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_flood_occurred ON public.flood_incidents USING btree (occurred_at);


--
-- Name: idx_flood_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_flood_status ON public.flood_incidents USING btree (status);


--
-- Name: idx_flood_ward; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_flood_ward ON public.flood_incidents USING btree (ward);


--
-- PostgreSQL database dump complete
--

\unrestrict qrG8s7FtNMyoqN0q4PR13WNGGcgA7kLTziMpqcKgGB9UIj2M1lenwjfCBBgraDZ

