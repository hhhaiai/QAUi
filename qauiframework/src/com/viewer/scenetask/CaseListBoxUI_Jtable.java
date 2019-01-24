package com.viewer.scenetask;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bean.TestCaseBean;
import com.constant.Cconfig;
import com.viewer.main.MainRun;

public class CaseListBoxUI_Jtable extends JFrame {
	Logger logger = LoggerFactory.getLogger(CaseListBoxUI_Jtable.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 5689338645043186746L;
	private JPanel contentPane;
	String scenename;

	Map<TestCaseBean, Boolean> tempcaserunMap = new LinkedHashMap<>();

	JButton btnOK;
	JButton btnCancel;
	private JButton btnSelectAll;
	ArrayList<JCheckBox> checkBoxslist = new ArrayList<>();
	boolean isselectall = true;

	JTable table;

	/**
	 * Create the frame.
	 */
	public CaseListBoxUI_Jtable(Map<TestCaseBean, Boolean> caserunMap, String scenename) {
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setResizable(false);
		setTitle("用例列表");
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainUI);
		setIconImage(MainRun.settingsBean.getLogo().getImage());

		/**
		 * 初始化
		 */
		this.scenename = scenename;
		caserunMap.entrySet().forEach(e -> {
			tempcaserunMap.put(e.getKey(), e.getValue());
		});
		logger.info("init runcase:" + showinfo(caserunMap));
		JScrollPane scroll = new JScrollPane(CaseJPanel(this.scenename));
		scroll.setBorder(new LineBorder(new Color(0, 0, 0)));
		scroll.setBounds(20, 10, 400, 225);
		contentPane.add(scroll);

		// 确定按钮
		btnOK = new JButton("修改");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press ok button");

				setCaseruninfoFromTable(caserunMap);
				dispose();

			}
		});
		btnOK.setBounds(241, 247, 100, 25);
		contentPane.add(btnOK);
		// 取消按钮
		btnCancel = new JButton("取消");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press cancel button");
				dispose();
			}
		});
		btnCancel.setBounds(344, 247, 100, 25);
		contentPane.add(btnCancel);
		// 全选按钮
		btnSelectAll = new JButton("反选");
		btnSelectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("press btnSelectAll button");
				if (isselectall) {
					btnSelectAll.setText("全选");
					isselectall = false;
					for (int i = 0; i < table.getModel().getRowCount(); i++) {
						if (table.getModel().getValueAt(i, 0) != null)
							table.getModel().setValueAt(false, i, 0);
					}
				} else {
					btnSelectAll.setText("反选");
					isselectall = true;
					for (int i = 0; i < table.getModel().getRowCount(); i++) {
						if (table.getModel().getValueAt(i, 0) != null)
							table.getModel().setValueAt(true, i, 0);
					}
				}
			}
		});
		btnSelectAll.setBounds(30, 247, 100, 25);
		contentPane.add(btnSelectAll);
	}

	/**
	 * 设置需要执行的用例
	 */
	private void setCaseruninfo(Map<TestCaseBean, Boolean> caserunMap) {
		caserunMap.clear();
		for (Entry<TestCaseBean, Boolean> entry : tempcaserunMap.entrySet()) {
			caserunMap.put(entry.getKey(), entry.getValue());
		}
		logger.info("change runcase:" + showinfo(caserunMap));
	}

	/**
	 * 返回需要执行的case信息
	 * 
	 * @param caserunMap
	 * @return
	 */
	private String showinfo(Map<TestCaseBean, Boolean> caserunMap) {
		StringBuffer stringBuffer = new StringBuffer();
		caserunMap.entrySet().forEach(e -> {
			if (e.getValue()) {
				stringBuffer.append(e.getKey().getNo() + ",");
			}
		});
		return stringBuffer.toString();
	}

	/**
	 * 设置需要执行的用例
	 * 
	 * @param caserunMap
	 */
	private void setCaseruninfoFromTable(Map<TestCaseBean, Boolean> caserunMap) {
		caserunMap.clear();
		TableModel tableModel = table.getModel();
		for (Entry<TestCaseBean, Boolean> entry : tempcaserunMap.entrySet()) {
			for (int i = 0; i < tableModel.getRowCount(); i++) {
				if (entry.getKey().getNo() == (Integer) tableModel.getValueAt(i, 1)
						&& entry.getKey().getName().equals((String) tableModel.getValueAt(i, 2))) {
					TestCaseBean testCaseBean = new TestCaseBean();
					testCaseBean.setNo(entry.getKey().getNo());
					testCaseBean.setName(entry.getKey().getName());
					testCaseBean.setRuntime((Integer) tableModel.getValueAt(i, 3));
					testCaseBean.setRetry((Integer) tableModel.getValueAt(i, 4));
					testCaseBean.setDesc(entry.getKey().getDesc());
					testCaseBean.setMethodName(entry.getKey().getMethodName());
					testCaseBean.setNotefailcase((Boolean) tableModel.getValueAt(i, 6));
					caserunMap.put(testCaseBean,
							tableModel.getValueAt(i, 0) != null ? (Boolean) tableModel.getValueAt(i, 0) : false);
				}
			}
		}
		logger.info("change runcase:" + showinfo(caserunMap));
	}

	/**
	 * 用例界面
	 * 
	 * @return
	 */
	private JPanel CaseJPanel(String name) {
		JPanel jPanel = new JPanel();
		jPanel.setLayout(null);
		jPanel.setSize(400, 1000);
		DefaultTableModel tableModel = new DefaultTableModel(
				new String[] { "选择", "序号", "名称", "执行次数", "重试次数", "描述", "标记重跑" }, 0);
		table = new JTable(tableModel) {
			private static final long serialVersionUID = -2024842925708469321L;

			@Override
			public boolean isCellEditable(int row, int column) {
				if (column == 1 || column == 2 || column == 5)
					return false;
				return true;
			}

			@Override
			public Class getColumnClass(int column) {
				if (column == 3)
					return Integer.class;
				if (column == 4)
					return Integer.class;
				return Object.class;
			}

			@Override
			public void setValueAt(Object value, int rowIndex, int columnIndex) {
				if (columnIndex == 3 || columnIndex == 4) {
					super.setValueAt(Math.abs((Integer) value), rowIndex, columnIndex);// 次数使用绝对值
				} else {
					super.setValueAt(value, rowIndex, columnIndex);
				}
			}

			@Override
			public String getToolTipText(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				int col = table.columnAtPoint(e.getPoint());
				String tiptextString = null;
				if (row > -1 && col > -1) {
					Object value = table.getValueAt(row, col);
					if (null != value && !"".equals(value))
						tiptextString = value.toString();// 悬浮显示单元格内容
				}
				return tiptextString;
			}

			@Override
			protected JTableHeader createDefaultTableHeader() { // 表头悬浮提示
				return new JTableHeader(columnModel) {
					private static final long serialVersionUID = -5056459233146543410L;

					public String getToolTipText(MouseEvent e) {
						int index = columnModel.getColumnIndexAtX(e.getPoint().x);
						// int realIndex = columnModel.getColumn(index).getModelIndex();
						return (String) columnModel.getColumn(index).getHeaderValue();
					}
				};
			}
		};
		// table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);// 失去焦点后停止编辑
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);// 关闭自适应大小
		table.setColumnSelectionAllowed(true);
		table.setRowSelectionAllowed(true);
		table.getTableHeader().setReorderingAllowed(false);
		JScrollPane scroll = new JScrollPane(table);
		scroll.setBounds(0, 0, 400, 225);
		jPanel.add(scroll);

		table.getColumnModel().getColumn(0).setCellEditor(new JtableCheckBoxEditor());
		table.getColumnModel().getColumn(0).setCellRenderer(new JtableCheckBoxRenderer());
		table.getColumnModel().getColumn(6).setCellEditor(new JtableCheckBoxEditor());
		table.getColumnModel().getColumn(6).setCellRenderer(new JtableCheckBoxRenderer());
		table.getColumnModel().getColumn(0).setPreferredWidth(40);
		table.getColumnModel().getColumn(1).setPreferredWidth(40);
		table.getColumnModel().getColumn(2).setPreferredWidth(140);
		table.getColumnModel().getColumn(3).setPreferredWidth(60);
		table.getColumnModel().getColumn(4).setPreferredWidth(60);
		table.getColumnModel().getColumn(5).setPreferredWidth(140);
		table.getColumnModel().getColumn(6).setPreferredWidth(60);
		setTableHeaderColor(3, Color.decode(Cconfig.BLUE));
		setTableHeaderColor(4, Color.decode(Cconfig.BLUE));
		setTableHeaderColor(6, Color.decode(Cconfig.BLUE));
		// 初始值判断
		for (Entry<TestCaseBean, Boolean> entry : tempcaserunMap.entrySet()) {
			Vector<Object> row = new Vector<Object>();
			row.add(entry.getKey().getNo() == 0 ? null : entry.getValue());
			row.add(entry.getKey().getNo());
			row.add(entry.getKey().getName());
			row.add(entry.getKey().getRuntime());
			row.add(entry.getKey().getRetry());
			row.add(entry.getKey().getDesc());
			row.add(entry.getKey().isNotefailcase());
			tableModel.addRow(row);
		}
		// jPanel.validate();//重新布局
		// jPanel.repaint(); //重绘界面
		return jPanel;
	}

	/**
	 * 设置表头颜色
	 * 
	 * @param columnIndex
	 * @param c
	 */
	private void setTableHeaderColor(int columnIndex, Color c) {
		TableColumn column = table.getTableHeader().getColumnModel().getColumn(columnIndex);
		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
		cellRenderer.setBackground(c);
		column.setHeaderRenderer(cellRenderer);
	}
}
