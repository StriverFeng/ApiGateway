package com.blueocn.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.blueocn.api.kong.client.ApiClient;
import com.blueocn.api.kong.model.Api;
import com.blueocn.api.response.RestfulResponse;
import com.blueocn.api.service.ApiService;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Title: ApiServiceImpl
 * Description:
 *
 * @author Yufan
 * @version 1.0.0
 * @since 2016-02-19 10:49
 */
@Service
public class ApiServiceImpl implements ApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiServiceImpl.class);

    @Autowired
    private ApiClient client;

    @Override
    public Api query(String apiId) {
        try {
            Api api = client.queryOne(apiId);
            LOGGER.debug("API的信息: {}", JSON.toJSONString(api));
            return api;
        } catch (IOException e) {
            LOGGER.warn("", e);
        }
        return null;
    }

    @Override
    public RestfulResponse save(Api api) {
        Preconditions.checkNotNull(api, "待保存的 API 信息不能为空");
        Api newApi;
        try {
            if (isBlank(api.getId())) {
                newApi = client.add(api);
            } else {
                newApi = client.update(api);
            }
        } catch (IOException e) {
            LOGGER.info("", e);
            return new RestfulResponse(e.getMessage());
        }
        if (newApi != null && isBlank(newApi.getErrorMessage())) {
            return new RestfulResponse();
        }
        return new RestfulResponse(newApi == null ? "保存失败" : newApi.getErrorMessage());
    }

    @Override
    public List<Api> queryAll(Api api) {
        try {
            Api queryApi = api == null ? new Api() : api;
            queryApi.setSize(getApiAmount());
            return client.query(queryApi);
        } catch (IOException e) {
            LOGGER.info("", e);
        }
        return Lists.newArrayList(); // Never return null
    }

    @Override
    public void delete(String apiId) {
        try {
            client.delete(apiId);
        } catch (IOException e) {
            LOGGER.info("", e);
        }
    }

    @Override
    public boolean isApiNameExist(String apiName) {
        Api api = query(apiName);
        return api != null && api.getId() != null;
    }

    /**
     * 查询 Kong 上面的 API 数量, 如果查询失败或者没有 API, 则会返回 null.
     *
     * @return API 数量
     */
    private Integer getApiAmount() {
        try {
            Api api = new Api();
            api.setSize(1);
            return client.totalSize(api);
        } catch (IOException e) {
            LOGGER.info("", e);
        }
        return null;
    }
}
