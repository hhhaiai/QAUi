package com.bean;

public class TestCaseBean {
	String methodName;
	int runtime;
	int no;
	String name;
	String desc;
	int retry;
	boolean notefailcase;

	public int getRetry() {
		return retry;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getRuntime() {
		return runtime;
	}

	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public boolean isNotefailcase() {
		return notefailcase;
	}

	public void setNotefailcase(boolean notefailcase) {
		this.notefailcase = notefailcase;
	}

}
