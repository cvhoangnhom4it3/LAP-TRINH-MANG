package v8;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import static java.lang.System.out;

import java.awt.Color;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JScrollPane;
import java.awt.Font;

public class Server extends JFrame {

	private JPanel contentPane;
	Vector<String> users = new Vector<String>();
	Vector<HandleClient> clients = new Vector<HandleClient>();
	ServerSocket server;
	JFrame j;
	Container c;
	JTextArea taServer;
	JLabel lblNewLabel;
	JScrollPane scrollPane;
	String allUsers;

	
	public void GUI() {
		j = new JFrame("SERVER");
		j.getContentPane().setBackground(new Color(240, 240, 240));
		
		j.setBounds(100, 100, 734, 436);
		contentPane = new JPanel();
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 25, 432, 272);
		
		lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon("C:\\Users\\ChuHoang\\Pictures\\chat.png"));
		lblNewLabel.setBounds(12, 50, 256, 256);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(274, 13, 430, 361);
		
		taServer = new JTextArea();
		taServer.setBackground(Color.BLACK);
		taServer.setForeground(Color.WHITE);
		scrollPane.setViewportView(taServer);
		taServer.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 19));
		taServer.setWrapStyleWord(true);
		taServer.setLineWrap(true);
		taServer.setEditable(false);
		taServer.setBorder(new LineBorder(Color.BLUE, 2));
		//
		c= j.getContentPane();
		c.setLayout(null);
		c.add(lblNewLabel);
	    c.add(scrollPane);
	    j.setVisible(true);
	}
	private void KhoiTao(){
		try {
			 server = new ServerSocket(9999);
		      out.println("Server Started...");
		      taServer.setText(taServer.getText()+"Server started "+"\n");
			  while( true) {
				 Socket clientsocket = server.accept();
				 HandleClient c;
				try {
					c = new HandleClient(clientsocket);
					clients.add(c);
					for (HandleClient hc : clients) {
						hc.getAllUsers();
					    hc.sendUsers();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
				 //c.start();
			 }  // end of while
			
	    } catch (IOException ex) {
	        System.out.println("Loi phan socket: " +ex.getMessage());
	    }
	}
	public Server() {
		GUI();
		KhoiTao();
	}
	public static void main(String[] args) {
		 Server b = new Server();
		
	}
	 public void broadcast(String user, String message)  {
		    // send message to all connected users
		 	
		    for ( HandleClient c : clients ) {
		       if ( ! c.getUserName().equals(user) )
		          c.sendMessage(user,message);
		       //System.out.println(c.getUserName());
		    }
		    
	  }
	  class  HandleClient extends Thread {
			String name = "";
			BufferedReader input;
			PrintWriter output;
			public HandleClient(Socket  clientsocket) throws Exception {
				// get input and output streams
				input = new BufferedReader( new InputStreamReader( clientsocket.getInputStream())) ;
				output = new PrintWriter ( clientsocket.getOutputStream(),true);
				// read name
				name  = input.readLine();
				users.add(name); // add to vector
				start();
				System.out.println(name + " had join group.");
				taServer.setText(taServer.getText()+ name + " had join group."+"\n");
		    }
		    public void sendMessage(String uname,String  msg)  {
		    	output.println( uname + ":" + msg);
		    	
		    	
			}
		    public void sendUsers() {
		    	
		    	output.println(allUsers);
		    }
			public void getAllUsers() {
				allUsers = "AllUser:";
				for ( HandleClient c : clients ) {
					allUsers += c.getUserName() + ",";
				}
				//substring cắt chuỗi mẹ thành chuỗi con bắt đầu bằng 0 
				allUsers = allUsers.substring(0, allUsers.length()-1);
				System.out.println(allUsers);
			}
	        public String getUserName() {  
	            return name; 
	        }
	        public void run()  {
	    	     String line;
			     try    {
		                while(true)   {
							 line = input.readLine();
//							 if ( line.equals("end") ) {
//								 clients.remove(this);
//								 users.remove(name);
//								 break;
//					         }
							 broadcast(name,line); // method  of outer class - send messages to all
						} // end of while
			     } // try
			     catch(Exception ex) {
			       System.out.println(ex.getMessage());
			       users.remove(name);
			       clients.remove(this);
			       for (HandleClient c : clients) {
			    	   c.output.println(name + " : had disconnected.");
			    	   c.getAllUsers();
			    	   c.sendUsers();
			       }
			       
			     }
	        } // end of run()
	   } // end of inner class
	
}

