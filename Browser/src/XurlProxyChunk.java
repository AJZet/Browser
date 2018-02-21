import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class XurlProxyChunk{

	public static void main(String[] args) {
		
		//System.setProperty("line.seperator", "\r\n");
		
        if ((args.length != 1) && (args.length != 3)) {
            System.err.println(
                "Usage: java Xurl <url> [<proxyname> <proxyport>]");
            System.exit(1);
        }
  	
		//URL passed in as an argument
		MyURL u = new MyURL(args[0]);
		
		String hostName = u.getHost();
		Integer portNumber = u.getPort();
		String path = u.getPath();
		String protocol = u.getProtocol();
			
		if (portNumber.equals(-1) && protocol.equals("http"))
			portNumber = 80;
		if (portNumber.equals(-1) && protocol.equals("https"))
			portNumber = 443;

		try {
			//connected socket
			//Socket mySocket = new Socket(hostName, portNumber);
			Socket mySocket = new Socket();
			
			InetSocketAddress ad = new InetSocketAddress(hostName, portNumber);
			//change ad to proxy address
			if (args.length == 3) {
				ad = new InetSocketAddress(args[1], Integer.parseInt(args[2]));
			}
			mySocket.connect(ad, 1000000);
			//output PrintStream object, writes the request to the server
			PrintStream out = new PrintStream(mySocket.getOutputStream());
			//input stream reads input from the server
			BufferedReader in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
			//request without proxy
			if (args.length == 1)
			{
				//System.out.println("GET " + path + " HTTP/1.1");
	            //System.out.println("Host: " + hostName);
	            //System.out.println("");
	
	            out.println("GET " + path + " HTTP/1.1\r");
	            out.println("Host: " + hostName + "\r");
	            out.println("\r");
	            out.flush();
			}
			//request with proxy
			else
			{
				//System.out.println("GET " + args[0] + " HTTP/1.1\r");
	            //System.out.println("Host: " + hostName + "\r");
	            //System.out.println("\r");
	
	            out.println("GET " + args[0] + " HTTP/1.1\r");
	            out.println("Host: " + hostName + "\r");
	            out.println("\r");
	            out.flush();
			}
			
            int contentLength = 0;
	    		//status code
	    		String status = "";
			
            boolean statusCode = false;
            boolean chunked = false;
            String l;
            while (!(l = in.readLine()).isEmpty()) {
            		//System.out.println(l);
            		if (statusCode == false) {
            			statusCode = true;
            			status = l.substring(9);
            			}
            	  	if (l.startsWith("Content-Length: "))
            	  		contentLength = Integer.parseInt(l.replace("Content-Length: ", ""));
            	  	if (l.startsWith("Transfer-Encoding: chunked"))
            	  		chunked = true;
            }
            //if the status code is OK -> download the file
            int carCount = 0;

            if (status.matches("2.....")) {
            		String filename = "";
        			if (path.endsWith("/"))
        				filename = "index.html";
        			if (path.contains(".")) {
        				String a [];
        				a = path.split("/");
        				filename = a[a.length - 1];
        			}
        				path = path.concat(".html");
                File file = new File(filename);
                FileOutputStream fos = new FileOutputStream(file);
                //start reading the input stream again and write it to file
                if (chunked == false) {
	                int c;
	                while (carCount < contentLength) {
	                		c = in.read();
	                		//System.out.print((char) c); 
	                		fos.write((char) c);
	                		carCount++;
	                }
                }
                else {
                		int c;
	                	int chunkSize = Integer.parseInt(in.readLine(), 16);
	                	while (chunkSize !=0) {
	                		for (int i = 0; i < chunkSize; i++) {
	                			c = in.read();
		                		//System.out.print((char) c); 
		                		fos.write((char) c);
	                		}
	                		in.readLine();
	                		//if (!(line.isEmpty()))
	                			//chunkSize = Long.parseLong(line, 16);
	                		//else
	                		chunkSize = Integer.parseInt(in.readLine(), 16);
	                		
	                	}
	            }
                fos.close();
            }
            in.close();
            out.close();
            mySocket.close();        		
		}
		catch(IOException e) {
	         e.printStackTrace();
	         System.exit(1);
	    }
		
	}

}

