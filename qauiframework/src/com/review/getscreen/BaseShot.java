package com.review.getscreen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;
import com.viewer.main.MainRun;

public abstract class BaseShot {
	Logger logger = LoggerFactory.getLogger(BaseShot.class);
	protected String udid;
	protected File screenfolder;
	protected int picTargetHight;
	protected int piccount = 0;
	protected int cuspiccount = 0;
	protected int zoom = 1;// 像素与点调整倍数,针对IOS
	protected int device_width = 1;// ios为point
	protected int device_hight = 1;// ios为point
	String shottype;
	int fontSize;// 文字大小
	int lineSize;// 线条粗细
	int ovalSize;// 圆点半径大小

	public BaseShot(String shottype, String udid, File reportFolder) {
		// TODO Auto-generated constructor stub
		this.udid = udid;
		this.shottype = shottype;
		this.screenfolder = new File(reportFolder.getAbsolutePath() + "/ScreenShot");
		if (!screenfolder.exists())
			screenfolder.mkdirs();
		picTargetHight = MainRun.sysConfigBean.getPicTargetHight();
		if (picTargetHight % 2 != 0)
			picTargetHight = +1;
	}

	/**
	 * 设置画笔大小
	 */
	protected void drawsize() {
		int adjustfontSize = 0;// 调整
		int adjustlineSize = 0;
		int adjustovalSize = 0;
		int pichight = getDevice_hight() * getZoom();
		if (pichight >= 1300) {
			adjustfontSize = -10;
			adjustlineSize = -30;
			adjustovalSize = -20;
		} else if (pichight < 1000) {
			adjustfontSize = 5;
			adjustlineSize = 20;
			adjustovalSize = 10;
		}
		if (MainRun.sysConfigBean.getPicFont() == Cconfig.SMALLFONT) {
			fontSize = pichight / (45 + adjustfontSize);
		} else if (MainRun.sysConfigBean.getPicFont() == Cconfig.LARGEFONT) {
			fontSize = pichight / (25 + adjustfontSize);
		} else {
			fontSize = pichight / (35 + adjustfontSize);
		}
		lineSize = pichight / (200 + adjustlineSize);
		ovalSize = pichight / (50 + adjustovalSize);
		if (fontSize < 1)
			fontSize = 36;
		if (lineSize < 1)
			lineSize = 6;
		if (ovalSize < 1)
			ovalSize = 25;
		logger.info("fontSize:" + fontSize + ",lineSize:" + lineSize + ",ovalSize:" + ovalSize);
	}

	/**
	 * 得到放大倍数
	 * 
	 * @return
	 */
	public int getZoom() {
		return zoom;
	}

	/**
	 * 得到设备的宽(iOS为point,Android为像素点)
	 * 
	 * @return
	 */
	public int getDevice_width() {
		return device_width;
	}

	/**
	 * 得到设备的高(iOS为point,Android为像素点)
	 * 
	 * @return
	 */
	public int getDevice_hight() {
		return device_hight;
	}

	/**
	 * 得到图片计数
	 * 
	 * @return
	 */
	public int getPiccount() {
		return piccount;
	}

	/**
	 * 截图,画矩形
	 * 
	 * @param name
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public String drawRect(String name, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		String picpath = ScreenShot(name);
		if (picpath != null)
			drawRectPicture(picpath, zoom * x, zoom * y, zoom * width, zoom * height, Color.GREEN);
		return picpath;
	}

	/**
	 * 截图,画矩形,自定义线条颜色
	 * 
	 * @param name
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 * @return
	 */
	public String drawRect(String name, int x, int y, int width, int height, Color color) {
		// TODO Auto-generated method stub
		String picpath = ScreenShot(name);
		if (picpath != null)
			drawRectPicture(picpath, zoom * x, zoom * y, zoom * width, zoom * height, color);
		return picpath;
	}

	/**
	 * 截图,写文字
	 * 
	 * @param name
	 * @param str
	 * @return
	 */
	public String drawText(String name, String str) {
		// TODO Auto-generated method stub
		String picpath = ScreenShot(name);
		if (picpath != null)
			drawTextPicture(picpath, str, true);
		return picpath;
	}

	/**
	 * 截图,写文字,不压缩
	 * 
	 * @param name
	 * @param str
	 * @return
	 */
	public String drawTextByCustomer(String name, String str, String picpath) {
		// TODO Auto-generated method stub
		picpath = ScreenShotByCustomer(name, picpath);
		if (picpath != null)
			drawTextPicture(picpath, str, false);
		return picpath;
	}

