import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Wget3 {
	
	private static class Download implements java.lang.Runnable {
		String url;
		String proxyHost;
		int proxyPort;
		//constructor
		Download(String url, String proxyHost, int proxyPort){
			this.url = url;
			this.proxyHost = proxyHost;
			this.proxyPort = proxyPort;
		}
		public void run() {
			Xurl.query(url, proxyHost, proxyPort);
			
		}
	}
	/*public class Enqueue implements java.lang.Runnable {
		String url;
		//constructor
		Enqueue(String url, URLQueue queue){
			this.url = url;
		}
		public void run() {
			while (!Thread.interrupted()) {
				lock.take();
				queue.enqueue(url);
			}
			
		}
	}*/
	
	//each call to Xurl.query is done in a new Thread.
	//In others words, whenever an url comes out of the queue, an new Thread is launched with a call to Xurl.query as its run method.
	public static void doMultiThreaded(String requestedURL, String proxyHost,
		      int proxyPort) {
	    final SynchronizedListQueue queue = new SynchronizedListQueue();
	    final Set<String> seen = Collections.synchronizedSet(new HashSet<String>());
	    URLprocessing.handler = new URLprocessing.URLhandler() {
	        // this method is called for each matched url
	        public void takeUrl(String url){
	        		System.out.println(Thread.currentThread() + " was here.");
	      	  	// check if the url is not in the seen set
	      	  	if (!seen.contains(url)) {
	      	  		// put new found url in the queue
	      	  		queue.enqueue(url);
	      	  		System.out.println("enqueue " + url);
	      	  	}
	        }
	      };
	     //amount of initial threads
	     int threadInit = Thread.activeCount();
	     System.out.println("Thread initial number " + threadInit);
	    
	    // to start, we push the initial url into the queue
	    URLprocessing.handler.takeUrl(requestedURL);
	    //determine when to stop the program!
	    while (Thread.activeCount() > threadInit || !queue.isEmpty()) {
	    		//if queue not empty -> dequeue element
	    		if(!queue.isEmpty()) {
		    		String url = queue.dequeue();
		    		System.out.println("dequeue " + url);
		    		if (!seen.contains(url)) {
		    			seen.add(url);
		    			System.out.println("seen " + url);
		    			Thread t = new Thread(new Download(url, proxyHost, proxyPort));
		    			t.start();
		    		}
		    	//if queue empty, put thread to sleep
		    	else {
		    		try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// Auto-generated catch block
						e.printStackTrace();
					}
		    	}
		    		
	    		}

	    }
		
	}

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
    doMultiThreaded(args[0], proxyHost, proxyPort);
  }

}