package com.PicInspect;

import java.io.IOException;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class PicInspectMainFXUI extends Stage {
	Logger logger = LoggerFactory.getLogger(PicInspectMainFXUI.class);

	public PicInspectMainFXUI() {
		// TODO Auto-generated constructor stub
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/PicInspect/PicInspectMain.fxml"));
		try {
			Parent root = fxmlLoader.load();
			PicInspectMainController controller = (PicInspectMainController) fxmlLoader.getController();

			// setAlwaysOnTop(true);
			setResizable(false);
			setTitle("图片查看器");
			setScene(new Scene(root));
			setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					// TODO Auto-generated method stub
					logger.info("picinspect stage closed");
					controller.cancelSlideTimer();
					for (Entry<Integer, InspectFXUI> entry : controller.getStageMap().entrySet()) {
						entry.getValue().closeWindows();
					}
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception", e);
		}
	}
}
