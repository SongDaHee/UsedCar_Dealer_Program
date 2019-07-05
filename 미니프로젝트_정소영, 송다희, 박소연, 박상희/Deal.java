package Deal;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


import javax.swing.JButton;

public class Deal extends JFrame {

	public DealRequested reqPanel = null;
	public DealFinished finPanel = null;
	public Dealing ingPanel = null;
	public Commission comiPanel = null;
	public CustomerInfo coustomerPanel = null;
	public static Deal win = new Deal();

	public void change(String panelName) {
		if(panelName.equals("req")) {
			reqPanel = new DealRequested(win);
		
			getContentPane().removeAll();
			getContentPane().add(reqPanel);
			revalidate();
			repaint();
		}
		else if(panelName.equals("fin")){
			finPanel = new DealFinished(win);
			
			getContentPane().removeAll();
			getContentPane().add(finPanel);
			revalidate();
			repaint();
		}
		else if(panelName.equals("ing")){
			ingPanel = new Dealing(win);
			
			getContentPane().removeAll();
			getContentPane().add(ingPanel);
			revalidate();
			repaint();
		}
		else if(panelName.equals("comi")){
			comiPanel = new Commission(win);
			
			getContentPane().removeAll();
			getContentPane().add(comiPanel);
			revalidate();
			repaint();
		}
		else if(panelName.equals("info")){
			coustomerPanel = new CustomerInfo(win);
			
			getContentPane().removeAll();
			getContentPane().add(coustomerPanel);
			revalidate();
			repaint();
		}
	}
	
	public static void main(String[] args) {
		win.setTitle("중고차 딜러용 프로그램");
		win.reqPanel = new DealRequested(win);
		win.finPanel = new DealFinished(win);
		win.ingPanel = new Dealing(win);
		win.comiPanel = new Commission(win);
		win.coustomerPanel = new CustomerInfo(win);
		
		
		win.add(win.reqPanel);
		win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		win.setSize(700, 600);
		win.setVisible(true);
	}
}
