package com.github.catvod.spider;

import android.content.Context;
import android.os.Looper;
import android.util.Base64;
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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Xingchen extends Spider {
    public static final String site_url = "http://www.hljtdmy.com";


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
        headers.put("Host", "www.hljtdmy.com");
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("DNT", "1");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        return headers;
    }

    @Override
    public String homeContent(boolean filter) {
        try {
            JSONObject result = new JSONObject();
            int num = 1;
            Document doc = Jsoup.parse(OkHttpUtil.string(site_url, getHeaders()));
            Elements el = doc.select("#example-navbar-collapse > ul > li");
            JSONArray classes = new JSONArray();
            for (Element e : el) {
                if (num > 5) {
                    break;
                }
                String name = e.select("a").text();
                if (name.equals("首页")) {
                    continue;
                }
                Log.e("name", name);
                String id = e.select("a").attr("href").toString();
                JSONObject clasz = new JSONObject();
                clasz.put("type_id", id);
                clasz.put("type_name", name);
                classes.put(clasz);

                //筛选
                /*
                if (filter) {
                    Document d = Jsoup.parse(OkHttpUtil.string(site_url + "/type/" + id + ".html", getHeaders()));
                    Elements es = d.select("div.ewave-pannel_hd > div.ewave-screen__list");
                    JSONArray ja = new JSONArray();
                    int key = 0;
                    for (Element m : es) {
                        JSONObject se = new JSONObject();
                        String label = m.select("label > span").text();
                        if (label.equals("按分类")) {
                            continue;
                        }
                        Elements types = m.select("div > ul > li");
                        JSONArray tys = new JSONArray();
                        for (Element type : types) {
                            JSONObject ty = new JSONObject();
                            String n = type.select("a").text();

                            String v = n;
                            if (n.equals("全部")) {
                                v = "";
                            }
                            ty.put("n", n);
                            ty.put("v", v);
                            tys.put(ty);
                        }
                        se.put("key", key);
                        se.put("name", label);
                        se.put("value", tys);
                        ja.put(se);
                        key++;
                    }

                    filters.put(id, ja);
                }
                */

                num++;
            }


            result.put("class", classes);
            //result.put("filters", filters);
            Log.d("class", result.toString());


            Elements els = doc.select("body > div.container > div > div:nth-child(2) > ul > li");
            JSONArray lists = new JSONArray();
            for (Element e : els) {
                JSONObject obj = new JSONObject();
                String name = e.select("a").attr("title");
                String id = e.select("a").attr("href");
                String pic = e.select("a").attr("data-original");
                String remarks = e.select("a > span.note.text-bg-r").text();
                obj.put("vod_id", id);
                obj.put("vod_name", name);
                obj.put("vod_pic", pic);
                obj.put("vod_remarks", remarks);
                lists.put(obj);
                Log.d("lists", lists.toString());
            }
            result.put("list", lists);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }

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
            String name = doc.select("#zanpian-score > h1").text();
            String pic = doc.select("body > div.container > div > div.layout-box.clearfix.p-0.mt-0 > div.col-md-9.col-sm-12.col-xs-12 > div.col-md-3.col-sm-3.col-xs-4.clearfix > div > a").attr("style").replace("background: url(", "").replace(") no-repeat top center;background-size:cover;", "");
            Elements line = doc.select("#zanpian-score > ul > li:nth-child(3) > a");

            List<String> li = new LinkedList<>();
            for (Element eeee : line) {
                li.add(eeee.text());
            }
            String tn = String.join("、", li);

            String vy = doc.select("#zanpian-score > ul > li:nth-child(10)").text().replace("年代：", "");
            String va = doc.select("#zanpian-score > ul > li.col-md-6.col-sm-6.col-xs-4.text.hidden-xs").text().replace("国家/地区：", "");
            String vr = doc.select("#zanpian-score > ul > li:nth-child(2)").text().replace("清晰：", "");
            line = doc.select("#zanpian-score > ul > li:nth-child(4) > a");
            li.clear();
            for (Element eeee : line) {
                li.add(eeee.text());
            }
            String vac = String.join("、", li);
            String vd = doc.select("#zanpian-score > ul > li:nth-child(6)").text().replace("导演：", "");
            String vct = doc.select("body > div.container > div > div.col-md-9.col-sm-12.box-main-content > div:nth-child(3) > div.swiper-container.vod-swiper-5.swiper-container-horizontal > span").text().replace("简介：", "").replace("详情", "");
            String y_result = doc.select("#playTab > li > a").text();

            Elements ju_ji = doc.select("#con_playlist_1 > li");
            ArrayList<String> ji_men = new ArrayList<>();
            for (Element es : ju_ji) {
                String pname = es.select("a").text();
                String pdir = es.select("a").attr("href");
                String all = pname + "$" + pdir;
                ji_men.add(all);
            }
            Collections.reverse(ji_men);
            String v_url = String.join("#", ji_men);

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
            list.put("vod_play_url", v_url);

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
        String url = site_url + "/search/?wd=" + key;
        Log.e("search", url);
        Map<String, String> m = new HashMap<>();
        Document doc = Jsoup.parse(OkHttpUtil.string(url, m));
        JSONArray lists = new JSONArray();
        try {
            Elements e = doc.select("#content > div");
            for (Element es : e) {
                JSONObject tv = new JSONObject();
                String id = es.select("div.col-md-9.col-sm-8.col-xs-9.clearfix.pb-0 > div > ul > li:nth-child(1) > a").attr("href");

                String name = es.select("div.col-md-9.col-sm-8.col-xs-9.clearfix.pb-0 > div > ul > li:nth-child(1) > a").attr("title");
                String pic = es.select("div.col-md-3.col-sm-4.col-xs-3.news-box-txt-l.clearfix > a").attr("data-original");
                String remarks = es.select("div.col-md-9.col-sm-8.col-xs-9.clearfix.pb-0 > div > ul > li:nth-child(1) > span").text();
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
                if (ob.startsWith("var zanpiancms_player")) {
                    int start = ob.indexOf("{");
                    JSONObject play = new JSONObject(ob.substring(start));
                    String u = play.get("url").toString();

                    result.put("playUrl", "");

                    result.put("url", u);
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
