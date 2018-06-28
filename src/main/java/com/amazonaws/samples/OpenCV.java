package com.amazonaws.samples;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

public class OpenCV extends JFrame {
	
	private JPanel contentPane;
	
/*	public static void main(String args[]){
		
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				try{
					OpenCV cv = new OpenCV();
					cv.setVisible(true);					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});				
	}*/
	
	public OpenCV(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 490);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//Dimension size = new Dimension(640, 480);
		//videoCap.setViewSize(size);
		//videoCap.open(true);
		
		new MyThread().start();
	}
	
	Webcam videoCap = Webcam.getDefault();
	
	@Override
	public void paint(Graphics g){	
		g = contentPane.getGraphics();
		g.drawImage(videoCap.getImage(), 0, 0, this);
		//System.out.println(videoCap.getImage().getWidth());
	}
	
	class MyThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("hello");
			for(int i=0; i<10000; i++){
				repaint();
				try{
					Thread.sleep(50);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		
	}
	
	

}
