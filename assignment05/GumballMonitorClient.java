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
			var out = new PrintWriter(reportSocket.getOutputStream(), true);
			var inData = new DataInputStream(reportSocket.getInputStream());
			var inScan = new Scanner(reportSocket.getInputStream());
			var inStr = new BufferedReader(new InputStreamReader(reportSocket.getInputStream()));
			var inObj = new ObjectInputStream(reportSocket.getInputStream());
			var stdIn = new Scanner(System.in);
		) {
			System.out.println("Make Status Request");	
			String userInput;
			String countResponse;
			String locationResponse;
			String stateResponse;
			while(stdIn.hasNextLine()) {
				userInput = stdIn.nextLine();
				switch(userInput.toLowerCase()) {
				case "location":
					out.println("getLocation");
					//Print to the console "GumballMachine Location is " followed by the location sent by the server
					locationResponse = inStr.readLine();
            		System.out.println("GumballMachine Location is " + locationResponse);
					break;
				case "count":
					out.println("getCount");
					//Print to the console "GumballMachine Count is " followed by the count sent by the server
					countResponse = inStr.readLine();
            		System.out.println("GumballMachine Count is " + countResponse);
					break;
				case "state":
					out.println("getState");
					//Print to the console "GumballMachine State is " followed by the state object sent by the server
					//NOTE, the toString of state will be called automatically when following a String
					// Object stateResponse = inObj.readObject();
					stateResponse = inStr.readLine();
            		System.out.println("GumballMachine State is " + inStr.readLine());
					break;
				case "report":
					// TODO:
					// use all the of the above to output a complete report as in the textbooks RMI version:
					// 	Gumball Machine: localhost
					// 	Current inventory: 44 gumballs
					// 	Current state: waiting for turn of crank
					// Request location, count, and state from the server
					//out.println("getLocation");
					//String locationReport = inStr.readLine(); // Read location response
					//out.println("getCount");
					//String countReport = inStr.readLine(); // Read count response
					//out.println("getState");
					//Object stateReport = inObj.readObject(); // Read state object
					    // Display the complete report
					System.out.println("Gumball Machine: " + locationResponse);
					System.out.println("Current inventory: " + countResponse + " gumballs");
					System.out.println("Current state: " + stateResponse);;
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
