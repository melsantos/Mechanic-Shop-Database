# CS166 Project: Mechanic Shop Database
### *By Melissa Santos and Joshua Riley*
A database system that tracks information about customers, car, mechanics, car ownership, service request and billing information. Built with Java and PostgreSQL.

## Functionality:
 1. **Add Customer:** The user must input the customer's first and last name, phone number, and address. The system will also prompt the user to input the customer's car information into the database as well.

 2. **Add Mechanic:** The user must input the mechanic's first and last name as well as their years of experience.
   
 3. **Add Car:** The user must input the car's VIN, make, model, and year. If the car belongs to an existing customer, the system will ask the user for the customer's last name and will show a list of records that match. The user would choose the customer from this list. If the car does not belong to an existing customer, the system will then prompt the user to input the car owner's information in a similar fashion to **Add Customer**.
 
 For **Add Customer**, **Add Mechanic**, and **Add Car**, the system will notify the user of an invalid input for a field and will continuely ask for valid input.
 
 4. **Initiate a Service Request:** The user is given an option to search for an existing customer by last name or to add a new customer (i.e. Add Customer). If the user chooses to insert a service request for an existing customer, the user is given a menu list to select from the search results. The system will then display a menu list of all the cars associated with the selected customer. The user may choose to insert a service request for any of these cars or to add another car for the customer in the database (i.e. Add Car). After the car has been selected, the user will input the following information to finish creating the service request.

 5. **Close A Service Request:** If an open service request exists, the user is prompted for their mechanic id. Afterwards, the user is given a menu list of all open service requests to select to close. The user will then input the following information to finish closing the service request:
   
For **Initiate a Service Request** and **Close A Service Request**, the date and time the request is open/closed is automatically set to be the current date and time.

## Database Queries
 6. List date, comment, and bill for all closed requests with bill lower than 100.
 7. List first and last name of customers having more than 20 different cars.
 8. List Make, Model, and Year of all cars build before 1995 having less than 50000 miles.
 9. List the make, model and number of service requests for the first k cars with the highest number of service orders.
 10. List the first name, last name and total bill of customers in descending order of their total bill for all cars brought to the mechanic.
 
## Contributors
 + [Joshua Riley](https://github.com/jrile002)
 + [Melissa Santos](https://github.com/melsantos)
