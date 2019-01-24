package com.Monkey;

public class PackageInfo {

	// 转换已知应用名
	public String getKnowName(String packagename) {
		return "<html><font color=\"#FF0000\">" + packagename.split(",")[0] + "</font>=" + packagename.split(",")[1]
				+ "</html>";
	}

	// 过滤包名
	public String FilterPackages(String packagename) {
		String[] strings = com.Viewer.MainRun.paramsBean.getMonkey_filterPackages().split(";");
		for (String str : strings) {
			if (str.contains(",")) {
				if (packagename.equals(str.split(",")[1])) {
					return str;
				}
			}
		}
		return null;
	}

}
