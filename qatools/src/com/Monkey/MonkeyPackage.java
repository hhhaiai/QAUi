package com.Monkey;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.CheckUE;
import com.Util.Excute;

public class MonkeyPackage {
	Logger logger = LoggerFactory.getLogger(MonkeyPackage.class);
	PackageInfo packageinfo = new PackageInfo();
	ArrayList<String> arrayApps = new ArrayList<String>();

	// get apps
	public ArrayList<String> getPMlistAPP(String udid) {
		arrayApps.clear();
		if (CheckUE.checkDevice(udid)) {
			List<String> resultlist = Excute.returnlist2(udid, "pm list package");
			// List<String> resultlist=Excute.returnlist("pm list package", 2, true);
			String temp;
			for (int i = 0; i < resultlist.size(); i++) {
				if (!resultlist.get(i).toString().equals("") && resultlist.get(i).toString().length() > 7) {
					temp = resultlist.get(i).toString().substring(8, resultlist.get(i).length());
					temp = packageinfo.FilterPackages(temp);
					if (temp != null) {
						temp = packageinfo.getKnowName(temp);
						arrayApps.add(temp);
					}
				}
			}
		}
		return arrayApps;
	}

}
