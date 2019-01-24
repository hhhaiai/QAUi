package com.task;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appium.ClassAssertException;
import com.bean.TestCaseBean;
import com.helper.TimeUtil;
import com.log.SceneLogUtil;

public class FactoryScene {
	static Logger logger = LoggerFactory.getLogger(FactoryScene.class);

	final static int EXCEPTION_BeforeRun = 100;
	final static int EXCEPTION_setup = 110;
	final static int EXCEPTION_beforeTest = 120;
	final static int EXCEPTION_case = 130;
	final static int EXCEPTION_afterTest = 140;
	final static int EXCEPTION_teardown = 150;
	final static int EXCEPTION_AfterRun = 160;
	final static int EXCEPTION_others = 199;
	final static int EXCEPTION_session = 200;

	final static int PASS = 0;

	public static boolean force_stop = false;// 强制停止
	public static int FLAG = PASS;
	public static int Session_FLAG = PASS;

	/**
	 * 初始化场景工厂
	 */
	public static void init() {
		FLAG = PASS;
		Session_FLAG = PASS;
		force_stop = false;
	}

	/**
	 * 运行场景
	 * 
	 * @param instance 场景实例
	 * @return
	 */
	public static int RunTest(Object instance) {
		// Class<?> cls = Class.forName(scenename);
		// classtype instance=cls.getConstructor().newInstance();
		Class<?> cls = instance.getClass();
		Class<?> clsParent = cls.getSuperclass();
		// Method[] methods= cls.getDeclaredMethods(); // 取得全部的方法
		// Annotation[] annotations=cls.getDeclaredAnnotations();//取得全部注解
		// String
		// udid=clsParent.getDeclaredField(Cparams.udid).get(instance).toString();//获取udid值
		// String udid=null;
		SceneLogUtil oplog = null;
		Map<TestCaseBean, Boolean> caserunMap = new LinkedHashMap<>();// 场景配置参数
		List<String> failcaseList = new ArrayList<>();// 失败用例
		Method addCaseReportLine;
		try {
			oplog = (SceneLogUtil) runMethod(clsParent, instance, "getOpLogUtil");
			Map<TestCaseBean, Boolean> tempcaserunMap = (Map<TestCaseBean, Boolean>) runMethod(clsParent, instance,
					"getCaserunMap");
			for (Entry<TestCaseBean, Boolean> entry : tempcaserunMap.entrySet()) {// 因为后面要删除,所以重新创建
				caserunMap.put(entry.getKey(), entry.getValue());
			}
			oplog.logInfo(cls.getName() + "开始初始化...");
			// udid=clsParent.getDeclaredMethod("getUdid").invoke(instance).toString();
			// 接口获取设置
			Class<?> interfaces[] = cls.getInterfaces();// 获取继承接口
			List<String> varsclass = new ArrayList<>();
			for (Class<?> inte : interfaces) {
				varsclass.add(inte.getName());
			}
			Method setVarsclass = clsParent.getDeclaredMethod("setVarsclass", List.class);
			setVarsclass.setAccessible(true);
			setVarsclass.invoke(instance, varsclass);

			// 执行beforerun
			runMethod(clsParent, instance, "BeforeRun");
			oplog.logInfo(cls.getName() + "初始化完成.");
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			// TODO Auto-generated catch block
			logger.error("EXCEPITON", e);
			FLAG = EXCEPTION_BeforeRun;
			oplog.logError("init失败,请检查配置环境!");
		}
		try {
			if (FLAG == PASS) {
				// 执行setup
				long setup_startTime = new Date().getTime();
				try {
					oplog.logInfo("执行所有用例前准备setup");
					runMethod(cls, instance, "setup");
				} catch (InvocationTargetException e) {
					// TODO: handle exception
					FLAG = EXCEPTION_setup;
					logger.error("EXCEPITON", e);
					if (e.getCause() instanceof org.openqa.selenium.NoSuchSessionException) {
						oplog.logError("setup: Appium session未找到,可能与服务器的连接被中断!");
						Session_FLAG = EXCEPTION_session;
					} else {
						oplog.logError("用例前准备setup出现内部错误");
					}
				}
				// 生成视频
				String setup_videopath = (String) runMethod(clsParent, instance, "createCaseVideo");
				long setup_endTime = new Date().getTime();
				// 用例报告
				addCaseReportLine = clsParent.getDeclaredMethod("addCaseReportLine", String.class, String.class,
						String.class, boolean.class, String.class, String.class);
				addCaseReportLine.setAccessible(true);
				addCaseReportLine.invoke(instance, "setup", "setup", "开始操作", FLAG == EXCEPTION_setup ? false : true,
						setup_videopath, TimeUtil.getUseTime(setup_startTime, setup_endTime));

				// 执行testcase
				oplog.logInfo(cls.getName() + "类共" + caserunMap.size() + "条用例.");
				boolean result = false;
				int needruncasecount = 0;
				for (Entry<TestCaseBean, Boolean> entry : caserunMap.entrySet()) {
					if (entry.getValue()) {
						needruncasecount++;
						oplog.logInfo("用例序号:" + entry.getKey().getNo() + "," + entry.getKey().getName() + ",执行次数="
								+ entry.getKey().getRuntime()
								+ (entry.getKey().getRetry() > 0 ? ",重试次数=" + entry.getKey().getRetry() : ""));
					}
				}
				oplog.logInfo("测试需执行用例共" + needruncasecount + "条,开始执行...");
				while (caserunMap.size() > 0) {
					// 用例信息获取
					TestCaseBean testCaseBean = null;
					Boolean needrun = false;
					for (Entry<TestCaseBean, Boolean> entry : caserunMap.entrySet()) {
						testCaseBean = entry.getKey();
						needrun = entry.getValue();
						break;
					}
					caserunMap.remove(testCaseBean);// 移除任务
					if (!needrun || testCaseBean.getNo() == 0)
						continue;// 排除不需要执行的用例
					int runtimes = testCaseBean.getRuntime();
					boolean hasretry = false;// 是否已经重试过
					while (runtimes > 0) {
						runtimes--;
						long startTime = new Date().getTime();
						Method method = cls.getDeclaredMethod(testCaseBean.getMethodName());
						oplog.logInfo("序号" + testCaseBean.getNo() + (hasretry ? "(重试)" : "") + ",用例<<"
								+ method.getName() + ">>开始执行...");
						result = false;
						if (!force_stop && Session_FLAG == PASS) {
							try {
								// 执行beforeTest
								oplog.logInfo("准备操作beforeTest");
								cls.getDeclaredMethod("beforeTest", boolean.class).invoke(instance, result);
								oplog.logInfo("准备完毕");
							} catch (InvocationTargetException e) {
								// TODO: handle exception
								FLAG = EXCEPTION_beforeTest;
								logger.error("EXCEPITON", e);
								if (e.getCause() instanceof org.openqa.selenium.NoSuchSessionException) {
									oplog.logError("beforeTest: Appium session未找到,可能与服务器的连接被中断!");
									Session_FLAG = EXCEPTION_session;
								} else {
									oplog.logError("准备操作beforeTest出现内部错误");
								}
							}
						}
						if (!force_stop && Session_FLAG == PASS) {
							try {
								// 执行testcase
								method.setAccessible(true);
								result = (Boolean) method.invoke(instance);
							} catch (InvocationTargetException e) {
								// TODO: handle exception
								FLAG = EXCEPTION_case;
								result = false;
								if (e.getCause() instanceof ClassAssertException) {
									oplog.logError("<<" + getAssertmsm(e) + ">>被抛出");
								} else if (e.getCause() instanceof org.openqa.selenium.NoSuchSessionException) {
									logger.error("EXCEPITON", e);
									oplog.logError("case: Appium session未找到,可能与服务器的连接被中断!!");
									Session_FLAG = EXCEPTION_session;
								} else {
									oplog.logError("用例<<" + method.getName() + ">>内部出现异常错误,请检查测试用例...");
									logger.error("用例内部错误:" + getCaseException(e));
								}
							}
						}
						if (!force_stop && Session_FLAG == PASS) {
							try {
								// 执行afterTest
								oplog.logInfo("结束操作afterTest");
								cls.getDeclaredMethod("afterTest", boolean.class).invoke(instance, result);
								oplog.logInfo("结束完毕");
							} catch (InvocationTargetException e) {
								// TODO: handle exception
								FLAG = EXCEPTION_afterTest;
								logger.error("EXCEPITON", e);
								if (e.getCause() instanceof org.openqa.selenium.NoSuchSessionException) {
									oplog.logError("afterTest: Appium session未找到,可能与服务器的连接被中断!!!");
									Session_FLAG = EXCEPTION_session;
								} else {
									oplog.logError("结束操作afterTest出现内部错误");
								}
							}
							try {
								Thread.sleep(1500);// 等待处理
							} catch (InterruptedException e) {
							}
						} // 运行方法结束
						if (result) {
							oplog.logInfo("用例<<" + method.getName() + ">>执行完毕,测试" + "通过");
						} else {
							oplog.logError("用例<<" + method.getName() + ">>执行完毕,测试" + "失败");
						}
						// 生成视频
						String videopath = (String) runMethod(clsParent, instance, "createCaseVideo");
						long endTime = new Date().getTime();
						// 用例报告
						addCaseReportLine.invoke(instance, testCaseBean.getMethodName(),
								hasretry ? testCaseBean.getName() + "(重试)" : testCaseBean.getName(),
								testCaseBean.getDesc(), result, videopath, TimeUtil.getUseTime(startTime, endTime));
						// 重试机制及失败用例处理
						if (hasretry && result)
							break;
						if (!result && runtimes == 0 && !hasretry) {// 失败后重试机制
							runtimes = testCaseBean.getRetry();
							if (runtimes == 0) {
								if (testCaseBean.isNotefailcase())
									failcaseList.add(testCaseBean.getMethodName());
							} else {
								hasretry = true;
							}
						}
					} // 单个用例循环结束
				} // 所有用例结束
					// 执行teardown
				long teardown_startTime = new Date().getTime();
				try {
					oplog.logInfo("执行所有用例后收尾teardown");
					runMethod(cls, instance, "teardown");
				} catch (InvocationTargetException e) {
					// TODO: handle exception
					FLAG = EXCEPTION_teardown;
					logger.error("EXCEPITON", e);
					if (e.getCause() instanceof org.openqa.selenium.NoSuchSessionException) {
						oplog.logError("teardown: Appium session未找到,可能与服务器的连接被中断!!!!");
						Session_FLAG = EXCEPTION_session;
					} else {
						oplog.logError("收尾teardown出现内部错误");
					}
				}
				// 生成视频
				String teardown_videopath = (String) runMethod(clsParent, instance, "createCaseVideo");
				long teardown_endTime = new Date().getTime();
				// 用例报告
				addCaseReportLine.invoke(instance, "teardown", "teardown", "结束操作",
						FLAG == EXCEPTION_teardown ? false : true, teardown_videopath,
						TimeUtil.getUseTime(teardown_startTime, teardown_endTime));
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			logger.error("EXCEPITON", e);
			FLAG = EXCEPTION_others;
			oplog.logError("生成测试报告异常!");
		}
		// 执行afterrun
		try {
			oplog.logInfo(cls.getName() + "运行完成,开始注销...");
			Method AfterRun = clsParent.getDeclaredMethod("AfterRun", List.class);
			AfterRun.setAccessible(true);
			AfterRun.invoke(instance, failcaseList);
			oplog.logInfo(cls.getName() + "注销完毕.");
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			// TODO Auto-generated catch block
			FLAG = EXCEPTION_AfterRun;
			logger.error("EXCEPITON", e);
			oplog.logError("Destory失败,请检查配置环境!");
		}
		return FLAG;
	}

	/**
	 * 将方法设置为public
	 * 
	 * @param clsParent
	 * @param methodname
	 * @param instance
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	private static Object runMethod(Class<?> clsParent, Object instance, String methodname)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Method getOpLogUtil = clsParent.getDeclaredMethod(methodname);
		getOpLogUtil.setAccessible(true);
		return getOpLogUtil.invoke(instance);
	}

	/**
	 * 打印断言自定义异常信息
	 * 
	 * @param e
	 * @return
	 */
	private static String getAssertmsm(Exception e) {
		String msg = null;
		if (e instanceof InvocationTargetException) {
			Throwable targetEx = ((InvocationTargetException) e).getTargetException();
			if (targetEx != null) {
				msg = targetEx.getMessage();
			}
		} else {
			msg = e.getMessage();
		}
		return msg;
	}

	/**
	 * 排除父异常,得到子异常信息
	 * 
	 * @param e
	 * @return
	 */
	private static String getCaseException(Exception exception) {
		String result = null;
		try {
			StringWriter writer = new StringWriter();
			exception.getCause().printStackTrace(new PrintWriter(writer, true));
			result = writer.toString();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("EXCEPTION", e);
		}
		return result;
	}

	/**
	 * 获取场景用例
	 * 
	 * @param name
	 * @return
	 */
	public static List<TestCaseBean> getCase(String name) {
		Class<?> clazz;
		List<TestCaseBean> caselist = new ArrayList<>();// case执行顺序列表
		try {
			clazz = Class.forName(name);
			Method[] methods = clazz.getDeclaredMethods(); // 取得全部的方法
			for (Method method : methods) {
				String mAuthority = Modifier.toString(method.getModifiers()); // 取得访问权限
				String mName = method.getName(); // 取得方法名称
				boolean methodHasAnno = method.isAnnotationPresent(TestCase.class);
				if (methodHasAnno) {
					// 得到注解
					TestCase methodAnno = method.getAnnotation(TestCase.class);
					// 将方法注解属性添加到顺序列表
					TestCaseBean testCaseBean = new TestCaseBean();
					testCaseBean.setMethodName(mName);
					testCaseBean.setNo(methodAnno.no());
					testCaseBean.setRuntime(methodAnno.runtime());
					if (methodAnno.name().equals("")) {
						testCaseBean.setName(mName);
					} else {
						testCaseBean.setName(methodAnno.name());
					}
					testCaseBean.setDesc(methodAnno.desc());
					testCaseBean.setRetry(methodAnno.retry());
					testCaseBean.setNotefailcase(methodAnno.notefailcase());
					caselist.add(testCaseBean);
				}
			}
			// caselist冒泡排序
			TestCaseBean tempcasebean; // 记录临时中间值
			for (int i = 0; i < caselist.size() - 1; i++) {
				for (int j = i + 1; j < caselist.size(); j++) {
					if (caselist.get(i).getNo() > caselist.get(j).getNo()) { // 交换两数的位置
						tempcasebean = caselist.get(i);
						caselist.set(i, caselist.get(j));
						caselist.set(j, tempcasebean);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("EXCEPTION", e);
		}
		return caselist;
	}
}
