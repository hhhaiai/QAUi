package com.Performance;

import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class PerformanceFXUIAPP extends Application {
	Logger logger = LoggerFactory.getLogger(PerformanceFXUIAPP.class);

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		try {
			// Read file fxml and draw interface.
			String udid = null;
			Parameters parameters = getParameters();
			for (Entry<String, String> entry : parameters.getNamed().entrySet()) {// --xx=xxx
				if (entry.getKey().equals("device")) {
					udid = entry.getValue();
				}
			}
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/Performance/PerformanceMain.fxml"));
			Parent root = fxmlLoader.load();
			PerformanceMainController controller = (PerformanceMainController) fxmlLoader.getController();
			controller.setUdid(udid);
			primaryStage.setTitle("Android性能监控--" + udid);
			primaryStage.setScene(new Scene(root));
			primaryStage.show();

			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					// TODO Auto-generated method stub
					// event.consume();//阻止关闭窗口
					logger.info("close windows");
					controller.close();
				}
			});
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}

}
