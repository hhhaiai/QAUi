package com.test;

import com.appium.ClassAssertException;
import com.appium.UiWatcher;
import com.task.BaseAndroidScene;
import com.task.TestCase;

import java.util.List;

public class Demo extends BaseAndroidScene implements Demo常量{
    @Override
    public void setup() {
        //当未找到元素时,自动处理权限弹出框
        OP.registerUiWatcher("异常处理", new UiWatcher() {
            @Override
            public boolean checkForCondition() {
                return OP.HandlePermission();
            }
        });
    }

    @Override
    public void teardown() {

    }

    @Override
    public void beforeTest(boolean lastcaseresult) {
        OP.closeApp();
       OP.launchApp();
    }

    @Override
    public void afterTest(boolean thiscaseresult) {
        OP.pressBACK(3,1000);
    }

    @TestCase(no = 100, name = "设置", desc = "设置功能遍历", runtime=1,retry=0,notefailcase=true)
    protected boolean 设置() throws ClassAssertException {
        //测试用例
        if(OP.findElement(设置_搜索栏).click()){
            OP.findElement(设置_搜索栏).sendText("com/test");
            OP.findElement(设置_搜索栏_返回).click();
        }
        ASSERT.True(CHECK.exist("设置",设置_搜索栏,设置_列表框),"未在设置界面");
        //取得设置列表的功能的名称
        List<String> funcs_list=OP.EXTEND_SWIPE().dragSeekbarTogetItems(OP.MobileBy(设置_列表框),OP.EXTEND_SWIPE().UP,OP.MobileBy(设置_列表框_功能子项),5,5);
        for(String name:funcs_list){
            //点击每个功能然后按返回键返回
            if(OP.EXTEND_SWIPE().dragSeekbarTo(OP.MobileBy(设置_列表框),OP.EXTEND_SWIPE().UP,5,5,OP.MobileBy(name),5).click()){
                OP.findElement(设置_返回).click();
            }
            ASSERT.True(CHECK.exist("设置"),"未在设置界面");
        }
        return true;//返回false或者抛出异常则判断该用例执行结果为失败
    }
}
