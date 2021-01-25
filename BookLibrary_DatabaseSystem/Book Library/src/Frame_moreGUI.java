import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;



// More option
class Frame_moreGUI extends JFrame{

	// Frame Layout
	private JFrame jf_FrameGUI;
	private JTextArea jta_AboutBook;
	private JPanel jp_Control;
	private JTextArea jta_SystemMessage;

	//Button Option
	private JButton jbt_Borrow = new JButton("Borrow");
	private JButton jbt_Return = new JButton("Return");
	private JButton jbt_Reserve= new JButton("Reserve");
	private JButton jbt_WaitingQueue = new JButton("Waiting Queue");

	//Extension
	private JPanel jp_Image;
	private JButton jbt_SetImage;
	private JButton jbt_ResetImage;

	//Book Status
	private Book MyBook;
	private MyQueue<String> BookQueueList;
	//Constructor
	public Frame_moreGUI(Book inBook) {
		MyBook = inBook;
		BookQueueList = inBook.getReservedQueue();
		jf_FrameGUI = new JFrame();
		jf_FrameGUI.setLayout(new BorderLayout());

		// Create sub panel
		init_SubPanel_AboutBook();
		init_SubPanel_ControlList();
		init_SubPanel_SystemMessage();

		//Extension
		init_SubPanel_Image();

		// arrange sub panel position
		add(jta_AboutBook, BorderLayout.NORTH);
		add(jp_Image,BorderLayout.EAST);
		add(jp_Control,BorderLayout.CENTER);
		add(jta_SystemMessage,BorderLayout.SOUTH);

		if (BookQueueList.getSize() == 0 && MyBook.isAvailable()==true){
			enableBorrow();
		}
		else {
			disableBorrow();
		}

		init_Button_Listener();

	}

	public boolean getBookStatus() {
		return MyBook.isAvailable();
	}

	public MyQueue<String> getBookQueue() {
		return BookQueueList;
	}

	public String getBookImagePath() {
		return MyBook.getPath();
	}

	void init_SubPanel_AboutBook() 
	{
		String strISBN = MyBook.getISBN();
		String strTitle = MyBook.getTitle();
		boolean available = MyBook.isAvailable();
		String strAvailable = available? "Available: true":"Available: false";

		String strStatus = "ISBN: "+ strISBN +"\r\n"+ "Title: "+ strTitle +"\r\n" + strAvailable;
		jta_AboutBook = new JTextArea();
		jta_AboutBook.setText(strStatus);

	}

	void init_SubPanel_ControlList() 
	{
		jp_Control = new JPanel();
		jp_Control.setLayout(new GridLayout(1,1));

		JPanel jp_SubPanel =  new JPanel();
		jp_SubPanel.setLayout(new FlowLayout());
		jp_SubPanel.add(jbt_Borrow);
		jp_SubPanel.add(jbt_Return);
		jp_SubPanel.add(jbt_Reserve);
		jp_SubPanel.add(jbt_WaitingQueue);

		//Extension
		if (MyBook.getPath()==null )
			jbt_SetImage = new JButton("Set Book Image");
		else {
			jbt_SetImage = new JButton("Change Book Image");
		}
		jbt_ResetImage = new JButton("Reset Book Image");
		jp_SubPanel.add(jbt_SetImage);
		jp_SubPanel.add(jbt_ResetImage);

		jp_Control.add(jp_SubPanel);
	}

	void init_SubPanel_SystemMessage() 
	{
		jta_SystemMessage = new JTextArea();
	}

