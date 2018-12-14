/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */

import javax.swing.JOptionPane;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;   
import java.util.Collections;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class MechanicShop{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public MechanicShop(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
 	 * Method that create triggers on customer, mechanic, and service request
 	 * Code to convert file into string grabbed from 
 	 * https://howtodoinjava.com/java/io/java-read-file-to-string-examples/
 	 */
	public void createTriggers() throws SQLException {
		StringBuilder query = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader("../sql/triggers.sql"));) {
			String currentLine;
			while((currentLine = reader.readLine()) != null)
			{
				query.append(currentLine).append("\n");
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}

		Statement stmt = this._connection.createStatement ();
		stmt.execute (query.toString());
		stmt.close();
	}
	
	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + MechanicShop.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		MechanicShop esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new MechanicShop (dbname, dbport, user, "");

			// Create triggers
			esql.createTriggers();
	
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. AddCustomer");
				System.out.println("2. AddMechanic");
				System.out.println("3. AddCar");
				System.out.println("4. InsertServiceRequest");
				System.out.println("5. CloseServiceRequest");
				System.out.println("6. ListCustomersWithBillLessThan100");
				System.out.println("7. ListCustomersWithMoreThan20Cars");
				System.out.println("8. ListCarsBefore1995With50000Milles");
				System.out.println("9. ListKCarsWithTheMostServices");
				System.out.println("10. ListCustomersInDescendingOrderOfTheirTotalBill");
				System.out.println("11. < EXIT");
				
				/*
				 * FOLLOW THE SPECIFICATION IN THE PROJECT DESCRIPTION
				 */
				switch (readChoice()){
					case 1: AddCustomer(esql, true); break;
					case 2: AddMechanic(esql); break;
					case 3: AddCar(esql, -1); break;
					case 4: InsertServiceRequest(esql); break;
					case 5: CloseServiceRequest(esql); break;
					case 6: ListCustomersWithBillLessThan100(esql); break;
					case 7: ListCustomersWithMoreThan20Cars(esql); break;
					case 8: ListCarsBefore1995With50000Milles(esql); break;
					case 9: ListKCarsWithTheMostServices(esql); break;
					case 10: ListCustomersInDescendingOrderOfTheirTotalBill(esql); break;
					case 11: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice
	
	public static void AddCustomer(MechanicShop esql, Boolean addNewCar){//1
		try {
			String insertCust = "INSERT INTO customer (fname, lname, phone, address) VALUES ";
			String fname = "";
			String lname = "";
			String phone = "";
			String address = "";
			Scanner scan = new Scanner(System.in);
			Boolean valid = false;

			// Check first name validty
			do {
				System.out.print("\tEnter customer's first name: $ ");
				try {
					fname = scan.next();	
					if (fname.length() == 0 || fname.length() > 32) {
						throw new IllegalArgumentException("Customer's first name must be between 1 and 32 characters (inclusive)");
					}
					if(!fname.matches("[a-zA-Z]+")) {
						throw new IllegalArgumentException("Customer's name must only contain letters");
					}
					valid = true;
				} catch (IllegalArgumentException ex) { 
					System.out.println(ex.getMessage());
					valid = false;
					scan.nextLine();
				}
			} while (!valid);			

			// Check last name validity
			do {
				System.out.print("\tEnter customer's last name: $ ");
				try {
					lname = scan.next();	
					if (lname.length() == 0 || lname.length() > 32) {
						throw new IllegalArgumentException("Customer's last name must be between 1 and 32 characters (inclusive)");
					}
					if(!lname.matches("[a-zA-Z]+")) {
						throw new IllegalArgumentException("Customer's name must only contain letters");
					}
					valid = true;
				} catch (IllegalArgumentException ex) { 
					System.out.println(ex.getMessage());
					valid = false;
					scan.nextLine();
				}
			} while (!valid);	
			
			// Check phone validty
			do {
				System.out.print("\tEnter customer's phone number in the format (###)###-####: $ ");
				try {
					phone = scan.next();	
					if (phone.length() == 0 || phone.length() > 13) {
						throw new IllegalArgumentException("Customer's phone number must be between 1 and 13 characters (inclusive)");
					}
					if(!phone.matches("\\(\\d{3}\\)\\d{3}\\-\\d{4}")) {
						throw new IllegalArgumentException("Customer's phone number must be of the format (###)###-####");
					}
					valid = true;
				} catch (IllegalArgumentException ex) { 
					System.out.println(ex.getMessage());
					valid = false;
					scan.nextLine();
				}
			} while (!valid);

			// Check address validity
			do {
				System.out.print("\tEnter customer's address: $ ");
				try {
					scan.nextLine();
					address = scan.nextLine();	
					if (address.length() == 0 || address.length() > 256) {
						throw new IllegalArgumentException("Customer's address must be between 1 and 256 characters (inclusive)");
					}
					valid = true;
				} catch (IllegalArgumentException ex) { 
					System.out.println(ex.getMessage());
					valid = false;
					scan.nextLine();
				}
			} while (!valid);

			insertCust += "(\'" /*+ id + "\', \'"*/ + fname + "\', \'" + lname + "\', \'" + phone + "\', \'" + address + "\');";
			// System.out.println(insertCust);
			esql.executeUpdate(insertCust);
	
			// Add a car and link to newly made customer
			if(addNewCar) {
				System.out.println("Please insert customer's car information as well: ");
				Boolean carInserted = false;
				int customerID = esql.getCurrSeqVal("cust_id_seq");
				// Will continously ask for a car until successful insertion of a car
				// Catches errors like duplicate vins, invalid vins, etc.
				do {
					try {
						AddCar(esql, customerID);
						carInserted = true;
					} catch(Exception e) {
						carInserted = false;
					}
				} while(!carInserted);
			}

		}
		catch(Exception e){
			System.err.println (e.getMessage());
		}	
	}
	
	public static void AddMechanic(MechanicShop esql){//2
		try {
			String insertMech = "INSERT INTO mechanic (fname, lname, experience) VALUES ";
			String fname = "";
			String lname = "";
			int years = 0;
			Scanner scan = new Scanner(System.in);
			Boolean valid = false;	

			// Check first name validty
			do {
				System.out.print("\tEnter mechanic's first name: $ ");
				try {
					fname = scan.next();	
					if (fname.length() == 0 || fname.length() > 32) {
						throw new IllegalArgumentException("Mechanic's first name must be between 1 and 32 characters (inclusive)");
					}
					if(!fname.matches("[a-zA-Z]+")) {
						throw new IllegalArgumentException("Mechanic's name must only contain letters");
					}
					valid = true;
				} catch (IllegalArgumentException ex) { 
					System.out.println(ex.getMessage());
					valid = false;
					scan.nextLine();
				}
			} while (!valid);			

			// Check last name validity
			do {
				System.out.print("\tEnter mechanic's last name: $ ");
				try {
					lname = scan.next();	
					if (lname.length() == 0 || lname.length() > 32) {
						throw new IllegalArgumentException("Mechanic's last name must be between 1 and 32 characters (inclusive)");
					}
					if(!lname.matches("[a-zA-Z]+")) {
						throw new IllegalArgumentException("Mechanic's name must only contain letters");
					}
					valid = true;
				} catch (IllegalArgumentException ex) { 
					System.out.println(ex.getMessage());
					valid = false;
					scan.nextLine();
				}
			} while (!valid);	
			
			// Check experience validity
			do {
				System.out.print("\tEnter mechanic's years of experience: $ ");
				try {
					years = scan.nextInt();	
					if(years < 0 || years > 99) {
						throw new IllegalArgumentException("Years of experience must be between 1 and 99 (inclusive)");
					}
					valid = true;
				} catch (java.util.InputMismatchException e) {
					System.out.println("Years of experience must be an integer");	
					scan.nextLine();
					valid = false;
				} catch (IllegalArgumentException ex) { 
					System.out.println(ex.getMessage());
					valid = false;
					scan.nextLine();
				}
			} while (!valid);

			// System.out.println(id + " " + fname + " " + lname + " " + years); 

			insertMech += "(\'" /*+ id + "\', \'"*/ + fname + "\', \'" + lname + "\', \'" + years + "\');";
			esql.executeUpdate(insertMech);
		}
		catch(Exception e){
			System.err.println (e.getMessage());
		} 
	}
	
	// if customerID is negative, then create new customer
	// if customerID is postive and exists in the database, then link car to customer
	public static void AddCar(MechanicShop esql, int customerID){//3
		try{
			String vin = "";
			String make = "";
			String model = "";
			String year = "";
			Boolean valid = false;

			// Get car vin loop
			do {
				int carExists = 0;
				System.out.print("\tEnter car vin: $ ");
				try {
					vin = in.readLine();
					if (vin.length() != 16){
						throw new IllegalArgumentException("Car VIN must be be 16 characters");
					}
					// Check if vin is unique
					String checkCarVins = "SELECT * FROM car WHERE vin=\'";
					       checkCarVins += vin +"\';";
					
					carExists = esql.executeQuery(checkCarVins);
					if (carExists != 0) {
						throw new IllegalArgumentException("Car VIN already exists in database, please input a unique VIN");
					}

					valid = true;
				} catch (IllegalArgumentException e) {
					System.out.println(e.getMessage());
					valid = false;
				}
			} while(!valid);

			// Get car make loop
			do {
				System.out.print("\tEnter car make: $ ");
				try {
					make = in.readLine();
					if (make.length() == 0 || make.length() > 32){
						throw new IllegalArgumentException("Car MAKE must be between 1 and 32 characters (inclusive)");
					}
					valid = true;
				} catch(IllegalArgumentException e) {
					System.out.println(e.getMessage());
					valid = false;
				}
			} while(!valid);

			// Get car model loop
			do {
				try{	
					System.out.print("\tEnter car model: $ ");
					model = in.readLine();
					if (model.length() == 0 || model.length() > 32){
						throw new IllegalArgumentException("Car MODEL must be between 1 and 32 characters (inclusive)");
					}
					valid = true;
				} catch(IllegalArgumentException e) {
					System.out.println(e.getMessage());
					valid = false;
				}
			} while(!valid);

			// Get car year loop
			do {
				try {
					System.out.print("\tEnter car year: $ ");
					year = in.readLine();
					if (year.length() != 4){
						throw new IllegalArgumentException("Invalid car YEAR");
					}

					// Throws an exception if year is not an integer amount
					int year_int = Integer.parseInt(year);
 
					if(year_int < 1970) {
						throw new IllegalArgumentException("Car YEAR must be 1970 or newer");
					}
					valid = true;
				} catch(NumberFormatException e) {
					System.out.println("Car YEAR must be an int");
					valid = false;

				} catch(IllegalArgumentException e) {
					System.out.println(e);
					valid = false;
				}
			} while(!valid);

			String query = "INSERT INTO Car VALUES (\'" + vin + "\', \'" + make + "\', \'" + model + "\', \'" + year + "\')";

			esql.executeUpdate(query);

			// Link car to customer id
			if(customerID >= 0) {
				String insertOwns = "INSERT INTO owns (customer_id, car_vin) VALUES (";
				       insertOwns += "\'" + customerID + "\', ";
				       insertOwns += "\'" + vin + "\');";
				esql.executeUpdate(insertOwns);
			// Ask user if they want to link this car to an existing customer or create a new customer
			} else {
				System.out.print("\tDoes this car belong to an existing customer? [Y/N]: $ ");

				String go = in.readLine();

				// Link car to existing customer
				if (go.equals("Y")) {
					// Continuely ask for lname that exists in the database
					List<List<String>> lnameResults;
					do {
						System.out.print("\tEnter customer's last name: $ ");
						String lname = in.readLine();
						String lnameQuery = "SELECT * FROM Customer WHERE lname = \'" + lname + "\';";
						lnameResults = esql.executeQueryAndReturnResult(lnameQuery);
						if (lnameResults.size() == 0) {
							System.out.println("Last name does not exist in database please try again.");
						}
					} while (lnameResults.size() == 0);

					// Display results
					for(int i = 0; i < lnameResults.size(); ++i) {
						System.out.print("\t");
						System.out.print(i);
						System.out.print(": ");
						for(int j = 1; j < lnameResults.get(i).size(); ++j) {
							String var1 = lnameResults.get(i).get(j);
							String var2 = var1.split("\\s+")[0];
							if(j != 4) {
								System.out.print(var2);
								System.out.print(", ");
							} 
							else {
								System.out.println(var1);
							}
						}
					}
					// Select customer from results via id
					int cust_choice = -1;
					do {
						System.out.print("\tEnter desired customer's option id: $ ");
						try {
							cust_choice = Integer.parseInt(in.readLine());
						} catch (NumberFormatException e) {
							cust_choice = -1;
						}

						if(cust_choice < 0 || cust_choice >= lnameResults.size()) {
							System.out.println("Invalid customer option id. Try again.");
						}
					} while(cust_choice < 0 || cust_choice >= lnameResults.size());

					int cid = cust_choice;
					String insertOwns = "INSERT INTO owns (customer_id, car_vin) VALUES (";
					       insertOwns += "\' " + lnameResults.get(cid).get(0) + "\', ";
					       insertOwns += "\'" + vin + "\');";
					//System.out.println("Query: " + insertOwns);
					esql.executeUpdate(insertOwns);
				
				} else { // Else create new customer for car
					System.out.println("Please insert customer information as well");
					AddCustomer(esql, false);
					int newCustID = esql.getCurrSeqVal("cust_id_seq");
					String insertOwns = "INSERT INTO owns (customer_id, car_vin) VALUES (";
					       insertOwns += "\'" + newCustID + "\', ";
					       insertOwns += "\'" + vin + "\');";
					esql.executeUpdate(insertOwns);
				}
			}
		}
		catch(Exception e){
			System.err.println (e.getMessage());
		}

	}
	

	public static void InsertServiceRequest(MechanicShop esql){//4
		try{
			boolean valid = false;
			int cid = -1;
			// Step 1: Search for customer's last name
			System.out.print("\tEnter customer's last name: $ ");
			String lname = in.readLine();
			String lnameQuery = "SELECT * FROM Customer WHERE Customer.lname = \'" + lname + "\'";
			List<List<String>> lnameResults = esql.executeQueryAndReturnResult(lnameQuery);
			
			if (lnameResults.size() > 0){
				int cust_choice = -1;
				do {
					// Display all matching results
					valid = false;
					try {
						for (int i = 0; i < lnameResults.size(); ++i){
							System.out.print("\t");
							System.out.print(i);
							System.out.print(": ");
							for (int j = 1; j < lnameResults.get(i).size(); ++j){
								String var1 = lnameResults.get(i).get(j);
								String var2 = var1.split("\\s+")[0];
								if (j != 4){
									System.out.print(var2);
									System.out.print(", ");
								}
								else {
									System.out.println(var1);
								}
							}
						}
						// Select customer from results
						System.out.print("\tEnter desired customer's option id: $ ");
						cust_choice = Integer.parseInt(in.readLine());
						if (cust_choice < 0 || cust_choice >= lnameResults.size()){
							throw new IllegalArgumentException("Invalid customer option id"); 
						}
						valid = true;
					}
					catch (Exception e) {
						System.err.println(e.getMessage());
					}
				} while(!valid);
				cid = cust_choice;
			}
			else {
				// Customer did not exist so offer to create a new one
				System.out.println("\tCould not find a customer with that last name");
				System.out.print("\tWould you like to add a new customer? [Y/N]: $ ");
				String go = in.readLine();
				if (go.equals("Y")){
					// Add customer
					AddCustomer(esql, false);
					// Grab customer based on last trigger sequence
					int recentCustomerID = esql.getCurrSeqVal("cust_id_seq");
					String cidQuery = "SELECT * FROM Customer WHERE Customer.id = " + recentCustomerID;
					List<List<String>> temp = esql.executeQueryAndReturnResult(cidQuery);
					lnameResults.add(temp.get(0));
					cid = 0;
				}
				else {
					// Cancel service request
					System.out.println("\tCancelling service request");
					return;
				}
			}
			// Step 2: List all cars associated with client
			boolean createCar = false;
			String vin = "";
		
			String carsOwnedQuery = "SELECT C.vin, C.make, C.model, C.year FROM Car C, Owns O WHERE O.customer_id = " + lnameResults.get(cid).get(0) + " AND O.car_vin = C.vin";
			List<List<String>> carsOwnedResults = esql.executeQueryAndReturnResult(carsOwnedQuery);
			if (carsOwnedResults.size() > 0){
				do {
					try {
						valid = false;
						// Display all matching results
						for (int i = 0; i < carsOwnedResults.size(); ++i){
							System.out.print("\t");
							System.out.print(i);
							System.out.print(": ");
							for (int j = 0; j < carsOwnedResults.get(i).size(); ++j){
								System.out.print(carsOwnedResults.get(i).get(j));
								System.out.print(", ");
							}
							System.out.print("\n");
						}
						// Select car from results
						do {
							try{
								System.out.print("\n\tWould you like to enter a service request for one of these cars? [Y/N]: $ ");
								String go = in.readLine();
								if (go.equals("Y")){
									// Select customer's car
									System.out.print("\tEnter desired car's option id: $ ");
									int car_choice = Integer.parseInt(in.readLine());
									if (car_choice < 0 || car_choice >= carsOwnedResults.size()){
										throw new IllegalArgumentException("Error: Invalid car option id"); 
									}
									vin = carsOwnedResults.get(car_choice).get(0);
									valid = true;

								}
								else if (go.equals("N")) {
									// Add a new car for the existing customer
									createCar = true;
									valid = true;
								}
								else {
									throw new IllegalArgumentException("Error: Please use either  Y or N");
								}
							}
							catch (Exception e){
								System.err.println(e.getMessage());
							}
						}while(!valid);
					}
					catch (Exception e){
						System.err.println(e.getMessage());
					}
				} while(!valid);
			
			}
			else{
				// Customer has no car associated in DB so create one
				System.out.println("\t" + lnameResults.get(cid).get(1).split("\\s+")[0] + " does not have a car registered in the database");
				createCar = true;
			}
			if (createCar == true){
				// Create and link car to the customer
				System.out.println("\tAdding a car for " + lnameResults.get(cid).get(1).split("\\s+")[0]);
				AddCar(esql, Integer.parseInt(lnameResults.get(cid).get(0)));
			}	

			// Step 3: Complete service request
			
			// Odometer
			int odometer = -1;
			do {
				valid = false;
				try{
					System.out.print("\tEnter the odometer reading of the car: $ ");
					odometer = Integer.parseInt(in.readLine());
					if (odometer <= 0){
						throw new IllegalArgumentException("Error: Odometer must be a valid positive integer amount");
 					}
					valid = true;
				}
				catch (NumberFormatException e){
					System.err.println ("Error: Odometer must be a valid positive integer amount");
				}
				catch(Exception e){
					System.err.println (e.getMessage());
				}
			}while(!valid);
			
			// Complaint
			String complaint = "";
			do{
				valid = false;
				try {
					System.out.print("\tEnter customer complaint about car: $ ");
					complaint = in.readLine();
					if (complaint.length() == 0){
						throw new IllegalArgumentException("Error: Customer must have a complaint listed for car to be serviced");
					}
					valid = true;
				}
				catch(Exception e){
					System.err.println (e.getMessage());
				}
			}while(!valid);
			
			// Date
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");  
			LocalDateTime now = LocalDateTime.now();  

			// Grab the car vin from the last tuple inserted into the OWNS relation
			int ownershipID = esql.getCurrSeqVal("owns_id_seq");
			String ownsQuery = "SELECT O.car_vin FROM owns O WHERE O.ownership_id = " + ownershipID;
			vin = esql.executeQueryAndReturnResult(ownsQuery).get(0).get(0);

			String query = "INSERT INTO Service_Request(customer_id, car_vin, date, odometer, complain) VALUES (";
			query += lnameResults.get(cid).get(0) + ", \'" + vin + "\', \'" + dtf.format(now) + "\', " + odometer + ", \'" + complaint + "\')";
			esql.executeUpdate(query); 

		}
		catch (NumberFormatException e){
			System.err.println ("Error: Input must be a valid integer amount");
		}
		catch(Exception e){
			System.err.println (e.getMessage());
		}
	}
	
	public static void CloseServiceRequest(MechanicShop esql) throws Exception{//5
		try {
			boolean valid = false;
			int mid = -1; // Mechanic id
			int rid = -1; // Service request number
			int bill_amount = -1; // Billing

			// Step 1: Ask for and validate mechanic id
			do{
				valid = false;
				try{
					System.out.print("\tEnter mechanic id: $ ");
					mid = Integer.parseInt(in.readLine());
					if (mid <= 0){
						throw new IllegalArgumentException("");
					}
					// Validate mechanic id
					String validateMechanicQuery = "SELECT M.id FROM Mechanic M WHERE M.id = " + mid;
					List<List<String>> validateMechanicResults = esql.executeQueryAndReturnResult(validateMechanicQuery);
					Integer.parseInt(validateMechanicResults.get(0).get(0)); // Throws exception if result is empty
					valid = true;
				}
				catch (Exception e){
					System.err.println ("Error: Invalid mechanic id. Must be a positive nonzero integer");
				}
			}while(!valid);

			// Step 2: Ask for and validate service request number
			do{
				valid = false;
				try{
					System.out.print("\tEnter service request number: $ ");
					rid = Integer.parseInt(in.readLine());
					if (rid <= 0){
						throw new IllegalArgumentException("");
					}
					// Validate service request number
					String validateServiceQuery = "SELECT S.rid FROM Service_Request S WHERE S.rid = " + rid;
					List<List<String>> validateServiceResults = esql.executeQueryAndReturnResult(validateServiceQuery);
					Integer.parseInt(validateServiceResults.get(0).get(0)); // Throws exception if result is empty

					// Check if service request is already closed
					String isRequestClosedQuery = "SELECT 1 FROM Closed_Request C WHERE C.rid = " + rid;
					List<List<String>> isRequestClosedResults = esql.executeQueryAndReturnResult(isRequestClosedQuery);
					try{
						Integer.parseInt(isRequestClosedResults.get(0).get(0)); // If it throws an exception, the service request is still open
						System.err.println ("Error: This service request has already been closed");						
						valid = false;
					}
					catch (Exception e){
						valid = true;
					}
					
				}
				catch (Exception e){
					System.err.println ("Error: Invalid service request number. Must be a positive non-zero integer");
				}
			}while(!valid);

			// Closing date
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");  
			LocalDateTime now = LocalDateTime.now();  

			// Comment
			System.out.print("\tEnter any outstanding comments: $ ");
			String comment = in.readLine();

			// Bill
			do{
				valid = false;
				try{
					System.out.print("\tEnter total payment due for rendered service: $ ");
					bill_amount = Integer.parseInt(in.readLine());
					if (bill_amount <= 0){
						throw new IllegalArgumentException("");
					}
					valid = true;
				}
				catch (Exception e){
					System.err.println("Error: Invalid payment amount. Must be a positive nonzero integer");
				}
			}while(!valid);

			// Complete service request
			String query = "INSERT INTO Closed_Request(rid, mid, date, comment, bill) VALUES (";
			query += rid + ", " + mid + ", \'" + dtf.format(now) + "\', \'" + comment + "\', " + bill_amount + ")";
			esql.executeUpdate(query); 
		}
		catch (Exception e){
			System.err.println (e.getMessage());
		}
	}
	
	public static void ListCustomersWithBillLessThan100(MechanicShop esql){//6
		try{
			String query = "SELECT cr.date, cr.comment, cr.bill ";
			       query += "FROM Closed_Request cr "; 
			       query += "WHERE cr.bill < 100;";
			esql.executeQueryAndPrintResult(query);
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
		}	
	}
	
	public static void ListCustomersWithMoreThan20Cars(MechanicShop esql){//7
		
		try{
			String query = "SELECT c.fname, c.lname ";
			       query += "FROM Customer c, Owns O ";
			       query += "WHERE c.id = o.customer_id ";
			       query += "GROUP BY c.id ";
			       query += "HAVING COUNT(*) > 20; ";
			esql.executeQueryAndPrintResult(query);
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void ListCarsBefore1995With50000Milles(MechanicShop esql){//8
		try{
			String query = "SELECT DISTINCT Car.make, Car.model, Car.year ";
			query += "FROM Car ";
			query += "WHERE Car.vin = Any( ";
				query += "SELECT S.car_vin ";
				query += "FROM Service_Request S ";
				query += "WHERE S.odometer < 50000 ";

				query += "INTERSECT ";

				query += "SELECT C.vin ";
				query += "FROM Car C ";
				query += "WHERE C.year < 1995); ";
			esql.executeQueryAndPrintResult(query);
		}
		catch(Exception e){
			System.err.println (e.getMessage());
		}
	}
	public static void ListKCarsWithTheMostServices(MechanicShop esql){//9
		try{
			boolean valid = false;
			String k = "";
			do {
				try{
					System.out.print("\tEnter the amount limit of results: $ ");
					k = in.readLine();
					int k_int = Integer.parseInt(k);
					if (k_int <= 0){
						throw new IllegalArgumentException("");
					}
					valid = true;
				}
				catch(Exception e){
					System.err.println ("Error: Amount limit must be a valid positive integer not equal to 0");
				}
			}while(!valid);	
			String query = "SELECT C.make, C.model, mostServices.amt_service ";
			query += "FROM Car C, (";
				query += "SELECT S.car_vin, COUNT(S.car_vin) as amt_service ";
				query += "FROM Service_Request S ";
				query += "WHERE S.rid NOT IN ( ";
					query += "SELECT C.rid ";
					query += "FROM Closed_Request C ";
				query += ") GROUP BY S.car_vin ";
				query += "ORDER BY amt_service DESC ";
				query += ") mostServices ";
			query += "WHERE C.vin = mostServices.car_vin ";
			query += "ORDER BY mostServices.amt_service DESC ";
			query += "LIMIT " + k + ";";
			esql.executeQueryAndPrintResult(query);

		}
		catch(Exception e){
			System.err.println (e.getMessage());
		}
		
	}

	public static void ListCustomersInDescendingOrderOfTheirTotalBill(MechanicShop esql){//10
		try {
			String query = "SELECT c.fname , c.lname, bill_total.total ";
			       query += "FROM Customer c, ";
			       query += "(SELECT SUM(CR.bill) AS total, sr.customer_id ";
			       query += "FROM Closed_Request cr, Service_Request sr ";
			       query += "WHERE cr.rid = sr.rid ";
			       query += "GROUP BY sr.customer_id) AS bill_total ";
			       query += "WHERE c.id=bill_total.customer_id ";
			       query += "ORDER BY bill_total.total DESC;";
			esql.executeQueryAndPrintResult(query);

		}
		catch(Exception e) {
			System.err.println(e.getMessage());
		}	
	}
	
}
