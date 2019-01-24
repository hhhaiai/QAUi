package com.IOSGetScreen;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectDeviceDialog extends JDialog{
	  /**
	 * 
	 */
	private static final long serialVersionUID = 2596037991003745754L;
	Logger logger = LoggerFactory.getLogger(SelectDeviceDialog.class);
	  private JList<String> mList;
	  private JScrollPane mScrollPane;
	  private JButton mOK;
	  private JButton mCancel;
	  private DefaultListModel<String> mModel;
	  private boolean mIsOK = false;
	  private int mSelectedIndex = -1;
	
	  public SelectDeviceDialog(Frame owner, boolean modal, List<String> initialList){
	    super(owner, modal);
	
	    setTitle("选择设备");
	    setBounds(0, 0, 240, 164);
	    setResizable(false);
	
	    mModel = new DefaultListModel<String>();
	    for (int i = 0; i < initialList.size(); ++i) {
	      mModel.addElement(initialList.get(i));
	    }
	
	    mList = new JList<String>(mModel);
	    if (mModel.getSize() > 0) {
	      mSelectedIndex = 0;
	      mList.setSelectedIndex(mSelectedIndex);
	    }
	    mList.addMouseListener(new MouseListener() {
	      public void mouseReleased(MouseEvent e) {
	      }
	
	      public void mousePressed(MouseEvent e) {
	      }
	
	      public void mouseExited(MouseEvent e) {
	      }
	
	      public void mouseEntered(MouseEvent e) {
	      }
	
	      public void mouseClicked(MouseEvent e) {
	        if (e.getClickCount() > 1)
	          SelectDeviceDialog.this.onOK();
	      }
	    });
	    mScrollPane = new JScrollPane(mList);
	    mScrollPane.setVerticalScrollBarPolicy(20);
	
	    mOK = new JButton("确定");
	    mOK.setEnabled(mModel.getSize() > 0);
	    mOK.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        SelectDeviceDialog.this.onOK();
	      }
	    });
	    mCancel = new JButton("取消");
	    mCancel.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        onCancel();
	      }
	    });
	    Container container1 = new Container();
	    GridLayout gridLayout = new GridLayout(1, 2, 0, 0);
	    container1.setLayout(gridLayout);
	    container1.add(mOK);
	    container1.add(mCancel);
	
	    Container containger = getContentPane();
	    containger.add(mScrollPane, "Center");
	    containger.add(container1, "South");
	
	    AbstractAction actionOK = new AbstractAction() {
	      /**
			 * 
			 */
			private static final long serialVersionUID = 1763676883852010567L;

		public void actionPerformed(ActionEvent e) {
	        onOK();
	      }
	    };
	    AbstractAction actionCancel = new AbstractAction() {
	      /**
			 * 
			 */
			private static final long serialVersionUID = 4897268159385870771L;

		public void actionPerformed(ActionEvent e) {
	        onCancel();
	      }
	    };
	    JComponent targetComponent = getRootPane();
	    InputMap inputMap = targetComponent.getInputMap();
	    inputMap.put(KeyStroke.getKeyStroke(10, 0), "OK");
	    inputMap.put(KeyStroke.getKeyStroke(27, 0), "Cancel");
	    targetComponent.setInputMap(1, inputMap);
	    targetComponent.getActionMap().put("OK", actionOK);
	    targetComponent.getActionMap().put("Cancel", actionCancel);
	  }
	
	  public int getSelectedIndex(){
	    return mSelectedIndex;
	  }
	  
	  public String getSelectedValue(){
		 return  mList.getSelectedValue();
	  }
	  public boolean isOK() {
	    return mIsOK;
	  }
	
	  private void onOK() {
	    mSelectedIndex = mList.getSelectedIndex();
	   
	    mIsOK = true;
	    dispose();
	  }
	
	  private void onCancel() {
	    dispose();
	  }

}