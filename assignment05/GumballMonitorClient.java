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
            while (stdIn.hasNextLine()) {
                userInput = stdIn.nextLine();
                switch (userInput.toLowerCase()) {
                    case "location":
                        out.println("getLocation");
                        String locationResponse = inStr.readLine(); // Read the location from the server
                        System.out.println("GumballMachine Location is " + locationResponse);
                        break;
                    case "count":
                        out.println("getCount");
                        String countResponse = inStr.readLine(); // Read the count from the server
                        System.out.println("GumballMachine Count is " + countResponse);
                        break;
                    case "state":
                        out.println("getState");
                        String stateResponse = inStr.readLine(); // Read the state from the server
                        System.out.println("GumballMachine State is " + stateResponse);
                        break;
                    case "report":
                        out.println("getLocation");
                        String reportLocation = inStr.readLine(); // Read the location from the server

                        out.println("getCount");
                        String reportCount = inStr.readLine(); // Read the count from the server

                        out.println("getState");
                        String reportState = inStr.readLine(); // Read the state from the server

                        System.out.println("Gumball Machine: " + reportLocation);
                        System.out.println("Current inventory: " + reportCount + " gumballs");
                        System.out.println("Current state: " + reportState);
                        break;
                    default:
                        System.out.println("Request \"location\", \"count\", \"state\", or \"report\"");
                }
                System.out.println("Make Request");
            }
        } 
		catch (ClassNotFoundException e) {
			System.err.println("Serialization problem");
			System.exit(1);
		}
		catch (UnknownHostException e) {
            System.err.println("Don't know about host " + location);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    location);
            System.exit(1);
        }
    }
}
