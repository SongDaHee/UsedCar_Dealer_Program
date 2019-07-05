/*
  		 ^---^
        { *w* }
         $    $
        {_|__|_}====
  FILE NAME: DealRequested.java
  DESCRIPTION: 중고차 딜러 프로 그램 중 거래 요청 페이지를 작성하였습니다.
 
*/
package Deal;

import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.table.TableColumn;
import java.awt.Font;

public class DealRequested extends JPanel {

	// DB에서 스윙 화면으로 테이블 값 가져오기(select) , 저장하기(insert), 수정하기(update), 삭제하기(delete)
	private static final long serialVersionUID = 1L;
	
	// 검색용 상수설정
	public final int SEARCH_P_ID = 0;
	public final int SEARCH_S_ID = 1;
	public final int SEARCH_P_NAME = 2;
	public final int SEARCH_P_YEAR = 3;
	public final int SEARCH_ALL = 4;
	public final int SEARCH_NONE = 5;

	private JLabel jTitleLabel = new JLabel("거래 요청");
	private JButton jBtnAddRow = null; // 테이블 한줄 추가 버튼
	private JButton jBtnSaveRow = null; // 테이블 한줄 저장 버튼
	private JButton jBtnEditRow = null; // 테이블 한줄 수정 버튼
	private JButton jBtnDelRow = null; // 테이블 한줄 삭제 버튼
	private JButton jBtnSearchRow = null; // 테이블 검색 버튼
	private JComboBox<String> jComboBox = null; // 검색어 콤보박스
	private JTextField jSearchField = new JTextField(20);// 검색어 입력 텍스트 필드

	private JTable table;
	private JScrollPane scrollPane; // 테이블 스크롤바 자동으로 생성되게 하기

	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@localhost:1521:xe"; // @호스트 IP : 포트 : SID
	private static String user = "myoracle";
	private static String password = "1234";
	private String colNames[] = { "거래번호", "판매자ID", "모델명", "연식", "가격", "등급", "상태" }; // 테이블 컬럼 값들
	private DefaultTableModel model = new DefaultTableModel(colNames, 0); // 테이블 데이터 모델 객체 생성\

	private void changeCellEditor(JTable table) { // 표내용 콤보박스로 바꾸기
		TableColumn col = table.getColumnModel().getColumn(6);

		JComboBox comboBox = new JComboBox();
		comboBox.addItem("거래요청");
		comboBox.addItem("거래중");
		comboBox.addItem("거래중지");

		col.setCellEditor(new DefaultCellEditor(comboBox));
	}

	private Connection con = null;
	private PreparedStatement pstmt = null;
	private Statement stmt = null;
	private ResultSet rs = null; // 리턴받아 사용할 객체 생성 ( select에서 보여줄 때 필요 )
	private Deal win;

	public DealRequested(Deal win) {
		setLayout(null); // 레이아웃 배치관리자 삭제
		jTitleLabel.setFont(new Font("굴림체", Font.BOLD, 20));

		jTitleLabel.setBounds(36, 28, 150, 82);
		add(jTitleLabel);
		table = new JTable(model); // 테이블에 모델객체 삽입
		changeCellEditor(table);
		table.addMouseListener(new JTableMouseListener()); // 테이블에 마우스리스너 연결
		scrollPane = new JScrollPane(table); // 테이블에 스크롤 생기게 하기
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
		
		// 테이블 새로 저장하는 부분
		jBtnSaveRow = new JButton();
		jBtnSaveRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getActionCommand()); // 선택된 버튼의 텍스트값 출력
				DefaultTableModel model2 = (DefaultTableModel) table.getModel();
				int row = table.getSelectedRow();
				if (row < 0)
					return; // 선택이 안된 상태면 -1리턴
				String query = "insert into ITEM(p_id, s_id, p_name, p_year, p_price, p_grade, p_sales)"
								+ "values (?,?,?,?,?,?,?)";
				try {
					Class.forName(driver); // 드라이버 로딩
					con = DriverManager.getConnection(url, user, password); // DB 연결
					pstmt = con.prepareStatement(query);
					
					// 물음표가 4개 이므로 4개 각각 입력해줘야한다.
					pstmt.setInt(1, Integer.parseInt(model2.getValueAt(row, 0).toString()));
					pstmt.setInt(2, Integer.parseInt(model2.getValueAt(row, 1).toString()));
					pstmt.setString(3, (String) model2.getValueAt(row, 2));
					pstmt.setString(4, (String) model2.getValueAt(row, 3));
					pstmt.setInt(5, Integer.parseInt( model2.getValueAt(row, 4).toString()));
					pstmt.setString(6, (String) model2.getValueAt(row, 5));
					pstmt.setString(7, (String) model2.getValueAt(row, 6));

					int cnt = pstmt.executeUpdate();
					// pstmt.executeUpdate(); create insert update delete
					// pstmt.executeQuery(); select
				} catch (Exception eeee) {
					System.out.println(eeee.getMessage());
					 if(eeee instanceof SQLException) {
						 SQLException se = (SQLException) eeee;
						 switch(se.getErrorCode()) {
						 case 1: // 무결성 제약 조건(MYORACLE.PK_P_ID)에 위배됩니다
							 JOptionPane.showMessageDialog(null, "아이디는 특별해야합니다.", "Message", JOptionPane.ERROR_MESSAGE);
							 return;
						 case 2291: // 무결성 제약조건(MYORACLE.SYS_C007404)이 위배되었습니다- 부모 키가 없습니다
							 int ans = JOptionPane.showConfirmDialog(null, "판매자가 등록되어 있지 않습니다. 고객정보를 등록하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
								if(ans==0) {
									win.change("info");
								}
							
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
				searchItem(SEARCH_NONE, null); // 저장 후 다시 전체 값들을 받아옴.
			}
		});
		jBtnSaveRow.setBounds(178, 406, 120, 25);
		jBtnSaveRow.setText("저장");
		add(jBtnSaveRow);
			
		initialize();
		searchItem(SEARCH_NONE, null);

	}

