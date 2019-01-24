package com.PicInspect;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OverlapImage {
	Logger logger = LoggerFactory.getLogger(OverlapImage.class);

	public void overlapImage(String bigPath, String smallPath, String outFile) {
		try {
			BufferedImage big = ImageIO.read(new File(bigPath));
			BufferedImage small = ImageIO.read(new File(smallPath));
			Graphics2D g = big.createGraphics();
			int x = (big.getWidth() - small.getWidth()) / 2;
			int y = (big.getHeight() - small.getHeight()) / 2;
			g.drawImage(small, x, y, small.getWidth(), small.getHeight(), null);
			g.dispose();
			ImageIO.write(big, outFile.split("\\.")[1], new File(outFile));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void start() {
		String bigPath = "/Users/auto/Desktop/QAToolsLogs/ScreenCap/图片1.png";
		File floder = new File("/Users/auto/Desktop/QAToolsLogs/ScreenCap");
		File[] pics = floder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return pathname.getName().endsWith("png");
			}
		});

		for (File pic : pics) {
			overlapImage(bigPath, pic.getAbsolutePath(), pic.getParent() + "/over/" + pic.getName());
		}
	}
}
