package com.lsd.fun.modules.cms.service.impl;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.common.utils.R;
import com.lsd.fun.config.baidu_map.BaiduMapProperties;
import com.lsd.fun.modules.cms.dto.BaiduMapLocation;
import com.lsd.fun.modules.cms.service.BaiduLBSService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by lsd
 * 2020-02-14 11:40
 */
@Slf4j
@Service
public class BaiduLBSServiceImpl implements BaiduLBSService {

    @Autowired
    private BaiduMapProperties baiduMapProperties;
    @Autowired
    private HttpClient httpClient;
    @Autowired
    private Gson gson;

    @Override
    public BaiduMapLocation parseAddress2Location(String address) {
        // 把要传的请求参数编码
        String encodeAddress;
        try {
            encodeAddress = URLEncoder.encode(address, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RRException("URLEncoder编码出错", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
        String url = String.format("%s?address=%s&output=json&ak=%s",
                baiduMapProperties.getGeocoderApiPrefix(), encodeAddress, baiduMapProperties.getApiKey());
        // 发起Http请求调用百度地图API
        try {
            final HttpResponse response = httpClient.execute(new HttpGet(url));
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RRException("百度地图地理编码服务Web API接口调用失败", HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }
            String resultStr = EntityUtils.toString(response.getEntity(), "UTF-8");
            // 解析为json结构
            JsonObject resultJson = gson.fromJson(resultStr, JsonObject.class);
            int status = resultJson.get("status").getAsInt();
            if (status != 0) {
                log.error("百度地图地理编码服务地址解析错误，status = {}，address = {}", status, address);
                throw new RRException("百度地图地理编码服务地址解析错误", HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }
            JsonObject locationJson = resultJson.getAsJsonObject("result").getAsJsonObject("location");
            return new BaiduMapLocation(locationJson.get("lng").getAsDouble(), locationJson.get("lat").getAsDouble());
        } catch (IOException e) {
            log.error("百度地图地理编码服务Web API接口调用失败，address = " + address, e);
            throw new RRException("百度地图地理编码服务Web API接口调用失败", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void upload(BaiduMapLocation location, String title, String address, Integer houseId, int price, int area) {
        // 封装表单参数
        List<NameValuePair> nvps = Lists.newArrayList(
                new BasicNameValuePair("ak", baiduMapProperties.getApiKey()),
                new BasicNameValuePair("geotable_id", baiduMapProperties.getPoi().getGeotableId()),
                new BasicNameValuePair("latitude", String.valueOf(location.getLatitude())),
                new BasicNameValuePair("longitude", String.valueOf(location.getLongitude())),
                new BasicNameValuePair("coord_type", "3"), // 类型为百度加密经纬度坐标
                new BasicNameValuePair("title", title),
                new BasicNameValuePair("houseId", String.valueOf(houseId)),
                new BasicNameValuePair("price", String.valueOf(price)),
                new BasicNameValuePair("area", String.valueOf(area)),
                new BasicNameValuePair("address", address)
        );
        // 先根据houseId查询
        JsonArray poisJson = this.query(houseId);
        // 已存在则更新，否则创建
        HttpPost post;
        if (poisJson.size() == 0) {
            post = new HttpPost(baiduMapProperties.getPoi().getCreateUrl());
        } else {
            post = new HttpPost(baiduMapProperties.getPoi().getUpdateUrl());
            // 更新POI数据需携带id
            String id = poisJson.get(0).getAsJsonObject().get("id").getAsString();
            nvps.add(new BasicNameValuePair("id", id));
        }
        try {
            // 表单参数编码
            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            HttpResponse response = httpClient.execute(post);
            String resultStr = EntityUtils.toString(response.getEntity(), "UTF-8");
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("poi数据上传失败，响应结果：{}", resultStr);
                throw new RRException("poi数据上传失败", HttpStatus.SC_INTERNAL_SERVER_ERROR);
            } else {
                final JsonObject resultJson = gson.fromJson(resultStr, JsonObject.class);
                int status = resultJson.get("status").getAsInt();
                if (status != 0) {
                    String message = resultJson.get("message").getAsString();
                    log.error("poi数据上传失败，status = {}, and message = {}", status, message);
                    throw new RRException("poi数据上传失败", HttpStatus.SC_INTERNAL_SERVER_ERROR);
                }
            }
        } catch (Exception e) {
            log.error("poi数据上传失败", e);
            throw new RRException("poi数据上传失败", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
        log.debug("poi数据上传成功，houseId = {}", houseId);
    }

    /**
     * 根据houseId查询POI数据
     *
     * @return pois结果集
     */
    private JsonArray query(Integer houseId) {
        String url = new StringBuilder(baiduMapProperties.getPoi().getQueryUrl())
                .append("?geotable_id=").append(baiduMapProperties.getPoi().getGeotableId())
                .append("&ak=").append(baiduMapProperties.getApiKey())
                .append("&houseId=").append(houseId).append(",").append(houseId) //int数据类型字段是范围查询,传递的格式为{columnName}={最小值},{最大值}，详见文档说明
                .toString();
        try {
            HttpResponse response = httpClient.execute(new HttpGet(url));
            String resultStr = EntityUtils.toString(response.getEntity(), "UTF-8");
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("请求查询指定条件的poi数据列表接口失败，响应结果：{}", resultStr);
                return new JsonArray();
            }
            final JsonObject resultJson = gson.fromJson(resultStr, JsonObject.class);
            // LBS云不存在此POI数据
            if (resultJson.get("size").isJsonNull() || resultJson.get("size").getAsInt() == 0) {
                return new JsonArray();
            }
            return resultJson.get("pois").getAsJsonArray();
        } catch (Exception e) {
            log.error("请求查询指定条件的poi数据列表接口失败", e);
            return new JsonArray();
        }
    }

    @Override
    public void remove(Integer houseId) {
        // 先根据houseId查询
        JsonArray poisJson = this.query(houseId);
        if (poisJson.size() == 0) {
            return;
        }
        // 删除单条POI数据需携带id
        String id = poisJson.get(0).getAsJsonObject().get("id").getAsString();
        List<NameValuePair> nvps = Lists.newArrayList(
                new BasicNameValuePair("geotable_id", baiduMapProperties.getPoi().getGeotableId()),
                new BasicNameValuePair("ak", baiduMapProperties.getApiKey()),
                new BasicNameValuePair("id", id)
        );
        try {
            HttpPost delete = new HttpPost(baiduMapProperties.getPoi().getDeleteUrl());
            delete.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            HttpResponse response = httpClient.execute(delete);
            String resultStr = EntityUtils.toString(response.getEntity(), "UTF-8");
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.error("请求删除poi数据接口失败，响应结果：{}", resultStr);
                throw new RRException("请求删除poi数据接口失败", HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }
            final JsonObject resultJson = gson.fromJson(resultStr, JsonObject.class);
            int status = resultJson.get("status").getAsInt();
            if (status != 0) {
                String message = resultJson.get("message").getAsString();
                log.error("请求删除poi数据接口失败，message = {}", message);
                throw new RRException("请求删除poi数据接口失败", HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("请求删除poi数据接口失败", e);
            throw new RRException("请求删除poi数据接口失败", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
        log.debug("poi数据删除成功，houseId = {}", houseId);
    }

}
