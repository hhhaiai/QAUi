package com.appium;

import java.time.Duration;

import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.SystemClock;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.android.AndroidDriver;

public class AndroidDriverWait extends FluentWait<AndroidDriver<WebElement>> {
	// public final static long DEFAULT_SLEEP_TIMEOUT=500;
	// private final WebDriver driver;
	//
	// public AndroidDriverWait(AndroidDriver driver,long timeOutInSeconds){
	// this(driver, new SystemClock(), Sleeper.SYSTEM_SLEEPER, timeOutInSeconds,
	// DEFAULT_SLEEP_TIMEOUT);
	// }
	//
	// public AndroidDriverWait(AndroidDriver driver,long timeOutInSeconds,long
	// sleepInMillis){
	// this(driver, new SystemClock(), Sleeper.SYSTEM_SLEEPER, timeOutInSeconds,
	// sleepInMillis);
	// }
	//
	// public AndroidDriverWait(AndroidDriver driver,Clock clock,Sleeper
	// sleeper,long timeOutInSeconds,long sleepTimeOut){
	// super(driver,clock,sleeper);
	// withTimeout(timeOutInSeconds,TimeUnit.SECONDS);
	// pollingEvery(sleepTimeOut,TimeUnit.MILLISECONDS);
	// ignoring(NotFoundException.class);
	// this.driver=driver;
	// }
	//
	// @Override
	// protected RuntimeException timeoutException(String message,Throwable
	// lastException){
	// TimeoutException ex=new TimeoutException(message,lastException);
	// ex.addInfo(WebDriverException.DRIVER_INFO,driver.getClass().getName());
	// if(driver instanceof RemoteWebDriver){
	// RemoteWebDriver remote=(RemoteWebDriver) driver;
	// if(remote.getSessionId()!=null){
	// ex.addInfo(WebDriverException.SESSION_ID,remote.getSessionId().toString());
	// }
	// if(remote.getCapabilities()!=null){
	// ex.addInfo("Capabilities",remote.getCapabilities().toString());
	// }
	// }
	// throw ex;
	// }

	// 默认轮询时间(毫秒)
	public final static long DEFAULT_POLLINGEVERY_TIMEMILLS = 1000;
	public final static long DEFAULT_TIMEOUT_INSECONDS = 5;

	/**
	 * Wait will ignore instances of NotFoundException that are encountered (thrown)
	 * by default in the 'until' condition, and immediately propagate all others.
	 * You can add more to the ignore list by calling ignoring(exceptions to add).
	 *
	 * @param driver
	 *            The AppiumDriver instance to pass to the expected conditions
	 * @see WebDriverWait#ignoring(java.lang.Class)
	 */
	public AndroidDriverWait(AndroidDriver<WebElement> driver) {
		this(driver, new SystemClock(), Sleeper.SYSTEM_SLEEPER, DEFAULT_TIMEOUT_INSECONDS,
				DEFAULT_POLLINGEVERY_TIMEMILLS);
	}

	/**
	 * Wait will ignore instances of NotFoundException that are encountered (thrown)
	 * by default in the 'until' condition, and immediately propagate all others.
	 * You can add more to the ignore list by calling ignoring(exceptions to add).
	 *
	 * @param driver
	 *            The AppiumDriver instance to pass to the expected conditions
	 * @param timeOutInSeconds
	 *            The timeout in seconds when an expectation is called
	 * @see WebDriverWait#ignoring(java.lang.Class)
	 */
	public AndroidDriverWait(AndroidDriver<WebElement> driver, long timeOutInSeconds) {
		this(driver, new SystemClock(), Sleeper.SYSTEM_SLEEPER, timeOutInSeconds, DEFAULT_POLLINGEVERY_TIMEMILLS);
	}

	/**
	 * Wait will ignore instances of NotFoundException that are encountered (thrown)
	 * by default in the 'until' condition, and immediately propagate all others.
	 * You can add more to the ignore list by calling ignoring(exceptions to add).
	 *
	 * @param driver
	 *            The WebDriver instance to pass to the expected conditions
	 * @param timeOutInSeconds
	 *            The timeout in seconds when an expectation is called
	 * @param sleepInMillis
	 *            The duration in milliseconds to sleep between polls.
	 * @see WebDriverWait#ignoring(java.lang.Class)
	 */
	public AndroidDriverWait(AndroidDriver<WebElement> driver, long timeOutInSeconds, long sleepInMillis) {
		this(driver, new SystemClock(), Sleeper.SYSTEM_SLEEPER, timeOutInSeconds, sleepInMillis);
	}

	protected AndroidDriverWait(AndroidDriver<WebElement> driver, Clock clock, Sleeper sleeper, long timeOutInSeconds,
			long sleepTimeOut) {
		super(driver, clock, sleeper);
		withTimeout(Duration.ofMillis(timeOutInSeconds * 1000));
		pollingEvery(Duration.ofMillis(sleepTimeOut));
		// withTimeout(timeOutInSeconds, TimeUnit.SECONDS);
		// pollingEvery(sleepTimeOut, TimeUnit.MILLISECONDS);
		ignoring(NotFoundException.class);
	}

}
