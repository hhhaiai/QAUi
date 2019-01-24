package com.xq;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appium.Translation;
import com.bean.XQAndroidElementBean;
import com.constant.CAndroidCMD;
import com.constant.Coperation;
import com.helper.ADBUtil;
import com.helper.HelperUtil;
import com.log.SceneLogUtil;
import com.review.getscreen.AndroidShot;

public class XQAndroidDriver {
	Logger logger = LoggerFactory.getLogger(XQAndroidDriver.class);
	String udid;
	AndroidShot Shot;
	SceneLogUtil oplog;
	Translation translation;
	File reportFolder;

	public XQAndroidDriver(String udid, File reportFolder, SceneLogUtil oplog, AndroidShot Shot,
			Translation translation) {
		// TODO Auto-generated constructor stub
		this.udid = udid;
		this.reportFolder = reportFolder;
		this.oplog = oplog;
		this.Shot = Shot;
		this.translation = translation;
	}

	public Translation getTranslation() {
		return translation;
	}

	public AndroidShot getShot() {
		return Shot;
	}

	public SceneLogUtil getOplog() {
		return oplog;
	}

	public File getReportFloder() {
		return reportFolder;
	}

	/**
	 * 得到udid
	 */
	public String getUdid() {
		return udid;
	}

	/**
	 * 通过uiautomator dump获取页面资源,(与Appium无法同时使用)
	 * 
	 * @return
	 */
	public String getPageByADB() {
		String result = ADBUtil.execcmd(udid, CAndroidCMD.DUMP_PAGE).toString();
		if (result.contains("UI hierchary dumped to")) {
			return ADBUtil.execcmd(udid, CAndroidCMD.CAT_DUMP_PAGE).toString();
		} else {
			oplog.logWarn("window_dump.xml获取失败:" + result);
			return "";
		}
	}

	/**
	 * 查找元素核心
	 * 
	 * @param element
	 * @param text    自动区分xpath/text/id/class及content-desc需要加上#
	 * @param regex   null为不使用正则表达式匹配的text
	 * @return
	 */
	public List<XQAndroidElement> findElementBy(Element element, String text, String regex) {
		List<XQAndroidElement> list;
		if (text != null && text.startsWith("#")) {
			list = analysisElement(element, Coperation.ELEMENT_CONTENT_DESC, text.substring(1), regex);
		} else if (text != null && text.contains(":id/")) {
			list = analysisElement(element, Coperation.ELEMENT_RESOURCE_ID, text.trim(), regex);
		} else if (text != null && text.startsWith("android.") && !HelperUtil.hasChinese(text)) {
			list = analysisElement(element, Coperation.ELEMENT_CLASS, text.trim(), regex);
		} else if (text != null && text.replace("(", "").startsWith("/")) {
			list = analysisElement(element, Coperation.XPATH, text.trim(), regex);
		} else {
			list = analysisElement(element, Coperation.ELEMENT_TEXT, text.trim(), regex);
		}
		return list;
	}

