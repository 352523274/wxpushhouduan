package com.lx.wxpush.controller;


import cn.hutool.core.date.ChineseDate;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.wxpush.bean.Content;
import com.lx.wxpush.bean.Pram;
import com.lx.wxpush.dao.ContentMapper;
import com.lx.wxpush.dao.PramMapper;
import com.lx.wxpush.http.AjaxResult;
import com.lx.wxpush.utils.DateUtil;
import com.lx.wxpush.utils.HttpUtil;
import com.lx.wxpush.utils.LunarCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@RestController
@RequestMapping("/wx")
public class wxController {

    @Value("${wx.config.appId}")
    private String appId;
    @Value("${wx.config.appSecret}")
    private String appSecret;
    @Value("${wx.config.templateId}")
    private String templateId;
    @Value("${wx.config.openid}")
    private String openid;
    @Value("${weather.config.appid}")
    private String weatherAppId;
    @Value("${weather.config.appSecret}")
    private String weatherAppSecret;
    @Value("${message.config.togetherDate}")
    private String togetherDate;
    @Value("${message.config.birthday}")
    private String birthday;
    @Value("${message.config.message}")
    private String message;

    @Value("${message.config.city}")
    private String city;
    @Autowired
    private ContentMapper contentMapper;
    @Autowired
    private PramMapper pramMapper;


    private String accessToken = "";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * ??????Token
     * ????????????7???30????????????
     *
     * @return
     */
    @Scheduled(cron = "0 30 8,20 ? * *")
    @RequestMapping("/getAccessToken")
    public String getAccessToken() {
        //????????????????????????????????????????????????????????????api
        String grant_type = "client_credential";
        //??????????????????
        String params = "grant_type=" + grant_type + "&secret=" + appSecret + "&appid=" + appId;
        //??????GET??????
        String sendGet = HttpUtil.sendGet("https://api.weixin.qq.com/cgi-bin/token", params);
        // ??????????????????????????????json?????????
        com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(sendGet);
        logger.info("??????token????????????=" + jsonObject1);
        //??????accesstoken
        accessToken = (String) jsonObject1.get("access_token");
        return sendWeChatMsg(accessToken, 0);
    }

    /**
     * ??????
     */
    @GetMapping("/test")
    public String get1() {
        return "ok";
    }

    /**
     * ??????Token
     * ????????????
     *
     * @return
     */
    @GetMapping
    public String get() {
        //????????????????????????????????????????????????????????????api
        String grant_type = "client_credential";
        //??????????????????
        String params = "grant_type=" + grant_type + "&secret=" + appSecret + "&appid=" + appId;
        //??????GET??????
        String sendGet = HttpUtil.sendGet("https://api.weixin.qq.com/cgi-bin/token", params);
        // ??????????????????????????????json?????????
        com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(sendGet);
        logger.info("??????token????????????=" + jsonObject1);
        //??????accesstoken
        accessToken = (String) jsonObject1.get("access_token");
        return sendWeChatMsg(accessToken, 0);
    }

    /**
     * ??????????????????
     *
     * @return
     */
    public String sendWeChatMsg(String accessToken, long id) {

        //?????????????????????
        String[] openIds = openid.split(",");
        List<JSONObject> errorList = new ArrayList();
        //????????????????????????(api????????????)
        JSONObject templateMsg = new JSONObject(new LinkedHashMap<>());
        //????????????
        String date = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        //??????????????????????????????
        JSONObject data = new JSONObject(new LinkedHashMap<>());
        //??????????????????
        addDateInfo(data);
        //???????????????????????????
        addTemperature2(data);
        //????????????????????????
        addSoftMsg(data);
        //???????????????
        addQinghua(data, id);
        //??????togetherDate
        addTogetherDate(data, date);
        //??????????????????
        addBirthDate(data, date);
        templateMsg.put("data", data);
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;
        for (String opedId : openIds) {
            templateMsg.put("touser", opedId);
            templateMsg.put("template_id", templateId);
            String sendPost = HttpUtil.sendPost(url, templateMsg.toJSONString());
            JSONObject WeChatMsgResult = JSONObject.parseObject(sendPost);
            if (!"0".equals(WeChatMsgResult.getString("errcode"))) {
                JSONObject error = new JSONObject();
                error.put("openid", opedId);
                error.put("errorMessage", WeChatMsgResult.getString("errmsg"));
                errorList.add(error);
            }
            logger.info("sendPost=" + sendPost);
        }
        JSONObject result = new JSONObject();
        result.put("result", "success");
        result.put("errorData", errorList);
        return result.toJSONString();

    }

