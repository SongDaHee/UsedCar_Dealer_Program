package Deal;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.ScrollPane;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import Deal.DealRequested.AboutDialog;

import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import java.awt.SystemColor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.Font;

public class DealFinished extends JPanel {

	private JPanel contentPane;
	private JTable table;
	private JButton jBtnEditRow = null;
	private JButton jBtnSearchRow = null; // 테이블 검색 버튼
	
	private String driver = "oracle.jdbc.driver.OracleDriver";
	private static String url = "jdbc:oracle:thin:@localhost:1521:xe";
	private static String user = "myoracle";
	private static String password = "1234";
	
	private String colNames[] = {"거래번호", "판매자ID", "제품명", "연식", "가격", "등급", "거래표시", "구매자ID"};
	private Connection con = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	private DefaultTableModel model = new DefaultTableModel(colNames, 0);
	private JMenuBar mb = null;
	private Statement stmt = null;
	private JLabel jTitleLabel = new JLabel("거래 완료");
	//private final Action action = new SwingAction();
	
	public final int SEARCH_P_ID = 0;
	public final int SEARCH_S_ID = 1;
	public final int SEARCH_P_NAME = 2;
	public final int SEARCH_P_YEAR = 3;
	public final int SEARCH_B_ID = 4;
	public final int SEARCH_ALL = 5;
	public final int SEARCH_NONE = 6;
	private JComboBox<String> jComboBox = null; // 검색어 콤보박스
	private JTextField jSearchField = new JTextField(20);// 검색어 입력 텍스트 필드
	
	
	private Deal win;

	public DealFinished(Deal win) {
		setLayout(null);
		
		jTitleLabel.setFont(new Font("굴림체", Font.BOLD, 20));
		jTitleLabel.setBounds(36, 28, 150, 82);
		add(jTitleLabel);
		
		table = new JTable(model);
		changeCellEditor(table);
		
		table.addMouseListener(new JTableMouseListener());
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setLocation(33, 166);
		scrollPane.setSize(570, 200);
		add(scrollPane);
		
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
		
		initialize();
		searchItem(SEARCH_NONE, null);
	}
	
	private void changeCellEditor(JTable table) {
		TableColumn col = table.getColumnModel().getColumn(6);
		
		JComboBox comboBox = new JComboBox();
		comboBox.addItem("거래완료");
        comboBox.addItem("거래중지");
        
        col.setCellEditor(new DefaultCellEditor(comboBox));
	}
	
