/* Melissa Santos & Joshua Riley
 * CS166 Project Mechanic Database
 * Triggers for auto-incrementing customer, mechanic, and service request ids
 */

/*
ALTER TABLE customer
ALTER COLUMN customer_id set default nextval('cust_id_seq');
ALTER SEQUENCE cust_id_seq owned by customer.id;
*/

/* Customer id trigger */
DROP TRIGGER IF EXISTS set_cust_id on customer;
DROP SEQUENCE IF EXISTS cust_id_seq;
--DROP EXTENSION IF EXISTS plpgsql;

-- Start customer id from max id of customer table
CREATE SEQUENCE cust_id_seq;
SELECT setval('cust_id_seq', (SELECT MAX(id) FROM customer));

--CREATE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION set_cust_id()
RETURNS "trigger" as $inc_cust_id$
	BEGIN
		NEW.id := nextval('cust_id_seq');
		RETURN NEW;
	END;
$inc_cust_id$
LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER set_cust_id BEFORE INSERT
ON customer FOR EACH ROW
EXECUTE PROCEDURE set_cust_id();

/* Mechanic id trigger */
DROP TRIGGER IF EXISTS set_mech_id on mechanic;
DROP SEQUENCE IF EXISTS mech_id_seq;

-- Start mechanic id from max id of mechanic table
CREATE SEQUENCE mech_id_seq;
SELECT setval('mech_id_seq', (SELECT MAX(id) FROM mechanic));

CREATE OR REPLACE FUNCTION set_mech_id()
RETURNS "trigger" as $inc_mech_id$
	BEGIN
		NEW.id := nextval('mech_id_seq');
		RETURN NEW;
	END;
$inc_mech_id$
LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER set_mech_id BEFORE INSERT
ON mechanic FOR EACH ROW
EXECUTE PROCEDURE set_mech_id();

/* Service Request rid trigger */
DROP TRIGGER IF EXISTS set_serv_rid on service_request;
DROP SEQUENCE IF EXISTS serv_rid_seq;

-- Start service request rid from max rid of service request table
CREATE SEQUENCE serv_rid_seq;
SELECT setval('serv_rid_seq', (SELECT MAX(rid) FROM service_request));

CREATE OR REPLACE FUNCTION set_serv_rid()
RETURNS "trigger" as $inc_serv_rid$
	BEGIN
		NEW.rid := nextval('serv_rid_seq');
		RETURN NEW;
	END;
$inc_serv_rid$
LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER set_serv_rid BEFORE INSERT
ON service_request FOR EACH ROW
EXECUTE PROCEDURE set_serv_rid();

