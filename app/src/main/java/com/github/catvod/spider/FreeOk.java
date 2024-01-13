package com.github.catvod.spider;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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

import java.net.URLDecoder;
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
    private JSONObject playerConfig;
    /**
     * 筛选配置
     */
    private JSONObject filterConfig;

    @Override
    public void init(Context context) {
        super.init(context);
        try {
            playerConfig = new JSONObject("{\"qqpic\":{\"sh\":\"不卡超清[推荐]\",\"pu\":\"http://good-vip.mmiyue.com/zhenbuka2/player/index.php?video_id=\",\"sn\":0,\"or\":999},\"niuxyun\":{\"sh\":\"不卡备用[推荐]\",\"pu\":\"http://good-vip.mmiyue.com/jiekou/zbk-bkby/jx.php?id=\",\"sn\":0,\"or\":999},\"vipjx\":{\"sh\":\"不卡备用③[推荐]\",\"pu\":\"https://cq.mmiyue.com/zhenbuka2/player/index.php?url=\",\"sn\":0,\"or\":999},\"bkm3u8\":{\"sh\":\"不卡备用②[推荐]\",\"pu\":\"http://good-vip.mmiyue.com/zhenbuka2/player/index.php?url=//good-vip.mmiyue.com/zhenbuka2/api/dymp4.php?video_id=\",\"sn\":0,\"or\":999},\"cy\":{\"sh\":\"动漫云\",\"pu\":\"https://cq.mmiyue.com/jiekou/zbk-bkby/jx.php?id=\",\"sn\":0,\"or\":999},\"mgtv\":{\"sh\":\"不卡芒果\",\"pu\":\"https://cq.mmiyue.com/zhenbuka2/player/index.php?url=\",\"sn\":0,\"or\":999},\"qiyi\":{\"sh\":\"不卡奇艺\",\"pu\":\"https://cq.mmiyue.com/zhenbuka2/player/index.php?url=\",\"sn\":0,\"or\":999},\"qq\":{\"sh\":\"不卡企鹅\",\"pu\":\"https://cq.mmiyue.com/zhenbuka2/player/index.php?url=\",\"sn\":0,\"or\":999},\"youku\":{\"sh\":\"不卡优酷\",\"pu\":\"https://cq.mmiyue.com/zhenbuka2/player/index.php?url=\",\"sn\":0,\"or\":999},\"dbm3u8\":{\"sh\":\"资源备用[不推荐]\",\"pu\":\"\",\"sn\":0,\"or\":999},\"niuyun\":{\"sh\":\"超清备用\",\"pu\":\"https://api.l32c.cn/danmu/niuyun.php?id=\",\"sn\":0,\"or\":999},\"123kum3u8\":{\"sh\":\"资源备用[不推荐]\",\"pu\":\"\",\"sn\":0,\"or\":999},\"sixpan\":{\"sh\":\"不卡备用②[推荐]\",\"pu\":\"https://cq.mmiyue.com/zhenbuka2/player/index.php?url=//cq.mmiyue.com/m3u8/buka/\",\"sn\":0,\"or\":999},\"qq1\":{\"sh\":\"超清云播\",\"pu\":\"https://api.nixingle.com/zhenbuka/player/index.php?id=\",\"sn\":0,\"or\":999}}");
            filterConfig = new JSONObject("{\"1\":[{\"key\":0,\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"动作片\",\"v\":\"6\"},{\"n\":\"喜剧片\",\"v\":\"7\"},{\"n\":\"爱情片\",\"v\":\"8\"},{\"n\":\"科幻片\",\"v\":\"9\"},{\"n\":\"恐怖片\",\"v\":\"10\"},{\"n\":\"剧情片\",\"v\":\"11\"},{\"n\":\"战争片\",\"v\":\"12\"},{\"n\":\"动画片\",\"v\":\"23\"},{\"n\":\"纪录片\",\"v\":\"25\"},{\"n\":\"犯罪片\",\"v\":\"26\"}]},{\"key\":1,\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"中国大陆\",\"v\":\"中国大陆\"},{\"n\":\"中国香港\",\"v\":\"中国香港\"},{\"n\":\"中国台湾\",\"v\":\"中国台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"意大利\",\"v\":\"意大利\"},{\"n\":\"西班牙\",\"v\":\"西班牙\"},{\"n\":\"加拿大\",\"v\":\"加拿大\"}]},{\"key\":11,\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"}]}],\"2\":[{\"key\":0,\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"国产剧\",\"v\":\"13\"},{\"n\":\"港台剧\",\"v\":\"14\"},{\"n\":\"日韩剧\",\"v\":\"15\"},{\"n\":\"欧美剧\",\"v\":\"16\"},{\"n\":\"海外剧\",\"v\":\"20\"},{\"n\":\"纪录片\",\"v\":\"24\"}]},{\"key\":1,\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"中国大陆\",\"v\":\"中国大陆\"},{\"n\":\"中国香港\",\"v\":\"中国香港\"},{\"n\":\"中国台湾\",\"v\":\"中国台湾\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":11,\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"}]}],\"3\":[{\"key\":1,\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"中国大陆\",\"v\":\"中国大陆\"},{\"n\":\"中国香港\",\"v\":\"中国香港\"},{\"n\":\"中国台湾\",\"v\":\"中国台湾\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"英国\",\"v\":\"英国\"}]},{\"key\":11,\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"}]}],\"4\":[{\"key\":1,\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"中国大陆\",\"v\":\"中国大陆\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"美国\",\"v\":\"美国\"}]},{\"key\":11,\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"}]}]}");
        } catch (JSONException e) {
            SpiderDebug.log(e);
        }
    }

    /**
     * 爬虫headers
     *
     * @return header 请求头
     */
    protected HashMap<String, String> getHeaders() {
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
            headers.put("origin", " https://nfxhd.com");
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
                    u = URLDecoder.decode(u, "utf-8");
                    result.put("parse", 0);
                    result.put("playUrl", u);
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
