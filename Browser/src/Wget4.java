import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Wget4 {
	
	private static class Download implements Callable<Integer> {
		private String url;
		private String proxyHost;
		private int proxyPort;
		private int id;
		//constructor
		public Download(String url, String proxyHost, int proxyPort, int id){
			this.url = url;
			this.proxyHost = proxyHost;
			this.proxyPort = proxyPort;
			this.id = id;
		}
		public Integer call() {
			System.out.println(Thread.currentThread() +" Statring task " + id);
			Xurl.query(url, proxyHost, proxyPort);
			System.out.println(Thread.currentThread() + " Completed task " + id);
			return(id);
		}
	}

	public static void doThreadedPool(String requestedURL, String proxyHost,
		      int proxyPort) {
		final ExecutorService executor = Executors.newFixedThreadPool(30);
	    final BlockingListQueue queue = new BlockingListQueue();
	    final Set<String> seen = Collections.synchronizedSet(new HashSet<String>());
	    URLprocessing.handler = new URLprocessing.URLhandler() {
	        // this method is called for each matched url
	        public void takeUrl(String url){
	        		//System.out.println(Thread.currentThread() + " was here.");
	        		System.out.println("Check if url already seen  " + url);
	      	  	// check if the url is not in the seen set
	      	  	if (!seen.contains(url)) {
	      	  		// put new found url in the queue
	      	  		System.out.println("enqueue " + url);
	      	  		queue.enqueue(url);
	      	  	}
	        }
	      };
	     //amount of initial threads
	     int threadInit = Thread.activeCount();
	     System.out.println("Thread initial number " + threadInit);
	    
	    // to start, we push the initial url into the queue
	    URLprocessing.handler.takeUrl(requestedURL);

	    //counter of the Download tasks and sum of the counter
	    int i = 0;
	    int sumi = 0;
	    //result of the completed task Download and sum of results
	    Future<Integer> future = null;
	    int sumf = 0;
	    //while (!Thread.interrupted()), you can check the interruption only once
	    while (( sumi != sumf || !queue.isEmpty() ) || sumi == 0) {
			System.out.println("Threads running " + Thread.activeCount());
	    		//if queue not empty -> dequeue element
		    	String url = queue.dequeue();
    			System.out.println("dequeue " + url);

	    		if (!seen.contains(url)) {
	    			seen.add(url);
	    			System.out.println("seen " + url);
	    			future = executor.submit(new Download(url, proxyHost, proxyPort, i));
	    			sumi = sumi + i;
	    			try {
						sumf = sumf + future.get();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			i++;
	    		}

	    }
	    executor.shutdown();
	    try {
			executor.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Threads running " + Thread.activeCount());
	}
	
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
		    			//Thread t = new Thread(new Download(url, proxyHost, proxyPort));
		    			//t.start();
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
    doThreadedPool(args[0], proxyHost, proxyPort);
  }

}