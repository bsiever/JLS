package jls;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * Handle end user license agreement approval.
 * 
 * @author David A. Poplawski
 */
public final class Eula extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 8017932670916954040L;
	// properties
	private static boolean ok = false;
	private JButton accept;
	private JButton deny;

	/**
	 * Check for existing agreement file in user.home.
	 * If none there, then show frame, get ok, and save agreement file.
	 */
	public static boolean accepted() {
		
		String home = System.getProperty("user.home");
		File f = new File(home + "/.jls" + JLSInfo.vers);
		try {
			Scanner input = new Scanner(new FileInputStream(f));
			if (input.hasNextLong()) {
				long code = input.nextLong();
				if (code%17==0 && code%97==0) {
					return true;
				}
			}
		}
		catch (FileNotFoundException ex) {
			// nothing to do but continue
		}
		presentEula();
		if (ok) {
			try {
				PrintWriter output = new PrintWriter(new FileOutputStream(f));
				int start = 1000000000;
				int stop = Integer.MAX_VALUE;
				long code = System.currentTimeMillis() % (stop-start) + start;
				while (code%17!=0 || code%97!=0)
					code += 1;
				output.printf("%d %s\n", code, new Date().toString());
				output.close();
			}
			catch (IOException ex) {
				if (GraphicsEnvironment.isHeadless()) {
					System.out.println("Can't create EULA file");
					System.exit(1);
				}
				else {
					JOptionPane.showMessageDialog(null,"Can't create EULA file");
					System.exit(1);
				}
			}
		}
		return ok;
	} // end of check method
	
	private static void presentEula() {
		if(!JLSInfo.batch) {
			try {new Eula((Frame)null,"License Agreement",true);}
			catch (HeadlessException e) {
				presentOnTerminal();
			}
		}
		else presentOnTerminal();
	}
	
	private static void presentOnTerminal() {
		System.out.println("Please read and accept the license agreement below.\n");
		for(String line : lines) {
			System.out.println(line);
		}
		System.out.print("\nDo you accept the terms of the license agreement [Y/n]? ");
		Scanner s = new Scanner(System.in);
		String in = s.nextLine();
		if(in.equalsIgnoreCase("y") || in.length() == 0) ok = true;
		s.close();
	}
	
	/**
	 * Show frame with text in a scrollpane and accept/reject buttons.
	 */
	public Eula(Frame f, String n, boolean m) {
		
		super(f,n,m);
		
		// set up GUI
		Container window = getContentPane();
		window.setLayout(new BorderLayout());
		JTextArea text = new JTextArea(lines.length,60);
		Dimension d = text.getPreferredSize();
		text.setEditable(false);
		text.setFont(new Font("Courier",Font.PLAIN,12));
		for (int i=0; i<lines.length; i+=1) {
			text.append(" "+lines[i].replace("JLSInfo.year",JLSInfo.year+"")+"\n");
		}
		JScrollPane pane = new JScrollPane(text);
		window.add(pane,BorderLayout.CENTER);
		JPanel buttons = new JPanel();
		buttons.setBackground(Color.BLACK);
		buttons.setLayout(new FlowLayout());
		accept = new JButton("AGREE");
		accept.setBackground(Color.GREEN);
		deny = new JButton("DO NOT AGREE");
		//	deny.setBackground(Color.RED);
//		deny.setBackground(Color.BLACK);
//		deny.setForeground(Color.WHITE);
		buttons.add(accept);
		buttons.add(deny);
		window.add(buttons,BorderLayout.SOUTH);
		
		// set up listeners
		accept.addActionListener(this);
		deny.addActionListener(this);
		
		// finish up
		setLocation(100,100);
		setSize(d.width,500);
		setVisible(true);
	} // end of constructor
	
	/**
	 * React to button pushes.
	 */
	public void actionPerformed(ActionEvent event) {
		
		ok = false;
		if (event.getSource() == accept) {
			ok = true;
		}
		dispose();
	} // end of actionPerformed method
	
	// agreement text
	private static String [] lines = {
		"JLS: J(ava) Logic Simulator",
	    "Copyright (C) " + JLSInfo.year,
	    "by " + JLSInfo.authors, 
	    "Repository for this release at: " + JLSInfo.repository, 
	    "",
	    "This program is free software: you can redistribute it and/or modify",
	    "it under the terms of the GNU General Public License as published by",
	    "the Free Software Foundation, either version 3 of the License, or",
	    "(at your option) any later version.",
	    "",
	    "This program is distributed in the hope that it will be useful,",
	    "but WITHOUT ANY WARRANTY; without even the implied warranty of",
	    "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the",
	    "GNU General Public License for more details.",
	    "",
	    "You should have received a copy of the GNU General Public License",
	    "along with this program.  If not, see <http://www.gnu.org/licenses/>."

	};

	
} // end of Eula class
