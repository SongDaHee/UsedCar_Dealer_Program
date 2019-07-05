/*
  		 ^---^
        { *w* }
         $    $
        {_|__|_}====
  FILE NAME: DealRequested.java
  DESCRIPTION: �߰��� ���� ���� �׷� �� �ŷ� ��û �������� �ۼ��Ͽ����ϴ�.
 
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

	// DB���� ���� ȭ������ ���̺� �� ��������(select) , �����ϱ�(insert), �����ϱ�(update), �����ϱ�(delete)
	private static final long serialVersionUID = 1L;
	
	// �˻��� �������
	public final int SEARCH_P_ID = 0;
	public final int SEARCH_S_ID = 1;
	public final int SEARCH_P_NAME = 2;
	public final int SEARCH_P_YEAR = 3;
	public final int SEARCH_ALL = 4;
	public final int SEARCH_NONE = 5;

	private JLabel jTitleLabel = new JLabel("�ŷ� ��û");
	private JButton jBtnAddRow = null; // ���̺� ���� �߰� ��ư
	private JButton jBtnSaveRow = null; // ���̺� ���� ���� ��ư
	private JButton jBtnEditRow = null; // ���̺� ���� ���� ��ư
	private JButton jBtnDelRow = null; // ���̺� ���� ���� ��ư
	private JButton jBtnSearchRow = null; // ���̺� �˻� ��ư
	private JComboBox<String> jComboBox = null; // �˻��� �޺��ڽ�
	private JTextField jSearchField = new JTextField(20);// �˻��� �Է� �ؽ�Ʈ �ʵ�

	private JTable table;
	private JScrollPane scrollPane; // ���̺� ��ũ�ѹ� �ڵ����� �����ǰ� �ϱ�

	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@localhost:1521:xe"; // @ȣ��Ʈ IP : ��Ʈ : SID
	private static String user = "myoracle";
	private static String password = "1234";
	private String colNames[] = { "�ŷ���ȣ", "�Ǹ���ID", "�𵨸�", "����", "����", "���", "����" }; // ���̺� �÷� ����
	private DefaultTableModel model = new DefaultTableModel(colNames, 0); // ���̺� ������ �� ��ü ����\

	private void changeCellEditor(JTable table) { // ǥ���� �޺��ڽ��� �ٲٱ�
		TableColumn col = table.getColumnModel().getColumn(6);

		JComboBox comboBox = new JComboBox();
		comboBox.addItem("�ŷ���û");
		comboBox.addItem("�ŷ���");
		comboBox.addItem("�ŷ�����");

		col.setCellEditor(new DefaultCellEditor(comboBox));
	}

	private Connection con = null;
	private PreparedStatement pstmt = null;
	private Statement stmt = null;
	private ResultSet rs = null; // ���Ϲ޾� ����� ��ü ���� ( select���� ������ �� �ʿ� )
	private Deal win;

	public DealRequested(Deal win) {
		setLayout(null); // ���̾ƿ� ��ġ������ ����
		jTitleLabel.setFont(new Font("����ü", Font.BOLD, 20));

		jTitleLabel.setBounds(36, 28, 150, 82);
		add(jTitleLabel);
		table = new JTable(model); // ���̺� �𵨰�ü ����
		changeCellEditor(table);
		table.addMouseListener(new JTableMouseListener()); // ���̺� ���콺������ ����
		scrollPane = new JScrollPane(table); // ���̺� ��ũ�� ����� �ϱ�
		scrollPane.setLocation(33, 166);
		scrollPane.setSize(570, 200);
		add(scrollPane);
		

		JButton req = new JButton("�ŷ���û");
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
		
		JButton ing = new JButton("�ŷ���");
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
		
		JButton fin = new JButton("�ŷ� �Ϸ�");
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
		
		JButton comi = new JButton("Ŀ�̼�");
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
		
		JButton info = new JButton("������");
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
		
		// ���̺� ���� �����ϴ� �κ�
		jBtnSaveRow = new JButton();
		jBtnSaveRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getActionCommand()); // ���õ� ��ư�� �ؽ�Ʈ�� ���
				DefaultTableModel model2 = (DefaultTableModel) table.getModel();
				int row = table.getSelectedRow();
				if (row < 0)
					return; // ������ �ȵ� ���¸� -1����
				String query = "insert into ITEM(p_id, s_id, p_name, p_year, p_price, p_grade, p_sales)"
								+ "values (?,?,?,?,?,?,?)";
				try {
					Class.forName(driver); // ����̹� �ε�
					con = DriverManager.getConnection(url, user, password); // DB ����
					pstmt = con.prepareStatement(query);
					
					// ����ǥ�� 4�� �̹Ƿ� 4�� ���� �Է�������Ѵ�.
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
						 case 1: // ���Ἲ ���� ����(MYORACLE.PK_P_ID)�� ����˴ϴ�
							 JOptionPane.showMessageDialog(null, "���̵�� Ư���ؾ��մϴ�.", "Message", JOptionPane.ERROR_MESSAGE);
							 return;
						 case 2291: // ���Ἲ ��������(MYORACLE.SYS_C007404)�� ����Ǿ����ϴ�- �θ� Ű�� �����ϴ�
							 int ans = JOptionPane.showConfirmDialog(null, "�Ǹ��ڰ� ��ϵǾ� ���� �ʽ��ϴ�. �������� ����Ͻðڽ��ϱ�?", "Ȯ��", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
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
				model2.setRowCount(0); // ��ü ���̺� ȭ���� ������
				searchItem(SEARCH_NONE, null); // ���� �� �ٽ� ��ü ������ �޾ƿ�.
			}
		});
		jBtnSaveRow.setBounds(178, 406, 120, 25);
		jBtnSaveRow.setText("����");
		add(jBtnSaveRow);
			
		initialize();
		searchItem(SEARCH_NONE, null);

	}

	class AboutDialog extends JDialog { // ���̾�α� ����
		JLabel id, name, tel, email, grade, account;
		JTextField s_id, s_name, s_tel, s_email, s_grade, s_account;
		private int row;

		public AboutDialog(int row) {
			this.row = row;
			setTitle("�Ǹ��� ����");
			setLayout(null);

			id = new JLabel("���̵�");
			name = new JLabel("�̸�");
			tel = new JLabel("��ȭ��ȣ");
			email = new JLabel("�̸���");
			grade = new JLabel("���");
			account = new JLabel("���¹�ȣ");

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
				rs = pstmt.executeQuery(); // ���Ϲ޾ƿͼ� �����͸� ����� ��ü����

				if (rs.next()) { // ���� ���� �����ͼ� ���̺����� �߰�
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
					con.close(); // ��ü ������ �ݴ� ������ ����� ��ü�� �ݾ��ش�.
				} catch (Exception e2) {
				}
			}

			setSize(400, 350);
			setLocation(300, 300);
			setResizable(false);

		}
	}

	private class JTableMouseListener implements MouseListener { // ���콺�� ��������Ȯ���ϱ�
		public void mouseClicked(java.awt.event.MouseEvent e) { // ���õ� ��ġ�� ���� ���

			JTable jtable = (JTable) e.getSource();

			int row = jtable.getSelectedRow(); // ���õ� ���̺��� �ప
			int col = jtable.getSelectedColumn(); // ���õ� ���̺��� ����

			AboutDialog ad = new AboutDialog(row); // �˾� ���̾� �α�

			System.out.println(model.getValueAt(row, col)); // ���õ� ��ġ�� ���� ���� ���

			System.out.println(row + "" + col);

			if (model.getValueAt(row, col) != "" && col == 0) {
				JOptionPane.showMessageDialog(null, "�ŷ���ȣ�� ������ �Ұ��մϴ�", "Message", JOptionPane.ERROR_MESSAGE);
			} else if (model.getValueAt(row, col) != "" && col == 1) {
				JOptionPane.showMessageDialog(null, "�Ǹ��� ID�� ������ �Ұ��մϴ�", "Message", JOptionPane.ERROR_MESSAGE);
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

	private void searchItem(int searchMode, String keyWord) { // ���̺� ���̱� ���� �˻�

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			stmt = con.createStatement();

			switch (searchMode) {
			// ��ǰ��ȣ
			case SEARCH_P_ID:
				rs = stmt.executeQuery(
						"SELECT p_id, s_id, p_name, p_year, p_price, p_grade, p_sales FROM item WHERE (p_id LIKE '%"
								+ keyWord + "%') AND (P_SALES IN ('�ŷ���û','�ŷ�����') OR P_SALES IS NULL)");
				break;

			// �Ǹ���ID
			case SEARCH_S_ID:
				rs = stmt.executeQuery(
						"SELECT p_id, s_id, p_name, p_year, p_price, p_grade, p_sales FROM item WHERE (s_id LIKE '%"
								+ keyWord + "%') AND (P_SALES IN ('�ŷ���û','�ŷ�����') OR P_SALES IS NULL)");
				break;

			// �𵨸�
			case SEARCH_P_NAME:
				rs = stmt.executeQuery(
						"SELECT p_id, s_id, p_name, p_year, p_price, p_grade, p_sales FROM item WHERE (p_name LIKE '%"
								+ keyWord + "%') AND (P_SALES IN ('�ŷ���û','�ŷ�����') OR P_SALES IS NULL)");
				break;

			// �𵨸�
			case SEARCH_P_YEAR:
				rs = stmt.executeQuery(
						"SELECT p_id, s_id, p_name, p_year, p_price, p_grade, p_sales FROM item WHERE (p_year LIKE '%"
								+ keyWord + "%') AND (P_SALES IN ('�ŷ���û','�ŷ�����') OR P_SALES IS NULL)");
				break;

			// ��� �˻�
			case SEARCH_ALL:
				rs = stmt.executeQuery(
						"SELECT DISTINCT p_id, s_id, p_name, p_year, p_price, p_grade, p_sales FROM item WHERE (p_id LIKE '%"
								+ keyWord + "%' OR s_id LIKE '%" + keyWord + "%' OR p_name LIKE '%" + keyWord
								+ "%' OR p_year LIKE '%" + keyWord + "%') AND (P_SALES IN ('�ŷ���û','�ŷ�����')OR P_SALES IS NULL)");
				break;

			// �˻� ����
			case SEARCH_NONE:
				rs = stmt.executeQuery("SELECT p_id, s_id, p_name, p_year, p_price, p_grade, p_sales FROM item where P_SALES IN ('�ŷ���û','�ŷ�����',null) OR P_SALES IS NULL");

				// ���� �˻� ��Ȳ�� �ʱ�ȭ
				jSearchField.setText(null);
				jBtnSearchRow.setText("�˻�");
				jComboBox.setSelectedIndex(0);
				break;

			}
			
			model.setRowCount(0); // ��ü ���̺� ȭ���� ������
			while (rs.next()) { // ���� ���� �����ͼ� ���̺����� �߰�
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
				con.close(); // ��ü ������ �ݴ� ������ ����� ��ü�� �ݾ��ش�.
			} catch (Exception e2) {
			}
		}
	}
	
	private void initialize() { // �׼��̺�Ʈ�� ��ư ������Ʈ ����

		// ���̺� ���� ���� �߰��ϴ� �κ�
		jBtnAddRow = new JButton();
		jBtnAddRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getActionCommand()); // ���õ� ��ư�� �ؽ�Ʈ�� ���
				DefaultTableModel model2 = (DefaultTableModel) table.getModel();
				model2.addRow(new String[] { "", "", "", "", "", "", "" }); // �����̺��� �ʱⰪ
			}
		});
		jBtnAddRow.setBounds(35, 406, 120, 25);
		jBtnAddRow.setText("�߰�");
		add(jBtnAddRow);

		// �޺��ڽ� ����

		jComboBox = new JComboBox<String>(new String[] { "�ŷ���ȣ", "�Ǹ���ID", "�𵨸�", "��ü�˻�" });
		jComboBox.setBounds(35, 108, 120, 25);
		add(jComboBox);

		// ���̺� �˻��ϴ� �κ�
		jBtnSearchRow = new JButton();
		jBtnSearchRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jBtnSearchRow.getText().equals("�˻�")) {

					if (jSearchField.getText().trim().length() > 0) {

						switch (jComboBox.getSelectedIndex()) {
						// �ŷ���ȣ
						case SEARCH_P_ID:
							searchItem(SEARCH_P_ID, jSearchField.getText().trim());
							break;

						// �Ǹ���ID
						case SEARCH_S_ID:
							searchItem(SEARCH_S_ID, jSearchField.getText().trim());
							break;

						// �𵨸�
						case SEARCH_P_NAME:
							searchItem(SEARCH_P_NAME, jSearchField.getText().trim());
							break;

						// ����
						case SEARCH_P_YEAR:
							searchItem(SEARCH_P_YEAR, jSearchField.getText().trim());
							break;

						case SEARCH_ALL:
							searchItem(SEARCH_ALL, jSearchField.getText().trim());
							break;
						}

						jBtnSearchRow.setText("���");
					} else {
						JOptionPane.showMessageDialog(null, "�˻�� �Է����ּ���.", "Message", JOptionPane.ERROR_MESSAGE);
					}
				}

				else {
					// �˻� �ʱ�ȭ
					searchItem(SEARCH_NONE, null);
				}
			}

		});
		jBtnSearchRow.setBounds(483, 109, 120, 25);
		jBtnSearchRow.setText("�˻�");
		add(jBtnSearchRow);

		jSearchField.setBounds(171, 108, 271, 25);
		add(jSearchField);

		
		// ���õ� ���̺� ���� �����ϴ� �κ�
		jBtnEditRow = new JButton();
		jBtnEditRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				System.out.println(e.getActionCommand()); // ���õ� ��ư�� �ؽ�Ʈ�� ���
				DefaultTableModel model2 = (DefaultTableModel) table.getModel();

				int row = table.getSelectedRow();
				int col = table.getSelectedColumn();

				if (row < 0)
					return; // ������ �ȵ� ���¸� -1����
				
				if (model2.getValueAt(row, 5) == null && model2.getValueAt(row, 6).equals("�ŷ���")) {
					JOptionPane.showMessageDialog(null, "�ŷ����Ͻ� ��� �Է��� �ʼ��Դϴ�.", "Message", JOptionPane.ERROR_MESSAGE);
					return;}
			
					

				String query = "UPDATE item SET p_name=?, p_year=?, p_price=?, p_grade =?, p_sales=? "
						+ "WHERE p_id=? AND s_id =?";
				
				
				try {
					Class.forName(driver); // ����̹� �ε�
					con = DriverManager.getConnection(url, user, password); // DB ����
					pstmt = con.prepareStatement(query);

					// ����ǥ�� 4�� �̹Ƿ� 4�� ���� �Է�������Ѵ�.
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
				model2.setRowCount(0); // ��ü ���̺� ȭ���� ������
				searchItem(SEARCH_NONE, null); // ���� �Ĵٽ� ��ü ������ �޾ƿ�.
			}
		});
		jBtnEditRow.setBounds(483, 406, 120, 25);
		jBtnEditRow.setText("����");
		add(jBtnEditRow);

		// ���õ� ���̺� ���� �����ϴ� �κ�
		jBtnDelRow = new JButton();
		jBtnDelRow.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				System.out.println(e.getActionCommand()); // ���õ� ��ư�� �ؽ�Ʈ�� ���
				DefaultTableModel model2 = (DefaultTableModel) table.getModel();
				int row = table.getSelectedRow();
				if (row < 0)
					return; // ������ �ȵ� ���¸� -1����
				String query = "DELETE FROM item WHERE p_id= ? AND s_id = ?";

				try {
					Class.forName(driver); // ����̹� �ε�
					con = DriverManager.getConnection(url, user, password); // DB ����
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
				model2.removeRow(row); // ���̺� ���� ���� ����
			}
		});
		jBtnDelRow.setBounds(new Rectangle(331, 406, 120, 25));
		jBtnDelRow.setText("����");
		add(jBtnDelRow);
	}

}