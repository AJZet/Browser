public class MyURL {
	
	//define methods on MyURL
	public String getProtocol() {
		return protocol;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getPath() {
		return path;
	}

	public final String protocol;
	public final String host;
	public int port;
	public String path;
	
	//defining a constructor
	MyURL(String url){
		//the URL
		String s = url;
			
		//array of splits
		String[] a = s.split("/", 4);
		
		if (a.length <4)
			throw new IllegalArgumentException("The url is too short");
		
		a[1]=a[2];
		
		if (a[0].isEmpty() || a[1].isEmpty())
			throw new IllegalArgumentException("URL needs to have protocol and hostname");
			
		//array that splits a[1] if port :port is present
		String[] b;
		if (a[1].contains(":")) {
			b = a[1].split(":",2);
			a[2]=b[1];
			a[1]=b[0];
		}
		else
			a[2] = "-1";
		//delete : from protocol
		a[0]=a[0].replace(":", "");
		//if there is no path, path is "/"	
		if (a[3].isEmpty() && !s.endsWith("/"))
			throw new IllegalArgumentException("URL must end with '/' if path is empty");
		
		//add / at the begining of the path string
		String slash = "/";
		a[3] = slash.concat(a[3]);
		
		this.protocol = a[0];
		this.host = a[1];
		this.port = Integer.parseInt(a[2]);
		this.path = a[3];
		
	}

	
}