	/**
	 * 得到根元素
	 * 
	 * @param xml
	 * @return
	 */
	public Element getRootByPage(String xml) {
		xml = xml.replace("&", "&amp;");// 转义,避免出错
		Element element = null;
		InputStream in = new ByteArrayInputStream(xml.getBytes());
		try {
			Document doc = new SAXReader().read(in);
			return doc.getRootElement();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
			oplog.logError("分析页面错误,无法识别的XML:" + xml);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("Exception", e);
				}
			}
		}
		return element;
	}

	/**
	 * 根据属性名和值在Element中查找元素
	 * 
	 * @param element
	 * @param type
	 * @param value
	 * @return
	 */
	private List<XQAndroidElement> analysisElement(Element element, String type, String value, String regex) {
		List<XQAndroidElement> list = new ArrayList<>();
		List<Element> elementsList = new ArrayList<>();
		elementsList = seekElements(element, type, value, regex, elementsList);
		for (Element ele : elementsList) {
			XQAndroidElement xqAndroidElement = new XQAndroidElement(this, parseXQBean(ele, value));
			list.add(xqAndroidElement);
		}
		return list;
	}

	/**
	 * 在Element中根据属性名和值查找元素
	 * 
	 * @param node
	 * @param type
	 * @param value
	 * @param regex
	 * @param list
	 */
	private List<Element> seekElements(Element ele, String type, String value, String regex, List<Element> list) {
		if (ele == null) {
			return list;
		}
		if (type.equals(Coperation.XPATH)) {
			List<Node> nodelist = ele.selectNodes(value);
			for (Node node : nodelist) {
				if (node instanceof Element) {
					Element element = (Element) node;
					if (regex != null) {
						Attribute text = element.attribute(Coperation.ELEMENT_TEXT);
						if (text != null && text.getValue().matches(regex)) {
							list.add(element);
						}
					} else {
						list.add(element);
					}
				}
			}
		} else {
			for (Iterator<Element> iter = ele.elementIterator(); iter.hasNext();) {
				Element element = iter.next();
				Attribute name = element.attribute(type);
				if (name != null) {
					if (name.getValue() != null && value.equals(name.getValue())) {
						if (regex != null) {
							Attribute text = element.attribute(Coperation.ELEMENT_TEXT);
							if (text != null && text.getValue().matches(regex)) {
								list.add(element);
							}
						} else {
							list.add(element);
						}
					}
					list = seekElements(element, type, value, regex, list);
				}
			}
		}
		return list;
	}

	/**
	 * 将Element转化为bean
	 * 
	 * @param element
	 * @return
	 */
	private XQAndroidElementBean parseXQBean(Element element, String value) {
		XQAndroidElementBean bean = new XQAndroidElementBean();
		bean.setElement(element);
		bean.setNickname(translation != null ? translation.getName(value) : value);
		if (element != null) {
			for (Attribute att : element.attributes()) {
				String val = att.getValue() == null ? "" : att.getValue();
				switch (att.getName()) {
				case Coperation.ELEMENT_BOUNDS:
					Matcher matcher = Pattern.compile("\\[(\\d+),(\\d+)\\]\\[(\\d+),(\\d+)\\]").matcher(val);
					if (matcher.find()) {
						int point_x = Integer.parseInt(matcher.group(1));
						int point_y = Integer.parseInt(matcher.group(2));
						int width = Integer.parseInt(matcher.group(3)) - point_x;
						int hight = Integer.parseInt(matcher.group(4)) - point_y;
						int x = point_x + width / 2;
						int y = point_y + hight / 2;
						// 范围外不会报错
						// if (x > Shot.getDevice_width()) {
						// x = Shot.getDevice_width();
						// } else if (x < 0) {
						// x = 0;
						// }
						// if (y > Shot.getDevice_hight()) {
						// y = Shot.getDevice_hight();
						// } else if (y < 0) {
						// y = 0;
						// }
						bean.setBounds_point_x(point_x);
						bean.setBounds_point_y(point_y);
						bean.setBounds_width(width);
						bean.setBounds_hight(hight);
						bean.setBounds_x(x);
						bean.setBounds_y(y);
					}
					break;
				case Coperation.ELEMENT_CHECKABLE:
					bean.setCheckable(val.equals("true") ? true : false);
					break;
				case Coperation.ELEMENT_CHECKED:
					bean.setChecked(val.equals("true") ? true : false);
					break;
				case Coperation.ELEMENT_CLASS:
					bean.setClassname(val);
					break;
				case Coperation.ELEMENT_CLICKABLE:
					bean.setClickable(val.equals("true") ? true : false);
					break;
				case Coperation.ELEMENT_CONTENT_DESC:
					bean.setContent_desc(val);
					break;
				case Coperation.ELEMENT_ENABLED:
					bean.setEnabled(val.equals("true") ? true : false);
					break;
				case Coperation.ELEMENT_FOCUSABLE:
					bean.setFocusable(val.equals("true") ? true : false);
					break;
				case Coperation.ELEMENT_FOCUSED:
					bean.setFocused(val.equals("true") ? true : false);
					break;
				case Coperation.ELEMENT_INDEX:
					bean.setIndex(Integer.parseInt(val));
				case Coperation.ELEMENT_INSTANCE:
					bean.setInstance(Integer.parseInt(val));
				case Coperation.ELEMENT_LONG_CLICKABLE:
					bean.setLong_clickable(val.equals("true") ? true : false);
				case Coperation.ELEMENT_NAF:
					bean.setNAF(val.equals("true") ? true : false);
				case Coperation.ELEMENT_PACKAGE:
					bean.setPackagename(val);
				case Coperation.ELEMENT_PASSWORD:
					bean.setPassword(val.equals("true") ? true : false);
				case Coperation.ELEMENT_RESOURCE_ID:
					bean.setResource_id(val);
				case Coperation.ELEMENT_SCROLLABLE:
					bean.setScrollable(val.equals("true") ? true : false);
				case Coperation.ELEMENT_SELECTED:
					bean.setSelected(val.equals("true") ? true : false);
				case Coperation.ELEMENT_TEXT:
					bean.setText(val);
					break;

				default:
					break;
				}
			}
		}
		return bean;
	}
}
