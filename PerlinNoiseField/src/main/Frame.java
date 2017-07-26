package main;

import java.awt.Dimension;

import javax.swing.JFrame;

public class Frame {
	
	JFrame frame;
	
	public Frame(int width, int height, String title, NoiseGen n) {

		frame = new JFrame(title);
				
		Dimension d = new Dimension(width, height);
		
		frame.setPreferredSize(d);
		frame.setMinimumSize(d);
		frame.setMaximumSize(d);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.requestFocus();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.add(n);
		n.start();
	
	}
}
