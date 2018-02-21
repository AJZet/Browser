import java.util.HashSet;

public class Wget2 {

  public static void doIterative(String requestedURL, String proxyHost,
      int proxyPort) {
    final URLQueue queue = new ListQueue();
    final HashSet<String> seen = new HashSet<String>();
    URLprocessing.handler = new URLprocessing.URLhandler() {
      // this method is called for each matched url
      public void takeUrl(String url) {
    	  	// check if the url is not in the seen set
    	  	if (!seen.contains(url)) {
    	  		// put new found url in the queue
    	  		queue.enqueue(url);
    	  		System.out.println("enqueue " + url);
    	  	}
      }
    };
    
    // to start, we push the initial url into the queue
    URLprocessing.handler.takeUrl(requestedURL);
    while (!queue.isEmpty()) {
    		String url = queue.dequeue();
    		System.out.println("dequeue " + url);
    		if (!seen.contains(url)) {
    			seen.add(url);
    			System.out.println("seen " + url);
    			Xurl.query(url, proxyHost, proxyPort);
    		}

    }
  }

  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("Usage: java Wget url [proxyHost proxyPort]");
      System.exit(-1);
    }
    String proxyHost = null;
    if (args.length > 1)
      proxyHost = args[1];
    int proxyPort = -1;
    if (args.length > 2)
      proxyPort = Integer.parseInt(args[2]);
    doIterative(args[0], proxyHost, proxyPort);
  }

}