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

-- Start customer id from max_id of customer table
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

