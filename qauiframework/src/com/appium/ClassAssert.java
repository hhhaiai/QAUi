package com.appium;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.log.SceneLogUtil;

/**
 * 
 * 断言类,主要用于判断本条用例是否还需要继续
 *
 */
public class ClassAssert {
	Logger logger = LoggerFactory.getLogger(ClassAssert.class);
	SceneLogUtil oplog;

	public ClassAssert(SceneLogUtil oplog) {
		this.oplog = oplog;
	}

	/**
	 * 比较String long int float double类型内容是否相同,相同则通过
	 * 
	 * @param expected
	 * @param actual
	 * @throws ClassAssertException
	 */
	public void Equals(Object expected, Object actual, String msm) throws ClassAssertException {
		if (expected != null && actual != null && expected.getClass() == actual.getClass()) {
			if (expected instanceof String) {
				if (!((String) expected).equals((String) actual)) {
					throw new ClassAssertException(msm);
				}
			} else if (expected instanceof Integer) {
				if ((Integer) expected != (Integer) actual) {
					throw new ClassAssertException(msm);
				}
			} else if (expected instanceof Long) {
				if ((Long) expected != (Long) actual) {
					throw new ClassAssertException(msm);
				}
			} else if (expected instanceof Float) {
				if ((Float) expected != (Float) actual) {
					throw new ClassAssertException(msm);
				}
			} else if (expected instanceof Double) {
				if ((Double) expected != (Double) actual) {
					throw new ClassAssertException(msm);
				}
			}
		} else {
			throw new ClassAssertException("对象类型不相同或对象为空.");
		}
		oplog.logAssert("<<" + msm + ">>未被抛出");
	}

	/**
	 * 如果condition为true通过,否则抛出异常
	 * 
	 * @param condition
	 * @throws ClassAssertException
	 */
	public void True(boolean condition, String msm) throws ClassAssertException {
		if (!condition)
			throw new ClassAssertException(msm);
		oplog.logAssert("<<" + msm + ">>未被抛出");
	}

	/**
	 * 如果condition为false通过,否则抛出异常
	 * 
	 * @param condition
	 * @throws ClassAssertException
	 */
	public void False(boolean condition, String msm) throws ClassAssertException {
		if (condition)
			throw new ClassAssertException(msm);
		oplog.logAssert("<<" + msm + ">>未被抛出");
	}

	/**
	 * 自定义抛出异常信息
	 * 
	 * @param msm
	 * @throws ClassAssertException
	 */
	public void Fail(String msm) throws ClassAssertException {
		throw new ClassAssertException(msm);
	}

	/**
	 * 对象为空,则抛出异常
	 * 
	 * @param object
	 * @throws ClassAssertException
	 */
	public void Null(Object object, String msm) throws ClassAssertException {
		if (object == null)
			throw new ClassAssertException(msm);
		oplog.logAssert("<<" + msm + ">>未被抛出");
	}

	/**
	 * 判断两个对象内存地址是否相同
	 * 
	 * @param expected
	 * @param actual
	 * @throws ClassAssertException
	 */
	public void Same(Object expected, Object actual, String msm) throws ClassAssertException {
		if (expected != actual)
			throw new ClassAssertException(msm);
		oplog.logAssert("<<" + msm + ">>未被抛出");
	}
}
