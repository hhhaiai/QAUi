package com.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class XmlParse {
	Logger logger = LoggerFactory.getLogger(XmlParse.class);
	private String configpath;
	private Document doc;

	/**
	 * 获取Document
	 * 
	 * @return
	 */
	public Document getDoc() {
		return doc;
	}

	/**
	 * 获取当前DOC路径
	 */
	public String getConfigpath() {
		return configpath;
	}

	/**
	 * 设置Document
	 */
	public boolean setDoc(String configpath) {
		// TODO Auto-generated method stub
		this.configpath = configpath;
		if (configpath != null && new File(configpath).exists()) {
			try {
				SAXReader reader = new SAXReader();
				doc = reader.read(configpath);
				logger.info("Start to read " + configpath);
				return true;
			} catch (DocumentException e) {
				logger.error("Exception", e);
			}
		} else {
			logger.error("doc file does not exist:" + configpath);
		}
		return false;
	}

	/**
	 * 保存修改,写入文件
	 * 
	 * @param doc
	 * @return
	 */
	public boolean writeDoc() {
		XMLWriter writer = null;
		try {
			// writer = new XMLWriter(new FileWriter(new File(configpath)));//默认windows遍历GBK
			writer = new XMLWriter(new FileOutputStream(new File(configpath)));
			writer.write(doc);
		} catch (Exception e) {
			logger.error("Exception", e);
			return false;
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Exception", e);
			}
		}
		return true;
	}

	/**
	 * 在根目录添加新元素
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean addRootElement(String key, String value) {
		if (doc == null)
			return false;
		// 创建一个新的元素对象
		Element ele = DocumentHelper.createElement(key);
		// 设置文本
		ele.setText(value);
		doc.getRootElement().add(ele);
		return writeDoc();
	}

	/**
	 * 向XPATH节点写入一对键值
	 * 
	 * @param XPath
	 * @param index
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean addElementByString(String XPath, int index, String key, String value) {
		if (doc == null)
			return false;
		List<Node> listnode = doc.selectNodes(XPath);
		if (null != listnode) {
			if (index != -1 && listnode.size() > index) {
				Node node = listnode.get(index);
				if (node instanceof Element) {
					Element indexele = (Element) node;
					Element ele = DocumentHelper.createElement(key);
					ele.setText(value);
					indexele.add(ele);
				}
			} else if (index == -1) {
				for (Node node : listnode) {
					if (node instanceof Element) {
						Element indexele = (Element) node;
						Element ele = DocumentHelper.createElement(key);
						ele.setText(value);
						indexele.add(ele);
					}
				}
			}
		}
		return writeDoc();
	}

	/**
	 * 在xpath添加新元素
	 * 
	 * @param XPath
	 * @param index 在第几个元素中插入,-1则全部插入
	 * @param map   新元素Map(key,value)
	 * @return
	 */
	public boolean addElementByMap(String XPath, int index, Map<String, String> map) {
		if (doc == null)
			return false;
		List<Node> listnode = doc.selectNodes(XPath);
		if (null != listnode) {
			if (index != -1 && listnode.size() > index) {
				Node node = listnode.get(index);
				if (node instanceof Element) {
					Element indexele = (Element) node;
					for (Entry<String, String> entry : map.entrySet()) {
						Element ele = DocumentHelper.createElement(entry.getKey());
						ele.setText(entry.getValue());
						indexele.add(ele);
					}
				}
			} else if (index == -1) {
				for (Node node : listnode) {
					if (node instanceof Element) {
						Element indexele = (Element) node;
						for (Entry<String, String> entry : map.entrySet()) {
							Element ele = DocumentHelper.createElement(entry.getKey());
							ele.setText(entry.getValue());
							indexele.add(ele);
						}
					}
				}
			}
		}
		return writeDoc();
	}

	/**
	 * 根据Xpath得到List
	 * 
	 * @param doc
	 * @param XPath
	 * @return
	 */
	public List<String> getListStringByXpath(String XPath, int index) {
		List<String> list = new ArrayList<>();
		if (doc == null)
			return list;
		List<Node> listnode = doc.selectNodes(XPath);
		if (null != listnode) {
			if (index != -1 && listnode.size() > index) {
				Node node = listnode.get(index);
				if (node instanceof Element) {
					for (Element e : ((Element) node).elements()) {
						list.add(e.getText());
					}
				}
			} else if (index == -1) {
				for (Node node : listnode) {
					if (node instanceof Element) {
						for (Element e : ((Element) node).elements()) {
							list.add(e.getText());
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * 根据Xpath得到MAP
	 * 
	 * @param doc
	 * @param XPath
	 * @return
	 */
	public Map<String, String> getMapByXpath(String XPath, int index) {
		Map<String, String> map = new LinkedHashMap<>();
		if (doc == null)
			return map;
		List<Node> listnode = doc.selectNodes(XPath);
		if (null != listnode) {
			if (index != -1 && listnode.size() > index) {
				Node node = listnode.get(index);
				if (node instanceof Element) {
					for (Element e : ((Element) node).elements()) {
						map.put(e.getName(), e.getText().trim());
					}
				}
			} else if (index == -1) {
				for (Node node : listnode) {
					if (node instanceof Element) {
						for (Element e : ((Element) node).elements()) {
							map.put(e.getName(), e.getText().trim());
						}
					}
				}
			}
		}
		return map;
	}

	/**
	 * 根据Xpath及子项名称得到listMap
	 * 
	 * @param XPath
	 * @param itemname
	 * @return
	 */
	public List<Map<String, String>> getListMapByXpath(String XPath, int index, String itemname) {
		List<Map<String, String>> list = new ArrayList<>();
		if (doc == null)
			return list;
		List<Node> listnode = doc.selectNodes(XPath);
		if (null != listnode) {
			if (index != -1 && listnode.size() > index) {
				Node node = listnode.get(index);
				if (node instanceof Element) {
					for (Element e : ((Element) node).elements()) {
						if (!e.getName().equals(itemname))
							continue;
						Map<String, String> map = new LinkedHashMap<>();
						for (Element sube : e.elements()) {
							map.put(sube.getName(), sube.getText());
						}
						list.add(map);
					}
				}
			} else if (index == -1) {
				for (Node node : listnode) {
					if (node instanceof Element) {
						for (Element e : ((Element) node).elements()) {
							if (!e.getName().equals(itemname))
								continue;
							Map<String, String> map = new LinkedHashMap<>();
							for (Element sube : e.elements()) {
								map.put(sube.getName(), sube.getText());
							}
							list.add(map);
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * 根据Xpath及子项名称得到listlist
	 * 
	 * @param XPath
	 * @param itemname
	 * @return
	 */
	public List<List<String>> getListListByXpath(String XPath, int index, String itemname) {
		List<List<String>> list = new ArrayList<>();
		if (doc == null)
			return list;
		List<Node> listnode = doc.selectNodes(XPath);
		if (null != listnode) {
			if (index != -1 && listnode.size() > index) {
				Node node = listnode.get(index);
				if (node instanceof Element) {
					for (Element e : ((Element) node).elements()) {
						if (!e.getName().equals(itemname))
							continue;
						List<String> templist = new ArrayList<>();
						for (Element sube : e.elements()) {
							templist.add(sube.getText());
						}
						list.add(templist);
					}
				}
			} else if (index == -1) {
				for (Node node : listnode) {
					if (node instanceof Element) {
						for (Element e : ((Element) node).elements()) {
							if (!e.getName().equals(itemname))
								continue;
							List<String> templist = new ArrayList<>();
							for (Element sube : e.elements()) {
								templist.add(sube.getText());
							}
							list.add(templist);
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * 根据Xpath得到String
	 * 
	 * @param doc
	 * @param XPath
	 * @param index
	 * @return
	 */
	public String getStringByXpath(String XPath, int index) {
		String str = "";
		if (doc == null)
			return "";
		List<Node> list = doc.selectNodes(XPath);
		if (null != list) {
			if (index >= 0 && list.size() > index) {
				str = list.get(index).getText();
			}
		}
		return str;
	}

	/**
	 * 根据map及父节点名称XPath,修改子节点内容
	 * 
	 * @param XPath 父节点Xpath
	 * @param map   子节点key与value
	 * @return 如果写入失败,则返回Map大小为0
	 */
	public Map<String, String> changeMapByXPath(String XPath, int index, Map<String, String> valueMap) {
		Map<String, String> map = new HashMap<>();
		valueMap.entrySet().forEach(e -> {
			map.put(e.getKey(), e.getValue());
		});
		if (doc == null)
			return map;
		List<Node> listnode = doc.selectNodes(XPath);
		if (listnode != null) {
			if (index != -1 && listnode.size() > index) {
				Node node = listnode.get(index);
				if (node instanceof Element) {
					for (Element e : ((Element) node).elements()) {
						for (Entry<String, String> entry : valueMap.entrySet()) {
							if (entry.getKey().equals(e.getName())) {
								e.setText(entry.getValue());
							}
						}
					}
				}
			} else if (index == -1) {
				for (Node node : listnode) {
					if (node instanceof Element) {
						for (Element e : ((Element) node).elements()) {
							for (Entry<String, String> entry : valueMap.entrySet()) {
								if (entry.getKey().equals(e.getName())) {
									e.setText(entry.getValue());
								}
							}
						}
					}
				}
			}
			writeDoc();
		}
		return map;
	}

	/**
	 * 根据XPath及下标修改节点内容
	 * 
	 * @param XPath
	 * @param index
	 * @param value
	 * @return 如果写入失败,则返回""
	 */
	public String changeStringByXPath(String XPath, int index, String value) {
		if (doc == null)
			return value;
		List<Node> list = doc.selectNodes(XPath);
		if (null != list) {
			if (index >= 0 && list.size() >= index) {
				list.get(index).setText(value);
				writeDoc();
			}
		}
		return value;
	}

}