import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation with a SynchronizedLinkedListQueue.
 */
public class BlockingListQueue implements URLQueue{

  //private LinkedList<String> queue = new LinkedList<String>();
  //private int limit;
  private Lock lock = new ReentrantLock();
 // private Lock pushLock = new ReentrantLock();
  private Condition notFull = this.lock.newCondition();
  private Condition notEmpty = this.lock.newCondition();
  
  private final LinkedList<String> queue;
  //constructor
  public BlockingListQueue() {
    queue = new LinkedList<String>();
  }
  /*constructor
  public SynchronizedListQueue(int limit) {
    this.limit = limit;
  }*/

  public boolean isEmpty() {
    return queue.size() == 0;
  }

  public boolean isFull() {
	  return false;
    //return queue.size() == this.limit;
  }

  //push/put
  public void enqueue(String url)
//	throws InterruptedException 
  {
	  	this.lock.lock();
	  	try {
		    while(this.isFull()) {
		    		try {
		    			System.out.println("waiting for not Full");
		    			notFull.await();
		    			System.out.println("awaken");
		    		}catch(InterruptedException e) {
		    			Thread.currentThread().interrupt();
		    		}
		    }
		    this.queue.add(url);
		    
		    if(this.queue.size() == 1) {
				System.out.println("Signal notEmpty");
				notEmpty.signalAll();
		    }

	  	}finally {
	  		this.lock.unlock();
	  	}
  }

  //pop/get
  public String dequeue()
//	throws InterruptedException
  {
	  String s = null;
	  //synchronized(lock)<-- locking on Object lock or synchronized(this) <-- locking on a queue
	  this.lock.lock();
	  try {
		   while(this.isEmpty()){
		    		try {
		    			System.out.println("waiting for not empty");
		    			notEmpty.await();
		    			System.out.println("awaken");
		    			//if thread is interrupt must break the loop,
		    			//don't try to dequeue
		    		}catch(InterruptedException e) {
		    			//return null --> to know there was an interruption
		    			//I know that I have been interrupted, but I will loose information if I want
		    			//to check it again. that's why I interrupt it again.
		    			Thread.currentThread().interrupt();
		    		}
		    }
		    
		    while(this.isFull()){
		      notFull.signalAll();
		    }
		    s = this.queue.remove();
	  	}
	  	finally {
	  		//must be always executed
	  		this.lock.unlock();
	  	}
	  	return s;
  }

}