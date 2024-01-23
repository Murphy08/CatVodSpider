package com.github.catvod.spider;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Liangzi extends Spider {
    public static final String site_url = "http://quanji456.com";
    public static final String play_url = "https://v.cdnlz3.com";
    private Pattern m3_url = Pattern.compile("var main = \"(\\d+).m3u8");

    @Override
    public void init(Context context) {
        super.init(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast t = Toast.makeText(context, "欢迎使用龙天接口", Toast.LENGTH_SHORT);
                t.setDuration(Toast.LENGTH_LONG);
                t.setGravity(Gravity.LEFT | Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
                t.show();
                Looper.loop();
            }
        }).start();
    }

    /**
     * 爬虫headers
     *
     * @return header 请求头
     */
    protected HashMap<String, String> getHeaders() {
        Log.e("mine", "调用了getHeaders");
        HashMap<String, String> headers = new HashMap<>();
        headers.put("method", "GET");
        //headers.put("Host", "quanji456.com/");
        //headers.put("Accept", "*/*");
        //headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Connection", "keep-alive");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0");
        return headers;
    }

    @Override
    public String homeContent(boolean filter) {

        return "";
    }

    /**
     * 获取分类信息数据
     *
     * @param tid    分类id
     * @param pg     页数
     * @param filter 同homeContent方法中的filter
     * @param extend 筛选参数{k:v, k1:v1}
     * @return
     */
    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        return "";

