# CS166 Project: Mechanic Shop Database
A database system that tracks information about customers, car, mechanics, car ownership, service request and billing information.

## Current Fuctions In Progress:
+ **List date, comment, and bill for all closed requests with bill lower than 100.** List the customers that have paid less than 100 dollars for repairs based on their previous service requests. 
+ **List first and last name of customers having more than 20 different cars.** 
Find how many cars each customer has counting from the ownership relation and discover who has more than 20 cars. 

## Finished Functions:
 + **Add Customer:** Add a new customer into the database. You should provide an interface that takes customer information (i.e. first, last name, phone, address) as input and checks if the provided information is valid based on the constraints of the database schema.
 + **Add Mechanic:** Add a new mechanic into the database. You should provide an interface that takes as input the information of a new mechanic (i.e. first, last, speciality, experience) and checks if the provided information is valid based on the constraints of the database schema.
 + **Add Car:** This function should allow a new car to be added in to the database. You should provide an interface that takes as input the information of a new car (i.e. vin, make, model, year) checking if the provided information is valid based on the constraints of the database schema.
+ **List Make, Model, and Year of all cars build before 1995 having less than 50000 miles.** Get the odometer from the service_requests and find all cars before 1995 having less than 50000 miles in the odometer. 
+ **List the make, model and number of service requests for the first k cars with the highest number of service orders.** Find for all cars in the database the number of service requests. Return the make,
model and number of service requests for the cars having the k highest number of
service requests. The k value should be positive and larger than 0. The user should
provide this value. Focus on the open service requests.
