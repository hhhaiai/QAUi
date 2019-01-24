package com.More;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Util.CMDUtil;

public class AutoEnv {
	Logger logger = LoggerFactory.getLogger(AutoEnv.class);

	public AutoEnv() {
		// TODO Auto-generated constructor stub
	}

	private void getMac_Bash_profile() {
		String[] results = CMDUtil.execcmd("cat ~/.bash_profile", CMDUtil.SYSCMD, true);
		if (!results[0].trim().equals("")) {

		}
	}
}