	/**
	 * 截图,画实心圆
	 * 
	 * @param name
	 * @param x
	 * @param y
	 * @param radius
	 * @return
	 */
	public String drawOval(String name, int x, int y) {
		// TODO Auto-generated method stub
		String picpath = ScreenShot(name);
		if (picpath != null)
			drawOvalPicture(picpath, zoom * x, zoom * y, Color.GREEN);
		return picpath;
	}

	/**
	 * 截图,画实心圆
	 * 
	 * @param name
	 * @param x
	 * @param y
	 * @param color
	 * @return
	 */
	public String drawOval(String name, int x, int y, Color color) {
		// TODO Auto-generated method stub
		String picpath = ScreenShot(name);
		if (picpath != null)
			drawOvalPicture(picpath, zoom * x, zoom * y, color);
		return picpath;
	}

	/**
	 * 截图,画箭头
	 * 
	 * @param name
	 * @param startx
	 * @param starty
	 * @param endx
	 * @param endy
	 * @return
	 */
	public String drawArrow(String name, int startx, int starty, int endx, int endy) {
		// TODO Auto-generated method stub
		String picpath = ScreenShot(name);
		if (picpath != null)
			drawArrowPicture(picpath, zoom * startx, zoom * starty, zoom * endx, zoom * endy, true);
		return picpath;
	}

	/**
	 * 截图,画箭头
	 * 
	 * @param name
	 * @param startx
	 * @param starty
	 * @param endx
	 * @param endy
	 * @param iscompress是否压缩
	 * @return
	 */
	public String drawArrow(String name, int startx, int starty, int endx, int endy, boolean iscompress) {
		// TODO Auto-generated method stub
		String picpath = ScreenShot(name);
		if (picpath != null)
			drawArrowPicture(picpath, zoom * startx, zoom * starty, zoom * endx, zoom * endy, iscompress);
		return picpath;
	}

	/**
	 * 画箭头
	 * 
	 * @param path
	 *            图片地址
	 * @param startx
	 * @param starty
	 * @param endx
	 * @param endy
	 * @param iscompress是否压缩
	 */
	public void drawArrowPic(String path, int startx, int starty, int endx, int endy, boolean iscompress) {
		drawArrowPicture(path, zoom * startx, zoom * starty, zoom * endx, zoom * endy, iscompress);
	}

	/**
	 * 得到屏幕截图,返回截图地址
	 * 
	 * @param name
	 * @return
	 */
	public abstract String ScreenShot(String name);

	/**
	 * 用户自定义截图
	 * 
	 * @param name
	 * @return
	 */
	public abstract String ScreenShotByCustomer(String name, String picpath);

	/**
	 * 原始截图
	 * 
	 * @param name
	 * @param path
	 * @param iscutomer
	 * @return
	 */
	public abstract String Shot(String name, String picpath, boolean iscutomer);

	/**
	 * 标注文字
	 * 
	 * @param path
	 * @param str
	 */
	public void drawTextPicture(String path, String text, boolean iscompress) {
		// 读取图片文件，得到BufferedImage对象
		Graphics2D g2d = null;
		BufferedImage bimg = null;
		try {
			File picfile = new File(path);
			bimg = ImageIO.read(picfile);
			int picwidth = bimg.getWidth();
			int pichight = bimg.getHeight();
			// logger.info("picwidth="+picwidth+" pichight="+pichight);
			if (text != null && !text.equals("")) {
				// 得到Graphics2D 对象
				g2d = (Graphics2D) bimg.getGraphics();
				// 设置颜色和画笔粗细
				g2d.setColor(Color.RED);
				g2d.setStroke(new BasicStroke(5));
				g2d.setBackground(Color.WHITE);
				// 绘制图案或文字
				Font font = new Font("微软雅黑", Font.PLAIN, fontSize);
				g2d.setFont(font);// 设置字体
				int fontlen = g2d.getFontMetrics(g2d.getFont()).charsWidth(text.toCharArray(), 0, text.length());
				int line = fontlen / picwidth;// 文字长度相对于图片宽度应该有多少行
				// int y = pichight- (line + 1)*fontSize;
				int y = (line + 1) * fontSize;
				// 文字叠加,自动换行叠加
				int tempX = 0;
				int tempY = y;
				int tempCharLen = 0;// 单字符长度
				int tempLineLen = 0;// 单行字符总长度临时计算
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < text.length(); i++) {
					char tempChar = text.charAt(i);
					tempCharLen = g2d.getFontMetrics(g2d.getFont()).charWidth(tempChar);
					tempLineLen += tempCharLen;
					if (tempLineLen >= picwidth) {
						// 长度已经满一行,进行文字叠加
						g2d.drawString(sb.toString(), tempX, tempY);
						sb.delete(0, sb.length());// 清空内容,重新追加
						tempY += fontSize;
						tempLineLen = 0;
					}
					sb.append(tempChar);// 追加字符
				}
				g2d.drawString(sb.toString(), tempX, tempY);// 最后叠加余下的文字
				// g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				// RenderingHints.VALUE_ANTIALIAS_ON);//消除锯齿
				// AttributedString ats = new AttributedString(str);
				// ats.addAttribute(TextAttribute.FONT, font, 0, str.length());
				// AttributedCharacterIterator iter = ats.getIterator();
				// g2d.drawString(iter, 0, pichight/10);//设置位置
			}
			// 保存新图片
			if (iscompress) {
				compressPicture(bimg, picfile);
			} else {
				ImageIO.write(bimg, "PNG", picfile);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		} finally {
			if (g2d != null) {
				g2d.dispose();
			}
			bimg = null;
		}
	}

