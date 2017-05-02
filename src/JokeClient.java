/*--------------------------------------------------------

1. Mingfei Shao / 09/21/2016:

2. Java version used: build 1.8.0_102-b14

3. Precise command-line compilation examples / instructions:
> javac JokeClient.java

4. Precise examples / instructions to run this program:
In separate shell windows:
> java JokeClient
to connect to the server using default address (localhost) and port number (4545).
or
> java JokeClient <IPAddr>
to connect to the server using user-defined address and port number (4545).
or
> java JokeClient <IPAddr> <IPAddr>
to connect to the server using user-defined primary address and primary port number (4545),
and using user-defined secondary address and secondary port number (4546).

5. List of files needed for running the program:
a. JokeServer.java
b. JokeClient.java
c. JokeClientAdmin.java
d. JokeLog.txt
e. checklist.html

5. Notes:
a. This JokeClient can connect to 1 or 2 servers at the same time, if a secondary server is in using, it can be switched
   by entering "s".
b. This JokeClient is capable to send request and receive jokes/proverbs from a JokeServer.
c. The command "quit" can be used to quit the client and signal the server so it can delete the status of this client stored.

----------------------------------------------------------*/

// Get the Input Output libraries

import java.io.*;
// Get the Java networking libraries
import java.net.*;
// Get the UUID API in Java utility libraries
import java.util.UUID;

public class JokeClient {
    // Define default primary port number
    private static final int DEFAULT_PRIMARY_SERVER_PORT = 4545;
    // Define default secondary port number
    private static final int DEFAULT_SECONDARY_SERVER_PORT = 4546;
    // Define default primary server address
    private static final String DEFAULT_SERVER_ADDR = "localhost";

    static void getRemoteResponse(String username, String uuid, String serverName, int serverPort) {
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
            toServer.println(username);
            toServer.flush();
            toServer.println(uuid);
            toServer.flush();
            // Read up to 3 lines from the reply of server and output them
            for (int i = 1; i <= 3; i++) {
                textFromServer = fromServer.readLine();
                if (textFromServer != null)
                    System.out.println(textFromServer);
            }
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
        serverNameList[0] = DEFAULT_SERVER_ADDR;
        // Leave secondary server address blank
        serverNameList[1] = null;
        // Create an array of integers to store primary and secondary server ports, 0 for primary and 1 for secondary
        int[] serverPortList = new int[2];
        // Assign default primary server port
        serverPortList[0] = DEFAULT_PRIMARY_SERVER_PORT;
        // Assign default secondary server port
        serverPortList[1] = DEFAULT_SECONDARY_SERVER_PORT;
        // Boolean value indicating if we have secondary server
        boolean hasSecondary = false;
        // Boolean value indicating if we are using secondary server
        boolean usingSecondary = false;
        // If we are using secondary server, this index is 1, otherwise it is 0
        int listIndex = (usingSecondary) ? 1 : 0;
        // Generate a random UUID for client
        UUID uuid = UUID.randomUUID();

        System.out.println("Mingfei Shao's Joke Client.");
        System.out.println();
        // Single argument, specifies user-defined primary server address
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
        System.out.println("Server one: " + serverNameList[0] + ", Port: " + serverPortList[0]);
        // Print secondary server info, if any
        if (hasSecondary) {
            System.out.println("Server two: " + serverNameList[1] + ", Port: " + serverPortList[1]);
        }

        // Initialize input stream as a BufferedReader
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            String username;
            String command;
            do {
                System.out.println("Please enter your name: ");
                System.out.flush();
                // Get username from user input
                username = in.readLine();

                // Empty input is not a vaild username
                if (username.isEmpty()) {
                    System.out.println("Username cannot be empty.");
                    System.out.flush();
                }
            } while (username.isEmpty());

            // Print current server info
            System.out.println("Now communicating with: " + serverNameList[listIndex] + ", port " + serverPortList[listIndex]);
            // Print hints for user
            System.out.println("In server query loop, press Enter for new joke/proverb, enter s to toggle server, enter quit to exit: ");
            // Flush output buffer to clean it
            System.out.flush();
            do {
                // Get command from user, "quit" to exit, "s" to switch between servers, anything other than that are used as request signals
                command = in.readLine();
                if (!command.equalsIgnoreCase("quit")) {
                    // Server switching command received
                    if (command.equalsIgnoreCase("s")) {
                        // No secondary server has been specified
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
                    } else {
                        // Send UUID and username to server, requesting new joke/proverb
                        getRemoteResponse(username, uuid.toString(), serverNameList[listIndex], serverPortList[listIndex]);
                    }
                } else {
                    // "quit" command received, send null as customized signal to server to delete status table
                    getRemoteResponse("", uuid.toString(), serverNameList[listIndex], serverPortList[listIndex]);
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
