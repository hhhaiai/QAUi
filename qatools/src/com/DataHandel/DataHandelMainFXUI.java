package com.DataHandel;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constant.Cconfig;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DataHandelMainFXUI extends Stage {
	Logger logger = LoggerFactory.getLogger(DataHandelMainFXUI.class);

	public DataHandelMainFXUI(String udid) {
		// TODO Auto-generated constructor stub
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/DataHandel/DataHandelMain.fxml"));
		try {
			Parent root = fxmlLoader.load();
			setScene(new Scene(root));
			DataHandelMainController controller = (DataHandelMainController) fxmlLoader.getController();
			String title = "";
			if (udid != null) {
				if (udid.length() > 35) {
					title = "数据处理,iOS设备=" + udid;
					controller.init(udid, Cconfig.IOS);
				} else {
					title = "数据处理,Android设备=" + udid;
					controller.init(udid, Cconfig.ANDROID);
				}
			} else {
				controller.init("", Cconfig.ANDROID);
			}
			setTitle(title);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}
	}

}
