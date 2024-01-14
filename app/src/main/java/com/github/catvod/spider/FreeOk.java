package com.github.catvod.spider;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.apache.commons.lang3.CharSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FreeOk extends Spider {
    public static final String site_url = "http://www.freeok.pro";
    Pattern regexCategory = Pattern.compile("/v-type/(\\S+).html");
    Pattern reg_video_id = Pattern.compile("/vod-detail/(\\S+).html");

    Pattern reg_play = Pattern.compile("/x-play/(\\S+).html");


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
            Document doc = Jsoup.parse(OkHttpUtil.string(site_url, getHeaders()));
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
            result.put("class", classes);
            Log.d("class", result.toString());
            Elements els = doc.select(" div.module-main.scroll-box > div > a");
            JSONArray lists = new JSONArray();
            for (Element e : els) {
                JSONObject obj = new JSONObject();
                String name = e.select("div.module-poster-item-info > div.module-poster-item-title").text();
                System.out.println(e.attr("href"));
                Matcher video_ma = reg_video_id.matcher(e.attr("href"));
                if (!video_ma.find())
                    continue;
                String id = video_ma.group(1).trim();
                String pic = e.select("div.module-item-cover > div.module-item-pic > img").attr("data-original");
                String remarks = e.select("div.module-item-cover > div.module-item-note").text();
                obj.put("vod_id", id);
                obj.put("vod_name", name);
                obj.put("vod_pic", pic);
                obj.put("vod_remarks", remarks);
                lists.put(obj);
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
        JSONObject result = new JSONObject();
        try {
            result.put("page", pg);
            result.put("limit", 40);
            result.put("pagecount", 10);
            result.put("total", 400);
            String url = site_url + "/vod-show/" + tid + "--------" + pg + "---.html";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders()));
            Elements elements = doc.select("div.module-main.module-page > div.module-items.module-poster-items-base > a");
            JSONArray lists = new JSONArray();
            for (Element e : elements) {
                JSONObject obj = new JSONObject();
                String name = e.select("div.module-poster-item-info > div.module-poster-item-title").text();
                System.out.println(e.attr("href"));
                Matcher video_ma = reg_video_id.matcher(e.attr("href"));
                if (!video_ma.find())
                    continue;
                String id = video_ma.group(1).trim();
                String img = e.select("div.module-item-pic >img").attr("data-original");
                String remarks = e.select("div.module-item-cover > div.module-item-note").text();
                obj.put("vod_id", id);
                obj.put("vod_name", name);
                obj.put("vod_pic", img);
                obj.put("vod_remarks", remarks);
                lists.put(obj);
            }
            result.put("list", lists);
            Log.d("aaa", result.toString());

            return result.toString();

        } catch (Exception e) {
            SpiderDebug.log(e);
        }


        return "";

    }

    @Override
    public String detailContent(List<String> ids) {
        Log.e("hihihihhi", "调用了详细页面" + ids.toString());
        boolean have_kuake = false;
        JSONObject result = new JSONObject();
        JSONArray ja = new JSONArray();
        JSONObject list = new JSONObject();

        try {
            list.put("vod_id", ids.get(0));
            String url = site_url + "/vod-detail/" + ids.get(0) + ".html";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders()));
            String name = doc.select("div > h1").text();
            String pic = doc.select("body > div > div.main > div > div.module.module-info > div.module-main > div.module-info-poster > div > div > img").attr("data-original");
            String tn = doc.select("body > div > div.main > div > div.module.module-info > div.module-main > div.module-info-main > div.module-info-heading > div.module-info-tag > div:nth-child(3)").text();
            String vy = doc.select("body > div > div.main > div > div.module.module-info > div.module-main > div.module-info-main > div.module-info-heading > div.module-info-tag > div:nth-child(1)").text();
            String va = doc.select("body > div > div.main > div > div.module.module-info > div.module-main > div.module-info-main > div.module-info-heading > div.module-info-tag > div:nth-child(2)").text();
            String vr = doc.select("body > div > div.main > div > div.module.module-info > div.module-main > div.module-info-main > div.module-info-content > div.module-info-items > div:nth-child(6) > div").text();
            String vac = doc.select("body > div > div.main > div > div.module.module-info > div.module-main > div.module-info-main > div.module-info-content > div.module-info-items > div:nth-child(4) > div").text();
            String vd = doc.select("body > div > div.main > div > div.module.module-info > div.module-main > div.module-info-main > div.module-info-content > div.module-info-items > div:nth-child(2) > div > a").text();
            String vct = doc.select("body > div > div.main > div > div.module.module-info > div.module-main > div.module-info-main > div.module-info-content > div.module-info-items > div.module-info-item.module-info-introduction > div > p").text();
            Elements yuan = doc.select("#y-playList > div");
            ArrayList<String> yr = new ArrayList<String>();
            for (Element ee : yuan) {
                String y = ee.select("span").text();

                if (!y.equals("夸克4K")) {
                    yr.add(y);
                } else {
                    have_kuake = true;
                }
            }
            String y_result = String.join("$$$", yr);
            Elements ju_ji = doc.select("#panel1 > div > div");
            ArrayList<String> yuan_men = new ArrayList<>();
            ArrayList<String> ji_men = new ArrayList<>();
            for (Element es : ju_ji) {
                if (have_kuake) {
                    have_kuake = false;
                    continue;
                }

                Elements pian = es.select("a");
                for (Element p : pian) {
                    String pname = p.select("span").text();
                    Matcher pMacher = reg_play.matcher(p.attr("href"));
                    if (!pMacher.find()) {
                        continue;
                    }
                    String pdir = pMacher.group(1).trim();
                    String all = pname + "$" + pdir;
                    ji_men.add(all);
                }
                String y_all = String.join("#", ji_men);
                ji_men.clear();
                yuan_men.add(y_all);
            }
            String v_url = String.join("$$$", yuan_men);

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
        String url = site_url + "/so1so/-------------.html?wd=" + key;
        Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders()));

        return "";
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        Log.e("hihihihhi", "调用了playerContent" + flag + "   " + id + "    " + vipFlags.toString());
        try {
            //定义播放用的headers
            JSONObject headers = new JSONObject();
            //headers.put("Host", " cokemv.co");
            headers.put("origin", "http://www.freeok.pro");
            headers.put("User-Agent", " Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
            headers.put("Accept", " */*");
            headers.put("Accept-Language", " zh-CN,zh;q=0.9,en-US;q=0.3,en;q=0.7");
            headers.put("Accept-Encoding", " gzip, deflate");
            JSONObject result = new JSONObject();

            // 播放页 url
            String url = site_url + "/x-play/" + id + ".html";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders()));
            Elements allScript = doc.select("script");
            for (Element js : allScript) {
                String ob = js.html().trim();
                if (ob.startsWith("var player_")) {
                    int start = ob.indexOf("{");
                    JSONObject play = new JSONObject(ob.substring(start));
                    String u = play.get("url").toString();
                    String au = URLDecoder.decode(u, "utf-8");

                    Log.d("44444444444444444444", au);
                    result.put("parse", 0);
                    result.put("playUrl", "");
                    result.put("url", au);
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
