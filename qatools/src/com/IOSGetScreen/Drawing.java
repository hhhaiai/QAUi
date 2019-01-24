package com.IOSGetScreen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.Serializable;
//图形绘制类 用于绘制各种图形
//父类，基本图形单元，用到串行的接口，保存使用到
//公共的属性放到超类中，子类可以避免重复定义

/*类通过实现 java.io.Serializable 接口以启用其序列化功能。
未实现此接口的类将无法使其任何状态序列化或反序列化。
可序列化类的所有子类型本身都是可序列化的。序列化接口没有方法或字段，
仅用于标识可序列化的语义。*/


public class Drawing implements Serializable {

/**
	 * 
	 */
	private static final long serialVersionUID = -6262795949635327766L;
int x1,x2,y1,y2;   	    //定义坐标属性
int  R,G,B;				//定义色彩属性
float stroke ;			//定义线条粗细的属性
int type;				//定义字体属性
String s1;				//定义字体的风格
String s2;				//定义字体的风格

void draw(Graphics2D g2d ){}//定义绘图函数
}

class Line extends Drawing//直线类
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -326644136649773114L;

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
class Rect extends Drawing{//矩形类
	/**
	 * 
	 */
	private static final long serialVersionUID = -9107933689959731859L;

	void draw(Graphics2D g2d ){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
	//	System.out.println(x1+"   "+x2+"   "+y1+"   "+y2);
		g2d.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1-x2), Math.abs(y1-y2));
	}
}

class fillRect extends Drawing{//实心矩形类
   /**
	 * 
	 */
	private static final long serialVersionUID = 1381660071752079085L;

void draw(Graphics2D g2d ){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1-x2), Math.abs(y1-y2));
	}
}

class Oval extends Drawing{//椭圆类
	/**
	 * 
	 */
	private static final long serialVersionUID = -2818891017303611274L;

	void draw(Graphics2D g2d ){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1-x2), Math.abs(y1-y2));
	}
}

class fillOval extends Drawing{//实心椭圆类
    /**
	 * 
	 */
	private static final long serialVersionUID = 3221084944432643524L;

	void draw(Graphics2D g2d ){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.fillOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1-x2), Math.abs(y1-y2));
	}
}

class Circle extends Drawing{//矩形类
	/**
	 * 
	 */
	private static final long serialVersionUID = -8010073146875122032L;

	void draw(Graphics2D g2d ){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.max(Math.abs(x1-x2), 
				Math.abs(y1-y2)), Math.max(Math.abs(x1-x2), Math.abs(y1-y2)));
	}
}

class fillCircle extends Drawing{//实心圆类
	/**
	 * 
	 */
	private static final long serialVersionUID = -748125054317562232L;

	void draw(Graphics2D g2d ){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.fillOval(Math.min(x1, x2), Math.min(y1, y2), Math.max(Math.abs(x1-x2),
				Math.abs(y1-y2)), Math.max(Math.abs(x1-x2), Math.abs(y1-y2)));
	}
}

class RoundRect extends Drawing{//圆角矩形类
	/**
	 * 
	 */
	private static final long serialVersionUID = -2252653231987287461L;

	void draw(Graphics2D g2d ){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.drawRoundRect(Math.min(x1, x2), Math.min(y1, y2),Math.abs(x1-x2), Math.abs(y1-y2),50,35);
	}
}

class fillRoundRect extends Drawing{//实心圆角矩形类
	/**
	 * 
	 */
	private static final long serialVersionUID = -3173758795187246893L;

	void draw(Graphics2D g2d ){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke));
		g2d.fillRoundRect(Math.min(x1, x2), Math.min(y1, y2),Math.abs(x1-x2), Math.abs(y1-y2),50,35);
	}
}

class Pencil extends Drawing{//随笔画类
	/**
	 * 
	 */
	private static final long serialVersionUID = -508728336241570819L;

	void draw(Graphics2D g2d ){
		g2d.setPaint(new Color(R,G,B));
		g2d.setStroke(new BasicStroke(stroke,BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL));
	    g2d.drawLine(x1, y1,x2, y2);
	}
}

class Rubber extends Drawing{//橡皮擦类
	/**
	 * 
	 */
	private static final long serialVersionUID = 4747677982713641299L;

	void draw(Graphics2D g2d ){
		g2d.setPaint(new Color(255,255,255));//白色
		g2d.setStroke(new BasicStroke(stroke+4,BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL));
	    g2d.drawLine(x1, y1,x2, y2);
	}
}

class Word extends Drawing{//输入文字类
	/**
	 * 
	 */
	private static final long serialVersionUID = 114245327708950145L;

	void draw(Graphics2D g2d ){
		g2d.setPaint(new Color(R,G,B));
		g2d.setFont(new Font(s2,x2+y2,((int)stroke)*18));//设置字体
	    if(s1 != null)
		g2d.drawString( s1, x1,y1);
	}
}