--
-- PostgreSQL database dump
--

\restrict d5rAUjoBwAoXTussdNYqqlnXyvwYRsAjQ0NofDbS5tbbnk5UVM4HGE8P3x0aT0T

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

DROP INDEX IF EXISTS public.idx_mining_ward;
DROP INDEX IF EXISTS public.idx_mining_status;
DROP INDEX IF EXISTS public.idx_mining_occurred;
DROP INDEX IF EXISTS public.idx_audit_incident;
ALTER TABLE IF EXISTS ONLY public.mining_incidents DROP CONSTRAINT IF EXISTS mining_incidents_pkey;
ALTER TABLE IF EXISTS ONLY public.incident_audit DROP CONSTRAINT IF EXISTS incident_audit_pkey;
DROP TABLE IF EXISTS public.mining_incidents;
DROP TABLE IF EXISTS public.incident_audit;
SET default_tablespace = '';

SET default_table_access_method = heap;

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
-- Name: mining_incidents; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.mining_incidents (
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
    accident_type character varying(255) NOT NULL,
    fatalities integer NOT NULL,
    injured_miners integer NOT NULL,
    mine_name character varying(255) NOT NULL,
    mine_type character varying(255) NOT NULL,
    rescue_ongoing boolean NOT NULL,
    trapped_miners integer NOT NULL,
    CONSTRAINT mining_incidents_accident_type_check CHECK (((accident_type)::text = ANY ((ARRAY['COLLAPSE'::character varying, 'GAS_EXPLOSION'::character varying, 'FLOODING'::character varying, 'FALL_OF_GROUND'::character varying])::text[]))),
    CONSTRAINT mining_incidents_mine_type_check CHECK (((mine_type)::text = ANY ((ARRAY['FORMAL'::character varying, 'ARTISANAL'::character varying])::text[]))),
    CONSTRAINT mining_incidents_severity_check CHECK (((severity)::text = ANY ((ARRAY['LOW'::character varying, 'MODERATE'::character varying, 'HIGH'::character varying, 'CRITICAL'::character varying])::text[]))),
    CONSTRAINT mining_incidents_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying, 'CORRECTION_REQUIRED'::character varying])::text[])))
);


--
-- Data for Name: incident_audit; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.incident_audit (id, action, actor_username, incident_id, new_status, previous_status, "timestamp") FROM stdin;
\.


--
-- Data for Name: mining_incidents; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.mining_incidents (id, created_at, district, latitude, longitude, occurred_at, province, rejection_reason, reporter_username, severity, status, updated_at, ward, accident_type, fatalities, injured_miners, mine_name, mine_type, rescue_ongoing, trapped_miners) FROM stdin;
\.


--
-- Name: incident_audit incident_audit_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.incident_audit
    ADD CONSTRAINT incident_audit_pkey PRIMARY KEY (id);


--
-- Name: mining_incidents mining_incidents_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mining_incidents
    ADD CONSTRAINT mining_incidents_pkey PRIMARY KEY (id);


--
-- Name: idx_audit_incident; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_audit_incident ON public.incident_audit USING btree (incident_id);


--
-- Name: idx_mining_occurred; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_mining_occurred ON public.mining_incidents USING btree (occurred_at);


--
-- Name: idx_mining_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_mining_status ON public.mining_incidents USING btree (status);


--
-- Name: idx_mining_ward; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_mining_ward ON public.mining_incidents USING btree (ward);


--
-- PostgreSQL database dump complete
--

\unrestrict d5rAUjoBwAoXTussdNYqqlnXyvwYRsAjQ0NofDbS5tbbnk5UVM4HGE8P3x0aT0T

