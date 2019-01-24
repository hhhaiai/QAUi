package com.viewer.scenetask;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 编辑器
 * 
 * @author auto
 *
 */
public class JtableCheckBoxEditor extends JCheckBox implements TableCellEditor {
	Logger logger = LoggerFactory.getLogger(JtableCheckBoxEditor.class);
	private static final long serialVersionUID = -3525402260540234936L;
	// //EventListenerList:保存EventListener 列表的类。
	// private EventListenerList listenerList = new EventListenerList();
	// //ChangeEvent用于通知感兴趣的参与者事件源中的状态已发生更改。
	// private ChangeEvent changeEvent = new ChangeEvent(this);
	CellEditorListener listener;
	int row, column;

	public JtableCheckBoxEditor() {
		super();
		ChangeEvent changeEvent = new ChangeEvent(this);
		addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// logger.info("click row="+row+",column="+column);
				if (listener != null)
					listener.editingStopped(changeEvent);
			}
		});
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		this.row = row;
		this.column = column;
		setForeground(table.getForeground());
		setBackground(table.getBackground());
		if (value == null) {// 所有列值都会依次来适配刷新界面
			setEnabled(false);
			setSelected(false);
		} else if (value instanceof Boolean) {
			setEnabled(true);
			setSelected((Boolean) value);
		}
		return this;
	}

	// private void fireEditingStopped() {
	// CellEditorListener listener;
	// Object[] listeners = listenerList.getListenerList();
	// for(int i = 0; i < listeners.length; i++){
	// if(listeners[i]== CellEditorListener.class){
	// listener= (CellEditorListener)listeners[i+1];
	// //让changeEvent去通知编辑器已经结束编辑
	// //在editingStopped方法中，JTable调用getCellEditorValue()取回单元格的值，
	// //并且把这个值传递给TableValues(TableModel)的setValueAt()
	// listener.editingStopped(changeEvent);
	// }
	// }
	// }

	@Override
	public Object getCellEditorValue() {
		return isEnabled() ? (Boolean) this.isSelected() : null;
	}

	@Override
	public boolean isCellEditable(EventObject anEvent) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean stopCellEditing() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void cancelCellEditing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCellEditorListener(CellEditorListener l) {
		// TODO Auto-generated method stub
		listener = l;
		// listenerList.add(CellEditorListener.class,l);
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l) {
		// TODO Auto-generated method stub
		listener = null;
		// listenerList.remove(CellEditorListener.class,l);
	}

}

/**
 * 渲染器
 * 
 * @author auto
 *
 */
class JtableCheckBoxRenderer extends JCheckBox implements TableCellRenderer {
	private static final long serialVersionUID = 8952840327909966189L;
	Logger logger = LoggerFactory.getLogger(JtableCheckBoxRenderer.class);

	public JtableCheckBoxRenderer() {
		// TODO Auto-generated constructor stub
		super();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		// TODO Auto-generated method stub
		setForeground(table.getForeground());
		setBackground(table.getBackground());
		if (value == null) {
			setEnabled(false);
			setSelected(false);
		} else if (value instanceof Boolean) {
			setEnabled(true);
			setSelected((Boolean) value);
		}
		return this;
	}

}

class JtableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6749978429444690676L;

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

}
