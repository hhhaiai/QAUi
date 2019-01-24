package com.Util;

public class TailerListenerAdapter implements TailerListener {
	public void init(Tailer tailer) {
	}

	public void fileNotFound() {
	}

	public void fileRotated() {
	}

	public void handle(String line) {
	}

	public void handle(Exception ex) {
	}

	public boolean isCancelled() {
		return false;
	}
}