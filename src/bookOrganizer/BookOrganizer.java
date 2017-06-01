package bookOrganizer;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class BookOrganizer {
	
	//fields
	private ArrayList<BookCase> bookCases;
	private ArrayList<Book> bookList;
	public static enum Room {};
	private PriorityQueue<Room> roomPreference;
}