//        try {
//
//            JSONObject result = new JSONObject();
//            String url = "";
//
//            Log.d("url", url);
//            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders()));
//            Elements pgcs = new Elements(doc.select("body > div:nth-child(2) > div > div.col-lg-wide-75.col-xs-1.padding-0 > ul > li:nth-child(8) > a"));
//            String pc = pgcs.get(0).attr("href");
//
//            Elements elements = doc.select("body > div:nth-child(2) > div > div.col-lg-wide-75.col-xs-1.padding-0 > div:nth-child(2) > div > div > ul > li");
//            JSONArray lists = new JSONArray();
//            for (Element e : elements) {
//                JSONObject obj = new JSONObject();
//                String name = e.select("div > div.ewave-vodlist__detail > h4 > a").attr("title");
//
//                String id = "";
//                String img = e.select("div > div.ewave-vodlist__thumb").attr("data-original");
//                String remarks = "";
//                obj.put("vod_id", "id");
//                obj.put("vod_name", "name");
//                obj.put("vod_pic", "img");
//                obj.put("vod_remarks", "remarks");
//                lists.put(obj);
//            }
//            result.put("page", "pg");
//            result.put("limit", 36);
//            result.put("pagecount", "pc");
//            result.put("total", 36 * Integer.parseInt(pc));
//            result.put("list", lists);
//            Log.d("aaa", result.toString());
//
//            return result.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }

    @Override
    public String detailContent(List<String> ids) {
        Log.e("hihihihhi", "调用了详细页面" + ids.toString());
        JSONObject result = new JSONObject();
        JSONArray ja = new JSONArray();
        JSONObject list = new JSONObject();

        try {
            list.put("vod_id", ids.get(0));
            String url = site_url + ids.get(0);
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders()));
            String name = doc.select("#main > div > div.box.view-heading > div.video-info > div.video-info-header > h1").text();
            String pic = doc.select("#main > div > div.box.view-heading > div.video-cover > div > div > img").attr("data-src").toString();

            String tn = doc.select("#main > div > div.box.view-heading > div.video-info > div.video-info-header > div > div > a:nth-child(1) > span").text();
            String vy = doc.select("#main > div > div.box.view-heading > div.video-info > div.video-info-header > div > div > a:nth-child(3)").html();
            String va = doc.select("#main > div > div.box.view-heading > div.video-info > div.video-info-header > div > div > a:nth-child(4)").text();
            String vr = doc.select("#main > div > div.box.view-heading > div.video-info > div.video-info-main > div:nth-child(4) > div").text();

            String vac = doc.select("#main > div > div.box.view-heading > div.video-info > div.video-info-main > div:nth-child(2) > div").text();
            String vd = doc.select("#main > div > div.box.view-heading > div.video-info > div.video-info-main > div:nth-child(1) > div > a").text();
            String vct = doc.select("#main > div > div.box.view-heading > div.video-info > div.video-info-main > div:nth-child(7) > div").text().replace("展开", "").replace("收起", "");
            Elements tv_from = doc.select("#main > div > div:nth-child(3) > div.module-heading > div.module-tab.module-player-tab > div > div.module-tab-content > div.module-tab-item.tab-item");
            List<String> lst_from = new ArrayList<>();
            for (Element f : tv_from) {
                lst_from.add(f.attr("data-dropdown-value").trim());
            }
            String y_result = String.join("$$$", lst_from);

            Elements yuans = doc.select("#main > div > div:nth-child(3) > div.module-list");
            List<String> allJi = new ArrayList<>();
            for (Element yuan : yuans) {
                Elements jis = yuan.select("div.module-blocklist.scroll-box.scroll-box-y > div > a");
                List<String> ji_men = new ArrayList<>();
                for (Element ji : jis) {
                    String ji_name = ji.select("span").text();
                    String ji_url = ji.attr("href");
                    String ji_all = ji_name + "$" + ji_url;
                    ji_men.add(ji_all);
                }
                String v_url = String.join("#", ji_men);
                allJi.add(v_url);
            }
            String tv_url = String.join("$$$", allJi);


            list.put("vod_name", name);
            list.put("vod_pic", pic);
            list.put("type_name", tn);
            list.put("vod_year", vy);
            list.put("vod_area", va);
            list.put("vod_remarks", vr);
            list.put("vod_actor", vac);
            list.put("vod_director", vd);
            list.put("vod_content", vct);
            list.put("vod_play_from", y_result);
            list.put("vod_play_url", tv_url);

            ja.put(list);
            result.put("list", ja);
            Log.d("ssss", list.toString());
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    @Override
    public String searchContent(String key, boolean quick) {
        String url = site_url + "/index.php/vod/search.html?wd=" + key;
        Log.e("search", url);
        Map<String, String> m = new HashMap<>();
        Document doc = Jsoup.parse(OkHttpUtil.string(url, m));
        JSONArray lists = new JSONArray();
        try {
            Elements e = doc.select("#main > div > div.module > div > div > div");
            for (Element es : e) {
                JSONObject tv = new JSONObject();
                String id = es.select("div.video-info > div.video-info-header > h3 > a").attr("href");

                String name = es.select("div.video-info > div.video-info-header > h3 > a").attr("title");
                String pic = es.select("div.video-cover > div > div > img").attr("data-src");
                String remarks = es.select("div.video-info > div.video-info-header > a").html();
                tv.put("vod_id", id);
                tv.put("vod_name", name);
                tv.put("vod_pic", pic);
                tv.put("vod_remarks", remarks);
                Log.e("hihihihiiii", tv.toString());
                lists.put(tv);

            }
            JSONObject search = new JSONObject();
            search.put("list", lists);
            search.put("page", 1);
            search.put("pagecount", 10);
            search.put("limit", 20);
            search.put("total", 100);
            Log.e("sssstring", search.toString());
            return search.toString();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return "";
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        Log.e("hihihihhi", "调用了playerContent" + flag + "   " + id + "    " + vipFlags.toString());
        try {
            Map<String, String> m = new HashMap<>();
            //定义播放用的headers
            JSONObject headers = new JSONObject();
            headers.put("User-Agent", " Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0");
            headers.put("Accept", " text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            m.put("User-Agent", " Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0");
            m.put("Accept", " text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            JSONObject result = new JSONObject();

            // 播放页 url
            String url = site_url + id;
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders()));
            Elements allScript = doc.select("script");
            for (Element js : allScript) {
                String ob = js.html().trim();
                if (ob.startsWith("var player_aaaa")) {
                    int start = ob.indexOf("{");
                    JSONObject play = new JSONObject(ob.substring(start));
                    String u = play.get("url").toString();
                    if (u.endsWith("m3u8")) {
                        result.put("url", u);
                    } else {
                        Document e = Jsoup.parse(OkHttpUtil.string("u", getHeaders()));
                        Elements scripts = e.select("script");
                        for (Element i : scripts) {
                            if (i.toString().contains("main")) {
                                Matcher t_mc = m3_url.matcher(i.toString());
                                if (t_mc.find()) {
                                    String the_url = t_mc.group(1).toString().trim();
                                    String final_url = play_url + the_url + ".m3u8";
                                    result.put("url", final_url);
                                }
                            }
                        }

                    }
                    result.put("playUrl", "");


                    result.put("header", headers.toString());
                    break;
                }
            }
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

}
