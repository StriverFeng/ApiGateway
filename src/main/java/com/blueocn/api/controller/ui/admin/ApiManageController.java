package com.blueocn.api.controller.ui.admin;

import com.blueocn.api.controller.ui.AbstractUIController;
import com.blueocn.api.enums.MessageTypeEnum;
import com.blueocn.api.kong.model.Api;
import com.blueocn.api.service.ApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Title: ApiManageController
 * Description:
 *
 * @author Yufan
 * @version 1.0.0
 * @since 2016-01-29 12:08
 */
@Controller
@RequestMapping("admin/api")
public class ApiManageController extends AbstractUIController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiManageController.class);

    @Autowired
    private ApiService apiService;

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String listApi() {
        return "admin/api/list";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String addApi() {
        return "admin/api/edit";
    }

    @RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
    public String editApi(@PathVariable(value = "id") String id, Model model) {
        Api api = apiService.query(id);
        if (api != null && isNotBlank(api.getId())) {
            model.addAttribute("api", api);
        } else {
            setMessage(model, MessageTypeEnum.ERROR, "未找到此 ID 对应的 API 信息, 请于 API 列表页查看是否该 API 已被删除, 或者直接刷新页面.");
            LOGGER.info("此 API 不存在, API ID {}", id);
        }
        return "admin/api/edit";
    }
}
