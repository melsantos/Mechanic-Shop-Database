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
					case 1: AddCustomer(esql); break;
					case 2: AddMechanic(esql); break;
					case 3: AddCar(esql); break;
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
	
	public static void AddCustomer(MechanicShop esql){//1
		try {
			String insertCust = "INSERT INTO customer (fname, lname, phone, address) VALUES ";
			// int id = 0;
			String fname = "";
			String lname = "";
			String phone = "";
			String address = "";
			Scanner scan = new Scanner(System.in);
			Boolean valid = false;
			
			// Check Customer ID validity
			/*do {
				System.out.print("\tEnter customer's ID: $");
				try {
					id = scan.nextInt();	
					valid = true;
				} catch (java.util.InputMismatchException e) { 
					System.out.println("Customer ID must be an integer");
					valid = false;
					scan.nextLine();
				}
			} while (!valid);*/

			// Check first name validty
			do {
				System.out.print("\tEnter customer's first name: $");
				try {
					fname = scan.next();	
					if (fname.length() == 0 || fname.length() > 32) {
						throw new IllegalArgumentException("Customer's first name must be between 1 and 32 characters (inclusive)");
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
				System.out.print("\tEnter customer's last name: $");
				try {
					lname = scan.next();	
					if (lname.length() == 0 || lname.length() > 32) {
						throw new IllegalArgumentException("Customer's last name must be between 1 and 32 characters (inclusive)");
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
				System.out.print("\tEnter customer's phone #: $");
				try {
					phone = scan.next();	
					if (phone.length() == 0 || phone.length() > 13) {
						throw new IllegalArgumentException("Customer's phone # must be between 1 and 13 characters (inclusive)");
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
				System.out.print("\tEnter customer's address: $");
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
		}
		catch(Exception e){
			System.err.println (e.getMessage());
		}	
	}
	
	public static void AddMechanic(MechanicShop esql){//2
		try {
			String insertMech = "INSERT INTO mechanic (fname, lname, experience) VALUES ";
			// int id = 0;
			String fname = "";
			String lname = "";
			int years = 0;
			Scanner scan = new Scanner(System.in);
			Boolean valid = false;
			
			// Check Mechanic ID validity
			/*do {
				System.out.print("\tEnter mechanic's ID: $");
				try {
					id = scan.nextInt();	
					valid = true;
				} catch (java.util.InputMismatchException e) { 
					System.out.println("Mechanic ID must be an integer");
					valid = false;
					scan.nextLine();
				}
			} while (!valid);*/

			// Check first name validty
			do {
				System.out.print("\tEnter mechanic's first name: $");
				try {
					fname = scan.next();	
					if (fname.length() == 0 || fname.length() > 32) {
						throw new IllegalArgumentException("Mechanic's first name must be between 1 and 32 characters (inclusive)");
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
				System.out.print("\tEnter mechanic's last name: $");
				try {
					lname = scan.next();	
					if (lname.length() == 0 || lname.length() > 32) {
						throw new IllegalArgumentException("Mechanic's last name must be between 1 and 32 characters (inclusive)");
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
				System.out.print("\tEnter mechanic's years of experience: $");
				try {
					years = scan.nextInt();	
					if(years < 0 || years > 99) {
						throw new IllegalArgumentException("Years of experience must be between 1 and 100");
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
	
	public static void AddCar(MechanicShop esql){//3
		try{
			String vin = "";
			String make = "";
			String model = "";
			String year = "";
			Boolean valid = false;

			// Get car vin loop
			do {
				System.out.print("\tEnter car vin: $");
				try {
					vin = in.readLine();
					if (vin.length() == 0 || vin.length() > 16){
						throw new IllegalArgumentException("Car VIN must be between 1 and 16 characters (inclusive)");
					}
					valid = true;
				} catch (IllegalArgumentException e) {
					System.out.println(e.getMessage());
					valid = false;
				}
			} while(!valid);

			// Get car make loop
			do {
				System.out.print("\tEnter car make: $");
				try {
					make = in.readLine();
					if (make.length() == 0 || make.length() > 32){
						throw new IllegalArgumentException("Car MAKE must be between 1 and 32 characters (inclusive)");
					}
					valid = true;
				} catch(IllegalArgumentException e) {
					System.err.println(e.getMessage());
					valid = false;
				}
			} while(!valid);

			// Get car model loop
			do {
				try{	
					System.out.print("\tEnter car model: $");
					model = in.readLine();
					if (model.length() == 0 || model.length() > 32){
						throw new IllegalArgumentException("Car MODEL must be between 1 and 32 characters (inclusive)");
					}
					valid = true;
				} catch(IllegalArgumentException e) {
					System.err.println(e.getMessage());
					valid = false;
				}
			} while(!valid);

			// Get car year loop
			do {
				try {
					System.out.print("\tEnter car year: $");
					year = in.readLine();
					if (year.length() == 0){
						throw new IllegalArgumentException("Invalid car YEAR");
					}

					// Throws an exception if year is not an integer amount
					int year_int = Integer.parseInt(year);
 
					if(year_int < 1970) {
						throw new IllegalArgumentException("Car YEAR must be 1970 or newer");
					}
					valid = true;
				} catch(NumberFormatException e) {
					System.err.println("Car YEAR must be an int");
					valid = false;

				} catch(IllegalArgumentException e) {
					System.err.println(e);
					valid = false;
				}
			} while(!valid);

			String query = "INSERT INTO Car VALUES (\'" + vin + "\', \'" + make + "\', \'" + model + "\', \'" + year + "\')";
			esql.executeUpdate(query);
		}
		catch(Exception e){
			System.err.println (e.getMessage());
		}

	}
	

	public static void InsertServiceRequest(MechanicShop esql){//4
		try{
			int cid = -1;
			// Step 1: Search last name
			System.out.print("\tEnter customer's last name: $ ");
			String lname = in.readLine();
			String lnameQuery = "SELECT * FROM Customer WHERE Customer.lname = \'" + lname + "\'";
			List<List<String>> lnameResults = esql.executeQueryAndReturnResult(lnameQuery);
			
			if (lnameResults.size() > 0){
				// Display all matching results
				for (int i = 0; i < lnameResults.size(); ++i){
					System.out.print("\t");
					System.out.print(i);
					System.out.print(": ");
					for (int j = 1; j < lnameResults.get(i).size(); ++j){
						System.out.print(lnameResults.get(i).get(j));
						System.out.print(", ");
					}
					System.out.print("\n");
				}
				// Select customer from results
				System.out.print("\n\tEnter desired customer's option id: $ ");
				int cust_choice = Integer.parseInt(in.readLine());
				if (cust_choice < 0 || cust_choice >= lnameResults.size()){
					throw new IllegalArgumentException("Invalid customer option id"); 
				}
				cid = Integer.parseInt(lnameResults.get(cust_choice).get(0));
			}
			else {
				// Customer did not exist so offer to create a new one
				System.out.println("\tCould not find a customer with that last name");
				System.out.print("\tWould you like to add a new customer? [Y/N]: $ ");
				String go = in.readLine();
				if (go.equals("Y")){
					// FIXME: Add new customer
					return;
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
		
			String carsOwnedQuery = "SELECT C.vin, C.make, C.model, C.year FROM Car C, Owns O WHERE O.customer_id = " + cid + " AND O.car_vin = C.vin";
			List<List<String>> carsOwnedResults = esql.executeQueryAndReturnResult(carsOwnedQuery);

			if (carsOwnedResults.size() > 0){
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
				System.out.print("\n\tWould you like to enter a service request for one of these cars? [Y/N]: $ ");
				String go = in.readLine();
				if (go.equals("Y")){
					// Select customer's car
					System.out.print("\tEnter desired car's option id: $ ");
					int car_choice = Integer.parseInt(in.readLine());
					if (car_choice < 0 || car_choice >= carsOwnedResults.size()){
						throw new IllegalArgumentException("Invalid car option id"); 
					}
					vin = carsOwnedResults.get(car_choice).get(0);

				}
				else {
					// Offer to create a car to continue the service request
					createCar = true;
				}
			
			}
			else{
				// Customer has no car associated in DB so ask to create one
				createCar = true;
			}
			if (createCar){
				// Offer to create a car
				System.out.print("\tWould you like to add a car for " + lnameResults.get(cid).get(1) +  "? [Y/N]: $ ");
				String go = in.readLine();
				if (go.equals("Y")){
					// FIXME: Add a new car
					// FIXME: Link it to customer
					// Return car vin
				}
				else{
					// Cancel service request
					System.out.println("\tCancelling service request");
					return;
				}
			}
			
			// Step 3: Complete service request
			
			// Odometer
			System.out.print("\tEnter the odometer reading of the car: $ ");
			int odometer = Integer.parseInt(in.readLine());
			if (odometer < 0){
				throw new IllegalArgumentException("Invalid customer option id");
 			}
			
			// Complaint
			System.out.print("\tEnter customer complaint about car: $ ");
			String complaint = in.readLine();
			
			// Date
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");  
			LocalDateTime now = LocalDateTime.now();  

			// FIXME: Need auto-increment for RID field
			String query = "INSERT INTO Service_Request(customer_id, car_vin, date, odometer, complain) VALUES (";
			query += cid + ", \'" + vin + "\', \'" + dtf.format(now) + "\', " + odometer + ", \'" + complaint + "\')";
			esql.executeQuery(query); 

		}
		catch (NumberFormatException e){
			System.err.println ("Error: Input must be a valid integer amount");
		}
		catch(Exception e){
			System.err.println (e.getMessage());
		}
	}
	
	public static void CloseServiceRequest(MechanicShop esql) throws Exception{//5
		
	}
	
	public static void ListCustomersWithBillLessThan100(MechanicShop esql){//6
		try{
			String query = "SELECT date,comment,bill FROM Closed_Request WHERE bill < 100;";
			esql.executeQueryAndPrintResult(query);
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
		}	
	}
	
	public static void ListCustomersWithMoreThan20Cars(MechanicShop esql){//7
		
		try{
			String query = "SELECT fname, lname FROM Customer, (SELECT customer_id,COUNT(customer_id) as car_num FROM Owns GROUP BY customer_id HAVING COUNT(customer_id) > 20) AS O WHERE O.customer_id = id;";
			esql.executeQueryAndPrintResult(query);
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void ListCarsBefore1995With50000Milles(MechanicShop esql){//8
		try{
			String query = "SELECT DISTINCT Car.vin, Car.make, Car.model, Car.year ";
			query += "FROM Car ";
			query += "WHERE Car.vin = Any( ";
				query += "SELECT S.car_vin ";
				query += "FROM Service_Request S ";
				query += "WHERE S.odometer = 50000 ";

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
			System.out.print("\tEnter k: $");
			String k = in.readLine();
			int k_int = Integer.parseInt(k);
			if (k_int != 0){			
				String query = "SELECT C.vin, C.make, C.model, C.year ";
				query += "FROM Car C, (";
					query += "SELECT S.car_vin, COUNT(S.car_vin) as amt_service ";
					query += "FROM Service_Request S ";
					query += "GROUP BY S.car_vin ";
					query += "ORDER BY amt_service DESC ";
					query += "LIMIT " + k + ") mostServices ";
				query += "WHERE C.vin = mostServices.car_vin; ";
				esql.executeQueryAndPrintResult(query);
			}
		}
		catch(Exception e){
			System.err.println (e.getMessage());
		}
		
	}

	public static void ListCustomersInDescendingOrderOfTheirTotalBill(MechanicShop esql){//10
		try {
			String query = "SELECT C.fname , C.lname, Total ";
			       query += "FROM Customer AS C,";
			       query += "(SELECT sr.customer_id, SUM(CR.bill) AS Total ";
			       query += "FROM Closed_Request AS CR, Service_Request AS SR ";
			       query += "WHERE CR.rid = SR.rid ";
			       query += "GROUP BY SR.customer_id) AS A ";
			       query += "WHERE C.id=A.customer_id ";
			       query += "ORDER BY A.Total DESC;";
			esql.executeQueryAndPrintResult(query);

		}
		catch(Exception e) {
			System.err.println(e.getMessage());
		}	
	}
	
}