	private void searchItem(int searchMode, String keyWord) { // 테이블에 보이기 위해 검색

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			stmt = con.createStatement();

			switch (searchMode) {
			// 상품번호
			case SEARCH_P_ID:
				rs = stmt.executeQuery(
						"SELECT * FROM item WHERE (p_id LIKE '%"
								+ keyWord + "%') AND (P_SALES IN ('거래완료','거래중지') OR P_SALES IS NULL)");
				break;

			// 판매자ID
			case SEARCH_S_ID:
				rs = stmt.executeQuery(
						"SELECT * FROM item WHERE (s_id LIKE '%"
								+ keyWord + "%') AND (P_SALES IN ('거래완료','거래중지') OR P_SALES IS NULL)");
				break;

			// 모델명
			case SEARCH_P_NAME:
				rs = stmt.executeQuery(
						"SELECT * FROM item WHERE (p_name LIKE '%"
								+ keyWord + "%') AND (P_SALES IN ('거래완료','거래중지') OR P_SALES IS NULL)");
				break;

			// 모델명
			case SEARCH_P_YEAR:
				rs = stmt.executeQuery(
						"SELECT * FROM item WHERE (p_year LIKE '%"
								+ keyWord + "%') AND (P_SALES IN ('거래완료','거래중지') OR P_SALES IS NULL)");
				break;

			case SEARCH_B_ID:
				rs = stmt.executeQuery(
						"SELECT * FROM item WHERE (b_id LIKE '%"
								+ keyWord + "%') AND (P_SALES IN ('거래완료','거래중지') OR P_SALES IS NULL)");
				break;
				
			// 모두 검색
			case SEARCH_ALL:
				rs = stmt.executeQuery(
						"SELECT DISTINCT * FROM item WHERE (p_id LIKE '%"
								+ keyWord + "%' OR s_id LIKE '%" + keyWord + "%' OR p_name LIKE '%" + keyWord
								+ "%' OR p_year LIKE '%" + keyWord + "%') AND (P_SALES IN ('거래완료','거래중지')OR P_SALES IS NULL)");
				break;

			// 검색 안함
			case SEARCH_NONE:
				rs = stmt.executeQuery("SELECT * FROM item where P_SALES IN ('거래완료','거래중지',null) OR P_SALES IS NULL");

				// 기존 검색 상황도 초기화
				jSearchField.setText(null);
				jBtnSearchRow.setText("검색");
				jComboBox.setSelectedIndex(0);
				break;

			}
			
			model.setRowCount(0); // 전체 테이블 화면을 지워줌
			while (rs.next()) { // 각각 값을 가져와서 테이블값들을 추가
				model.addRow(new Object[] { rs.getInt("p_id"), rs.getInt("s_id"), rs.getString("p_name"),
						rs.getString("p_year"), rs.getString("p_price"), rs.getString("p_grade"),
						rs.getString("p_sales"), rs.getInt("b_id") });
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				rs.close();
				pstmt.close();
				con.close(); // 객체 생성한 반대 순으로 사용한 객체는 닫아준다.
			} catch (Exception e2) {
			}
		}
	}

	
	class AboutDialogBuyer extends JDialog { // 다이얼로그 생성
		JLabel id, name, tel, email, grade, account;
		JTextField b_id, b_name, b_tel, b_email, b_grade, b_account;
		private int row;

		public AboutDialogBuyer(int row) {
			this.row = row;
			setTitle("구매자 정보");
			setLayout(null);

			id = new JLabel("아이디");
			name = new JLabel("이름");
			tel = new JLabel("전화번호");
			email = new JLabel("이메일");
			grade = new JLabel("등급");
			account = new JLabel("계좌번호");

			id.setBounds(90, 50, 100, 20);
			name.setBounds(90, 90, 100, 20);
			tel.setBounds(90, 130, 100, 20);
			email.setBounds(90, 170, 100, 20);
			grade.setBounds(90, 210, 100, 20);
			account.setBounds(90, 250, 100, 20);

			add(id);
			add(name);
			add(tel);
			add(email);
			add(grade);
			add(account);

			b_id = new JTextField(10);
			b_name = new JTextField(10);
			b_tel = new JTextField(10);
			b_email = new JTextField(10);
			b_grade = new JTextField(10);
			b_account = new JTextField(10);

			b_id.setBounds(160, 50, 150, 20);
			b_name.setBounds(160, 90, 150, 20);
			b_tel.setBounds(160, 130, 150, 20);
			b_email.setBounds(160, 170, 150, 20);
			b_grade.setBounds(160, 210, 150, 20);
			b_account.setBounds(160, 250, 150, 20);

			add(b_id);
			add(b_name);
			add(b_tel);
			add(b_email);
			add(b_grade);
			add(b_account);

			b_id.setEditable(false);
			b_name.setEditable(false);
			b_tel.setEditable(false);
			b_email.setEditable(false);
			b_grade.setEditable(false);
			b_account.setEditable(false);

			DefaultTableModel model2 = (DefaultTableModel) table.getModel();
			System.out.println(row);

			String query = "SELECT b_id,b_name,b_tel,b_email,b_grade,b_account FROM BUYER WHERE b_id = ?";
			try {
				Class.forName(driver);
				con = DriverManager.getConnection(url, user, password);
				pstmt = con.prepareStatement(query);

				System.out.println(row);
				pstmt.setInt(1, Integer.parseInt(model2.getValueAt(row, 7).toString()));
				rs = pstmt.executeQuery(); // 리턴받아와서 데이터를 사용할 객체생성

				if (rs.next()) { // 각각 값을 가져와서 테이블값들을 추가
					b_id.setText(String.valueOf(rs.getInt("b_id")));
					b_name.setText(rs.getString("b_name"));
					b_tel.setText(rs.getString("b_tel"));
					b_email.setText(rs.getString("b_email"));
					b_grade.setText(rs.getString("b_grade"));
					b_account.setText(rs.getString("b_account"));
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				try {
					rs.close();
					pstmt.close();
					con.close(); // 객체 생성한 반대 순으로 사용한 객체는 닫아준다.
				} catch (Exception e2) {
				}
			}

			setSize(400, 350);
			setLocation(300, 300);
			setResizable(false);

		}
	}
	class AboutDialogSeller extends JDialog { // 다이얼로그 생성
		JLabel id, name, tel, email, grade, account;
		JTextField s_id, s_name, s_tel, s_email, s_grade, s_account;
		private int row;

		public AboutDialogSeller(int row) {
			this.row = row;
			setTitle("판매자 정보");
			setLayout(null);

			id = new JLabel("아이디");
			name = new JLabel("이름");
			tel = new JLabel("전화번호");
			email = new JLabel("이메일");
			grade = new JLabel("등급");
			account = new JLabel("계좌번호");

			id.setBounds(90, 50, 100, 20);
			name.setBounds(90, 90, 100, 20);
			tel.setBounds(90, 130, 100, 20);
			email.setBounds(90, 170, 100, 20);
			grade.setBounds(90, 210, 100, 20);
			account.setBounds(90, 250, 100, 20);

			add(id);
			add(name);
			add(tel);
			add(email);
			add(grade);
			add(account);

			s_id = new JTextField(10);
			s_name = new JTextField(10);
			s_tel = new JTextField(10);
			s_email = new JTextField(10);
			s_grade = new JTextField(10);
			s_account = new JTextField(10);

			s_id.setBounds(160, 50, 150, 20);
			s_name.setBounds(160, 90, 150, 20);
			s_tel.setBounds(160, 130, 150, 20);
			s_email.setBounds(160, 170, 150, 20);
			s_grade.setBounds(160, 210, 150, 20);
			s_account.setBounds(160, 250, 150, 20);

			add(s_id);
			add(s_name);
			add(s_tel);
			add(s_email);
			add(s_grade);
			add(s_account);

			s_id.setEditable(false);
			s_name.setEditable(false);
			s_tel.setEditable(false);
			s_email.setEditable(false);
			s_grade.setEditable(false);
			s_account.setEditable(false);

			DefaultTableModel model2 = (DefaultTableModel) table.getModel();
			System.out.println(row);

			String query = "select s_id,s_name,s_tel,s_email,s_grade,s_account FROM seller WHERE s_id = ?";
			try {
				Class.forName(driver);
				con = DriverManager.getConnection(url, user, password);
				pstmt = con.prepareStatement(query);

				System.out.println(row);
				pstmt.setInt(1, (int) model2.getValueAt(row, 1));
				rs = pstmt.executeQuery(); // 리턴받아와서 데이터를 사용할 객체생성

				if (rs.next()) { // 각각 값을 가져와서 테이블값들을 추가
					s_id.setText(String.valueOf(rs.getInt("s_id")));
					s_name.setText(rs.getString("s_name"));
					s_tel.setText(rs.getString("s_tel"));
					s_email.setText(rs.getString("s_email"));
					s_grade.setText(rs.getString("s_grade"));
					s_account.setText(rs.getString("s_account"));

				}

			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				try {
					rs.close();
					pstmt.close();
					con.close(); // 객체 생성한 반대 순으로 사용한 객체는 닫아준다.
				} catch (Exception e2) {
				}
			}

			setSize(400, 350);
			setLocation(300, 300);
			setResizable(false);

		}
	}
	public class JTableMouseListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			JTable jtable = (JTable)e.getSource();
			int row = jtable.getSelectedRow();
			int col = jtable.getSelectedColumn();

			AboutDialogBuyer ad1 = new AboutDialogBuyer(row); // 팝업 다이얼 로그
			AboutDialogSeller ad2 = new AboutDialogSeller(row);

			System.out.println(model.getValueAt(row, col)); // 선택된 위치의 값을 얻어내서 출력

			System.out.println(row + "" + col);

			if (model.getValueAt(row, col) != "" && col == 0) {
				JOptionPane.showMessageDialog(null, "거래번호는 수정이 불가합니다", "Message", JOptionPane.ERROR_MESSAGE);
			} else if (model.getValueAt(row, col) != "" && col == 7) {
				JOptionPane.showMessageDialog(null, "구매자 ID는 수정이 불가합니다", "Message", JOptionPane.ERROR_MESSAGE);
				ad1.setVisible(true);
			}
			else if (model.getValueAt(row, col) != "" && col == 1) {
				JOptionPane.showMessageDialog(null, "판매자 ID는 수정이 불가합니다", "Message", JOptionPane.ERROR_MESSAGE);
				ad2.setVisible(true);
			}
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public void initialize() {
		jBtnEditRow = new JButton();
		jBtnEditRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getActionCommand());
				DefaultTableModel model2 = (DefaultTableModel)table.getModel();
				int row = table.getSelectedRow();
				if(row < 0) return;
				
				String query = "UPDATE ITEM SET P_PRICE = ?, P_SALES = ?, B_ID = ? WHERE P_ID=?";
				
				try {
					Class.forName(driver);
					con = DriverManager.getConnection(url, user, password);
					pstmt = con.prepareStatement(query);
					
					String tempPrice = model2.getValueAt(row, 4).toString();
					int price = Integer.parseInt(tempPrice);
					 
					String tempBID = model2.getValueAt(row, 7).toString();
					int BID = Integer.parseInt(tempBID);
					
					pstmt.setInt(1,  price);
					pstmt.setString(2,  (String)model2.getValueAt(row, 6));
					pstmt.setInt(3,  BID);
					pstmt.setInt(4, Integer.parseInt(model2.getValueAt(row,0).toString()));
					
					int cnt = pstmt.executeUpdate();
				}catch(Exception ee) {
					System.out.println(ee.getMessage());
				}finally {
					try {
						pstmt.close();
						con.close();
					}catch (Exception ee2) {}
				}
				model2.setRowCount(0);
				searchItem(SEARCH_NONE, null);
			}
		});
		jBtnEditRow.setBounds(483, 406, 120, 25);
		jBtnEditRow.setText("수정");
		add(jBtnEditRow);
		
		jComboBox = new JComboBox<String>(new String[] { "거래번호", "판매자ID", "모델명","구매자ID", "전체검색" });
		jComboBox.setBounds(35, 108, 120, 25);
		add(jComboBox);
		
		// 테이블 검색하는 부분
		jBtnSearchRow = new JButton();
		jBtnSearchRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jBtnSearchRow.getText().equals("검색")) {
		
					if (jSearchField.getText().trim().length() > 0) {

						switch (jComboBox.getSelectedIndex()) {
						// 거래번호
						case SEARCH_P_ID:
							searchItem(SEARCH_P_ID, jSearchField.getText().trim());
							break;

						// 판매자ID
						case SEARCH_S_ID:
							searchItem(SEARCH_S_ID, jSearchField.getText().trim());
							break;

						// 모델명
						case SEARCH_P_NAME:
							searchItem(SEARCH_P_NAME, jSearchField.getText().trim());
							break;

						// 연식
						case SEARCH_P_YEAR:
							searchItem(SEARCH_P_YEAR, jSearchField.getText().trim());
							break;
							
						//판매자 ID
						case SEARCH_B_ID:
							searchItem(SEARCH_B_ID, jSearchField.getText().trim());
							break;
							
						case SEARCH_ALL:
							searchItem(SEARCH_ALL, jSearchField.getText().trim());
							break;
						}

						jBtnSearchRow.setText("취소");
					} else {
						JOptionPane.showMessageDialog(null, "검색어를 입력해주세요.", "Message", JOptionPane.ERROR_MESSAGE);
					}
				}

				else {
					// 검색 초기화
					searchItem(SEARCH_NONE, null);
				}
			}

		});
		jBtnSearchRow.setBounds(483, 109, 120, 25);
		jBtnSearchRow.setText("검색");
		add(jBtnSearchRow);

		jSearchField.setBounds(171, 108, 271, 25);
		add(jSearchField);
	}
}
