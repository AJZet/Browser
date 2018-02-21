import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XserverThreadedPool {
	private static class handleConnection implements java.lang.Runnable {
		private Socket socket;
		public handleConnection (Socket socket) {
			this.socket = socket;
		}
		public void run() {
			//System.out.println(Thread.currentThread() +" Statring task ");
			try {
				//output PrintStream object, writes response to the client
				PrintStream out = new PrintStream(socket.getOutputStream());
				//input stream reads request from the client
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				String l;
				int counter = 0;
				String path = null;
				//String hostName = null;
				String status = null;
				
				//read the get request
				while (!(l = in.readLine()).isEmpty()) {
					//System.out.println(l);
					if (counter == 0) {
						if (l.startsWith("GET ") && l.endsWith(" HTTP/1.1")) {
							String [] a = l.split(" ");
							path = a[1];
							//System.out.println(path);
	
						}
						else
							status = "400";
					}
					if (counter == 1) {
						if (l.startsWith("Host: ")) {
							//String [] a = l.split(" ");
							//hostName = a[1];
							status = "200";
						}
						else
							status = "400";
					}
					else
						status = "400";
					
					counter++;
					
				}
				//System.out.println(status);
	
				
				//reply to the client
				
				//bad request
				if (status == "400" || status == null) {
					out.println("HTTP/1.1 400 Bad Request");
					socket.close();
				}
				//respond
				else {
					if (path == "/") {
						out.println("HTTP/1.1 200 OK");
						socket.close();
					}
					//find the file
					else {
	
						path = path.substring(1, path.length());
						//System.out.println(path);
						File file = new File(path);
						//file not found
						if (file.length() == 0) {
							out.println("HTTP/1.1 404 Not Found");
							socket.close();
						}
						else {
							BufferedReader reader = new BufferedReader(new FileReader(file));
							//print header information
							out.println("HTTP/1.1 200 OK");
							out.println("Content-Length: " + file.length());
							out.println("Connection: close");
							out.println();
							System.out.println("HTTP/1.1 200 OK");
							System.out.println("Content-Length: " + file.length());
							System.out.println("Connection: close");
							System.out.println();
							//print content of the file
							/*String line ="";
							while (!(line = reader.readLine()).isEmpty()){
								System.out.println(line);
							    out.println(line);
							}*/
							int i;
							while ((i = reader.read()) != -1){
								//System.out.print((char) i);
							    out.write((char) i);
							}
							reader.close();
						}
					}
				}
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//System.out.println(Thread.currentThread() +" Completed task ");

		}

	}
	
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
            System.err.println(
                "Usage: java Xserver serverPort");
            System.exit(1);
        }
		int serverPort = Integer.parseInt(args[0]);
		ServerSocket sSocket = new ServerSocket(serverPort, 5);
		
		final ExecutorService executor = Executors.newFixedThreadPool(30);
		try {
			while (true) {
				//System.out.println("Server waiting for connection");
				Socket cSocket = sSocket.accept();
				//System.out.println("Connection accepted");
				executor.submit(new handleConnection(cSocket));
				//System.out.println("Connection closed");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			//System.out.println("Close the server");
			sSocket.close();
		}

	}
}
