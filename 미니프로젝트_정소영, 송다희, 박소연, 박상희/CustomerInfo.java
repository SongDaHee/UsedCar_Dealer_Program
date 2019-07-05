package Deal;

import java.awt.Rectangle;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;

public class CustomerInfo extends JPanel {

	// DB에서 스윙 화면으로 테이블 값 가져오기(select) , 저장하기(insert), 수정하기(update), 삭제하기(delete)
	private static final long serialVersionUID = 1L;

	// 테이블용 상수설정
	public final int SELLER_TABLE = 0;
	public final int BUYER_TABLE = 1;
	private JLabel tablenames = new JLabel("");

	private JLabel jTitleLabel = new JLabel("고객 정보");
	private JButton jBtnAddRow = null; // 테이블 한줄 추가 버튼
	private JButton jBtnSaveRow = null; // 테이블 한줄 저장 버튼
	private JButton jBtnEditRow = null; // 테이블 한줄 수정 버튼
	private JButton jBtnDelRow = null; // 테이블 한줄 삭제 버튼

	private JTable seltable;
	private JTable buytable;
	private JScrollPane scrollPane; // 테이블 스크롤바 자동으로 생성되게 하기

	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@localhost:1521:xe"; // @호스트 IP : 포트 : SID
	private static String user = "myoracle";
	private static String password = "1234";
	private String selcolNames[] = { "판매자ID", "판매자이름", "전화번호", "이메일", "등급", "계좌번호" }; // 테이블 컬럼 값들
	private DefaultTableModel selmodel = new DefaultTableModel(selcolNames, 0); // 테이블 데이터 모델 객체 생성\

	private String buycolNames[] = { "구매자ID", "구매자이름", "전화번호", "이메일", "등급", "계좌번호" }; // 테이블 컬럼 값들
	private DefaultTableModel buymodel = new DefaultTableModel(buycolNames, 0); // 테이블 데이터 모델 객체 생성\

	private Deal win;
	private Connection con = null;
	private PreparedStatement pstmt = null;
	private Statement stmt = null;
	private Statement stmt2 = null;
	private ResultSet rs = null; // 리턴받아 사용할 객체 생성 ( select에서 보여줄 때 필요 )

