package com.github.catvod.spider;

import android.app.Activity;
import android.content.Context;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;

import java.lang.reflect.Field;
import java.util.Map;

public class Init {
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
        SpiderDebug.log("自定义爬虫代码加载成功！");
    }


}
