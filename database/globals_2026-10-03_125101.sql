--
-- PostgreSQL database cluster dump
--

\restrict GQLWK4pbqN0emkGGhSr8fe1tH8e7uEZHDroO5sI5dEv0LqGzpIKewXs2WTc5aQw

SET default_transaction_read_only = off;

SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

--
-- Roles
--

CREATE ROLE dpdms;
ALTER ROLE dpdms WITH SUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION NOBYPASSRLS PASSWORD 'SCRAM-SHA-256$4096:TQZQdA6iMa3aRB9JqCa85g==$dHms7EAmcni9B8YWXv7wnDmwxb5TpTUcNW/JruZAWCc=:5E74FECHfOqEJDL0diL9hQm/zwW1LcZDgh3bmenL0Xg=';
CREATE ROLE postgres;
ALTER ROLE postgres WITH SUPERUSER INHERIT CREATEROLE CREATEDB LOGIN REPLICATION BYPASSRLS PASSWORD 'SCRAM-SHA-256$4096:0LMhWvKDfZnkDQGWYdBodg==$F3VLD/G3+Rl2zJ7XvNfS8+wSeUgu9eOJez9B032igbM=:SRK3oatYmwDxoAh7PABgIgernP3PQxPMz4JezEcG4MU=';

--
-- User Configurations
--








\unrestrict GQLWK4pbqN0emkGGhSr8fe1tH8e7uEZHDroO5sI5dEv0LqGzpIKewXs2WTc5aQw

--
-- PostgreSQL database cluster dump complete
--

