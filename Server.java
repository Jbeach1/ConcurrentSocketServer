import java.net.*;
import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public class Server {

	public static void main(String args[]) throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please enter host to run on");
		String host = reader.readLine();
		System.out.println("Please enter port to run on.");
		int port = Integer.parseInt(reader.readLine());

		InetAddress host_address = InetAddress.getByName(host);

		// Make connection
		ServerSocket server = new ServerSocket(port, 25, host_address);
		System.out.println("Server started, waiting for a client...");

		try {
			while (true) {
				Socket socket = server.accept();
				new Thread(() -> {
					try {
						// Get command & iterations from client
						BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						PrintWriter output = new PrintWriter(socket.getOutputStream());
						String command;

						if ((command = input.readLine()) != null) {
							// Do the commands
							switch (command) {
							case "1":
								run_cmd("date", output);
								break;
							case "2":
								run_cmd("uptime", output);
								break;
							case "3":
								run_cmd("free", output);
								break;
							case "4":
								run_cmd("netstat", output);
								break;
							case "5":
								run_cmd("who", output);
								break;
							case "6":
								run_cmd("ps aux", output);
								break;
							default:
								System.out.println("Command not recognized");
							}
						}
						// Exit system
						output.flush();
						output.close();
						input.close();
						socket.close();

					} catch (IOException i) {
						System.out.println(i);
					}

				}).start();
			}
		} catch (IOException i) {
			System.out.println(i);
		}
	}

	private static void run_cmd(String cmd, PrintWriter output) throws IOException {
		String cmd_output_string = null;
		Runtime rt = Runtime.getRuntime();
		Process process = rt.exec(cmd);
		BufferedReader command_output = new BufferedReader(new InputStreamReader(process.getInputStream()));
		while ((cmd_output_string = command_output.readLine()) != null) {
			output.println(cmd_output_string);
			System.out.println(cmd_output_string);
		}
	}

}