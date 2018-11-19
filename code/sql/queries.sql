--List all closed requests having bill less than 100 dollars
SELECT date,comment,bill FROM Closed_Request WHERE bill < 100;

--List first and last name of customers having more than 20 different cars
SELECT fname, lname
FROM Customer,(
		SELECT customer_id,COUNT(customer_id) as car_num
		FROM Owns
		GROUP BY customer_id
		HAVING COUNT(customer_id) > 20
	) AS O
WHERE O.customer_id = id;

-- List Make,Model, and Year of all cars build before 1995 having less than 50000 miles
SELECT DISTINCT make,model, year
FROM Car AS C, Service_Request AS S
WHERE year < 1995 and S.car_vin = C.vin and S.odometer < 50000;

-- List the make, model and number of service requests for the first k cars with the highest number of service orders.
SELECT make, model, R.creq
FROM Car AS C, (
SELECT car_vin, COUNT(rid) AS creq
FROM Service_Request
GROUP BY car_vin ) AS R
WHERE R.car_vin = C.vin
ORDER BY R.creq DESC
LIMIT 10;

-- List the first name, last name and total bill of customers in order total bill for all car brought to the mechanic.
SELECT C.fname , C.lname, Total
FROM Customer AS C,
(SELECT sr.customer_id, SUM(CR.bill) AS Total
FROM Closed_Request AS CR, Service_Request AS SR
WHERE CR.rid = SR.rid
GROUP BY SR.customer_id) AS A
WHERE C.id=A.customer_id
ORDER BY A.Total DESC;





