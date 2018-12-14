/*
 * Tests to see if insertions populated database correctly
 * Constants determined by amount of data in original .csv files
*/

-- Add Customer
SELECT *
FROM Customer C
WHERE C.id >= 500;


-- Add Mechanic
SELECT *
FROM Mechanic M
WHERE M.id >= 250;

-- Add Car
SELECT *
FROM Car C
WHERE C.vin IN (
	SELECT O.car_vin
	FROM Owns O
	WHERE O.ownership_id >= 5000
);

-- Display vehicles linked to customers
SELECT C.fname, C.lname, O.car_vin
FROM Owns O, Customer C
WHERE O.ownership_id >= 5000 AND O.customer_id = C.id;

-- Initiate a Service Request
SELECT *
FROM Service_Request S
WHERE S.rid > 30000;

-- Close a Service Request
SELECT *
FROM Closed_Request C
WHERE C.wid > 30000;
