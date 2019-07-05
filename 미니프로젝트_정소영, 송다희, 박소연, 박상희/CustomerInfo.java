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

	// DB���� ���� ȭ������ ���̺� �� ��������(select) , �����ϱ�(insert), �����ϱ�(update), �����ϱ�(delete)
	private static final long serialVersionUID = 1L;

	// ���̺�� �������
	public final int SELLER_TABLE = 0;
	public final int BUYER_TABLE = 1;
	private JLabel tablenames = new JLabel("");

	private JLabel jTitleLabel = new JLabel("�� ����");
	private JButton jBtnAddRow = null; // ���̺� ���� �߰� ��ư
	private JButton jBtnSaveRow = null; // ���̺� ���� ���� ��ư
	private JButton jBtnEditRow = null; // ���̺� ���� ���� ��ư
	private JButton jBtnDelRow = null; // ���̺� ���� ���� ��ư

	private JTable seltable;
	private JTable buytable;
	private JScrollPane scrollPane; // ���̺� ��ũ�ѹ� �ڵ����� �����ǰ� �ϱ�

	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@localhost:1521:xe"; // @ȣ��Ʈ IP : ��Ʈ : SID
	private static String user = "myoracle";
	private static String password = "1234";
	private String selcolNames[] = { "�Ǹ���ID", "�Ǹ����̸�", "��ȭ��ȣ", "�̸���", "���", "���¹�ȣ" }; // ���̺� �÷� ����
	private DefaultTableModel selmodel = new DefaultTableModel(selcolNames, 0); // ���̺� ������ �� ��ü ����\

	private String buycolNames[] = { "������ID", "�������̸�", "��ȭ��ȣ", "�̸���", "���", "���¹�ȣ" }; // ���̺� �÷� ����
	private DefaultTableModel buymodel = new DefaultTableModel(buycolNames, 0); // ���̺� ������ �� ��ü ����\

	private Deal win;
	private Connection con = null;
	private PreparedStatement pstmt = null;
	private Statement stmt = null;
	private Statement stmt2 = null;
	private ResultSet rs = null; // ���Ϲ޾� ����� ��ü ���� ( select���� ������ �� �ʿ� )

	public CustomerInfo(Deal win) {
		setLayout(null); // ���̾ƿ� ��ġ������ ����
		jTitleLabel.setFont(new Font("����ü", Font.BOLD, 20));

		JButton req = new JButton("�ŷ���û");
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

		JButton ing = new JButton("�ŷ���");
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

		JButton fin = new JButton("�ŷ� �Ϸ�");
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

		JButton comi = new JButton("Ŀ�̼�");
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

		JButton info = new JButton("������");
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
		seltable = new JTable(selmodel); // ���̺� �𵨰�ü ����
		seltable.addMouseListener(new JTableMouseListener()); // ���̺� ���콺������ ����
		scrollPane = new JScrollPane(seltable); // ���̺� ��ũ�� ����� �ϱ�
		scrollPane.setLocation(33, 166);
		scrollPane.setSize(285, 200);
		add(scrollPane);

		buytable = new JTable(buymodel); // ���̺� �𵨰�ü ����
		buytable.addMouseListener(new JTableMouseListener()); // ���̺� ���콺������ ����
		scrollPane = new JScrollPane(buytable); // ���̺� ��ũ�� ����� �ϱ�
		scrollPane.setLocation(335, 166);
		scrollPane.setSize(285, 200);
		add(scrollPane);
		
		add(tablenames);

		initialize();
		searchItem();

	}

	private class JTableMouseListener implements MouseListener { // ���콺�� ��������Ȯ���ϱ�
		public void mouseClicked(java.awt.event.MouseEvent e) { // ���õ� ��ġ�� ���� ���

			JTable jtable = (JTable) e.getSource();

			int row = jtable.getSelectedRow(); // ���õ� ���̺��� �ప
			int col = jtable.getSelectedColumn(); // ���õ� ���̺��� ����
			
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

	private void searchItem() { // ���̺� ���̱� ���� �˻�

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			stmt = con.createStatement();
			stmt2 = con.createStatement();
			rs = stmt.executeQuery("SELECT s_id,s_name,s_tel,s_email,s_grade, s_account FROM seller");
			while (rs.next()) { // ���� ���� �����ͼ� ���̺����� �߰�
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
				con.close(); // ��ü ������ �ݴ� ������ ����� ��ü�� �ݾ��ش�.
			} catch (Exception e2) {
			}
		}
	}

	private void initialize() { // �׼��̺�Ʈ�� ��ư ������Ʈ ����

		// ���̺� ���� ���� �߰��ϴ� �κ�(�Ǹ���)
		jBtnAddRow = new JButton();
		jBtnAddRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getActionCommand()); // ���õ� ��ư�� �ؽ�Ʈ�� ���
				DefaultTableModel model2 = (DefaultTableModel) seltable.getModel();
				model2.addRow(new String[] { "", "", "", "", "", "" }); // �����̺��� �ʱⰪ
			}
		});
		jBtnAddRow.setBounds(35, 406, 120, 25);
		jBtnAddRow.setText("�Ǹ����߰�");
		add(jBtnAddRow);

		// ���̺� ���� ���� �߰��ϴ� �κ�(������)
		jBtnAddRow = new JButton();
		jBtnAddRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getActionCommand()); // ���õ� ��ư�� �ؽ�Ʈ�� ���
				DefaultTableModel model2 = (DefaultTableModel) buytable.getModel();
				model2.addRow(new String[] { "", "", "", "", "", "" }); // �����̺��� �ʱⰪ
			}
		});
		jBtnAddRow.setBounds(172, 406, 120, 25);
		jBtnAddRow.setText("�������߰�");
		add(jBtnAddRow);

		// ���̺� ���� �����ϴ� �κ�
		jBtnSaveRow = new JButton();
		jBtnSaveRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.getActionCommand()); // ���õ� ��ư�� �ؽ�Ʈ�� ���
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
					return; // ������ �ȵ� ���¸� -1����

				try {
					Class.forName(driver); // ����̹� �ε�
					con = DriverManager.getConnection(url, user, password); // DB ����
					pstmt = con.prepareStatement(query);

					// ����ǥ�� 4�� �̹Ƿ� 4�� ���� �Է�������Ѵ�.
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
						case 1: // ���Ἲ ���� ����(MYORACLE.PK_P_ID)�� ����˴ϴ�
							JOptionPane.showMessageDialog(null, "���̵�� Ư���ؾ��մϴ�.", "Message", JOptionPane.ERROR_MESSAGE);
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
				model2.setRowCount(0); // ��ü ���̺� ȭ���� ������
				searchItem(); // ���� �� �ٽ� ��ü ������ �޾ƿ�.
			}
		});
		jBtnSaveRow.setBounds(319, 406, 79, 25);
		jBtnSaveRow.setText("����");
		add(jBtnSaveRow);

		// ���õ� ���̺� ���� �����ϴ� �κ�
		jBtnEditRow = new JButton();
		jBtnEditRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				System.out.println(e.getActionCommand()); // ���õ� ��ư�� �ؽ�Ʈ�� ���
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
					return; // ������ �ȵ� ���¸� -1����

				try {
					Class.forName(driver); // ����̹� �ε�
					con = DriverManager.getConnection(url, user, password); // DB ����
					pstmt = con.prepareStatement(query);

					// ����ǥ�� 4�� �̹Ƿ� 4�� ���� �Է�������Ѵ�.
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
				model2.setRowCount(0); // ��ü ���̺� ȭ���� ������
				searchItem(); // ���� �Ĵٽ� ��ü ������ �޾ƿ�.
			}
		});
		jBtnEditRow.setBounds(524, 406, 79, 25);
		jBtnEditRow.setText("����");
		add(jBtnEditRow);

		// ���õ� ���̺� ���� �����ϴ� �κ�
		jBtnDelRow = new JButton();
		jBtnDelRow.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				System.out.println(e.getActionCommand()); // ���õ� ��ư�� �ؽ�Ʈ�� ���
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
					return; // ������ �ȵ� ���¸� -1����
				
				try {
					Class.forName(driver); // ����̹� �ε�
					con = DriverManager.getConnection(url, user, password); // DB ����
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
				model2.removeRow(row); // ���̺� ���� ���� ����
			}
		});
		jBtnDelRow.setBounds(new Rectangle(425, 406, 79, 25));
		jBtnDelRow.setText("����");
		add(jBtnDelRow);
	}

}