	/**
	 * 空心矩形标记
	 */
	protected void drawRectPicture(String path, int x, int y, int width, int height, Color color) {
		Graphics2D g2d = null;
		BufferedImage bimg = null;
		try {
			File picfile = new File(path);
			bimg = ImageIO.read(picfile);
			// 得到Graphics2D 对象
			g2d = (Graphics2D) bimg.getGraphics();
			// 设置颜色和画笔粗细
			g2d.setColor(color);
			g2d.setStroke(new BasicStroke(lineSize));
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);// 消除锯齿
			// 绘制图案或文字
			g2d.drawRect(x + lineSize / 2, y + lineSize / 2, width - lineSize, height - lineSize);
			// 保存新图片
			compressPicture(bimg, picfile);
			// ImageIO.write(bimg, "PNG",new FileOutputStream(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		} finally {
			if (g2d != null) {
				g2d.dispose();
			}
			bimg = null;
		}
	}

	/**
	 * 实心圆标记
	 */
	protected void drawOvalPicture(String path, int x, int y, Color color) {
		Graphics2D g2d = null;
		BufferedImage bimg = null;
		try {
			File picfile = new File(path);
			bimg = ImageIO.read(picfile);
			// 得到Graphics2D 对象
			g2d = (Graphics2D) bimg.getGraphics();
			// 设置颜色和画笔粗细
			g2d.setColor(color);
			g2d.setStroke(new BasicStroke(6));
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);// 消除锯齿
			// 绘制图案或文字
			g2d.fillOval(x, y, ovalSize, ovalSize);
			// 保存新图片
			compressPicture(bimg, picfile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		} finally {
			if (g2d != null) {
				g2d.dispose();
			}
			bimg = null;
		}
	}

	/**
	 * 直线箭头标记
	 */
	protected void drawArrowPicture(String path, int startx, int starty, int endx, int endy, boolean iscompress) {
		Graphics2D g2d = null;
		BufferedImage bimg = null;
		try {
			File picfile = new File(path);
			bimg = ImageIO.read(picfile);
			// 得到Graphics2D 对象
			g2d = (Graphics2D) bimg.getGraphics();
			// 设置颜色和画笔粗细
			g2d.setColor(Color.GREEN);
			g2d.setStroke(new BasicStroke(lineSize));
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);// 消除锯齿
			// 画直线
			g2d.drawLine(startx, starty, endx, endy);
			g2d.drawPolygon(getArrow(startx, starty, endx, endy, 18, 0, 0.5)); // startx,starty,endx,endy,headsize,difference,factor
			g2d.fillPolygon(getArrow(startx, starty, endx, endy, 18, 0, 0.5));
			// 保存新图片
			if (iscompress) {
				compressPicture(bimg, picfile);
			} else {
				ImageIO.write(bimg, "PNG", picfile);
			}
			// ImageIO.write(bimg, "PNG",new FileOutputStream(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		} finally {
			if (g2d != null) {
				g2d.dispose();
			}
			bimg = null;
		}
	}

	/**
	 * 压缩图片
	 * 
	 * @throws IOException
	 */
	protected void compressPicture(BufferedImage bimg, File picfile) throws IOException {
		BufferedImage new_bimg = null;
		int picwidth = bimg.getWidth();
		int pichight = bimg.getHeight();
		if (picTargetHight != 0 && bimg.getHeight() > picTargetHight) {// 缩小图片
			int picTargetwidth = (int) (((double) picwidth / pichight) * picTargetHight);
			if (picTargetwidth % 2 != 0)
				picTargetwidth += 1;
			new_bimg = new BufferedImage(picTargetwidth, picTargetHight, BufferedImage.TYPE_INT_RGB);
			new_bimg.getGraphics().drawImage(bimg.getScaledInstance(picTargetwidth, picTargetHight, Image.SCALE_SMOOTH),
					0, 0, null);
			ImageIO.write(new_bimg, "PNG", picfile);
		} else {
			ImageIO.write(bimg, "PNG", picfile);
		}
		new_bimg = null;
	}

	/**
	 * 画箭头
	 * 
	 * @param g2
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void LineArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
		g2.drawPolygon(getArrow(x1, y1, x2, y2, 18, 0, 0.5)); // startx,starty,endx,endy,headsize,difference,factor
		g2.fillPolygon(getArrow(x1, y1, x2, y2, 18, 0, 0.5));
	}

	public Polygon getArrow(int x1, int y1, int x2, int y2, int headsize, int difference, double factor) {
		int[] crosslinebase = getArrowHeadLine(x1, y1, x2, y2, headsize);
		int[] headbase = getArrowHeadLine(x1, y1, x2, y2, headsize - difference);
		int[] crossline = getArrowHeadCrossLine(crosslinebase[0], crosslinebase[1], x2, y2, factor);
		Polygon head = new Polygon();
		head.addPoint(headbase[0], headbase[1]);
		head.addPoint(crossline[0], crossline[1]);
		head.addPoint(x2, y2);
		head.addPoint(crossline[2], crossline[3]);
		head.addPoint(headbase[0], headbase[1]);
		head.addPoint(x1, y1);
		return head;
	}

	public int[] getArrowHeadLine(int xsource, int ysource, int xdest, int ydest, int distance) {
		int[] arrowhead = new int[2];
		int headsize = distance;
		double stretchfactor = 0;
		stretchfactor = 1 - (headsize
				/ (Math.sqrt(((xdest - xsource) * (xdest - xsource)) + ((ydest - ysource) * (ydest - ysource)))));
		arrowhead[0] = (int) (stretchfactor * (xdest - xsource)) + xsource;
		arrowhead[1] = (int) (stretchfactor * (ydest - ysource)) + ysource;
		return arrowhead;
	}

	public int[] getArrowHeadCrossLine(int x1, int x2, int b1, int b2, double factor) {
		int[] crossline = new int[4];
		int xdest = (int) (((b1 - x1) * factor) + x1);
		int ydest = (int) (((b2 - x2) * factor) + x2);
		crossline[0] = (int) ((x1 + x2 - ydest));
		crossline[1] = (int) ((x2 + xdest - x1));
		crossline[2] = crossline[0] + (x1 - crossline[0]) * 2;
		crossline[3] = crossline[1] + (x2 - crossline[1]) * 2;
		return crossline;
	}

	/**
	 * 从内存字节数组中读取图像
	 * 
	 * @param imgBytes
	 *            未解码的图像数据
	 * @return 返回 {@link BufferedImage}
	 * @throws IOException
	 *             当读写错误或不识别的格式时抛出
	 */
	public BufferedImage readMemoryImage(byte[] imgBytes) throws IOException {
		if (null == imgBytes || 0 == imgBytes.length)
			throw new NullPointerException("the argument 'imgBytes' must not be null or empty");
		// 将字节数组转为InputStream，再转为MemoryCacheImageInputStream
		ImageInputStream imageInputstream = new MemoryCacheImageInputStream(new ByteArrayInputStream(imgBytes));
		// 获取所有能识别数据流格式的ImageReader对象
		Iterator<ImageReader> it = ImageIO.getImageReaders(imageInputstream);
		// 迭代器遍历尝试用ImageReader对象进行解码
		while (it.hasNext()) {
			ImageReader imageReader = it.next();
			// 设置解码器的输入流
			imageReader.setInput(imageInputstream, true, true);
			// 图像文件格式后缀
			String suffix = imageReader.getFormatName().trim().toLowerCase();
			// 图像宽度
			int width = imageReader.getWidth(0);
			// 图像高度
			int height = imageReader.getHeight(0);
			// System.out.printf("format %s,%dx%d\n", suffix, width, height);
			try {
				// 解码成功返回BufferedImage对象
				// 0即为对第0张图像解码(gif格式会有多张图像),前面获取宽度高度的方法中的参数0也是同样的意思
				return imageReader.read(0, imageReader.getDefaultReadParam());
			} catch (Exception e) {
				// 如果解码失败尝试用下一个ImageReader解码
			} finally {
				imageReader.dispose();
			}
		}
		imageInputstream.close();
		// 没有能识别此数据的图像ImageReader对象，抛出异常
		throw new IOException("unsupported image format");
	}

	/**
	 * 得到用户自定义截图默认文件夹路径
	 * 
	 * @return
	 */
	public File getDefalutCustomerScreenShotPath() {
		return new File(screenfolder.getParent().replace("\\", "/") + "/" + Cconfig.CUSTOMER_FOLDER);
	}
}
