package Deal;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class Commission extends JPanel {

	private JTable jtable;
	private Deal win;
	
	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@localhost:1521:xe";
	String user = "myoracle";
	String password = "1234";
	
	PreparedStatement pstmt = null;
	Connection con = null;
	ResultSet rs = null;
	
	JLabel jtmp = new JLabel();

	public Commission(Deal win) {
		setLayout(null);
		
		JLabel title = new JLabel("커미션");
		title.setFont(new Font("굴림", Font.BOLD, 20));
		title.setBounds(36, 28, 150, 82);
		add(title);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "JPanel title", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(50, 50, 600, 150);
		add("CENTER",panel);
		panel.setBorder(null);
		panel.setLayout(null);
		
		LineBorder bb = new LineBorder(Color.gray,1,true);
		
		JLabel total = new JLabel("총 판매금액");
		total.setHorizontalAlignment(JLabel.CENTER);
		total.setBounds(0, 50, 300, 50);
		total.setBorder(bb);
		panel.add(total);
		
		
		JButton req = new JButton("거래요청");
		req.setSize(100,20);
		req.setLocation(5,10);
		req.setOpaque(false);
		req.setBorderPainted(false);
		req.setContentAreaFilled(false);
		add(req);
		req.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				win.change("req");
			}
		});
		
		JButton ing = new JButton("거래중");
		ing.setSize(100,20);
		ing.setLocation(70,10);
		ing.setOpaque(false);
		ing.setBorderPainted(false);
		ing.setContentAreaFilled(false);
		add(ing);
		ing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				win.change("ing");
			}
		});
		
		JButton fin = new JButton("거래 완료");
		fin.setSize(100,20);
		fin.setLocation(135,10);
		fin.setOpaque(false);
		fin.setBorderPainted(false);
		fin.setContentAreaFilled(false);
		add(fin);
		fin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				win.change("fin");
			}
		});
		
		JButton comi = new JButton("커미션");
		comi.setSize(100,20);
		comi.setLocation(195,10);
		comi.setOpaque(false);
		comi.setBorderPainted(false);
		comi.setContentAreaFilled(false);
		add(comi);
		comi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				win.change("comi");
			}
		});
		
		JButton info = new JButton("고객정보");
		info.setSize(100,20);
		info.setLocation(255,10);
		info.setOpaque(false);
		info.setBorderPainted(false);
		info.setContentAreaFilled(false);
		add(info);
		info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				win.change("info");
			}
		});
		
		String query2 = "SELECT C_PRICE FROM COMMISSION";
		JLabel total_price = new JLabel();
		total_price.setHorizontalAlignment(JLabel.CENTER);
		total_price.setBounds(300, 50, 300, 50);
		total_price.setBorder(bb);
		total_price.setText(totalprice(query2));
		panel.add(total_price);
		
		JLabel select = new JLabel("선택 날짜 판매금액");
		select.setHorizontalAlignment(JLabel.CENTER);
		select.setBounds(0, 100, 300, 50);
		select.setBorder(bb);
		panel.add(select);
		
		JLabel select_price = new JLabel();
		select_price.setHorizontalAlignment(JLabel.CENTER);
		select_price.setBounds(300, 100, 300, 50);
		select_price.setBorder(bb);
		panel.add(select_price);
		
		JComboBox year = new JComboBox();
		year.setBounds(50, 240, 140, 20);
	    for(int i=2000;i<=2020;i++) 
	    {
	    	year.addItem(i); 
	    } 
		add(year);
		
		JComboBox month = new JComboBox();
		month.setBounds(200, 240, 140, 20);
	    for(int i=1;i<=12;i++) 
	    { 
	    	String tmp = String.format("%02d", i);
	    	month.addItem(tmp); 
	    } 
		add(month);
		
		JComboBox day = new JComboBox();
		day.setBounds(354, 240, 141, 20);
	    for(int i=1;i<=31;i++) 
	    { 
	    	String tmp2 = String.format("%02d", i);
	    	day.addItem(tmp2); 
	    } 
		add(day);
		
		String[] col = {"거래번호", "날짜", "판매자 ID", "구매자 ID", "금액"};
		DefaultTableModel model = new DefaultTableModel(col,0);
		String query1 = "SELECT * FROM COMMISSION";
		jtable = new JTable(model);
		ojdbc(query1);
		try {
			while(rs.next()) {
					model.addRow(new Object[] {
							rs.getString("P_ID"), 
							rs.getString("C_DATE"),
							rs.getString("S_ID"),
							rs.getString("B_ID"),
							rs.getString("C_PRICE")});
				}
		} catch (Exception e) {
			System.out.println("ss"+e.getMessage());
		}
		jtable.setBounds(50, 204, 600, 135);
		
		JScrollPane scrollPane = new JScrollPane(jtable);
		scrollPane.setBounds(50, 294, 600, 135);
		add(scrollPane);
			
		
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.setNumRows(0);
				select_price.setText("");
				jtmp.setText(year.getSelectedItem().toString() + "-" +
						month.getSelectedItem().toString() + "-" +
						day.getSelectedItem().toString());
				search(query1, model, select_price);
			}
		};

		JButton search = new JButton("검색");
		search.setBounds(507, 240, 100, 25);
		search.addActionListener(listener);
		add(search);
		
		try {
			rs.close();
			pstmt.close();
			con.close();
		}catch(Exception e2) {}
			
	}
	
	public void search(String query1, DefaultTableModel model, JLabel select_price) {
		ojdbc(query1);
		int selecttmp=0;
		try {
			while(rs.next()) {
				if(rs.getString("C_DATE").contentEquals(jtmp.getText())) {					
					model.addRow(new Object[] {rs.getString("P_ID"), 
							rs.getString("C_DATE"),
							rs.getString("S_ID"),
							rs.getString("B_ID"),
							rs.getString("C_PRICE")});
					selecttmp += rs.getInt("C_PRICE");
					select_price.setText(Integer.toString(selecttmp));
				}
				}
		} catch (Exception e) {
			System.out.println("ss"+e.getMessage());
		}
	}
	
	public String totalprice(String query2) {
		ojdbc(query2);
		int tmp = 0;
		try {
			while(rs.next()) {
				tmp += Integer.parseInt(rs.getString("C_PRICE"));
				}
		} catch (Exception e) {
			System.out.println("ss"+e.getMessage());
		}
		String sum = Integer.toString(tmp);
		return sum;
	}
	
	public void ojdbc(String query1) {
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			pstmt = con.prepareStatement(query1);
			rs = pstmt.executeQuery();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
}