    public static void main(String[] args) throws ParseException {
        String url = "https://jisutqybmf.market.alicloudapi.com/weather/query"
                + "?Authorization=APPCODE 50c05b8398e541fc81637176308d5061&city=??????";

        String host = "https://jisutqybmf.market.alicloudapi.com";
        String path = "/weather/query";
        String method = "GET";//GET/POST ??????
        String appcode = "????????????AppCode";
        Map<String, String> headers = new HashMap<String, String>();
        //?????????header????????????(?????????????????????)???Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //??????API??????????????????????????????Content-Type
        headers.put("Content-Type", "application/json; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("city", "??????");
        querys.put("citycode", "citycode");
        querys.put("cityid", "cityid");
        querys.put("ip", "ip");
        querys.put("location", "location");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers1 = new HttpHeaders();
        headers1.set("Authorization",
                "APPCODE 50c05b8398e541fc81637176308d5061");
        ResponseEntity<JSONObject> exchange = restTemplate.exchange("https://jisutqybmf.market.alicloudapi.com/weather/query?city=??????"
                , HttpMethod.GET, new HttpEntity<>(null, headers1), JSONObject.class);
        JSONObject body = exchange.getBody();
        System.out.println(body);

    }

    /**
     * ??????????????????
     */
    private void addBirthDate(JSONObject data, String date) {

        JSONObject birthDate = new JSONObject();
        String birthDay = "????????????";
        try {
            Calendar calendar = Calendar.getInstance();
            String newD = calendar.get(Calendar.YEAR) + "-" + birthday;
            birthDay = DateUtil.daysBetween(date, newD);
            if (Integer.parseInt(birthDay) < 0) {
                Integer newBirthDay = Integer.parseInt(birthDay) + 365;
                birthDay = newBirthDay + "???";
            } else {
                birthDay = birthDay + "???";
            }
        } catch (ParseException e) {
            logger.error("togetherDate????????????" + e.getMessage());
        }
        birthDate.put("value", birthDay);
        birthDate.put("color", "#6EEDE2");
        data.put("birthDate", birthDate);
    }


    /**
     * ??????togetherDate
     */
    private void addTogetherDate(JSONObject data, String date) {
        JSONObject togetherDateObj = new JSONObject();
        String togetherDay = "";
        try {
            togetherDay = "???" + DateUtil.daysBetween(togetherDate, date) + "???";
        } catch (ParseException e) {
            logger.error("togetherDate????????????" + e.getMessage());
        }
        togetherDateObj.put("value", togetherDay);
        togetherDateObj.put("color", "#FEABB5");
        data.put("togetherDate", togetherDateObj);
    }

    /**
     * ????????????
     */
    private void addQinghua(JSONObject data, long id) {
        JSONObject messageObj = new JSONObject();
        //????????????:https://api.shadiao.pro/chp"
        String qinghua = getqinghua(id);
        messageObj.put("value", qinghua);
        //????????????
        messageObj.put("color", toHexFromColor());
        data.put("message", messageObj);
    }

    /**
     * ?????????????????????????????????2
     */
    private void addTemperature2(JSONObject data) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers1 = new HttpHeaders();
        headers1.set("Authorization",
                "APPCODE 50c05b8398e541fc81637176308d5061");
        ResponseEntity<JSONObject> exchange = restTemplate.exchange(
                "https://jisutqybmf.market.alicloudapi.com/weather/query?city=" + city
                , HttpMethod.GET, new HttpEntity<>(null, headers1), JSONObject.class);
        JSONObject body = exchange.getBody();
        //??????
        String weather = "????????????";
        //????????????
        String temp = "????????????";
        //????????????
        String temphigh = "????????????";
        //????????????
        String templow = "????????????";
        //????????????
        String ganmao = "????????????";
        //????????????
        String chuanyi = "????????????";

        if ("0".equals(body.get("status") + "")) {
            HashMap result = (HashMap) body.get("result");
            weather = (String) result.get("weather");
            temp = (String) result.get("temp");
            temphigh = (String) result.get("temphigh");
            templow = (String) result.get("templow");
            List<Map<String, String>> index = (List<Map<String, String>>) result.get("index");
            Map<String, String> ganmaoM = index.get(3);
            ganmao = ganmaoM.get("detail");
            Map<String, String> chuanyiM = index.get(6);
            chuanyi = chuanyiM.get("detail");
            //????????????
            JSONObject cityo = new JSONObject();
            cityo.put("value", result.get("city"));
            cityo.put("color", "#60AEF2");
            data.put("city", cityo);

//            ????????????
            weather = weather + ", ?????????" + templow + "?? ~ " + temphigh + "??,???????????????" + temp + "??";
            JSONObject temperatures = new JSONObject();
            temperatures.put("value", weather);
            temperatures.put("color", "#44B549");
            data.put("temperature", temperatures);
//            ??????????????????
            JSONObject chuanyiO = new JSONObject();
            chuanyiO.put("value", chuanyi);
            chuanyiO.put("color", toHexFromColor());
            data.put("chuanyi", chuanyiO);

//            ??????????????????
            JSONObject ganmaoO = new JSONObject();
            ganmaoO.put("value", ganmao);
            ganmaoO.put("color", toHexFromColor());
            data.put("ganmao", ganmaoO);
        }
    }

