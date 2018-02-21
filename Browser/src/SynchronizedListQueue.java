import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation with a SynchronizedLinkedListQueue.
 */
public class SynchronizedListQueue implements URLQueue{

  //private LinkedList<String> queue = new LinkedList<String>();
  private int limit;
  private Lock lock = new ReentrantLock();
 // private Lock pushLock = new ReentrantLock();
  private Condition notFull = this.lock.newCondition();
  private Condition notEmpty = this.lock.newCondition();
  
  private final LinkedList<String> queue;
  //constructor
  public SynchronizedListQueue() {
    queue = new LinkedList<String>();
  }
  //constructor
  public SynchronizedListQueue(int limit) {
    this.limit = limit;
    queue = new LinkedList<String>();

  }

  public boolean isEmpty() {
    return queue.size() == 0;
  }

  public boolean isFull() {
	  //return false;
    return queue.size() == this.limit;
  }

  //push/put
  public synchronized void enqueue(String url)
//	throws InterruptedException 
  {
	  	this.lock.lock();
	  	try {
		    while(this.isFull()) {
		    		try {
		    			notFull.await();
		    		}catch(InterruptedException e) {
		    			Thread.currentThread().interrupt();
		    		}
		    }
		    if(this.isEmpty()){
			      notEmpty.signal();
			    }
		    this.queue.add(url);
	  	}finally {
	  		this.lock.unlock();
	  	}
  }

  //pop/get
  public synchronized String dequeue()
//	throws InterruptedException
  {
	  	this.lock.lock();
	  	try {
		    while(this.isEmpty()){
		    		try {
		    			notEmpty.await();
		    		}catch(InterruptedException e) {
		    			Thread.currentThread().interrupt();
		    		}
		    }
		    
		    if(this.isFull()){
		      notFull.signal();
		    }
		
		    return this.queue.remove(0);
	  	}
	  	finally {
	  		this.lock.unlock();
	  	}
  }

}