import java.net.*;
import java.io.*;
import java.nio.Buffer;

public class Client {

	public static void main(String args[]) throws IOException, InterruptedException {

		System.out.println("Please ensure server is started, enter host:");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String host = reader.readLine();

		System.out.println("Please enter the port");
		int port = Integer.parseInt(reader.readLine());

		// Get command
		System.out.println("Please choose a command to run [1-6]");
		System.out.println("1. date | 2. uptime | 3. free | 4. netstat | 5. who | 6. ps aux");
		String command = getCommand(reader);

		// Get number of simulations
		System.out.println("How many users would you like to simulate?");
		System.out.println("Options: 1, 5, 10, 15, 20, 25");
		int simulations = getSimulations(reader);

		System.out.println("Command: " + command + " Simulations: " + simulations + "\n");

		Thread[] threads = new Thread[simulations];
		final long[] time = new long[simulations];
		final String[] server_response = new String[simulations];

		for (int i = 0; i < simulations; i++) {
			server_response[i] = "";
		}

		for (int i = 0; i < simulations; i++) {
			final int temp = i;
			threads[i] = new Thread(() -> {
				try {
					long startTime = System.currentTimeMillis();
					// Connect and issue commands & simulations
					Socket socket = new Socket(host, port);
					PrintWriter input = new PrintWriter(socket.getOutputStream());
					BufferedReader server_output = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					System.out.println("Connected, sending commands");
					input.println(command);
					input.flush();

					// To do: Build output from what server returns
					String server_output_string = "";
					while ((server_output_string = server_output.readLine()) != null) {
						server_response[temp] += server_output_string + "\n";
					}

					// Close connection
					input.close();
					socket.close();
					time[temp] = System.currentTimeMillis() - startTime;
				} catch (Exception e) {
					System.out.println(e);
				}
			});
		}

		for (int i = 0; i < simulations; i++) {
			threads[i].start();
		}
		for (int j = 0; j < simulations; j++) {
			threads[j].join();
		}
		for (int i = 0; i < simulations; i++) {
			System.out.println(server_response[i]);
		}
		System.out.println("Connection closed");
		long total = 0;
		long avg = 0;
		for (int i = 0; i < simulations; i++) {
			System.out.println("Thread " + (i + 1) + ": " + "Turnaround time: " + time[i] + "ms");
			total = total + time[i];
		}
		System.out.println("Total time: " + total + "ms");
		System.out.println("Average time: " + total / simulations + "ms");
	}

	private static String getCommand(BufferedReader input) throws IOException {
		String command = input.readLine();
		while (!command.matches("[1-6]")) {
			System.out.println("Please input a number 1-6");
			command = input.readLine();
		}
		return command;
	}

	private static int getSimulations(BufferedReader input) throws IOException {
		String command = input.readLine();
		while (!command.matches("(1|5|10|15|20|25)")) {
			System.out.println("Please enter a valid choice: 1, 5, 10, 15, 20, 25");
			command = input.readLine();
		}
		return Integer.parseInt(command);
	}

}