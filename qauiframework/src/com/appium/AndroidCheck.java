package com.appium;

import java.awt.Color;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helper.AndroidInfo;
import com.log.SceneLogUtil;
import com.review.getscreen.AndroidShot;

import io.appium.java_client.android.AndroidDriver;

public class AndroidCheck extends BaseCheck {
	Logger logger = LoggerFactory.getLogger(AndroidCheck.class);
	AndroidDriver<WebElement> driver;
	AndroidShot Shot;

	public AndroidCheck(Object driver, Object baseShot, SceneLogUtil oplog, Translation translation) {
		super(oplog, translation);
		this.driver = (AndroidDriver<WebElement>) driver;
		this.Shot = (AndroidShot) baseShot;
	}

	@Override
	public String getPagesource() {
		// System.out.println(driver.getPageSource());
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		}
		return AndroidInfo.waitForNewWindow(driver, oplog);
	}

	/**
	 * 检查指定toast是否出现
	 * 
	 * @param toast
	 *            toast内容
	 * @param seconds
	 *            等待时间
	 * @return
	 */
	public boolean existToast(String toast, int seconds) {
		try {
			WebElement webElement = new AndroidDriverWait(driver, seconds)
					.until(new AndroidExpectedCondition<WebElement>() {
						@Override
						public WebElement apply(AndroidDriver driver) {
							// TODO Auto-generated method stub
							return driver.findElement(By.xpath(".//*[contains(@text,'" + toast + "')]"));
						}
					});
			if (webElement != null) {
				oplog.logCheck("Toast: " + toast + " 存在");
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		oplog.logWarn("Toast: " + toast + " 不存在");
		return false;
	}

	@Override
	public boolean notExist(String... strs) {
		StringBuffer strtrue = new StringBuffer("检查:<<");
		StringBuffer strfalse = new StringBuffer("检查:<<");
		boolean exist = true;
		String pagesource = getPagesource();
		if (strs.length > 0) {
			for (String str : strs) {
				if (pagesource.contains(str.trim())) {
					strtrue.append(translation.getName(str) + ",");
					exist = false;
				} else {
					strfalse.append(translation.getName(str) + ",");
				}
			}
		}
		strtrue.append(">>存在;");
		strfalse.append(">>不存在;");
		if (exist) {
			oplog.logCheck(strfalse.toString());
			// Shot.drawRect("检查通过", 0, 0, Shot.getDevice_width(),
			// Shot.getDevice_hight(),Color.GREEN);
		} else {
			oplog.logCheck(strfalse.toString());
			oplog.logWarn(strtrue.toString());
			Shot.drawRect("检查不通过", 0, 0, Shot.getDevice_width(), Shot.getDevice_hight(), Color.RED);
		}
		return exist;
	}

	@Override
	public boolean notExistByRegex(String... regexs) {
		StringBuffer strtrue = new StringBuffer("正则检查:<<");
		StringBuffer strfalse = new StringBuffer("正则检查:<<");
		boolean exist = true;
		String pagesource = getPagesource();
		Matcher m;
		if (regexs.length > 0) {
			for (String regex : regexs) {
				try {
					m = Pattern.compile(regex).matcher(pagesource);
					if (m.find()) {
						strtrue.append(translation.getName(regex) + ",");
						exist = false;
					} else {
						strfalse.append(translation.getName(regex) + ",");
					}
				} catch (Exception e) {
					// TODO: handle exception
					logger.warn("Excepiton", e);
					oplog.logWarn("<<" + regex + ">>正则表达式错误!");
				}
			}
		}
		strtrue.append(">>存在;");
		strfalse.append(">>不存在;");
		if (exist) {
			oplog.logCheck(strfalse.toString());
			// Shot.drawRect("检查通过", 0, 0, Shot.getDevice_width(),
			// Shot.getDevice_hight(),Color.GREEN);
		} else {
			oplog.logCheck(strfalse.toString());
			oplog.logWarn(strtrue.toString());
			Shot.drawRect("检查不通过", 0, 0, Shot.getDevice_width(), Shot.getDevice_hight(), Color.RED);
		}
		m = null;
		return exist;
	}

	@Override
	public boolean exist(String... strs) {
		StringBuffer strtrue = new StringBuffer("检查:<<");
		StringBuffer strfalse = new StringBuffer("检查:<<");
		boolean exist = true;
		String pagesource = getPagesource();
		if (strs.length > 0) {
			for (String str : strs) {
				if (str.startsWith("#"))
					str = str.substring(1);// 以#开头则以accessibilityid查找
				if (pagesource.contains(str.trim())) {
					strtrue.append(translation.getName(str) + ",");
				} else {
					strfalse.append(translation.getName(str) + ",");
					exist = false;
				}
			}
		}
		strtrue.append(">>存在;");
		strfalse.append(">>不存在;");
		if (exist) {
			oplog.logCheck(strtrue.toString());
			// Shot.drawRect("检查通过", 0, 0, Shot.getDevice_width(),
			// Shot.getDevice_hight(),Color.GREEN);
		} else {
			oplog.logCheck(strtrue.toString());
			oplog.logWarn(strfalse.toString());
			Shot.drawRect("检查不通过", 0, 0, Shot.getDevice_width(), Shot.getDevice_hight(), Color.RED);
		}
		return exist;
	}

	@Override
	public boolean existByRegex(String... regexs) {
		StringBuffer strtrue = new StringBuffer("正则检查:<<");
		StringBuffer strfalse = new StringBuffer("正则检查:<<");
		boolean exist = true;
		String pagesource = getPagesource();
		Matcher m;
		if (regexs.length > 0) {
			for (String regex : regexs) {
				try {
					m = Pattern.compile(regex).matcher(pagesource);
					if (m.find()) {
						strtrue.append(translation.getName(regex) + ",");
					} else {
						strfalse.append(translation.getName(regex) + ",");
						exist = false;
					}
				} catch (Exception e) {
					// TODO: handle exception
					logger.warn("Excepiton", e);
					oplog.logWarn("<<" + regex + ">>正则表达式错误!");
				}
			}
		}
		strtrue.append(">>存在;");
		strfalse.append(">>不存在;");
		if (exist) {
			oplog.logCheck(strtrue.toString());
			// Shot.drawRect("检查通过", 0, 0, Shot.getDevice_width(),
			// Shot.getDevice_hight(),Color.GREEN);
		} else {
			oplog.logCheck(strtrue.toString());
			oplog.logWarn(strfalse.toString());
			Shot.drawRect("检查不通过", 0, 0, Shot.getDevice_width(), Shot.getDevice_hight(), Color.RED);
		}
		m = null;
		return exist;
	}

	@Override
	public int getCount(String uniquestr) {
		uniquestr = uniquestr.trim();
		String pagesource = getPagesource();
		int index = 0;
		int count = 0;
		int end = 0;
		while ((index = pagesource.indexOf(uniquestr, end)) != -1) {
			end = index + uniquestr.length();
			count++;
		}
		// Shot.drawText("检查", "<<"+translation.getName(uniquestr)+">>共发现"+count+"处");
		oplog.logCheck("检查:<<" + translation.getName(uniquestr) + ">>共发现" + count + "处");
		return count;
	}

	@Override
	public int getCountByRegex(String regex) {
		int count = 0;
		String pagesource = getPagesource();
		try {
			Matcher m = Pattern.compile(regex).matcher(pagesource);
			while (m.find()) {
				System.out.println(m.group());
				count++;
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn("Excepiton", e);
			oplog.logWarn("<<" + regex + ">>正则表达式错误!");
		}
		// Shot.drawText("检查", "<<"+translation.getName(regex)+">>共发现"+count+"处");
		oplog.logCheck("正则检查:<<" + translation.getName(regex) + ">>共发现" + count + "处");
		return count;
	}

	@Override
	public ArrayList<String> getList(String uniquestr) {
		uniquestr = uniquestr.trim();
		ArrayList<String> list = new ArrayList<String>();
		String pagesource = getPagesource();
		int index = 0;
		int end = 0;
		int start = 0;
		while ((index = pagesource.indexOf(uniquestr, end)) != -1) {
			start = pagesource.lastIndexOf("<", index);
			end = pagesource.indexOf(">", index);
			list.add(pagesource.substring(start, end + 1));
		}
		return list;
	}
}
