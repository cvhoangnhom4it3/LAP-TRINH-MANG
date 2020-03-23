package v8;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

import static java.lang.System.out;

import java.awt.Color;
import java.awt.Container;

import javax.swing.ImageIcon;
import java.awt.Font;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;

public class Client extends JFrame {

	private JPanel contentPane;
	//final static int SERVER_PORT = 12345; 
	// frame
	JFrame j;
	Container c;
	JTextArea taInput;
	JTextArea taOutput;
	JTextArea taUsers;
	JLabel lblNewLabel;
	JButton btnSend;
	JButton btnShare;
	JScrollPane scrollPane;
	JScrollPane scrollPane_1;
	JScrollPane scrollPane_2;
	// Biến của socket
	String uname;
    PrintWriter pw;
    BufferedReader br;
    Socket client;
    public Client(String uname,String servername) throws Exception {
        super(uname);  // set title for frame
        this.uname = uname;
        client  = new Socket(servername,9999);
        br = new BufferedReader( new InputStreamReader( client.getInputStream()) ) ;
        pw = new PrintWriter(client.getOutputStream(),true);
        pw.println(uname);  // send name to server
        buildInterface();
        new MessagesThread().start();  // create thread to listen for messages
    }
	public void buildInterface() {
		j = new JFrame(uname);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setBounds(100, 100, 681, 468);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 13, 450, 329);
		
		taOutput = new JTextArea();
		taOutput.setEditable(false);
		taOutput.setForeground(Color.BLUE);
		taOutput.setBorder(new LineBorder(Color.BLUE));
		taOutput.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 19));
		taOutput.setLineWrap(true);
		taOutput.setWrapStyleWord(true);
		scrollPane.setViewportView(taOutput);
		
		scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(475, 160, 176, 182);
		
		taUsers = new JTextArea();
		taUsers.setEditable(false);
		taUsers.setBorder(new LineBorder(Color.BLUE));
		taUsers.setForeground(Color.BLUE);
		taUsers.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 19));
		taUsers.setWrapStyleWord(true);
		taUsers.setLineWrap(true);
		scrollPane_2.setViewportView(taUsers);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 355, 450, 53);
		
		taInput = new JTextArea();
		taInput.setBorder(new LineBorder(Color.BLUE));
		taInput.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 19));
		taInput.setLineWrap(true);
		taInput.setWrapStyleWord(true);
		scrollPane_1.setViewportView(taInput);
		
		btnSend = new JButton("");
		btnSend.setContentAreaFilled(false);
		btnSend.setBorderPainted(false);
		btnSend.setIcon(new ImageIcon("C:\\Users\\ChuHoang\\Pictures\\email.png"));
		btnSend.setBounds(563, 355, 88, 53);
		
		lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon("C:\\Users\\ChuHoang\\Pictures\\camp.png"));
		lblNewLabel.setBounds(500, 19, 137, 128);
		
		btnShare = new JButton("");
		btnShare.setContentAreaFilled(false);
		btnShare.setBorderPainted(false);
		btnShare.setIcon(new ImageIcon("C:\\Users\\ChuHoang\\Pictures\\chain.png"));
		btnShare.setBounds(475, 355, 80, 53);
		
		guidi gd = new guidi();
		btnSend.addActionListener(gd);
		// container
	    c= j.getContentPane();
	    c.setLayout(null);
	    c.add(btnSend);
	    c.add(btnShare);
	    c.add(scrollPane_1);
	    c.add(scrollPane);
	    c.add(scrollPane_2);
	    c.add(lblNewLabel);
	    
	    // hiện frame
	    j.setVisible(true);
	}
	class guidi implements ActionListener{

	    @Override
	    public void actionPerformed(ActionEvent e) {
	    	String mess = taInput.getText();
			
			try {
		
				pw.println(taInput.getText());
				taOutput.setText(taOutput.getText()+"(Me): "+mess+"\n");
				taInput.setText("");
			} catch (Exception ex) {
				try {
					client.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
	    }
	}
    public static void main(String[]args) {
    
        // take username from user
        String name = JOptionPane.showInputDialog(null,"Enter your name :", "Username",
            JOptionPane.PLAIN_MESSAGE);
        	String servername = "localhost";  
        try {
            new Client( name ,servername);
        } catch(Exception ex) {
            out.println( "Error --> " + ex.getMessage());
        }
        
    } // end of main
    
    // inner class for Messages Thread
    class  MessagesThread extends Thread {
        public void run() {
            String line;
            try {
                while(true) {
                    line = br.readLine();
                    System.out.println(line);
                    //Tách chuỗi thành mảng hay để cắt chuỗi dựa trên dấu :
                    String tmp[] = line.split(":");
                    if (!tmp[0].equals("AllUser")) {
                    	taOutput.append(line + "\n");
                    } else {
                    	display(tmp[1]);
                    }
                } // end of while
            } catch(Exception ex) {}
        }
        
        public void display(String users) {
        	System.out.println(users);
        	String arrUser[] = users.split(",");
        	taUsers.setText("");
        	for (String user : arrUser) {
        		taUsers.append(user + "\n");
        	}
        }
    }
}