    /**
     * ?????????????????????????????????
     */
    private void addTemperature(JSONObject data) {
        String TemperatureUrl = "https://www.yiketianqi.com/free/day?appid=" +
                weatherAppId + "&appsecret=" + weatherAppSecret + "&unescape=1" + "&cityid=101210106";
        String sendGet = HttpUtil.sentMyGet(TemperatureUrl, null);
        JSONObject temperature = JSONObject.parseObject(sendGet);
        String address = "????????????";
        //????????????
        String tem_day = "????????????";
        //????????????
        String tem_night = "????????????";
        //????????????
        String tem = "????????????";
        String weatherStatus = "";
        if (temperature.getString("city") != null) {
            tem_day = temperature.getString("tem_day") + "??";
            tem_night = temperature.getString("tem_night") + "??";
            address = temperature.getString("city");
            tem = temperature.getString("tem");
            weatherStatus = temperature.getString("wea");
        }
        JSONObject city = new JSONObject();
        city.put("value", address);
        city.put("color", "#60AEF2");
        String weather = weatherStatus + ", ?????????" + tem_night + " ~ " + tem_day;
        JSONObject temperatures = new JSONObject();
        temperatures.put("value", weather);
        temperatures.put("color", "#44B549");
        data.put("city", city);
        data.put("temperature", temperatures);
    }

    /**
     * ????????????????????????
     */
    private void addDateInfo(JSONObject data) {
        JSONObject first = new JSONObject();
        String date = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        String week = DateUtil.getWeekOfDate(new Date());
        String day = date + " " + week;
        //??????????????????
        ChineseDate dateC = new ChineseDate(new Date());
        String chineseMonthName = dateC.getChineseMonthName();
        String chineseDay = dateC.getChineseDay();
        first.put("value", day + " " + chineseMonthName + chineseDay);
        first.put("color", "#EED016");
        data.put("first", first);
    }


    /**
     * ????????????????????????heshui
     */
    private void addSoftMsg(JSONObject data) {
        //????????????
        JSONObject heshuiObj = new JSONObject();
        List<Pram> prams = pramMapper.selectList(null);
        Pram pram = prams.get(0);

        heshuiObj.put("value", pram.getSoftmsg());
        //????????????
        heshuiObj.put("color", toHexFromColor());
        data.put("heshui", heshuiObj);
    }

    /**
     * ???????????????????????????
     */
    @GetMapping("/querySoft")
    public AjaxResult querySoftMsg() {
        List<Pram> prams = pramMapper.selectList(null);
        Pram pram = prams.get(0);
        return AjaxResult.success("????????????", pram.getSoftmsg());
    }

    /**
     * ???????????????????????????
     */
    @PostMapping("/modifySoft")
    public AjaxResult modifySoftMsg(@RequestBody Map<String, Object> msgMap) {
        String softMsg = (String) msgMap.get("softMsg");
        List<Pram> prams = pramMapper.selectList(null);
        Pram pram = prams.get(0);
        pram.setSoftmsg(softMsg);
        pramMapper.updateById(pram);
        return AjaxResult.success("????????????");
    }


