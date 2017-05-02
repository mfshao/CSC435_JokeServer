/*--------------------------------------------------------

1. Mingfei Shao / 09/21/2016:

2. Java version used: build 1.8.0_102-b14

3. Precise command-line compilation examples / instructions:
> javac JokeClientAdmin.java

4. Precise examples / instructions to run this program:
In separate shell windows:
> java JokeClientAdmin
to connect to the admin server using default address (localhost) and port number (5050).
or
> java JokeClientAdmin <IPAddr>
to connect to the admin server using user-defined address and port number (5050).
or
> java JokeClientAdmin <IPAddr> <IPAddr>
to connect to the admin server using user-defined primary address and primary port number (5050),
and using user-defined secondary address and secondary port number (5051).

5. List of files needed for running the program:
a. JokeServer.java
b. JokeClient.java
c. JokeClientAdmin.java
d. JokeLog.txt
e. checklist.html

5. Notes:
a. This JokeClient can connect to 1 or 2 servers at the same time, if a secondary server is in using, it can be switched
   by entering "s".
b. This JokeClient is capable to send server mode change command to the server, which can switch the server between joke
   and proverb modes.
c. The command "quit" can be used to quit the client.

----------------------------------------------------------*/

// Get the Input Output libraries

import java.io.*;
// Get the Java networking libraries
import java.net.*;

public class JokeClientAdmin {
    // Define default primary port number
    private static final int DEFAULT_PRIMARY_ADMIN_PORT = 5050;
    // Define default secondary port number
    private static final int DEFAULT_SECONDARY_ADMIN_PORT = 5051;
    // Define default primary server address
    private static final String DEFAULT_PRIMARY_ADMIN_ADDR = "localhost";

    static void sendSignal(String command, String serverName, int serverPort) {
        Socket sock;
        BufferedReader fromServer;
        PrintStream toServer;
        String textFromServer;
        try {
            // Open socket using given server address and port number
            sock = new Socket(serverName, serverPort);
            // Initialize the input stream of the socket as BufferedReader
            fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            // Initialize the output stream of the socket as PrintStream
            toServer = new PrintStream(sock.getOutputStream());
            // Send user input server name to server for query.
            toServer.println(command);
            toServer.flush();
            textFromServer = fromServer.readLine();
            if (textFromServer != null)
                System.out.println(textFromServer);
            // Close the socket
            sock.close();
            // In case the socket cannot be created for some reason
        } catch (IOException x) {
            System.out.println("Socket error.");
            x.printStackTrace();
        }
    }

    public static void main(String args[]) {
        // Create an array of Strings to store primary and secondary server addresses, 0 for primary and 1 for secondary
        String[] serverNameList = new String[2];
        // Assign default primary server address
        serverNameList[0] = DEFAULT_PRIMARY_ADMIN_ADDR;
        // Leave secondary server address blank
        serverNameList[1] = null;
        // Create an array of integers to store primary and secondary server ports, 0 for primary and 1 for secondary
        int[] serverPortList = new int[2];
        // Assign default primary server port
        serverPortList[0] = DEFAULT_PRIMARY_ADMIN_PORT;
        // Assign default secondary server port
        serverPortList[1] = DEFAULT_SECONDARY_ADMIN_PORT;
        // Boolean value indicating if we have secondary server
        boolean hasSecondary = false;
        // Boolean value indicating if we are using secondary server
        boolean usingSecondary = false;
        // If we are using secondary server, this index is 1, otherwise it is 0
        int listIndex = (usingSecondary) ? 1 : 0;

        System.out.println("Mingfei Shao's Joke Admin Client.");
        System.out.println();
        if (args.length == 1) {
            serverNameList[0] = args[0];
            // Multiple arguments, means we are using secondary server
        } else if (args.length > 1) {
            hasSecondary = true;
            serverNameList[0] = args[0];
            // Assign user-defined secondary server address
            serverNameList[1] = args[1];
        }

        // Print primary server info
        System.out.println("Admin server one: " + serverNameList[0] + ", Port: " + serverPortList[0]);
        // Print secondary server info, if any
        if (hasSecondary) {
            System.out.println("Admin server two: " + serverNameList[1] + ", Port: " + serverPortList[1]);
        }

        // Initialize input stream as a BufferedReader
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        // Print current server info
        System.out.println("Now communicating with: " + serverNameList[listIndex] + ", port " + serverPortList[listIndex]);

        try {
            String command;

            // Print hints for user
            System.out.print("Press Enter to change server mode, enter s to toggle server, enter shutdown to shutdown server, enter quit to exit: ");
            // Flush output buffer to clean it
            System.out.flush();
            do {
                command = in.readLine(); // Get command from user, "quit" to exit, "s" to switch between servers, anything other than that are used as toggling signals
                if (!command.equalsIgnoreCase("quit")) {
                    // Server switching command received
                    if (command.equalsIgnoreCase("s")) {
                        if (serverNameList[1] == null) {
                            System.out.println("No secondary server being used.");
                        } else {
                            // Change if we are using the secondary server
                            usingSecondary = !usingSecondary;
                            // Get new index, 1 for using secondary server, 0 for using primary server
                            listIndex = (usingSecondary) ? 1 : 0;
                            // Print current server info
                            System.out.println("Now communicating with: " + serverNameList[listIndex] + ", port " + serverPortList[listIndex]);
                        }
                        // Toggling signals
                    }else if (command.equalsIgnoreCase("shutdown")){
                        sendSignal(command, serverNameList[listIndex], serverPortList[listIndex]);
                    } else{
                        System.out.println("Server mode toggling signal sent to " + ((usingSecondary) ? "secondary" : "primary") + " Admin server.");
                        // Send toggling command to server
                        sendSignal(command, serverNameList[listIndex], serverPortList[listIndex]);
                    }
                }
            }
            // do-while to make sure the above logic will be executed at least once
            while (!command.equalsIgnoreCase("quit"));
            System.out.println("Cancelled by user request.");
        } catch (IOException x) {
            // In case read from input stream fails
            x.printStackTrace();
        }
    }
}
