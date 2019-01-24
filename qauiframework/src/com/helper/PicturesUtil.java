package com.helper;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PicturesUtil {
	static Logger logger = LoggerFactory.getLogger(PicturesUtil.class);

	/**
	 * 比较两张图片是否一致 jpg/jpeg/png/bmp
	 * 
	 * @param fileA
	 * @param fileB
	 * @return boolean
	 */
	public static boolean compare(File fileA, File fileB) {
		if (!fileA.exists() || !fileB.exists())
			return false;
		if (contrast(fileA, fileB) >= 0.9999999D)
			return true;
		return false;
	}

	/**
	 * 比较两张图片是否一致 jpg/jpeg/png/bmp
	 * 
	 * @param fileA
	 * @param fileB
	 * @param x
	 *            开始比较的x坐标
	 * @param y
	 *            开始比较的y坐标
	 * @param width
	 *            需要比较的宽
	 * @param height
	 *            需要比较的高
	 * @return boolean
	 */
	public static boolean compare(File fileA, File fileB, int x, int y, int width, int height) {
		if (!fileA.exists() || !fileB.exists())
			return false;
		if (contrast(fileA, fileB, x, y, width, height) >= 0.9999999D)
			return true;
		return false;
	}

	/**
	 * 比较图片
	 * 
	 * @param fileA
	 * @param fileB
	 * @return [0.9999999753632214,1]大约在此范围相同图片
	 */
	protected static double contrast(File fileA, File fileB) {
		try {
			BufferedImage bImageA = ImageIO.read(fileA);
			BufferedImage bImageB = ImageIO.read(fileB);
			ImageComparer imageCom = new ImageComparer(bImageA, bImageB);// 比较算法
																			// //http://blog.csdn.net/jia20003/article/details/7771651
																			// 来源
			double similarity = imageCom.modelMatch();
			logger.info("FileA=" + fileA.getName() + ",FileB=" + fileB.getName() + ",similarity=" + similarity);
			return imageCom.modelMatch();// 返回相似度
		} catch (IOException e) {
			logger.error("Exception", e);
		}
		return 0;
	}

	/**
	 * 比较图片指定区域
	 * 
	 * @param fileA
	 * @param fileB
	 * @param x
	 *            开始比较的x坐标
	 * @param y
	 *            开始比较的y坐标
	 * @param width
	 *            需要比较的宽
	 * @param height
	 *            需要比较的高
	 * @return [0.9999999753632214,1]大约在此范围相同图片
	 */
	protected static double contrast(File fileA, File fileB, int x, int y, int width, int height) {
		try {
			String lastdirA = fileA.getName().substring(fileA.getName().lastIndexOf(".") + 1, fileA.getName().length());
			String lastdirB = fileB.getName().substring(fileB.getName().lastIndexOf(".") + 1, fileB.getName().length());
			if (!lastdirA.equalsIgnoreCase(lastdirB))
				return 0;
			BufferedImage bImageA = cut(lastdirA, fileA, x, y, width, height);
			BufferedImage bImageB = cut(lastdirB, fileB, x, y, width, height);
			ImageComparer imageCom = new ImageComparer(bImageA, bImageB);// 比较算法
			double similarity = imageCom.modelMatch();
			logger.info("FileA=" + fileA.getName() + ",FileB=" + fileB.getName() + ",similarity=" + similarity);
			return imageCom.modelMatch();// 返回相似度
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return 0;
	}

	/**
	 * 裁剪图片
	 * 
	 * @param srcpath
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param savepath
	 * @return
	 */
	public static boolean cutPicture(String srcpath, int x, int y, int width, int height, String savepath) {
		File srcfile = new File(srcpath);
		File savefile = new File(savepath);
		if (!srcfile.exists() || !srcfile.isFile()) {
			return false;
		}
		String suffix = srcfile.getName().substring(srcfile.getName().lastIndexOf(".") + 1, srcfile.getName().length());
		BufferedImage bufferedImage = PicturesUtil.cut(suffix, srcfile, x, y, width, height);
		try {
			ImageIO.write(bufferedImage, "PNG", savefile);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("EXCEPTION", e);
		}
		return false;
	}

	/**
	 * 裁剪指定图片
	 * 
	 * @param lastdir
	 * @param srcfile
	 * @param x
	 *            开始剪切的x坐标
	 * @param y
	 *            开始剪切的y坐标
	 * @param width
	 *            需要剪切的宽
	 * @param height
	 *            需要剪切的高
	 * @return
	 */
	public static BufferedImage cut(String lastdir, File srcfile, int x, int y, int width, int height) {
		FileInputStream is = null;
		ImageInputStream iis = null;
		try {
			// 读取图片文件
			is = new FileInputStream(srcfile);
			/*
			 * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader 声称能够解码指定格式。 参数：formatName -
			 * 包含非正式格式名称 .（例如 "jpeg" 或 "tiff"）等 。
			 */
			Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(lastdir);
			ImageReader reader = it.next();
			// 获取图片流
			iis = ImageIO.createImageInputStream(is);
			/*
			 * <p>iis:读取源.true:只向前搜索 </p>.将它标记为 ‘只向前搜索’。 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader
			 * 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
			 */
			reader.setInput(iis, true);

			/*
			 * <p>描述如何对流进行解码的类<p>.用于指定如何在输入时从 Java Image I/O
			 * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件 将从其 ImageReader 实现的 getDefaultReadParam
			 * 方法中返回 ImageReadParam 的实例。
			 */
			ImageReadParam param = reader.getDefaultReadParam();
			/*
			 * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象 的左上顶点的坐标（x，y）、宽度和高度可以定义这个区域。
			 */
			Rectangle rect = new Rectangle(x, y, width, height);
			// 提供一个 BufferedImage，将其用作解码像素数据的目标。
			param.setSourceRegion(rect);
			/*
			 * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将 它作为一个完整的 BufferedImage 返回。
			 */
			BufferedImage bi = reader.read(0, param);
			// ImageIO.write(bi, "PNG",new File(srcfile.getAbsolutePath()+".png"));
			return bi;
		} catch (IOException e) {
			// TODO: handle exception
			logger.error("EXCEPTION", e);
		} finally {
			try {
				if (is != null)
					is.close();
				if (iis != null)
					iis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("EXCEPTION", e);
			}
		}
		return null;
	}

	/**
	 * 裁剪BufferedImage
	 * 
	 * @param lastdir
	 * @param image
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	protected static BufferedImage cut(String lastdir, BufferedImage image, int x, int y, int width, int height) {
		ImageInputStream iis = null;
		ByteArrayOutputStream out = null;
		try {
			/*
			 * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader 声称能够解码指定格式。 参数：formatName -
			 * 包含非正式格式名称 .（例如 "jpeg" 或 "tiff"）等 。
			 */
			Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(lastdir);
			/** 因为是内存中的图片对象，所以没有后缀，就给一个jpg后缀，我给png后缀出错，不知是不是我的BufferedImage对象不对 */
			ImageReader reader = it.next();
			// 获取图片流
			out = new ByteArrayOutputStream();
			boolean flag = ImageIO.write(image, lastdir, out);
			if (!flag)
				return null;
			byte[] b = out.toByteArray();
			iis = ImageIO.createImageInputStream(new ByteArrayInputStream(b));
			/*
			 * <p>iis:读取源.true:只向前搜索 </p>.将它标记为 ‘只向前搜索’。 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader
			 * 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
			 */
			reader.setInput(iis, true);
			/*
			 * <p>描述如何对流进行解码的类<p>.用于指定如何在输入时从 Java Image I/O
			 * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件 将从其 ImageReader 实现的 getDefaultReadParam
			 * 方法中返回 ImageReadParam 的实例。
			 */
			ImageReadParam param = reader.getDefaultReadParam();
			/*
			 * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象 的左上顶点的坐标（x，y）、宽度和高度可以定义这个区域。
			 */
			Rectangle rect = new Rectangle(x, y, width, height);
			// 提供一个 BufferedImage，将其用作解码像素数据的目标。
			param.setSourceRegion(rect);
			/*
			 * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将 它作为一个完整的 BufferedImage 返回。
			 */
			BufferedImage bi = reader.read(0, param);
			return bi;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("EXCEPTION", e);
		} finally {
			try {
				if (iis != null)
					iis.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("EXCEPTION", e);
			}

		}
		return null;
	}
}
