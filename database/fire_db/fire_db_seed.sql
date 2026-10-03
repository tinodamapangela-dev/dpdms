--
-- PostgreSQL database dump
--

\restrict EMZxDe6XF1lLX6804pFoUXLY7mhULo7RzK3fjXStle8tNJgBCpEd0R5cWIVzBgT

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

DROP INDEX IF EXISTS public.idx_fire_ward;
DROP INDEX IF EXISTS public.idx_fire_status;
DROP INDEX IF EXISTS public.idx_fire_occurred;
DROP INDEX IF EXISTS public.idx_audit_incident;
ALTER TABLE IF EXISTS ONLY public.incident_audit DROP CONSTRAINT IF EXISTS incident_audit_pkey;
ALTER TABLE IF EXISTS ONLY public.fire_incidents DROP CONSTRAINT IF EXISTS fire_incidents_pkey;
DROP TABLE IF EXISTS public.incident_audit;
DROP TABLE IF EXISTS public.fire_incidents;
SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: fire_incidents; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fire_incidents (
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
    active boolean NOT NULL,
    area_burned_ha double precision NOT NULL,
    fatalities integer NOT NULL,
    injuries integer NOT NULL,
    structures_destroyed integer NOT NULL,
    suspected_cause character varying(255) NOT NULL,
    CONSTRAINT fire_incidents_severity_check CHECK (((severity)::text = ANY ((ARRAY['LOW'::character varying, 'MODERATE'::character varying, 'HIGH'::character varying, 'CRITICAL'::character varying])::text[]))),
    CONSTRAINT fire_incidents_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying, 'CORRECTION_REQUIRED'::character varying])::text[]))),
    CONSTRAINT fire_incidents_suspected_cause_check CHECK (((suspected_cause)::text = ANY ((ARRAY['NATURAL'::character varying, 'ACCIDENTAL'::character varying, 'DELIBERATE'::character varying])::text[])))
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
-- Data for Name: fire_incidents; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.fire_incidents (id, created_at, district, latitude, longitude, occurred_at, province, rejection_reason, reporter_username, severity, status, updated_at, ward, active, area_burned_ha, fatalities, injuries, structures_destroyed, suspected_cause) FROM stdin;
\.


--
-- Data for Name: incident_audit; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.incident_audit (id, action, actor_username, incident_id, new_status, previous_status, "timestamp") FROM stdin;
\.


--
-- Name: fire_incidents fire_incidents_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fire_incidents
    ADD CONSTRAINT fire_incidents_pkey PRIMARY KEY (id);


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
-- Name: idx_fire_occurred; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_fire_occurred ON public.fire_incidents USING btree (occurred_at);


--
-- Name: idx_fire_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_fire_status ON public.fire_incidents USING btree (status);


--
-- Name: idx_fire_ward; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_fire_ward ON public.fire_incidents USING btree (ward);


--
-- PostgreSQL database dump complete
--

\unrestrict EMZxDe6XF1lLX6804pFoUXLY7mhULo7RzK3fjXStle8tNJgBCpEd0R5cWIVzBgT

