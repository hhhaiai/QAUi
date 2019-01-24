package com.task;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ ElementType.FIELD, ElementType.METHOD }) // 定义注解的作用目标**作用范围字段、枚举的常量/方法
@Documented // 说明该注解将被包含在javadoc中
public @interface TestCase {
	/**
	 * 执行次数
	 * 
	 * @return
	 */
	int runtime() default 1;

	/**
	 * 执行顺序,0为不执行,负数为默认不选中
	 * 
	 * @return
	 */
	int no() default 0;

	/**
	 * 用例名称,如果不填则以方法名称代替
	 * 
	 * @return
	 */
	String name() default "";

	/**
	 * 用例描述
	 * 
	 * @return
	 */
	String desc() default "";

	/**
	 * 失败后重试次数
	 * 
	 * @return
	 */
	int retry() default 0;

	/**
	 * 失败后是否写入note.xml
	 * 
	 * @return
	 */
	boolean notefailcase() default true;
}
