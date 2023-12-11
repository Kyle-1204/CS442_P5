import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class GumballMonitorClient {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Usage: java GumballMachineClient <location> <port number>");
			System.exit(1);
		}

		int portNumber = Integer.parseInt(args[1]); 
		String location = args[0];
		try (
				var reportSocket = new Socket(location, portNumber);
				var out = new PrintWriter(reportSocket.getOutputStream(), true );
				//wrap the report socket output stream as a PrintWriter with autoFlush set to true
				// https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/io/PrintWriter.html
				var inData = new DataInputStream(reportSocket.getInputStream());
				//wrap the report socket input stream as a DataInputStream
				// https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/io/DataInputStream.html
				var inScan = new Scanner(reportSocket.getInputStream());
				//wrap the report socket input stream as a Scanner
				// https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/Scanner.html
				var inStr = new BufferedReader(new InputStreamReader(reportSocket.getInputStream())); //This didn't compile previously without new InputStreamReader
				// wrap the report socket input stream as a BufferedReader, wrapping an InputStreamReader
				//https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/io/BufferedReader.html
				// https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/io/InputStreamReader.html
				var inObj = new ObjectInputStream(reportSocket.getInputStream());
				//wrap the report socket input stream as an ObjectInputStream
				// https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/io/ObjectInputStream.html
				var stdIn = new Scanner(System.in);
		) {
			System.out.println("Make Status Request");	
			String userInput;
			while(stdIn.hasNextLine()) {
				userInput = stdIn.nextLine();
				switch(userInput.toLowerCase()) {
				case "location":
					out.println("getLocation");
					//Print to the console "GumballMachine Location is " followed by the location sent by the server
					String locationResponse = inStr.readLine();
            		System.out.println("GumballMachine Location is " + locationResponse);
					break;
				case "count":
					out.println("getCount");
					//Print to the console "GumballMachine Count is " followed by the count sent by the server
					String countResponse = inStr.readLine();
            		System.out.println("GumballMachine Count is " + countResponse);
					break;
				case "state":
					out.println("getState");
					//Print to the console "GumballMachine State is " followed by the state object sent by the server
					//NOTE, the toString of state will be called automatically when following a String
					Object stateResponse = inObj.readObject();
            		System.out.println("GumballMachine State is " + stateResponse);
					break;
				case "report":
					// TODO:
					// use all the of the above to output a complete report as in the textbooks RMI version:
					// 	Gumball Machine: localhost
					// 	Current inventory: 44 gumballs
					// 	Current state: waiting for turn of crank
					// Request location, count, and state from the server
					out.println("getLocation");
					String locationReport = inStr.readLine(); // Read location response
					out.println("getCount");
					String countReport = inStr.readLine(); // Read count response
					out.println("getState");
					Object stateReport = inObj.readObject(); // Read state object
					    // Display the complete report
					System.out.println("Gumball Machine: " + locationReport);
					System.out.println("Current inventory: " + countReport + " gumballs");
					System.out.println("Current state: " + stateReport.toString());
					break;
				default:
					System.out.println("Request \"location\", \"count\", or \"state\" ");
				}
				System.out.println("Make Request");	
			}
		} catch (ClassNotFoundException e) {
			System.err.println("Serialization problem");
			System.exit(1);
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + location);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " +
					location);
			System.exit(1);
		} 	
	}
}
