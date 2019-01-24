package com.Performance;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Viewer.MainRun;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class PerformanceUI extends JFrame {
	Logger logger = LoggerFactory.getLogger(PerformanceUI.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 3224760275322114205L;
	PerformanceMainController controller;

	/**
	 * Create the frame.
	 */
	public PerformanceUI(String udid) {
		setBounds(100, 100, 1200, 800);
		// setResizable(false);
		setTitle("性能监控 设备=" + udid);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainRun.mainFrame);
		setIconImage(MainRun.imagelogo);

		JFXPanel jfxPanel = new JFXPanel();
		add(jfxPanel, BorderLayout.CENTER);

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/Performance/PerformanceMain.fxml"));
				try {
					Parent root = fxmlLoader.load();
					controller = (PerformanceMainController) fxmlLoader.getController();
					controller.setUdid(udid);
					jfxPanel.setScene(new Scene(root));
					// Platform.setImplicitExit(false);// 不退出JAVAFX APP
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("Exception", e);
				}
			}
		});
		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				logger.info("window closing");
				controller.close();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

}
