import java.util.Iterator;

public class Book {
	private String title; // store the title of the book
	private String ISBN; // store the ISBN of the book
	private boolean available; // keep the status of whether the book is available;
	// initially should be true
	private MyQueue<String> reservedQueue = new MyQueue<String>(); // store the queue of waiting list
	
	//Extension
	private String path;	//store the path of the book
	
	public Book() {
		ISBN="";
		title= "";
		available=true;
		path="";
	}
	
	public Book(String ISBN,String title){
		this.title= title.trim();
		this.ISBN= ISBN.trim();
		available=true;
	}
	
	/**getter**/
	public String getTitle() {
		return title;
	}
	public String getISBN() {
		return ISBN;
	}
	public boolean isAvailable() {
		return available;
	}
	public MyQueue<String> getReservedQueue() {
		return reservedQueue;
	}
	public String getPath() {
		return path;
	}
	
	
	/**setter**/
	public void setTitle(String title) {
		this.title=title;
	}
	public void setISBN(String ISBN) {
		this.ISBN=ISBN;
	}
	public void setAvailable(boolean available) {
		this.available=available;
	}
	public void setPath(String path) {
		this.path = path;
	}

}
