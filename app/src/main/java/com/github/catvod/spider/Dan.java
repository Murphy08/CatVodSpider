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

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dan extends Spider {
    public static final String site_url = "https://dandanju.me";
    Pattern regexCategory = Pattern.compile("/type/(\\S+).html");
    Pattern reg_video_id = Pattern.compile("/video/(\\S+).html");

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
//        headers.put("Host", "www.freeok.pro");
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
            Elements el = doc.select("div.ewave-header_bd.clearfix > div.ewave-header__menu > ul > li");
            JSONArray classes = new JSONArray();
            JSONObject filters = new JSONObject();
            for (Element e : el) {
                if (num > 5) {
                    break;
                }
                String name = e.select("a").text();
                if (name.equals("首页")) {
                    continue;
                }
                Log.e("name", name);
                Matcher reg = regexCategory.matcher(e.select("a").attr("href"));
                if (!reg.find())
                    continue;
                String id = reg.group(1).trim();
                JSONObject clasz = new JSONObject();
                clasz.put("type_id", id);
                clasz.put("type_name", name);
                classes.put(clasz);

                //筛选
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


                num++;
            }
            result.put("class", classes);
            result.put("filters", filters);
            Log.d("class", result.toString());


            Elements els = doc.select("div.tab-content.ewave-pannel_bd > li");
            JSONArray lists = new JSONArray();
            for (Element e : els) {
                JSONObject obj = new JSONObject();
                String name = e.select("div> div").attr("title");
                Matcher video_ma = reg_video_id.matcher(e.select("div > div > a").attr("href"));
                if (!video_ma.find())
                    continue;
                String id = video_ma.group(1).trim();
                String pic = e.select("div > div").attr("data-original");
                String remarks = "";
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
        Log.d("eeeeeeeeextend", extend.toString());
        JSONObject result = new JSONObject();
        try {
            String url = "";
            if (extend.size() > 0) {
                //https://dandanju.me/show/3-内地--选秀-----2---2022.html
                String juqing = "";
                String diqu = "";
                String nianfen = "";
                if (extend.get("0") == null) {
                    juqing = "";
                } else {
                    juqing = extend.get("0");
                }
                if (extend.get("1") == null) {
                    diqu = "";
                } else {
                    diqu = extend.get("1");
                }
                if (extend.get("2") == null) {
                    nianfen = "";
                } else {
                    nianfen = extend.get("2");
                }
                url = site_url + "/show/" + tid + "-" + diqu + "--" + juqing + "-----" + pg + "---" + nianfen + ".html";
            } else {
                url = site_url + "/type/" + tid + "-" + pg + ".html";
            }


            Log.d("url", url);
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders()));
            Elements pgcs = new Elements(doc.select("body > div:nth-child(2) > div > div.col-lg-wide-75.col-xs-1.padding-0 > ul > li:nth-child(8) > a"));
            String pc = pgcs.get(0).attr("href");
            int start = pc.indexOf("-") + 1;
            int end = pc.indexOf(".");
            pc = pc.substring(start, end);

            Elements elements = doc.select("body > div:nth-child(2) > div > div.col-lg-wide-75.col-xs-1.padding-0 > div:nth-child(2) > div > div > ul > li");
            JSONArray lists = new JSONArray();
            for (Element e : elements) {
                JSONObject obj = new JSONObject();
                String name = e.select("div > div.ewave-vodlist__detail > h4 > a").attr("title");
                Matcher video_ma = reg_video_id.matcher(e.select("div > div.ewave-vodlist__detail > h4 > a").attr("href"));
                if (!video_ma.find())
                    continue;
                String id = video_ma.group(1).trim();
                String img = e.select("div > div.ewave-vodlist__thumb").attr("data-original");
                String remarks = "";
                obj.put("vod_id", id);
                obj.put("vod_name", name);
                obj.put("vod_pic", img);
                obj.put("vod_remarks", remarks);
                lists.put(obj);
            }
            result.put("page", pg);
            result.put("limit", 36);
            result.put("pagecount", pc);
            result.put("total", 36 * Integer.parseInt(pc));
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
