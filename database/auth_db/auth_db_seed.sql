--
-- PostgreSQL database dump
--

\restrict m82oDTnHo9ffinmxgwjYSskIO0pwA9ie2AMomzZ4Onx2RmJmXw6KfLKwZh863km

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

ALTER TABLE IF EXISTS ONLY public.users DROP CONSTRAINT IF EXISTS users_pkey;
ALTER TABLE IF EXISTS ONLY public.users DROP CONSTRAINT IF EXISTS uk_r43af9ap4edm43mmtq01oddj6;
DROP TABLE IF EXISTS public.users;
SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    email character varying(255),
    enabled boolean NOT NULL,
    full_name character varying(255),
    password_hash character varying(255) NOT NULL,
    phone character varying(255),
    role character varying(255) NOT NULL,
    username character varying(255) NOT NULL,
    ward character varying(255),
    CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['FLOOD_RECORDER'::character varying, 'DROUGHT_RECORDER'::character varying, 'FIRE_RECORDER'::character varying, 'ZOONOTIC_RECORDER'::character varying, 'MINING_RECORDER'::character varying, 'FLOOD_SUPERVISOR'::character varying, 'DROUGHT_SUPERVISOR'::character varying, 'FIRE_SUPERVISOR'::character varying, 'ZOONOTIC_SUPERVISOR'::character varying, 'MINING_SUPERVISOR'::character varying, 'PROVINCIAL_ADMIN'::character varying, 'NATIONAL_USER'::character varying])::text[])))
);


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.users (id, created_at, email, enabled, full_name, password_hash, phone, role, username, ward) FROM stdin;
b65aa16e-1e15-4043-90d5-03defbf14357	2026-09-26 16:44:53.098961	flood.recorder.wardA@dpdms.uz.ac.zw	t	flood.recorder.wardA	$2a$10$tkkCL0qlP3GavMeWgqSAoulGJfNLRZlw9YuM3Nniq4d8Q7McaDvzq	\N	FLOOD_RECORDER	flood.recorder.wardA	Ward A
775fb49f-82de-4c54-9a4d-5f58ec481d6e	2026-09-26 16:44:54.68974	drought.recorder.wardA@dpdms.uz.ac.zw	t	drought.recorder.wardA	$2a$10$hlKLTIgNofnhBV2Jkato.OcwIIqnfDOh1VM1hs2uDZbUZcVPzKvXK	\N	DROUGHT_RECORDER	drought.recorder.wardA	Ward A
d658f5b5-555e-4809-bda1-944db59d678b	2026-09-26 16:44:55.20736	fire.recorder.wardB@dpdms.uz.ac.zw	t	fire.recorder.wardB	$2a$10$kP1aWHSF9..MYu1fTNRz.uT3rN7GvNSkXr3qvJplnBruUuni7QcUa	\N	FIRE_RECORDER	fire.recorder.wardB	Ward B
f1d785cf-e412-4031-8f43-0dc83bb1e516	2026-09-26 16:44:55.889048	zoonotic.recorder.wardC@dpdms.uz.ac.zw	t	zoonotic.recorder.wardC	$2a$10$uR5TU1c0SXkuLMTEcC.ofuoEexIoAOENGyvtKpD.rwEcsCOG1ISoS	\N	ZOONOTIC_RECORDER	zoonotic.recorder.wardC	Ward C
abd20afc-7596-41e6-acb7-b61e5c177d4b	2026-09-26 16:44:56.386793	mining.recorder.wardD@dpdms.uz.ac.zw	t	mining.recorder.wardD	$2a$10$GNPVy9IqREJPDBqdq7t4X.18sxn50jYMY1.H3CnXe4o95VNOfagta	\N	MINING_RECORDER	mining.recorder.wardD	Ward D
e8747717-8cae-4953-b0b7-8a033dd8f9ff	2026-09-26 16:44:56.760888	flood.supervisor@dpdms.uz.ac.zw	t	flood.supervisor	$2a$10$pt05Os/tFsngwHs5y9yTA.ETBfxcK.HSL7.pMdC.HKHaUJaLuloLu	\N	FLOOD_SUPERVISOR	flood.supervisor	\N
1a976d31-38b0-4b05-8313-61c9eabc7b43	2026-09-26 16:44:57.286379	drought.supervisor@dpdms.uz.ac.zw	t	drought.supervisor	$2a$10$qTOq5jHQ4j6tBdeLQ8efhedaUHBKMiE8DZpoA1TCU.68gCe.JOWey	\N	DROUGHT_SUPERVISOR	drought.supervisor	\N
b8e11e8d-470c-4a6b-97f2-d72d806ee5cb	2026-09-26 16:44:59.833095	fire.supervisor@dpdms.uz.ac.zw	t	fire.supervisor	$2a$10$IHl.kqTRXNujxvbaTwYnae7AnXWLYcgBJ3zPcmKyO6JpT2AzJZjsW	\N	FIRE_SUPERVISOR	fire.supervisor	\N
bbd59ef1-3c60-4a3d-8275-56c4d7899310	2026-09-26 16:45:00.356212	zoonotic.supervisor@dpdms.uz.ac.zw	t	zoonotic.supervisor	$2a$10$xyv3LZlHMZrt6XDN.j4FQeBN0wEn1xYyu9e.FOyK6DzvIkaoRwafO	\N	ZOONOTIC_SUPERVISOR	zoonotic.supervisor	\N
a571f8b9-6f83-4916-8285-30c56492d2b2	2026-09-26 16:45:00.650787	mining.supervisor@dpdms.uz.ac.zw	t	mining.supervisor	$2a$10$Qki6jIC8ZP9C4hHkIBtN.OxHT9XRFGyZen7FmdPg2bLA71I6/UDwe	\N	MINING_SUPERVISOR	mining.supervisor	\N
187848ab-9027-4dee-a9da-ab5935db8059	2026-09-26 16:45:00.89158	provincial.admin@dpdms.uz.ac.zw	t	provincial.admin	$2a$10$gPh2fNb1spg134uWhziPzunnA516nEZIyNP2ZVPY7Ck3vnMfYO/yy	\N	PROVINCIAL_ADMIN	provincial.admin	\N
1c1b2ee2-9a81-4c88-823f-6ffee06d4905	2026-09-26 16:45:01.141445	national.user@dpdms.uz.ac.zw	t	national.user	$2a$10$OlepnswSFATeiEjUdGGpOepgIruunnZkJ7pzBG1uuzzvSd6BNt6zi	\N	NATIONAL_USER	national.user	\N
\.


--
-- Name: users uk_r43af9ap4edm43mmtq01oddj6; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_r43af9ap4edm43mmtq01oddj6 UNIQUE (username);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

\unrestrict m82oDTnHo9ffinmxgwjYSskIO0pwA9ie2AMomzZ4Onx2RmJmXw6KfLKwZh863km

