package com.AutoScript;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;
//图形绘制类 用于绘制各种图形
//父类，基本图形单元，用到串行的接口，保存使用到
//公共的属性放到超类中，子类可以避免重复定义

/*类通过实现 java.io.Serializable 接口以启用其序列化功能。
未实现此接口的类将无法使其任何状态序列化或反序列化。
可序列化类的所有子类型本身都是可序列化的。序列化接口没有方法或字段，
仅用于标识可序列化的语义。*/


public class AutoScriptDrawing implements Serializable {

/**
	 * 
	 */
	private static final long serialVersionUID = -8234939078251848057L;
int x1,x2,y1,y2;   	    //定义坐标属性
int  R,G,B;				//定义色彩属性
float stroke ;			//定义线条粗细的属性

void draw(Graphics2D g2d ){}//定义绘图函数
}

class Drag extends AutoScriptDrawing//直线类
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3334791271343200577L;

	void draw(Graphics2D g2d) {
		g2d.setPaint(new Color(R, G, B));// 为 Graphics2D 上下文设置 Paint 属性。
		// 使用为 null 的 Paint 对象调用此方法对此 Graphics2D 的当前 Paint 属性没有任何影响。

		g2d.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_BEVEL));
		// setStroke(Stroke s)为 Graphics2D 上下文设置 Stroke
		// BasicStroke 类定义针对图形图元轮廓呈现属性的一个基本集合
		// BasicStroke.CAP_ROUND使用半径等于画笔宽度一半的圆形装饰结束未封闭的子路径和虚线线段
		// BasicStroke.JOIN_BEVEL通过直线连接宽体轮廓的外角，将路径线段连接在一起。
		g2d.drawLine(x1, y1, x2, y2);// 画直线
		//g2d.draw(new Line2D.Double(x1, y1, x2, y2));
	}
}

class Tap extends AutoScriptDrawing{//实心圆类
	/**
	 * 
	 */
	private static final long serialVersionUID = 8076137252663320912L;

	void draw(Graphics2D g2d ){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.fillOval(x2-5,y2-5,10,10);
	}
}
class Longtap extends AutoScriptDrawing{//实心圆类
	/**
	 * 
	 */
	private static final long serialVersionUID = -4361206931691009158L;

	void draw(Graphics2D g2d ){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.fillOval(x2-5,y2-5,10,10);
	}
}