	class AboutDialog extends JDialog { // 다이얼로그 생성
		JLabel id, name, tel, email, grade, account;
		JTextField s_id, s_name, s_tel, s_email, s_grade, s_account;
		private int row;

		public AboutDialog(int row) {
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

	private class JTableMouseListener implements MouseListener { // 마우스로 눌려진값확인하기
		public void mouseClicked(java.awt.event.MouseEvent e) { // 선택된 위치의 값을 출력

			JTable jtable = (JTable) e.getSource();

			int row = jtable.getSelectedRow(); // 선택된 테이블의 행값
			int col = jtable.getSelectedColumn(); // 선택된 테이블의 열값

			AboutDialog ad = new AboutDialog(row); // 팝업 다이얼 로그

			System.out.println(model.getValueAt(row, col)); // 선택된 위치의 값을 얻어내서 출력

			System.out.println(row + "" + col);

			if (model.getValueAt(row, col) != "" && col == 0) {
				JOptionPane.showMessageDialog(null, "거래번호는 수정이 불가합니다", "Message", JOptionPane.ERROR_MESSAGE);
			} else if (model.getValueAt(row, col) != "" && col == 1) {
				JOptionPane.showMessageDialog(null, "판매자 ID는 수정이 불가합니다", "Message", JOptionPane.ERROR_MESSAGE);
				ad.setVisible(true);
			}

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

	private void searchItem(int searchMode, String keyWord) { // 테이블에 보이기 위해 검색

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			stmt = con.createStatement();

			switch (searchMode) {
			// 상품번호
			case SEARCH_P_ID:
				rs = stmt.executeQuery(
						"SELECT p_id, s_id, p_name, p_year, p_price, p_grade, p_sales FROM item WHERE (p_id LIKE '%"
								+ keyWord + "%') AND (P_SALES IN ('거래요청','거래중지') OR P_SALES IS NULL)");
				break;

			// 판매자ID
			case SEARCH_S_ID:
				rs = stmt.executeQuery(
						"SELECT p_id, s_id, p_name, p_year, p_price, p_grade, p_sales FROM item WHERE (s_id LIKE '%"
								+ keyWord + "%') AND (P_SALES IN ('거래요청','거래중지') OR P_SALES IS NULL)");
				break;

			// 모델명
			case SEARCH_P_NAME:
				rs = stmt.executeQuery(
						"SELECT p_id, s_id, p_name, p_year, p_price, p_grade, p_sales FROM item WHERE (p_name LIKE '%"
								+ keyWord + "%') AND (P_SALES IN ('거래요청','거래중지') OR P_SALES IS NULL)");
				break;

			// 모델명
			case SEARCH_P_YEAR:
				rs = stmt.executeQuery(
						"SELECT p_id, s_id, p_name, p_year, p_price, p_grade, p_sales FROM item WHERE (p_year LIKE '%"
								+ keyWord + "%') AND (P_SALES IN ('거래요청','거래중지') OR P_SALES IS NULL)");
				break;

			// 모두 검색
			case SEARCH_ALL:
				rs = stmt.executeQuery(
						"SELECT DISTINCT p_id, s_id, p_name, p_year, p_price, p_grade, p_sales FROM item WHERE (p_id LIKE '%"
								+ keyWord + "%' OR s_id LIKE '%" + keyWord + "%' OR p_name LIKE '%" + keyWord
								+ "%' OR p_year LIKE '%" + keyWord + "%') AND (P_SALES IN ('거래요청','거래중지')OR P_SALES IS NULL)");
				break;

			// 검색 안함
			case SEARCH_NONE:
				rs = stmt.executeQuery("SELECT p_id, s_id, p_name, p_year, p_price, p_grade, p_sales FROM item where P_SALES IN ('거래요청','거래중지',null) OR P_SALES IS NULL");

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
						rs.getString("p_sales") });
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

		// 테이블 새로 한줄 추가하는 부분
		jBtnAddRow = new JButton();
		jBtnAddRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getActionCommand()); // 선택된 버튼의 텍스트값 출력
				DefaultTableModel model2 = (DefaultTableModel) table.getModel();
				model2.addRow(new String[] { "", "", "", "", "", "", "" }); // 새테이블의 초기값
			}
		});
		jBtnAddRow.setBounds(35, 406, 120, 25);
		jBtnAddRow.setText("추가");
		add(jBtnAddRow);

