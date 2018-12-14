# CS166 Project: Mechanic Shop Database
### *By Melissa Santos and Joshua Riley*
A database system that tracks information about customers, car, mechanics, car ownership, service request and billing information.

## Features:
 + Customer, mechanic, and service request ID's are automatically incremented and set when inserting a new record.

## Finished Functions:
 + **Add Customer:** The user must input the following fields:
   + *first name*: Must be between 1-32 characters. Must be entirely alphabetic.
   + *last name*: Must be between 1-32 characters. Must be entirely alphabetic.
   + *phone*: Must be of the format (###)###-####. Must only be digits.
   + *address*: A string of 256 characters.

   The system will also prompt the user to input the customer's car information into the database as well.

 + **Add Mechanic:** The user must input the following fields:
   + *first name*: Must be between 1-32 characters. Must be entirely alphabetic.
   + *last name*: Must be between 1-32 characters. Must be entirely alphabetic.
   + *years of experience*: Must be an integer between 0-99.
   
 + **Add Car:** The user must input the following fields.
   + *vin*: A string of **exactly** 16 characters.
   + *make*: A string of 32 characters.
   + *model*: A string of 32 characters.
   + *year*: Must be a 4 digit integer. Car year must be 1970 or newer.
   
   The system will ask the user if the user if the car belongs to a existing customer. If true, the system will ask the user for the customer's last name and will show a list of records that match. The user would then choose from the list and the car would then be linked to that customer. If the car does not belong to an existing customer, then the system will prompt the user to input the car owner information as well (i.e. Add Customer).
 
 For **Add Customer**, **Add Mechanic**, and **Add Car**, the system will notify the user of an invalid input for a field and will continuely ask for valid input.
 
 + **Initiate a Service Request:** This function allows the user to insert a service request into the database. The user is given an option to search for an existing customer by last name or to add a new customer (i.e. Add Customer). If the user chooses to insert a service request for an existing customer, the user is given a menu list to select from the search results. The system will then display a menu list of all the cars associated with the selected customer. The user may choose to insert a service request for any of these cars or to add another car for the customer in the database (i.e. Add Car). After the car has been selected, the user will input the following information to finish creating the service request:
   + *odometer*: A positive nonzero integer.
   + *customer complaint*: A string of nonzero length.

The date and time of the request is automatically set to be the current date and time.

 + **Close A Service Request**: This function allows the user to complete an open service request in the database. If an open service request exists, the user is prompted for their mechanic id. Afterwards, the user is given a menu list of all open service requests to select to close. The user will then input the following information to finish closing the service request:
   + *comment*: A string.
   + *bill*: A positive nonzero integer.
   
The date and time the request is closed is automatically set to be the current date and time.

 + **List date, comment, and bill for all closed requests with bill lower than 100.**
 + **List first and last name of customers having more than 20 different cars.** 
 + **List Make, Model, and Year of all cars build before 1995 having less than 50000 miles.**
 + **List the make, model and number of service requests for the first k cars with the highest number of service orders.**
 + **List the first name, last name and total bill of customers in descending order of their total bill for all cars brought to the mechanic.**
