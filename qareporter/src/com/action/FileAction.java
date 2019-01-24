package com.action;

import java.io.File;
import java.io.FileFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import javax.swing.filechooser.FileSystemView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bean.NoteBean;
import com.helper.HelperUtil;

public class FileAction {
	Logger logger = LoggerFactory.getLogger(FileAction.class);

	String reportpath = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + "/QAUiReport";

	public void setReportPath(String path) {
		this.reportpath = path;
	}

	public String getReportPath() {
		return reportpath;
	}

	/**
	 * 修改标题
	 * 
	 * @param jsonstr
	 * @return
	 */
	public JSONObject changeTitle(String jsonstr) {
		JSONObject jsonresult = null;
		try {
			jsonresult = new JSONObject("{\"success\":\"false\",\"value\":\"修改标题失败\"}");// 定义返回初始值
			JSONObject jsonObject = new JSONObject(jsonstr);// 转化请求为json
			if (jsonObject.getString("type").equals("QAUiReport")) {
				File file = new File(reportpath + "/" + jsonObject.getString("subfoldername") + "/note.xml");
				String title = jsonObject.getString("title");
				if (file.exists() && file.isFile()) {
					logger.info("change title:" + file.getAbsolutePath() + ",title=" + title);
					FileXmlParse fileXmlParse = new FileXmlParse(file);
					fileXmlParse.changeStringByXPath("//outline/title", 0, title);
					jsonresult = new JSONObject("{\"success\":\"true\",\"value\":\"修改标题成功\"}");
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}
		return jsonresult;
	}

	/**
	 * 删除目录
	 * 
	 * @param jsonstr
	 * @return
	 */
	public JSONObject delCata(String jsonstr) {
		JSONObject jsonresult = null;
		try {
			jsonresult = new JSONObject("{\"success\":\"false\",\"value\":\"删除失败\"}");// 定义返回初始值
			JSONObject jsonObject = new JSONObject(jsonstr);// 转化请求为json
			if (jsonObject.getString("type").equals("QAUiReport")) {
				File file = new File(reportpath + "/" + jsonObject.getString("subfoldername"));
				if (file.exists() && file.isDirectory() && file.getName().matches("^[\\d_]+-\\w+$")) {
					logger.info("del:" + file.getAbsolutePath());
					HelperUtil.delFolder(file.getAbsolutePath());
				}
				if (!file.exists()) {
					jsonresult = new JSONObject("{\"success\":\"true\",\"value\":\"删除成功\"}");
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}
		return jsonresult;
	}

	/**
	 * 获取目录信息
	 * 
	 * @param jsonstr
	 * @return
	 */
	public JSONObject getCataInfo(String jsonstr) {
		JSONObject jsonresult = null;
		try {
			jsonresult = new JSONObject("{\"success\":\"false\",\"value\":\"查询失败\"}");// 定义返回初始值
			JSONObject jsonObject = new JSONObject(jsonstr);// 转化请求为json
			if (jsonObject.getString("type").equals("QAUiReport")) {
				File catalogfile;
				if (jsonObject.getString("foldername").toLowerCase().equals("android")) {
					catalogfile = new File(reportpath + "/Android");
				} else {
					catalogfile = new File(reportpath + "/iOS");
				}
				if (catalogfile.exists() && catalogfile.isDirectory()) {
					File[] files = catalogfile.listFiles(new FileFilter() {

						@Override
						public boolean accept(File f) {
							// TODO Auto-generated method stub
							return f.isDirectory() && f.getName().matches("^[\\d_]+-\\w+$");
						}
					});
					// 排序
					files = sortFileByCreateTime(files);
					StringBuffer Buf = new StringBuffer();
					for (File file : files) {
						String string = getReportInfo(file);
						if (!string.equals(""))
							Buf.append(string + ",");
					}
					// logger.info("JSON:"+"{\"success\":\"true\",\"value\":["+Buf.toString()+"]}");
					jsonresult = new JSONObject("{\"success\":\"true\",\"value\":[" + Buf.toString() + "]}");
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}

		return jsonresult;
	}

	/**
	 * 获取报告信息
	 * 
	 * @param file
	 * @param force 强制更新一次txt
	 * @return
	 */
	public String getReportInfo(File file) {
		File note = new File(file.getAbsolutePath() + "/note.xml");
		// if (note.exists() && note.isFile()) {
		try {
			FileXmlParse fileXmlParse = new FileXmlParse(note);
			NoteBean noteBean = fileXmlParse.getNoteBean();
			// if (noteBean.getDone().equals("false"))
			// return "";
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("done", noteBean.getDone());
			jsonObject.put("folder", noteBean.getFolder().equals("") ? file.getName() : noteBean.getFolder());
			jsonObject.put("title", noteBean.getTitle());
			jsonObject.put("summary", noteBean.getSummary());
			jsonObject.put("createtime", noteBean.getCreatetime());
			JSONArray jsonArray_scene = new JSONArray();
			for (Map<String, String> map : noteBean.getItems_scene()) {
				jsonArray_scene.put(new JSONObject(map));
			}
			jsonObject.put("items_scene", jsonArray_scene);
			JSONArray jsonArray_monkey_android_sys = new JSONArray();
			for (Map<String, String> map : noteBean.getItems_monkey_android_sys()) {
				jsonArray_monkey_android_sys.put(new JSONObject(map));
			}
			jsonObject.put("items_monkey_android_sys", jsonArray_monkey_android_sys);
			return jsonObject.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.error("EXCEPTION", e);
		}
		// }
		return "";
	}

	/**
	 * 按照创建时间排序,从大到小
	 * 
	 * @param files
	 * @return
	 */
	private File[] sortFileByCreateTime(File[] files) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MMdd_HHmm_ss");
		Arrays.sort(files, new Comparator<File>() {

			@Override
			public int compare(File a, File b) {
				// TODO Auto-generated method stub
				try {
					long timea = simpleDateFormat.parse(a.getName().substring(0, a.getName().lastIndexOf("-")))
							.getTime();
					long timeb = simpleDateFormat.parse(b.getName().substring(0, b.getName().lastIndexOf("-")))
							.getTime();
					long diff = timea - timeb;
					if (diff > 0) {
						return -1;
					} else if (diff == 0) {
						return 0;
					} else {
						return 1;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					logger.error("Exception", e);
				}
				return 0;
			}

		});
		return files;
	}

	/**
	 * 按照序号排序,从小到大
	 * 
	 * @param files
	 * @return
	 */
	private File[] sortFileByNo(File[] files) {
		Arrays.sort(files, new Comparator<File>() {

			@Override
			public int compare(File a, File b) {
				// TODO Auto-generated method stub
				int noa = Integer.parseInt(a.getName().substring(0, a.getName().indexOf("-")));
				int nob = Integer.parseInt(b.getName().substring(0, b.getName().indexOf("-")));
				long diff = noa - nob;
				if (diff > 0) {
					return 1;
				} else if (diff == 0) {
					return 0;
				} else {
					return -1;
				}
			}

		});
		return files;
	}
}
