import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.io.*;
import java.util.Scanner; // Import the Scanner class to read text files

import javax.swing.filechooser.*;

public class Frame_mainGUI extends JFrame {
	// Database
	private MyLinkedList<Book> bookLinkedList = new MyLinkedList<Book>();
	private Book[] bookArray;

	//Table
	private boolean reverseTitle=true;
	private boolean reverseISBN=true;
	private Vector<Vector<String>> rowData = new Vector<Vector<String>>();
	private Vector<String> columnNames=new Vector<String>();
	DefaultTableModel model=new DefaultTableModel(){
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	private int status=0; //sort mode (0:default, 1:ISBN, 2:Title)
	private Book preChangeBook;
	private boolean searchMode=false;

	// Frame Layout
	private JFrame jf_FrameGUI;
	private JTextArea jta_AboutMe;
	private JScrollPane jsp_ScrollList;
	private JTable jtb_BookTable;
	private JPanel jp_Control;
	private Frame_moreGUI jf_MoreGUI;

	//Label Option
	private JLabel jlb_ISBN = new JLabel("ISBN");
	private JTextField jtf_ISBN = new JTextField(10);
	private JLabel jlb_Title = new JLabel("Title");
	private JTextField jtf_Title = new JTextField(30);

	//Button Option
	private JButton jbt_Add = new JButton("Add");
	private JButton jbt_Edit = new JButton("Edit");
	private JButton jbt_Save = new JButton("Save");
	private JButton jbt_Delete = new JButton("Delete");
	private JButton jbt_Search = new JButton("Search");
	private JButton jbt_More = new JButton("More>>");
	private JButton jbt_LoadTestData = new JButton("Load Test Data");
	private JButton jbt_DisplayAll = new JButton("Display All");
	private JButton jbt_DisplayAllByISBN = new JButton("Display All By ISBN");
	private JButton jbt_DisplayAllByTitle = new JButton("Display All By Title");
	private JButton jbt_Exit = new JButton("Exit");

	//Extension for advanced function
	private JButton jbt_LoadFromFile = new JButton("Load From File");
	private JButton jbt_SaveToFile = new JButton("Save To File");
	private JTextField jtf_FileName = new JTextField("DatabaseLibrary.txt",30);

	private JButton[] buttonGroup = {jbt_Add,jbt_Edit,jbt_Delete,jbt_Search,jbt_More,jbt_LoadTestData,jbt_DisplayAll
			,jbt_DisplayAllByISBN,jbt_DisplayAllByTitle,jbt_Exit, jbt_LoadFromFile,jbt_SaveToFile};

	private String jointKey_ISBN = "<#Title:>";
	private String jointKey_Title = "<#Available:>";
	private String jointKey_Available = "<#Path:>";
	private String jointKey_Path = "<#Queue:>";


	/** Main frame Boarder Layout **/
	public Frame_mainGUI() {
		jf_FrameGUI = new JFrame();
		jf_FrameGUI.setLayout(new BorderLayout());

		// Create sub panel
		init_SubPanel_AboutAuthor();
		init_SubPanel_BookListTable();
		init_SubPanel_ControlButton();

		// arrange sub panel position
		add(jta_AboutMe, BorderLayout.NORTH);
		add(jsp_ScrollList,BorderLayout.CENTER);
		add(jp_Control,BorderLayout.SOUTH);

		// Add listen to button events
		init_Listener(); 
	}

	/** Main method */
	public static void main(String[] args) {
		Frame_mainGUI frame = new Frame_mainGUI();
		frame.setTitle("Library Admin System");
		frame.setSize(1000, 500);
		frame.setLocationRelativeTo(null); // Center the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}


	/** Sub Panel -- About the Author information **/
	private void init_SubPanel_AboutAuthor(){
		String StudentA = "Student Name and ID: Chan Chun Man (16060132D)\r\n";
		String StudentB = "Student Name and ID: Tsang Wai (16084067D)\r\n";
		String Date = "\r\n";
		jta_AboutMe = new JTextArea();
		jta_AboutMe.append(StudentA);
		jta_AboutMe.append(StudentB);
		jta_AboutMe.append(Date);	
		timeUpdate();
	}


	/** Sub Panel -- About the Book List information**/
	private void init_SubPanel_BookListTable(){
		columnNames.add("  ISBN"); 
		columnNames.add("  Title"); 
		columnNames.add("Available"); 
		model.setDataVector(rowData, columnNames);
		jtb_BookTable = new JTable(model);
		jsp_ScrollList = new JScrollPane(jtb_BookTable);
		jtb_BookTable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int row = jtb_BookTable.getSelectedRow();
				jtf_ISBN.setText((String)jtb_BookTable.getValueAt(row,0));
				jtf_Title.setText((String)jtb_BookTable.getValueAt(row,1));
			}
		});
		jtb_BookTable.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode()==KeyEvent.VK_UP||e.getKeyCode()==KeyEvent.VK_DOWN) {
					int row = jtb_BookTable.getSelectedRow();
					jtf_ISBN.setText((String)jtb_BookTable.getValueAt((e.getKeyCode()==KeyEvent.VK_UP)?(row-1>=0)?row-1:0:(row+1>=bookLinkedList.size())?bookLinkedList.size()-1:row+1,0));
					jtf_Title.setText((String)jtb_BookTable.getValueAt((e.getKeyCode()==KeyEvent.VK_UP)?(row-1>=0)?row-1:0:(row+1>=bookLinkedList.size())?bookLinkedList.size()-1:row+1,1));
				}
			}
		});
	}


	/** Sub Panel -- About the Control Button information **/
	private void init_SubPanel_ControlButton(){
		jp_Control = new JPanel();
		jp_Control.setLayout(new GridLayout(4,1));

		JPanel jp_SubPanel1 =  new JPanel();
		jp_SubPanel1.setLayout(new FlowLayout());
		jp_SubPanel1.add(jlb_ISBN);
		jp_SubPanel1.add(jtf_ISBN);
		jp_SubPanel1.add(jlb_Title);
		jp_SubPanel1.add(jtf_Title);

		JPanel jp_SubPanel2 =  new JPanel();
		jp_SubPanel2.setLayout(new FlowLayout());
		jp_SubPanel2.add(jbt_Add);
		jp_SubPanel2.add(jbt_Edit);
		jp_SubPanel2.add(jbt_Save);
		jbt_Save.setEnabled(false);
		jp_SubPanel2.add(jbt_Delete);
		jp_SubPanel2.add(jbt_Search);
		jp_SubPanel2.add(jbt_More);

		JPanel jp_SubPanel3 =  new JPanel();
		jp_SubPanel3.setLayout(new FlowLayout());
		jp_SubPanel3.add(jbt_LoadTestData);
		jp_SubPanel3.add(jbt_DisplayAll);
		jp_SubPanel3.add(jbt_DisplayAllByISBN);
		jp_SubPanel3.add(jbt_DisplayAllByTitle);
		jp_SubPanel3.add(jbt_Exit);

		//Extension for advanced function
		JPanel jp_SubPanel4 =  new JPanel();
		jp_SubPanel4.setLayout(new FlowLayout());
		jp_SubPanel4.add(jbt_LoadFromFile);
		jp_SubPanel4.add(jbt_SaveToFile);
		jp_SubPanel4.add(jtf_FileName);


		jp_Control.add(jp_SubPanel1);
		jp_Control.add(jp_SubPanel2);
		jp_Control.add(jp_SubPanel3);
		jp_Control.add(jp_SubPanel4);
	}

	public void init_Listener(){
		jbt_Add.addActionListener(new ButtonListener());
		jbt_Edit.addActionListener(new ButtonListener());
		jbt_Save.addActionListener(new ButtonListener());
		jbt_Delete.addActionListener(new ButtonListener());
		jbt_Search.addActionListener(new ButtonListener());
		jbt_More.addActionListener(new ButtonListener());
		jbt_LoadTestData.addActionListener(new ButtonListener());
		jbt_DisplayAll.addActionListener(new ButtonListener());
		jbt_DisplayAllByISBN.addActionListener(new ButtonListener());
		jbt_DisplayAllByTitle.addActionListener(new ButtonListener());
		jbt_Exit.addActionListener(new ButtonListener());
		//Extension for advanced function
		jbt_LoadFromFile.addActionListener(new ButtonListener());
		jbt_SaveToFile.addActionListener(new ButtonListener());

	}


	public void timeUpdate() { //update time only when performing operations of Database / change of table
		String [] lines = jta_AboutMe.getText().split("\n");
		lines[2] = java.util.Calendar.getInstance().getTime().toString()+" "+(status==0?searchMode?"(Search result)":"(Displaying All)":searchMode?"(Sorting the result)":"(Sorting)")+"\r\n";
		jta_AboutMe.setText(String.join("\n", lines));
	}

	public void updateTable() {//according to the current bookArray to refresh the table
		rowData.clear();
		for (int i=bookArray.length-1; i>=0;i--) {
			Vector<String> row = new Vector<String>();
			row.add(((Book)bookArray[i]).getISBN());
			row.add(((Book)bookArray[i]).getTitle());
			row.add(((Boolean)(((Book)bookArray[i]).isAvailable())).toString());
			rowData.add(row);
		}
		//model.fireTableDataChanged();
		model.fireTableStructureChanged();
	}


	// Button Event
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == jbt_Add) {
				fn_Add();
			}
			else if (e.getSource() == jbt_Edit) {
				fn_Edit();
			}
			else if (e.getSource() == jbt_Save)	{
				fn_Save();
			}
			else if (e.getSource() == jbt_Delete) {
				fn_Delete();
			}
			else if (e.getSource() == jbt_Search) {
				fn_Search();
			}
			else if (e.getSource() == jbt_More){
				fn_More();
			}
			else if (e.getSource() == jbt_LoadTestData) {
				fn_LoadTestData();
			}
			else if (e.getSource() == jbt_DisplayAll) {
				fn_DisplayAll();
			}
			else if (e.getSource() == jbt_DisplayAllByISBN) {
				fn_DisplayAllByISBN();
			}	
			else if (e.getSource() == jbt_DisplayAllByTitle) {
				fn_DisplayAllByTitle();
			}
			//Extension for advanced function
			else if (e.getSource() == jbt_LoadFromFile) {
				fn_LoadFromFile();
			}
			else if (e.getSource() == jbt_SaveToFile) {
				fn_SaveToFile();
			}
			if (e.getSource() == jbt_Exit)
				System.exit(0);
		}

		public void fn_Add()
		{
			String strISBN = jtf_ISBN.getText();
			String strTitle = jtf_Title.getText();
			boolean existInDB=false;
			if(strISBN.isEmpty()) {
				JOptionPane.showMessageDialog(null,"Error: ISBN cannot be blank.");
			}
			else if(strTitle.isEmpty()) {
				JOptionPane.showMessageDialog(null,"Error: Title cannot be blank.");
			}
			else{
				try {
					//Use try catch to check if input is 10-digit number
					Integer.parseInt(strISBN);

					if (strISBN.length() == 10)
					{
						Iterator<Book> it = bookLinkedList.iterator();
						while (it.hasNext()){
							Book bk=it.next();
							if(bk.getISBN().equals(strISBN)) {
								JOptionPane.showMessageDialog(null,"Error: book ISBN"+strISBN+" is already in the database.");
								existInDB=true;
								jbt_DisplayAll.doClick();
								break;
							}
						}
						if(!existInDB) {
							jbt_DisplayAll.doClick();
							bookLinkedList.addFirst(new Book(strISBN,strTitle)); //Add new Book
							bookLinkedList.getFirst().setAvailable(true);
							bookArray=bookLinkedList.toArray(new Book[bookLinkedList.size()]);
							Vector<String> row = new Vector<String>();
							row.add(bookArray[0].getISBN());
							row.add(bookArray[0].getTitle());
							row.add(((Boolean)(bookArray[0].isAvailable())).toString());
							rowData.add(row);	
							model.fireTableDataChanged();
							jtf_ISBN.setText("");
							jtf_Title.setText("");
							jbt_DisplayAll.doClick();
							timeUpdate();
						}
					}
					else
					{
						JOptionPane.showMessageDialog(null,"Error: ISBN should be a 10-digit number.");
					}

				} catch(NumberFormatException e) {
					JOptionPane.showMessageDialog(null,"Error: ISBN is not a number.");
				}	

			}

		}

		public void fn_Add(boolean status, String imgpath, String[] queue)
		{
			String strISBN = jtf_ISBN.getText();
			String strTitle = jtf_Title.getText();
			boolean existInDB=false;
			if(strISBN.isEmpty()) {
				JOptionPane.showMessageDialog(null,"Error: ISBN cannot be blank.");
			}
			else if(strTitle.isEmpty()) {
				JOptionPane.showMessageDialog(null,"Error: Title cannot be blank.");
			}
			else{
				try {
					//Use try catch to check if input is 10-digit number
					Integer.parseInt(strISBN);

					if (strISBN.length() == 10)
					{
						Iterator<Book> it = bookLinkedList.iterator();
						while (it.hasNext()){
							Book bk=it.next();
							if(bk.getISBN().equals(strISBN)) {
								JOptionPane.showMessageDialog(null,"Error: book ISBN"+strISBN+" is already in the database.");
								existInDB=true;
								jbt_DisplayAll.doClick();
								break;
							}
						}
						if(!existInDB) {
							jbt_DisplayAll.doClick();
							bookLinkedList.addLast(new Book(strISBN,strTitle)); //Add new Book
							bookLinkedList.getLast().setAvailable(status);
							if (queue !=null)
							{
								for (int i=0; i<queue.length; i++)
									bookLinkedList.getLast().getReservedQueue().enqueue(queue[i]);
							}
							//Extension
							if (imgpath !=null)
							{
								bookLinkedList.getLast().setPath(imgpath);
							}


							bookArray=bookLinkedList.toArray(new Book[bookLinkedList.size()]);
							Vector<String> row = new Vector<String>();
							row.add(bookArray[0].getISBN());
							row.add(bookArray[0].getTitle());
							row.add(((Boolean)(bookArray[0].isAvailable())).toString());
							rowData.add(row);	
							model.fireTableDataChanged();

							jbt_DisplayAll.doClick();
							timeUpdate();

						}
					}
					else
					{
						JOptionPane.showMessageDialog(null,"Error: ISBN should be a 10-digit number.");
					}

				} catch(NumberFormatException e) {
					JOptionPane.showMessageDialog(null,"Error: ISBN is not a number.");
				}
			}

		}

		public void fn_Edit()
		{
			String strISBN = jtf_ISBN.getText();
			if(bookLinkedList.size()==0) {
				JOptionPane.showMessageDialog(null,"Error: Database is empty.");
			}
			else {
				if(strISBN.isEmpty()) {
					JOptionPane.showMessageDialog(null,"Error: ISBN cannot be blank.");
				}
				else {
					boolean existInDB=false;
					Iterator<Book> it = bookLinkedList.iterator();
					while (it.hasNext()){
						Book bk=it.next();
						if(bk.getISBN().equals(strISBN)) {
							existInDB=true;
							jtf_Title.setText(bk.getTitle());
							preChangeBook=bk;
							break;
						}
					}	
					if(!existInDB) {
						JOptionPane.showMessageDialog(null,"Error: book ISBN "+strISBN+" is not in the database.");
					}
					else if(existInDB) {
						jbt_Save.setEnabled(true);
						for(JButton button: buttonGroup) {
							button.setEnabled(false);
						}
					}
				}
			}

		}

		public void fn_Save()
		{
			String strISBN = jtf_ISBN.getText();
			String strTitle = jtf_Title.getText();
			if(strISBN.isEmpty()) {
				JOptionPane.showMessageDialog(null,"Error: ISBN cannot be blank.");
			} 
			else if(strTitle.isEmpty()) {
				JOptionPane.showMessageDialog(null,"Error: Title cannot be blank.");
			}
			else {
				try {
					Integer.parseInt(strISBN);	//Use try catch to check if input is 10-digit number
					if (strISBN.length() == 10){
						boolean existInDB=false;
						Iterator<Book> it = bookLinkedList.iterator();
						while (it.hasNext()){
							Book bk=it.next();
							if((bk.getISBN().equals(strISBN)) && !(strISBN.equals(preChangeBook.getISBN()))) {
								existInDB=true;
								JOptionPane.showMessageDialog(null,"Error: book ISBN"+strISBN+" is already in the database.");
								break;
							}
						}	
						if(!existInDB) {
							preChangeBook.setISBN(strISBN);
							preChangeBook.setTitle(strTitle);
							jtf_ISBN.setText("");
							jtf_Title.setText("");
							jbt_Save.setEnabled(false);
							for(JButton button: buttonGroup) {
								button.setEnabled(true);
							}
							//Repeat Display all since jbt_DisplayAll is disable now
							columnNames.set(0,"  ISBN");
							columnNames.set(1,"  Title");
							reverseTitle=true;
							reverseISBN=true;
							status=0;
							bookArray=bookLinkedList.toArray(new Book[bookLinkedList.size()]); //reset bookArray order to current Database order
							updateTable();
							timeUpdate();
						}
					}else{
						JOptionPane.showMessageDialog(null,"Error: ISBN should be a 10-digit number.");
					}
				} catch(NumberFormatException e) {
					JOptionPane.showMessageDialog(null,"Error: ISBN is not a number.");
				}	
			}

		}


		public void fn_Delete() 
		{
			String strISBN = jtf_ISBN.getText();
			if(bookLinkedList.size()==0) {
				JOptionPane.showMessageDialog(null,"Error: Database is empty.");
			}
			else {
				if(strISBN.isEmpty()) {
					JOptionPane.showMessageDialog(null,"Error: ISBN cannot be blank.");
				}
				else {
					boolean existInDB=false;
					Iterator<Book> it = bookLinkedList.iterator();
					while (it.hasNext()){
						Book bk=it.next();
						if(bk.getISBN().equals(strISBN)) {
							existInDB=true;
							jtf_Title.setText(bk.getTitle());
							preChangeBook=bk;
							break;
						}
					}	
					if(!existInDB) {
						JOptionPane.showMessageDialog(null,"Error: book ISBN "+strISBN+" is not in the database.");
					}
					else if(existInDB) {
						bookLinkedList.remove(bookLinkedList.indexOf(preChangeBook));
						jbt_DisplayAll.doClick();
						jtf_ISBN.setText("");
						jtf_Title.setText("");
					}
				}
			}

		}

		public void fn_Search() 
		{
			String strISBN = jtf_ISBN.getText();
			String strTitle = jtf_Title.getText();
			jbt_DisplayAll.doClick();
			if(!(strISBN.isEmpty() && strTitle.isEmpty())){Book[] temp=new Book[bookLinkedList.size()];
			int index=0;
			Iterator<Book> it = bookLinkedList.iterator();
			while (it.hasNext()){
				Book bk=it.next();
				if(bk.getISBN().contains(strISBN)&&!strISBN.isEmpty()) {
					temp[index]=bk;
					index++;
				}
				else if(bk.getTitle().contains(strTitle)&&!strTitle.isEmpty()) {
					temp[index]=bk;
					index++;
				}
			}
			bookArray=new Book[index];
			for(int i=0; i<index; i++) {
				bookArray[i] =  temp[i];
			}
			updateTable();
			searchMode=true;
			timeUpdate();
			}

		}

		public void fn_More() 
		{
			String strISBN = jtf_ISBN.getText();
			String strTitle = jtf_Title.getText();

			if(strISBN.isEmpty()) {
				JOptionPane.showMessageDialog(null,"Error: Please select a book or input ISBN");
			}
			else {
				boolean existInDB=false;

				Book bk = null;
				Iterator<Book> it = bookLinkedList.iterator();
				while (it.hasNext()){
					bk=it.next();
					if(bk.getISBN().equals(strISBN)) {
						existInDB=true;
						preChangeBook=bk;
						break;
					}
				}	
				if(!existInDB) {
					JOptionPane.showMessageDialog(null,"Error: book ISBN "+strISBN+" is not in the database.");
				}
				else if(existInDB) {
					// Disable control for main UI
					jbt_Save.setEnabled(false);
					for(JButton button: buttonGroup) {
						button.setEnabled(false);
					}

					// Create UI for more menu
					jf_MoreGUI = new Frame_moreGUI(preChangeBook);
					jf_MoreGUI.setTitle(preChangeBook.getTitle());
					jf_MoreGUI.setSize(800, 500);
					jf_MoreGUI.setLocationRelativeTo(null); // Center the frame
					jf_MoreGUI.setDefaultCloseOperation(jf_MoreGUI.DISPOSE_ON_CLOSE);
					jf_MoreGUI.setVisible(true);
					//For MoreGUI
					jf_MoreGUI.addWindowListener(new WindowListener() {

						@Override
						public void windowOpened(WindowEvent arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void windowIconified(WindowEvent arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void windowDeiconified(WindowEvent arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void windowDeactivated(WindowEvent arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void windowClosing(WindowEvent arg0) {
							int queueSize =  jf_MoreGUI.getBookQueue().getSize();
							preChangeBook.setAvailable(jf_MoreGUI.getBookStatus());
							for (int i=0; i<queueSize; i++)
							{
								preChangeBook.getReservedQueue().enqueue(jf_MoreGUI.getBookQueue().dequeue());
							}
							preChangeBook.setPath(jf_MoreGUI.getBookImagePath());

							timeUpdate();
							updateTable();

							// Enable back control for main UI
							jbt_Save.setEnabled(false);
							for(JButton button: buttonGroup) {
								button.setEnabled(true);
							}
						}

						@Override
						public void windowClosed(WindowEvent arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void windowActivated(WindowEvent arg0) {
							// TODO Auto-generated method stub

						}
					});
				}

			}
		}

		public void fn_LoadTestData() {
			jtf_ISBN.setText("0131450913");
			jtf_Title.setText("HTML How to Program");
			jbt_Add.doClick();
			jtf_ISBN.setText("0131857576");
			jtf_Title.setText("C++ How to Program");
			jbt_Add.doClick();
			jtf_ISBN.setText("0132222205");
			jtf_Title.setText("Java How to Program");
			jbt_Add.doClick();

		}

		public void fn_DisplayAll() {
			columnNames.set(0,"  ISBN");
			columnNames.set(1,"  Title");
			reverseTitle=true;
			reverseISBN=true;
			searchMode=false;
			status=0;
			bookArray=bookLinkedList.toArray(new Book[bookLinkedList.size()]); //reset bookArray order to current Database order
			updateTable();
			timeUpdate();

		}

		public void fn_DisplayAllByISBN() {
			if (bookLinkedList.size()!=0) {
				columnNames.set(1,"  Title");
				reverseTitle=true; //reset reverse of "Title"
				status=1;
				if(reverseISBN) {
					columnNames.set(0,"^ ISBN");
					reverseISBN=false;
					Arrays.sort(bookArray,Comparator.comparing(Book:: getISBN).reversed()); //sort current bookArray
					updateTable();
					timeUpdate();
				} else {
					columnNames.set(0,"v ISBN");
					reverseISBN=true;
					Arrays.sort(bookArray,Comparator.comparing(Book:: getISBN));  //sort current bookArray				
					updateTable();
					timeUpdate();
				}
			}

		}

		public void fn_DisplayAllByTitle() {
			if (bookLinkedList.size()!=0) {
				columnNames.set(0,"  ISBN");
				reverseISBN=true; //reset reverse of "ISBN"
				status=2;
				if(reverseTitle) {
					columnNames.set(1,"^ Title");
					reverseTitle=false;
					Arrays.sort(bookArray,Comparator.comparing(Book:: getTitle).reversed()); //sort current bookArray
					updateTable();
					timeUpdate();
				} else {
					columnNames.set(1,"v Title");
					reverseTitle=true;
					Arrays.sort(bookArray,Comparator.comparing(Book:: getTitle)); //sort current bookArray
					updateTable();
					timeUpdate();
				}
			}
		}

		public void fn_LoadFromFile() {
			String strFileName = jtf_FileName.getText();
			if(strFileName.isEmpty()) {
				JOptionPane.showMessageDialog(null,"Error: File Name cannot be blank.");
			}
			else
			{
				//Clear old data
				//bookLinkedList.clear();

				//Load from file
				try {
					File MyFile = new File(strFileName);
					Scanner MyReader = new Scanner(MyFile);
					while (MyReader.hasNextLine()) {
						String data = MyReader.nextLine();
						String [] segA = data.split(jointKey_ISBN);
						String bkISBN = segA[0];

						String [] segB = segA[1].split(jointKey_Title);
						String bkTitle = segB[0];

						String [] segC = segB[1].split(jointKey_Available);
						boolean bkAvailable = segC[0].equals("True")? true:false;

						String [] segD = segC[1].split(jointKey_Path);
						String bkImagePath = segD[0];
						if (bkImagePath.equals("nullPath"))
							bkImagePath=null;


						//Record to data
						jtf_ISBN.setText(bkISBN);
						jtf_Title.setText(bkTitle);
						if (segD.length ==1)
							fn_Add(bkAvailable,bkImagePath, null);
						else
						{
							String []bkQueue = segD[1].split(";");
							fn_Add(bkAvailable,bkImagePath,bkQueue);
						}


					}
					MyReader.close();
					timeUpdate();
					updateTable();
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(null,"Error: File not found.");
				} catch (ArrayIndexOutOfBoundsException e)
				{
					JOptionPane.showMessageDialog(null,"Error: Database is damaged.");
				}
			}
		}

		public void fn_SaveToFile() {
			String strFileName = jtf_FileName.getText();
			if(strFileName.isEmpty()) {
				JOptionPane.showMessageDialog(null,"Error: File Name cannot be blank.");
			}
			else
			{
				try {
					//Create file
					File file = new File(strFileName);
					file.setWritable(true);
					file.setReadable(true);

					//Write file
					FileWriter myWriter = new FileWriter(file);
					Iterator<Book> it = bookLinkedList.iterator();
					while (it.hasNext()){
						Book bk=it.next();
						String bkISBN = bk.getISBN();
						String bkTitle = bk.getTitle();
						String bkAvailable =  bk.isAvailable()? "True":"False";

						//Get Book path
						String bkPath = bk.getPath();
						if (bkPath== null)
							bkPath = "nullPath";

						//Get Book queue list
						String bkQueue = "";
						int queueSize = bk.getReservedQueue().getSize();
						for (int i = 0; i< queueSize; i++)
							bkQueue += bk.getReservedQueue().getList().get(i) +";";


						myWriter.write(bkISBN + jointKey_ISBN + bkTitle + jointKey_Title + bkAvailable + jointKey_Available + bkPath + jointKey_Path + bkQueue  + "\r\n" );
					}	
					JOptionPane.showMessageDialog(null,"Database is saved to: \"" + file.getAbsolutePath() + "\".");
					myWriter.close();
					file.setWritable(false);

					timeUpdate();
					updateTable();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null,"Error: IO Exception.");
				} catch(NullPointerException e) {
					JOptionPane.showMessageDialog(null,"Error: No data recorded.");
				}
			}

		}


	}


}