    /**
     * ????????????????????????
     *
     * @return
     */
    private String getqinghua(long id) {
        String qinghua = "???";
        if (id == 0) {
            Page<Content> contentPage = new Page<>();
            contentPage.setCurrent(1);
            contentPage.setSize(1);
            LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Content::getHadSent, 0);
            queryWrapper.orderByAsc(Content::getId);
            Page<Content> contentPage1 = contentMapper.selectPage(contentPage, queryWrapper);
            if (contentPage1.getRecords() != null && contentPage1.getRecords().size() > 0) {
                Content content = contentPage1.getRecords().get(0);
                qinghua = contentPage1.getRecords().get(0).getContent();
                //????????????????????????????????????
                content.setHadSent(1);
                contentMapper.updateById(content);
            }
        } else {
            Content content = contentMapper.selectById(id);
            if (content != null) {
                qinghua = content.getContent();
                //????????????????????????????????????
                content.setHadSent(1);
                contentMapper.updateById(content);
            }
        }
        return qinghua;
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    private static String toHexFromColor() {
        Random random = new Random();
        final float hue = random.nextFloat();
        final float saturation = (random.nextInt(2000) + 1000) / 10000f;
        final float luminance = 0.9f;
        Color color = Color.getHSBColor(hue, saturation, luminance);
        String r, g, b;
        StringBuilder su = new StringBuilder();
        r = Integer.toHexString(color.getRed());
        g = Integer.toHexString(color.getGreen());
        b = Integer.toHexString(color.getBlue());
        r = r.length() == 1 ? "0" + r : r;
        g = g.length() == 1 ? "0" + g : g;
        b = b.length() == 1 ? "0" + b : b;
        r = r.toUpperCase();
        g = g.toUpperCase();
        b = b.toUpperCase();
        su.append("#");
        su.append(r);
        su.append(g);
        su.append(b);
        //0xFF0000FF
        return su.toString();
    }

    /**
     * ????????????
     *
     * @param id
     * @return
     */
    @GetMapping("send/{id}")
    private AjaxResult send(@PathVariable long id) {

        //????????????????????????????????????????????????????????????api
        String grant_type = "client_credential";
        //??????????????????
        String params = "grant_type=" + grant_type + "&secret=" + appSecret + "&appid=" + appId;
        //??????GET??????
        String sendGet = HttpUtil.sendGet("https://api.weixin.qq.com/cgi-bin/token", params);
        // ??????????????????????????????json?????????
        com.alibaba.fastjson.JSONObject jsonObject1 = com.alibaba.fastjson.JSONObject.parseObject(sendGet);
        logger.info("??????token????????????=" + jsonObject1);
        //??????accesstoken
        accessToken = (String) jsonObject1.get("access_token");
        sendWeChatMsg(accessToken, id);
        return AjaxResult.success("???????????????");
    }

    /**
     * ????????????????????????
     */
    @GetMapping("caihong")
    public AjaxResult getOneCaihongp() {
        String qinghua = HttpUtil.sentMyGet("https://api.shadiao.pro/chp", null);
        Object o = ((Map) JSON.parseObject(qinghua).get("data")).get("text");
        return AjaxResult.success("????????????", o);
    }

    /**
     * ??????????????????
     */
    @GetMapping("/queryPage")
    public AjaxResult queryPage(@RequestParam(defaultValue = "1") long currentPage,
                                @RequestParam(defaultValue = "10") long pageSize) {
        Page<Content> contentPage = new Page<>();
        contentPage.setCurrent(currentPage);
        contentPage.setSize(pageSize);
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();
        Page<Content> contentPage1 = contentMapper.selectPage(contentPage, queryWrapper);
        return AjaxResult.success("????????????", contentPage1);
    }

    /**
     * ??????????????????
     */
    @DeleteMapping("{id}")
    public AjaxResult del(@PathVariable long id) {
        contentMapper.deleteById(id);
        return AjaxResult.success("????????????");
    }

    /**
     * ??????????????????
     */
    @PostMapping
    public AjaxResult add(@RequestBody Content content) {
        contentMapper.insert(content);
        return AjaxResult.success("???????????????");
    }


}