		// 콤보박스 생성

		jComboBox = new JComboBox<String>(new String[] { "거래번호", "판매자ID", "모델명", "전체검색" });
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

		
		// 선택된 테이블 한줄 수정하는 부분
		jBtnEditRow = new JButton();
		jBtnEditRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				System.out.println(e.getActionCommand()); // 선택된 버튼의 텍스트값 출력
				DefaultTableModel model2 = (DefaultTableModel) table.getModel();

				int row = table.getSelectedRow();
				int col = table.getSelectedColumn();

				if (row < 0)
					return; // 선택이 안된 상태면 -1리턴
				
				if (model2.getValueAt(row, 5) == null && model2.getValueAt(row, 6).equals("거래중")) {
					JOptionPane.showMessageDialog(null, "거래중일시 등급 입력은 필수입니다.", "Message", JOptionPane.ERROR_MESSAGE);
					return;}
			
					

				String query = "UPDATE item SET p_name=?, p_year=?, p_price=?, p_grade =?, p_sales=? "
						+ "WHERE p_id=? AND s_id =?";
				
				
				try {
					Class.forName(driver); // 드라이버 로딩
					con = DriverManager.getConnection(url, user, password); // DB 연결
					pstmt = con.prepareStatement(query);

					// 물음표가 4개 이므로 4개 각각 입력해줘야한다.
					pstmt.setString(1, (String) model2.getValueAt(row, 2)); // p_name
					pstmt.setString(2, (String) model2.getValueAt(row, 3)); // p_year
					pstmt.setInt(3, Integer.parseInt(model2.getValueAt(row, 4).toString())); // p_price
					pstmt.setString(4, (String) model2.getValueAt(row, 5)); // p_grade
					pstmt.setString(5, (String) model2.getValueAt(row, 6)); // p_sales
					pstmt.setInt(6, Integer.parseInt(model2.getValueAt(row, 0).toString())); // p_id
					pstmt.setInt(7, Integer.parseInt(model2.getValueAt(row, 1).toString())); // s_id

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
				searchItem(SEARCH_NONE, null); // 수정 후다시 전체 값들을 받아옴.
			}
		});
		jBtnEditRow.setBounds(483, 406, 120, 25);
		jBtnEditRow.setText("수정");
		add(jBtnEditRow);

		// 선택된 테이블 한줄 삭제하는 부분
		jBtnDelRow = new JButton();
		jBtnDelRow.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				System.out.println(e.getActionCommand()); // 선택된 버튼의 텍스트값 출력
				DefaultTableModel model2 = (DefaultTableModel) table.getModel();
				int row = table.getSelectedRow();
				if (row < 0)
					return; // 선택이 안된 상태면 -1리턴
				String query = "DELETE FROM item WHERE p_id= ? AND s_id = ?";

				try {
					Class.forName(driver); // 드라이버 로딩
					con = DriverManager.getConnection(url, user, password); // DB 연결
					pstmt = con.prepareStatement(query);

					pstmt.setInt(1, Integer.parseInt(model2.getValueAt(row, 0).toString()));
					pstmt.setInt(2, Integer.parseInt(model2.getValueAt(row, 1).toString()));
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
		jBtnDelRow.setBounds(new Rectangle(331, 406, 120, 25));
		jBtnDelRow.setText("삭제");
		add(jBtnDelRow);
	}

}