	public CustomerInfo(Deal win) {
		setLayout(null); // 레이아웃 배치관리자 삭제
		jTitleLabel.setFont(new Font("굴림체", Font.BOLD, 20));

		JButton req = new JButton("거래요청");
		req.setSize(100, 20);
		req.setLocation(5, 10);
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
		ing.setSize(100, 20);
		ing.setLocation(70, 10);
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
		fin.setSize(100, 20);
		fin.setLocation(135, 10);
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
		comi.setSize(100, 20);
		comi.setLocation(195, 10);
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
		info.setSize(100, 20);
		info.setLocation(255, 10);
		info.setOpaque(false);
		info.setBorderPainted(false);
		info.setContentAreaFilled(false);
		add(info);
		info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				win.change("info");
			}
		});

		jTitleLabel.setBounds(36, 28, 150, 82);
		add(jTitleLabel);
		seltable = new JTable(selmodel); // 테이블에 모델객체 삽입
		seltable.addMouseListener(new JTableMouseListener()); // 테이블에 마우스리스너 연결
		scrollPane = new JScrollPane(seltable); // 테이블에 스크롤 생기게 하기
		scrollPane.setLocation(33, 166);
		scrollPane.setSize(285, 200);
		add(scrollPane);

		buytable = new JTable(buymodel); // 테이블에 모델객체 삽입
		buytable.addMouseListener(new JTableMouseListener()); // 테이블에 마우스리스너 연결
		scrollPane = new JScrollPane(buytable); // 테이블에 스크롤 생기게 하기
		scrollPane.setLocation(335, 166);
		scrollPane.setSize(285, 200);
		add(scrollPane);
		
		add(tablenames);

		initialize();
		searchItem();

	}

	private class JTableMouseListener implements MouseListener { // 마우스로 눌려진값확인하기
		public void mouseClicked(java.awt.event.MouseEvent e) { // 선택된 위치의 값을 출력

			JTable jtable = (JTable) e.getSource();

			int row = jtable.getSelectedRow(); // 선택된 테이블의 행값
			int col = jtable.getSelectedColumn(); // 선택된 테이블의 열값
			
			if(jtable.getModel() == selmodel) {
				tablenames.setText("SELLER_TABLE");
			}
			else {
				tablenames.setText("BUYER_TABLE");
			}

			System.out.println(jtable.getModel().getValueAt(row, col));

			System.out.println(row + "" + col);

		}

		public void mouseEntered(java.awt.event.MouseEvent e) {
		}

		public void mouseExited(java.awt.event.MouseEvent e) {
		}

		public void mousePressed(java.awt.event.MouseEvent e) {
		}

		public void mouseReleased(java.awt.event.MouseEvent e) {
		}

	}

	private void searchItem() { // 테이블에 보이기 위해 검색

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			stmt = con.createStatement();
			stmt2 = con.createStatement();
			rs = stmt.executeQuery("SELECT s_id,s_name,s_tel,s_email,s_grade, s_account FROM seller");
			while (rs.next()) { // 각각 값을 가져와서 테이블값들을 추가
				selmodel.addRow(new Object[] { rs.getInt("s_id"), rs.getString("s_name"), rs.getString("s_tel"),
						rs.getString("s_email"), rs.getString("s_grade"), rs.getInt("s_account") });
			}

			rs = stmt2.executeQuery("SELECT b_id,b_name,b_tel,b_email,b_grade, b_account FROM buyer");
			while (rs.next()) {
				buymodel.addRow(new Object[] { rs.getInt("B_ID"), rs.getString("B_NAME"), rs.getString("B_TEL"),
						rs.getString("B_EMAIL"), rs.getString("B_GRADE"), rs.getInt("B_ACCOUNT") });
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

	private void initialize() { // 액션이벤트와 버튼 컴포넌트 설정

		// 테이블 새로 한줄 추가하는 부분(판매자)
		jBtnAddRow = new JButton();
		jBtnAddRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getActionCommand()); // 선택된 버튼의 텍스트값 출력
				DefaultTableModel model2 = (DefaultTableModel) seltable.getModel();
				model2.addRow(new String[] { "", "", "", "", "", "" }); // 새테이블의 초기값
			}
		});
		jBtnAddRow.setBounds(35, 406, 120, 25);
		jBtnAddRow.setText("판매자추가");
		add(jBtnAddRow);

		// 테이블 새로 한줄 추가하는 부분(구매자)
		jBtnAddRow = new JButton();
		jBtnAddRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getActionCommand()); // 선택된 버튼의 텍스트값 출력
				DefaultTableModel model2 = (DefaultTableModel) buytable.getModel();
				model2.addRow(new String[] { "", "", "", "", "", "" }); // 새테이블의 초기값
			}
		});
		jBtnAddRow.setBounds(172, 406, 120, 25);
		jBtnAddRow.setText("구매자추가");
		add(jBtnAddRow);

		// 테이블 새로 저장하는 부분
		jBtnSaveRow = new JButton();
		jBtnSaveRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getActionCommand()); // 선택된 버튼의 텍스트값 출력
				DefaultTableModel model2 = null;
				String query ="";
				int row = 0;
				String tname = tablenames.getText();
				if (tname.equals("SELLER_TABLE")) {
					model2 = (DefaultTableModel) seltable.getModel();
					query = "insert into seller(s_id, s_name, s_tel, s_email, s_grade, s_account)"
							+ "values (?,?,?,?,?,?)";
					row = seltable.getSelectedRow();
				} else {
					model2 = (DefaultTableModel) buytable.getModel();
					query = "insert into BUYER(B_ID, B_NAME, B_TEL, B_EMAIL, B_GRADE, B_ACCOUNT)"
							+ "values (?,?,?,?,?,?)";
					row  = buytable.getSelectedRow();
				}
				
				if (row < 0)
					return; // 선택이 안된 상태면 -1리턴

				try {
					Class.forName(driver); // 드라이버 로딩
					con = DriverManager.getConnection(url, user, password); // DB 연결
					pstmt = con.prepareStatement(query);

					// 물음표가 4개 이므로 4개 각각 입력해줘야한다.
					pstmt.setInt(1, Integer.parseInt(model2.getValueAt(row, 0).toString()));
					pstmt.setString(2, (String) model2.getValueAt(row, 1));
					pstmt.setString(3, (String) model2.getValueAt(row, 2));
					pstmt.setString(4, (String) model2.getValueAt(row, 3));
					pstmt.setString(5, (String) model2.getValueAt(row, 4));
					pstmt.setInt(6, Integer.parseInt(model2.getValueAt(row, 5).toString()));

					int cnt = pstmt.executeUpdate();
					// pstmt.executeUpdate(); create insert update delete
					// pstmt.executeQuery(); select
				} catch (Exception eeee) {
					System.out.println(eeee.getMessage());
					if (eeee instanceof SQLException) {
						SQLException se = (SQLException) eeee;
						switch (se.getErrorCode()) {
						case 1: // 무결성 제약 조건(MYORACLE.PK_P_ID)에 위배됩니다
							JOptionPane.showMessageDialog(null, "아이디는 특별해야합니다.", "Message", JOptionPane.ERROR_MESSAGE);
							return;

						}
					}
				} finally {
					try {
						pstmt.close();
						con.close();
					} catch (Exception e2) {
					}
				}
				model2.setRowCount(0); // 전체 테이블 화면을 지워줌
				searchItem(); // 저장 후 다시 전체 값들을 받아옴.
			}
		});
		jBtnSaveRow.setBounds(319, 406, 79, 25);
		jBtnSaveRow.setText("저장");
		add(jBtnSaveRow);

		// 선택된 테이블 한줄 수정하는 부분
		jBtnEditRow = new JButton();
		jBtnEditRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				System.out.println(e.getActionCommand()); // 선택된 버튼의 텍스트값 출력
				DefaultTableModel model2 = null;
				int row = 0;
				String query ="";
				String tname = tablenames.getText();
				if (tname.equals("SELLER_TABLE")) {
					model2 = (DefaultTableModel) seltable.getModel();
					query = "UPDATE seller SET s_name=?, s_tel=?, s_email=?, s_grade =?, s_account=? "
							+ "WHERE s_id =?";
					row = seltable.getSelectedRow();
				} else {
					model2 = (DefaultTableModel) buytable.getModel();
					query =  "UPDATE BUYER SET b_name=?, b_tel=?, b_email =?, b_grade=?, b_account=? "
							+ "WHERE b_id=?";
					row  = buytable.getSelectedRow();
				}
				if (row < 0)
					return; // 선택이 안된 상태면 -1리턴

				try {
					Class.forName(driver); // 드라이버 로딩
					con = DriverManager.getConnection(url, user, password); // DB 연결
					pstmt = con.prepareStatement(query);

					// 물음표가 4개 이므로 4개 각각 입력해줘야한다.
					pstmt.setString(1, (String) model2.getValueAt(row, 1)); // s_name
					pstmt.setString(2, (String) model2.getValueAt(row, 2)); // s_tel
					pstmt.setString(3, (String) model2.getValueAt(row, 3)); // s_email
					pstmt.setString(4, (String) model2.getValueAt(row, 4)); // s_grade
					pstmt.setInt(5, Integer.parseInt(model2.getValueAt(row, 5).toString())); // s_account
					pstmt.setInt(6, Integer.parseInt(model2.getValueAt(row, 0).toString())); // s_id

					int cnt = pstmt.executeUpdate();
					// pstmt.executeUpdate(); create insert update delete
					// pstmt.executeQuery(); select
				} catch (Exception eeee) {
					System.out.println(eeee.getMessage());
				} finally {
					try {
						pstmt.close();
						con.close();
					} catch (Exception e2) {
					}
				}
				model2.setRowCount(0); // 전체 테이블 화면을 지워줌
				searchItem(); // 수정 후다시 전체 값들을 받아옴.
			}
		});
		jBtnEditRow.setBounds(524, 406, 79, 25);
		jBtnEditRow.setText("수정");
		add(jBtnEditRow);

		// 선택된 테이블 한줄 삭제하는 부분
		jBtnDelRow = new JButton();
		jBtnDelRow.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				System.out.println(e.getActionCommand()); // 선택된 버튼의 텍스트값 출력
				DefaultTableModel model2 = null;
				int row = 0;
				String query ="";
				String tname = tablenames.getText();
				if (tname.equals("SELLER_TABLE")) {
					model2 = (DefaultTableModel) seltable.getModel();
					query = "DELETE FROM SELLER WHERE s_id= ?";
					row = seltable.getSelectedRow();
				} else {
					model2 = (DefaultTableModel) buytable.getModel();
					query =  "DELETE FROM BUYER WHERE b_id= ?";
					row  = buytable.getSelectedRow();
				}
				
				if (row < 0)
					return; // 선택이 안된 상태면 -1리턴
				
				try {
					Class.forName(driver); // 드라이버 로딩
					con = DriverManager.getConnection(url, user, password); // DB 연결
					pstmt = con.prepareStatement(query);

					pstmt.setInt(1, Integer.parseInt(model2.getValueAt(row, 0).toString()));
					int cnt = pstmt.executeUpdate();
					// pstmt.executeUpdate(); create insert update delete
					// pstmt.executeQuery(); select
				} catch (Exception eeee) {
					System.out.println(eeee.getMessage());
				} finally {
					try {
						pstmt.close();
						con.close();
					} catch (Exception e2) {
					}
				}
				model2.removeRow(row); // 테이블 상의 한줄 삭제
			}
		});
		jBtnDelRow.setBounds(new Rectangle(425, 406, 79, 25));
		jBtnDelRow.setText("삭제");
		add(jBtnDelRow);
	}

}
