package com.appium;

import org.openqa.selenium.WebElement;

import com.google.common.base.Function;

import io.appium.java_client.android.AndroidDriver;

public interface AndroidExpectedCondition<T> extends Function<AndroidDriver<WebElement>, T> {

}
