# CS166 Project: Mechanic Shop Database
A database system that tracks information about customers, car, mechanics, car ownership, service request and billing information.

## Features:
 + Customer, mechanic, and service request ID's are automatically incremented and set when inserting a new record.

## In Progress:
 + **Close A Service Request**: This function will allow you to complete an existing service request. Given a service request number and an employee id, the client application should verify the information provided and attempt to create a closing request record. You should make sure to check for the validity of the provided inputs (i.e. does the mechanic exist, does the request exist, valid closing date after request date, etc.)

## Finished Functions:
 + **Add Customer:** The user must input the following fields:
   + first name: Must be between 1-32 characters. Must be entirely alphabetic.
   + last name: Must be between 1-32 characters. Must be entirely alphabetic.
   + phone: Must be of the format (###)###-####. Must only be digits.
   + address: A string of 256 characters.

   The system will also prompt the user to input the customer's car information into the database as well.

 + **Add Mechanic:** The user must input the following fields:
   + first name: Must be between 1-32 characters. Must be entirely alphabetic.
   + last name: Must be between 1-32 characters. Must be entirely alphabetic.
   + years of experience: Must be an integer between 0-99.
   
 + **Add Car:** The user must input the following fields.
   + vin: A string of **exactly** 16 characters.
   + make: A string of 32 characters.
   + model: A string of 32 characters.
   + year: Must be a 4 digit integer. Car year must be 1970 or newer.
   
   The system will ask the user if the user if the car belongs to a existing customer. If true, the system will ask the user for the customer's last name and will show a list of records that match. The user would then choose from the list and the car would then be linked to that customer. If the car does not belong to an existing customer, then the system will prompt the user to input the car owner information as well (i.e. Add Customer).
 
 For **Add Customer**, **Add Mechanic**, and **Add Car**, the system will notify the user of an invalid input for a field and will continuely ask for valid input.
 
 + **Initiate a Service Request:** This function will allow you to add a service request for a customer into the database. Given a last name, the function should search the database of existing customers. If many customers match, a menu option should appear listing all customers with the given last name asking the user to choose which customer has initiated the service request. Otherwise, the client application should provide the option of adding a new customer. If an existing customer is chosen, the client application should list all cars associated with that client providing the option to initiate the service request for one of the listed cars, otherwise a new car should be added along with the service request information for it. 
 + **List date, comment, and bill for all closed requests with bill lower than 100.**
 + **List first and last name of customers having more than 20 different cars.** 
 + **List Make, Model, and Year of all cars build before 1995 having less than 50000 miles.**
 + **List the make, model and number of service requests for the first k cars with the highest number of service orders.**
 + **List the first name, last name and total bill of customers in descending order of their total bill for all cars brought to the mechanic.**
