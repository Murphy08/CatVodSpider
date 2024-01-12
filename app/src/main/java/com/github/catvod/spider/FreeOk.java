package com.github.catvod.spider;

import android.content.Context;
import android.util.Log;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FreeOk extends Spider {
    public static final String site_url="http://www.freeok.pro";
    Pattern regexCategory = Pattern.compile("/v-type/(\\S+).html");
    @Override
    public void init(Context context) {
        super.init(context);
    }
    /**
     * 爬虫headers
     *
     * @param url
     * @return header 请求头
     */
    protected HashMap<String, String> getHeaders(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("method", "GET");
        headers.put("Host", "www.freeok.pro");
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("DNT", "1");
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        return headers;
    }
    @Override
    public String homeContent(boolean filter) {
        try {
            JSONObject result = new JSONObject();
            int num = 1;
            Document doc = Jsoup.parse(OkHttpUtil.string(site_url, getHeaders(site_url)));
            Elements el = doc.select("li.swiper-slide> a");
            JSONArray classes = new JSONArray();
            for (Element e : el) {
                if (num > 6) {
                    break;
                }
                String name = e.text();
                Matcher reg = regexCategory.matcher(e.attr("href"));
                if (!reg.find())
                    continue;
                String id = reg.group(1).trim();
                JSONObject clasz = new JSONObject();
                clasz.put("type_id", id);
                clasz.put("type_name", name);
                classes.put(clasz);
                num++;
            }
            result.put("class",classes);
            Log.d("class",result.toString());

        }catch (Exception e){
            SpiderDebug.log(e);
        }

        return "";
    }

    @Override
    public String homeVideoContent() {
        return super.homeVideoContent();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        return super.categoryContent(tid, pg, filter, extend);
    }

    @Override
    public String detailContent(List<String> ids) {
        return super.detailContent(ids);
    }

    @Override
    public String searchContent(String key, boolean quick) {
        return super.searchContent(key, quick);
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        return super.playerContent(flag, id, vipFlags);
    }

    @Override
    public boolean isVideoFormat(String url) {
        return super.isVideoFormat(url);
    }

    @Override
    public boolean manualVideoCheck() {
        return super.manualVideoCheck();
    }
}
