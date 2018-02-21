import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class XurlProxy {

	public static void main(String[] args) throws IOException {
		
        System.setProperty("line.separator","\r\n");
		
        if (args.length != 1 || args.length != 3) {
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
		//status code
		String status = "";
			
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
				System.out.println("GET " + path + " HTTP/1.1");
	            System.out.println("Host: " + hostName);
	            System.out.println("");
	
	            out.println("GET /" + path + " HTTP/1.1");
	            out.println("Host: " + hostName);
	            out.println("");
			}
			//request with proxy
			else
			{
				System.out.println("GET " + args[0] + " HTTP/1.1");
	            System.out.println("Host: " + hostName);
	            System.out.println("");
	
	            out.println("GET /" + args[0] + " HTTP/1.1");
	            out.println("Host: " + hostName);
	            out.println("");
			}
			
            boolean statusCode = false;
            boolean html = false;
            String loc = "";
            
            //read the input stream char by char
            int c;
            while ((c = in.read()) != -1) {
            	  Character car = (char) c;
               System.out.print(car);
            	   out.print(car);
        		   String s = Character.toString(car);
            	   if (statusCode == true && status.length() < 3) {
            		   status = status.concat(s);
            	   }
            	   if (Character.isWhitespace(car) && status == "")
            		   statusCode = true;
            	   if (Character.isWhitespace(car) && status != "")
            		   statusCode = false;
            	   
            	   //if status code is OK and 2 enters were met, stop reading the input stream
                if (status.matches("2..")) {
		            if (s.equals("\n"))
		     			   html = true;
		     		if (html == true)
		     			loc = loc.concat(s);
		     		if (loc.equals("\n\r\n"))
		     			  break;
		     		if (!(loc.equals("\n\r") || loc.equals("\n"))) {
		     			loc = "";
		     			html = false;
		     		}
                			
                }
            }
            //if the status code is OK -> download the file
            if (status.matches("2..")) {
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
                while ((c = in.read()) != -1) {
                		fos.write((char) c);
                }
                fos.close();
            }
            mySocket.close();       		
		}
		catch(IOException e) {
	         e.printStackTrace();
	         System.exit(1);
	    }
		
	}

}

