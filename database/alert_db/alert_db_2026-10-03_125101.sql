--
-- PostgreSQL database dump
--

\restrict GYKbIkmWDJDEsPmUUwQvkYIuxH4y8eBvvjgFkqMDaLppcBB4njTi65yagRRTPi9

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

DROP INDEX IF EXISTS public.idx_alert_incident;
ALTER TABLE IF EXISTS ONLY public.alert_logs DROP CONSTRAINT IF EXISTS alert_logs_pkey;
DROP TABLE IF EXISTS public.alert_logs;
SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: alert_logs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.alert_logs (
    id uuid NOT NULL,
    channel character varying(255) NOT NULL,
    delivery_status character varying(255) NOT NULL,
    error_message character varying(1000),
    incident_id uuid NOT NULL,
    message character varying(1000),
    recipient character varying(255) NOT NULL,
    "timestamp" timestamp(6) without time zone NOT NULL
);


--
-- Data for Name: alert_logs; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.alert_logs (id, channel, delivery_status, error_message, incident_id, message, recipient, "timestamp") FROM stdin;
08cdbf92-9804-4407-8ef8-bd1fd1c75c75	EMAIL	FAILED	Authentication failed	5b81ac75-2c9a-46f0-8bb2-91a2322d056c	FLOOD THRESHOLD EXCEEDED at Ward A	tinodamapangela@gmail.com	2026-09-27 17:30:58.488039
7d226bcb-ef22-4aeb-9da3-f676e8f4fdbb	WHATSAPP	FAILED	401 Unauthorized: [no body]	5b81ac75-2c9a-46f0-8bb2-91a2322d056c	Flood threshold exceeded at Ward A	+263783081413	2026-09-27 17:31:02.757262
\.


--
-- Name: alert_logs alert_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.alert_logs
    ADD CONSTRAINT alert_logs_pkey PRIMARY KEY (id);


--
-- Name: idx_alert_incident; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_alert_incident ON public.alert_logs USING btree (incident_id);


--
-- PostgreSQL database dump complete
--

\unrestrict GYKbIkmWDJDEsPmUUwQvkYIuxH4y8eBvvjgFkqMDaLppcBB4njTi65yagRRTPi9

