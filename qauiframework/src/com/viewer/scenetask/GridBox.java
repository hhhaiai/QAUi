package com.viewer.scenetask;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GridBox extends GridBagConstraints{
	//http://blog.csdn.net/feilongzaitianhehe/article/details/53207172
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4259345520756654064L;
	public GridBox(){
		this.fill =GridBagConstraints.BOTH;// 当格子有剩余空间时，填充空间
		this.anchor = GridBagConstraints.NORTH; // 当组件没有空间大时，使组件处在北部
		setInsets(0, 0, 2, 2);
	}
	
	/**
	 * 当组件没有空间大时，GridBagConstraints
	 * @param anchor
	 * @return
	 */
	public GridBox setAnchor(int anchor){
		this.anchor=anchor;
		return this;
	}
	/**
	 * 当格子有剩余空间时，GridBagConstraints
	 * @param fill
	 * @return
	 */
	public GridBox setFill(int fill){
		this.fill =fill;
		return this;
	}
	/**
	 * 设置组件彼此的间距,上左下右
	 * @param top
	 * @param left
	 * @param bottom
	 * @param right
	 * @return
	 */
	public GridBox setInsets(int top,int left,int bottom,int right){
		this.insets = new Insets(top, left, bottom, right);// 组件彼此的间距 上左下右
		return this;
	}
	/**
	 * 设置网格坐标
	 * @param gridx
	 * @param gridy
	 */
	public GridBox setGridXY(int gridx,int gridy){
		this.gridx=gridx;
		this.gridy=gridy;
		return this;
	}
	/**
	 * 设置组件大小变化的增量值，如设置weightx=100，组件会随着单元格而变化，设置weightx=0时，组件大小不会发生变化。
	 * @param weightx
	 * @param weighty
	 * @return
	 */
	public GridBox setWeight(double weightx, double weighty){
		this.weightx = weightx;  
		this.weighty = weighty;  
	    return this; 
	}
	/**
	 * 恢复默认组件大小变化增量值=0
	 * @return
	 */
	public GridBox resetWeight(){
		this.weightx=0;
		this.weighty=0;
		return this;
	}
	/**
	 * 设置网格大小
	 * @param width
	 * @param height
	 * @return
	 */
	public GridBox setGridWH(int width,int height){
		this.gridwidth=width;
		this.gridheight=height;
		return this;
	}
	/**
	 * 设置网格坐标与大小
	 * @param gridx
	 * @param gridy
	 * @param width
	 * @param height
	 * @return
	 */
	public GridBox setGridXYWH(int gridx,int gridy,int width,int height){
		this.gridx=gridx;
		this.gridy=gridy;
		this.gridwidth=width;
		this.gridheight=height;
		return this;
	}
	/**
	 * 将行坐标置为0
	 * @return
	 */
	public GridBox resetGridX(){
		this.gridx=0;
		return this;
	}
	/**
	 * 将列坐标置为0
	 * @return
	 */
	public GridBox resetGridY(){
		this.gridy=0;
		return this;
	}
	/**
	 * 设置大小为默认1
	 * @return
	 */
	public GridBox resetGridWH(){
		this.gridwidth=1;
		this.gridheight=1;
		return this;
	}
	/**
	 * 将行坐标自增1
	 * @return
	 */
	public GridBox autoGridX(){
		this.gridx+=1;
		return this;
	}
	/**
	 * 将列坐标自增1
	 * @return
	 */
	public GridBox autoGridY(){
		this.gridy+=1;
		return this;
	}

}
