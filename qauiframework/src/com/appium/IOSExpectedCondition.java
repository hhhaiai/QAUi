package com.appium;

import org.openqa.selenium.WebElement;

import com.google.common.base.Function;

import io.appium.java_client.ios.IOSDriver;

public interface IOSExpectedCondition<T> extends Function<IOSDriver<WebElement>, T> {

}