	//Extension
	void init_SubPanel_Image() 
	{
		jp_Image = new JPanel();
		ImageIcon icon = new ImageIcon(MyBook.getPath());		

		//Scale Image
		Image image = icon.getImage(); // transform it 
		Image newimg = image.getScaledInstance(300, 300,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
		icon = new ImageIcon(newimg);  // transform it back

		//Add to panel
		JLabel jlb_img = new JLabel(icon);
		jp_Image.add(jlb_img);
		jp_Image.updateUI();

		if (MyBook.getPath()==null )
			jbt_ResetImage.setEnabled(false);
		else
			jbt_ResetImage.setEnabled(true);


	}

	void enableBorrow()
	{
		jbt_Borrow.setEnabled(true);
		jbt_Return.setEnabled(false);
		jbt_Reserve.setEnabled(false);
		jbt_WaitingQueue.setEnabled(false);
	}

	void disableBorrow()
	{
		jbt_Borrow.setEnabled(false);
		jbt_Return.setEnabled(true);
		jbt_Reserve.setEnabled(true);
		jbt_WaitingQueue.setEnabled(true);
	}

	void update_jtextAboutBook() {
		String strISBN = MyBook.getISBN();
		String strTitle = MyBook.getTitle();
		boolean available = MyBook.isAvailable();
		String strAvailable = available? "Available: true":"Available: false";

		String strStatus = "ISBN: "+ strISBN +"\r\n"+ "Title: "+ strTitle +"\r\n" + strAvailable;
		jta_AboutBook.setText(strStatus);
		if (available == true) {
			enableBorrow();
		}
		else {
			disableBorrow();
		}
	}

	void update_jtextSystemMessage(String strText) {
		jta_SystemMessage.setText(strText);
	}

	public void init_Button_Listener(){
		jbt_Borrow.addActionListener(new ButtonListener());
		jbt_Return.addActionListener(new ButtonListener());
		jbt_Reserve.addActionListener(new ButtonListener());
		jbt_WaitingQueue.addActionListener(new ButtonListener());
		jbt_SetImage.addActionListener(new ButtonListener());
		jbt_ResetImage.addActionListener(new ButtonListener());
	}

	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == jbt_Borrow) {
				fn_Borrow();
			}
			else if (e.getSource() == jbt_Return) {
				fn_Return();
			}
			else if (e.getSource() == jbt_Reserve)	{
				fn_Reserve();
			}
			else if (e.getSource() == jbt_WaitingQueue) {
				fn_WaitingQueue();
			}
			// Extension
			else if (e.getSource() == jbt_SetImage) {
				fn_SetImage();
			}
			else if (e.getSource() == jbt_ResetImage) {
				fn_ResetImage();
			}
		}
		void fn_Borrow() {
			MyBook.setAvailable(false);

			//Update GUI
			update_jtextAboutBook();
			update_jtextSystemMessage("The book is borrowed.");
		}

		void fn_Return() {
			String displayText;
			if (BookQueueList.getSize() == 0) {
				MyBook.setAvailable(true);
				displayText = "The book is returned.";
			}
			else
			{
				String name = BookQueueList.dequeue();
				displayText = "The book is returned\r\n" + "The book is now borrowed by " + name + ".";
			}

			//Update GUI
			update_jtextAboutBook();
			update_jtextSystemMessage(displayText);
		}

		void fn_Reserve() {	
			String name = JOptionPane.showInputDialog("What's your name?");
			if (name!=null)
			{
				BookQueueList.enqueue(name);

				String displayText = "The book is reserved by " + name + ".";
				update_jtextSystemMessage(displayText);
			}
		}

		void fn_WaitingQueue() {

			String name = "";
			int sizeList = BookQueueList.getSize();

			for (int i=0; i<sizeList; i++)
				name += BookQueueList.getList().get(i) + "\r\n";

			String displayText = "The waiting queue:\r\n" + name;
			update_jtextSystemMessage(displayText);

		}

		public void fn_SetImage(){

			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					".jpg, .gif, ...", "jpg", "gif");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(null);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				if (chooser.getSelectedFile().getPath() != null)
				{
					String path = chooser.getSelectedFile().getPath();

					//Check file format
					if (getFileExtension(path).equals(".jpg") || getFileExtension(path).equals(".gif"))
					{
						ImageIcon icon = new ImageIcon(path);

						//Scale Image
						Image image = icon.getImage(); // transform it 
						Image newimg = image.getScaledInstance(300, 300,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
						icon = new ImageIcon(newimg);  // transform it back

						JLabel jlb_img = new JLabel(icon);
						//Set Image path
						MyBook.setPath(path);
						jp_Image.removeAll();
						jp_Image.add(jlb_img);
						jp_Image.updateUI();

						jbt_SetImage.setText("Change Book Image");
						jbt_ResetImage.setEnabled(true);
						jta_SystemMessage.setText("Image from \""+ path + "\" is set.");
					}
					else
					{
						JOptionPane.showMessageDialog(null,"Error: Wrong image format.");
					}
				}
			}

		}
		public void fn_ResetImage(){
			MyBook.setPath(null);
			jp_Image.removeAll();
			jp_Image.updateUI();

			jbt_SetImage.setText("Set Book Image");
			jbt_ResetImage.setEnabled(false);
			jta_SystemMessage.setText("Image is reset.");
		}


		private String getFileExtension(String file) {
			int lastIndexOf = file.lastIndexOf(".");
			if (lastIndexOf == -1) {
				return ""; // empty extension
			}
			return file.substring(lastIndexOf);
		}

	}

}