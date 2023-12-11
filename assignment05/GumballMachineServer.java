import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class GumballMachineServer {

	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println("Usage: java GumballMachineTestDrive <location> <port number> <num gumballs>");
			System.exit(1);
		}

		int portNumber = Integer.parseInt(args[1]); 
		int count = Integer.parseInt(args[2]);
		var gumballMachine = new GumballMachine(args[0], count);
		try(ServerSocket soc = new ServerSocket(portNumber)) {
			soc.close();
	  	} catch(IOException e) {
			System.out.println("That port is in use");
			System.exit(0);
	  	}
		while(true) {
			System.out.println("\nOpening socket and waiting");
			int countforGumballActions = 0;
			try (
					ServerSocket serverSocket = new ServerSocket(portNumber);
					Socket clientSocket = serverSocket.accept();
					var outData = new DataOutputStream(clientSocket.getOutputStream());
					//wrap the client socket output stream as a DataOutputStream
					// https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/io/DataOutputStream.html
					var outText = new PrintWriter(clientSocket.getOutputStream(), true );
					//wrap the client socket output stream as a PrintWriter with autoFlush set to true
					// https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/io/PrintWriter.html
					var outObj = new DataOutputStream(clientSocket.getOutputStream());
					//wrap the client socket output stream as an ObjectOutputStream
					// https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/io/ObjectOutputStream.html
					BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					//wrap the client socket input stream as a BufferedReader, wrapping an InputStreamReader
					//https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/io/BufferedReader.html
					// https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/io/InputStreamReader.html
			) {	
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					System.out.println("Received " + inputLine);
					if(inputLine.toLowerCase().equals("getlocation")) {
						// send gumballMachine location (text)
						outText.println(gumballMachine.getLocation());
					} else if(inputLine.toLowerCase().equals("getcount")) {
						// send gumballMachine count (int data)
						outText.println(gumballMachine.getCount());
						
					} else if(inputLine.toLowerCase().equals("getstate")) {
						// send gumballMachine state object (serialized object)
						outText.println(gumballMachine.getState());
						
					} else {
						outText.println("Invalid Request");
					}

					// SIMULATE SOME ACTIVITY IN THE GumballMachine
					switch (countforGumballActions) {
					case 0: gumballMachine.insertQuarter();
					break;
					case 1: gumballMachine.turnCrank();
					break;
					case 2: gumballMachine.insertQuarter();
					break;
					case 3: gumballMachine.ejectQuarter();
					}
					countforGumballActions = (countforGumballActions+1)%4;
				}
			} catch (SocketException e) {
				continue;
			} catch (IOException e) {
				System.out.println("Exception caught when trying to listen on port "
						+ portNumber + " or listening for a connection");
				System.out.println(e.getClass());
				System.out.println(e.getMessage());
			}
		}
	}
}
