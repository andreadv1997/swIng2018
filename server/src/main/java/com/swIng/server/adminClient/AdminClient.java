package com.swIng.server.adminClient;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Date;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Status;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.swIng.server.commons.AlreadyExistingException;
import com.swIng.server.commons.ErrorCodes;
import com.swIng.server.commons.FlashMob;
import com.google.gson.Gson;

public class AdminClient extends JFrame {
	private String username;
	private String password;
	private JTextField nameField ;
	private JTextField descriptionField ;
	private JTextField startDateField ;
	private JTextField endDateField ;
	private JTextField userField ;
	private JTextField passwordField ;
	private JTextField login_log ;	
	private JTextField request_log ;
	private JButton loginButton ;
	private JButton reset_credentials ;
	private JButton postButton ;
	private JButton logoutButton ;
	private JButton reset_field = new JButton("Reset");
	
	
	
	public AdminClient() {
		JFrame frame = new JFrame("Admin Client");
		frame.getContentPane().setLayout(new FlowLayout());
		
		
		JLabel user = new JLabel("UserName");
		JLabel password = new JLabel("Password");
		userField = new JTextField("");
		userField.setMinimumSize(new Dimension(10,20));
		passwordField = new JTextField("");
		passwordField.setMinimumSize(new Dimension(10,20));
		
		login_log = new JTextField("Login Log",JLabel.LEFT);
		login_log.setSize(new Dimension(10,40));
		login_log.setMinimumSize(new Dimension(10,40));
		login_log.setEnabled(false);
		
		
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(user);
		leftPanel.add(userField);
		leftPanel.add(password);
		leftPanel.add(passwordField);
		
		
		JLabel flash_Name= new JLabel("Name");
		JLabel flash_Description= new JLabel("Description");
		JLabel flash_Start_Date = new JLabel("Start Date");
		JLabel flash_End_Date = new JLabel("End Date");
		
		nameField = new JTextField("");
		nameField.setEnabled(false);
		nameField.setMinimumSize(new Dimension(10,30));
		descriptionField = new JTextField("");
		descriptionField.setEnabled(false);
		descriptionField.setMinimumSize(new Dimension(10,30));
		startDateField = new JTextField("");
		startDateField.setEnabled(false);
		startDateField.setMinimumSize(new Dimension(10,30));
		endDateField = new JTextField("");
		endDateField.setEnabled(false);
		endDateField.setMinimumSize(new Dimension(10,30));
		
		request_log = new JTextField("Request Log");
		request_log.setSize(new Dimension(10,30));
		request_log.setMinimumSize(new Dimension(10,40));
		request_log.setHorizontalAlignment(SwingConstants.LEFT);
		request_log.setEnabled(false);

		
		
		JPanel rightPanel= new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.add(flash_Name);
		rightPanel.add(nameField);
		rightPanel.add(flash_Description);
		rightPanel.add(descriptionField);
		rightPanel.add(flash_Start_Date);
		rightPanel.add(startDateField);
		rightPanel.add(flash_End_Date);
		rightPanel.add(endDateField);
		
		loginButton = new JButton("Login");
		reset_credentials = new JButton("Reset");
		
		JPanel leftSubPanel = new JPanel();
		leftSubPanel.setLayout(new FlowLayout());
		
		leftSubPanel.add(loginButton);
		leftSubPanel.add(reset_credentials);
		leftPanel.add(leftSubPanel);
		leftPanel.add(login_log);
		
		postButton = new JButton("Post");
		postButton.setEnabled(false);
		logoutButton = new JButton("Logout");
		logoutButton.setEnabled(false);
		reset_field = new JButton("Reset");
		reset_field.setEnabled(false);
		
		
		JPanel rightSubPanel = new JPanel();
		rightSubPanel.setLayout(new FlowLayout());
		
		rightSubPanel.add(postButton);
		rightSubPanel.add(logoutButton);
		rightSubPanel.add(reset_field);
		rightPanel.add(rightSubPanel);
		rightPanel.add(request_log);
		
		
		
		
		
		
		
		reset_credentials.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent action) {
				userField.setText("");
				passwordField.setText("");
			}  
		} );
		
		reset_field.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent action) {
			
				nameField.setText("");
				descriptionField.setText("");
				startDateField.setText("");
				endDateField.setText("");	    				
			}  
		} );
		
		
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent action) {
				login(userField.getText(),passwordField.getText());
			}
				
		} );
		
		
		logoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent action) {
				AdminClient.this.username=null;
				AdminClient.this.password=null;
				
				nameField.setEnabled(false);
				descriptionField.setEnabled(false);
				startDateField.setEnabled(false);
				endDateField.setEnabled(false);
				
				userField.setEnabled(true);
				passwordField.setEnabled(true);
				loginButton.setEnabled(true);
				reset_credentials.setEnabled(true);
				login_log.setText("Login Log");
				
				try {
					FileOutputStream out = new FileOutputStream(new File("lastAccess.txt"));
					out.write("".getBytes());
					out.close();
					
					
				} catch( IOException e) {
					
					e.printStackTrace();
				}
				
				
				
				postButton.setEnabled(false);
				logoutButton.setEnabled(false);
				reset_field.setEnabled(false);	    				
			}  
		} );
		
		postButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent action) {
				ClientResource cr = new ClientResource("http://localhost:8182/content/admin/flashmob");
				
				// Add the client authentication to the call
				ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
				ChallengeResponse authentication = new ChallengeResponse(scheme,
						AdminClient.this.username,AdminClient.this.password);
				cr.setChallengeResponse(authentication);

				try {
					//dovrei implementare il controllo sulla data: per motivi di testing non viene implementatp
					Date start = FlashMob.DATEFORMAT.parse(startDateField.getText());
					Date end = FlashMob.DATEFORMAT.parse(endDateField.getText());
					if(end.before(start) || end.equals(start)) throw new InvalidDateException("End date cannot have this value");
					if(nameField.getText().contains(" ")) {
						request_log.setText("Name cannot contain \" \" character");
					}
					else{
						FlashMob fm = new FlashMob(nameField.getText(), descriptionField.getText(), start, end);
						String response;
						response = cr.post(new Gson().toJson(fm,FlashMob.class)).getText();
						if (cr.getStatus().getCode()==ErrorCodes.USERNAME_ALREADY_EXISTING)
							throw new Gson().fromJson(response, AlreadyExistingException.class);
						request_log.setText("Insertion ok for new FlashMob");
					}
					
				} catch (ResourceException  e) {
					if (Status.CLIENT_ERROR_UNAUTHORIZED.equals(cr.getStatus())) {
						// Unauthorized access
						login_log.setText("Access unauthorized by the server, check your credentials");
					} else {
						
						login_log.setText("Exception occurred on server");
					}
				}
				catch (IOException e) {
					if (Status.CLIENT_ERROR_UNAUTHORIZED.equals(cr.getStatus())) {
						// Unauthorized access
						login_log.setText("Access unauthorized by the server, check your credentials");
					} else {
						
						login_log.setText("Exception occurred on server");
					}
				}
				catch(AlreadyExistingException e) {
					request_log.setText(e.getMessage());
				}
				catch(ParseException e) {
					request_log.setText("Date Format Exception. Re-insert dates in the correct format");
				}
				catch(InvalidDateException e) {
					request_log.setText(e.getMessage());
				}
			}  
		} );
		
		
		frame.getContentPane().add(leftPanel);
		frame.getContentPane().add(rightPanel);
		
		File accessFile = new File("lastAccess.txt");
		if(accessFile.length()!=0) {
			try {
				Scanner in = new Scanner (accessFile);
				//non controllo la lunghezza perchè il file se è pieno ha due righe
				String uName = in.nextLine();
				String pass = in.nextLine();
				login(uName,pass);
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}
		}
		
		frame.pack();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);



		
		
	}
	
	
	public void login(String username, String password) {
		ClientResource cr = new ClientResource("http://localhost:8182/content/admin/authentication");
		
		// Add the client authentication to the call
		ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
		ChallengeResponse authentication = new ChallengeResponse(scheme,
				username,password);
		cr.setChallengeResponse(authentication);

		try {
			String response;
			response = cr.get().getText();
			login_log.setText("Bentornato, "+ username);
			this.username = username;
			this.password = password;
			
			PrintStream out = new PrintStream(new File("lastAccess.txt"));
			out.println(username);
			out.println(password);
			
			out.close();
			
			nameField.setEnabled(true);
			descriptionField.setEnabled(true);
			startDateField.setEnabled(true);
			endDateField.setEnabled(true);
			
			userField.setEnabled(false);
			passwordField.setEnabled(false);
			loginButton.setEnabled(false);
			reset_credentials.setEnabled(false);
			
			
			postButton.setEnabled(true);
			logoutButton.setEnabled(true);
			reset_field.setEnabled(true);
			
			
		} catch (ResourceException e) {
			if (Status.CLIENT_ERROR_UNAUTHORIZED.equals(cr.getStatus())) {
				// Unauthorized access
				login_log.setText("Access unauthorized by the server, check your credentials");
			} else {
				
				login_log.setText("Exception occurred on server");
			}
		}
		catch (IOException e) {
			if (Status.CLIENT_ERROR_UNAUTHORIZED.equals(cr.getStatus())) {
				// Unauthorized access
				login_log.setText("Access unauthorized by the server, check your credentials");
			} else {
				
				login_log.setText("Exception occurred on server");
			}
		}
	}
	
	
	public static void main(String[] args) {
		JFrame frame = new AdminClient();

	}

}
