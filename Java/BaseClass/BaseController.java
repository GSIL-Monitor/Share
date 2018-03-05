package com.beta.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.beta.domain.dao.generated.entity.UserInfoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.beta.api.common.ApiConfig;
import com.beta.api.common.Response;
import com.beta.api.common.ResponseGenerator;
import com.beta.api.dto.LoginUser;
import com.beta.api.exception.ApiErrorEnum;
import com.beta.api.exception.ApiException;
import com.beta.api.exception.ApiInternalException;
import com.beta.api.helper.CookieHelper;
import com.beta.api.helper.JwtHelper;
import com.beta.api.helper.RequestHelper;
import com.beta.core.BetaProperties;
import com.beta.core.crypto.DesHelper;
import com.beta.domain.enums.LoginChannel;
import com.beta.domain.helper.MyCatPlugin;
import com.beta.utils.mapper.JacksonUtil;
import com.google.common.base.Strings;

/**
 * <br>
 * 与当前登陆相关的接口需要加上 @AuthPassport 注解， controller 需要继承 BaseController ，通过调用
 * getCurrentUser 获取当前登陆用户
 * 
 * @author sucre
 *
 */
public abstract class BaseController {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());

    static final int SECOND_7_DAYS = 7 * 24 * 3600;

    @Autowired
    private BetaProperties betaProperties;

    public void assertInnerIp() throws ApiException {
        boolean isInnerIp = RequestHelper.fromInnerIp(getCurrentRequest());
        if (!isInnerIp) {
            this.throwError(ApiErrorEnum.Request.Unauthorized);
        }
    }

    public void saveLoginCookie(HttpServletRequest request, HttpServletResponse response, LoginUser loginUser)
            throws Exception {

        int expire = SECOND_7_DAYS;
        //第一次生成jwtLoginToken
        String jwtLoginToken = JwtHelper.encode(betaProperties.getSecurity().getLoginSecret(),
                JacksonUtil.toMap(loginUser));
        CookieHelper.addCookieByDomain(request, response, ApiConfig.ACCESS_TOKEN, jwtLoginToken, expire);

        String accessUserId = DesHelper.encrypt(loginUser.getUserId().toString(), ApiConfig.ENCRYPT_USER_ID_KEY);
        CookieHelper.addCookieByDomain(request, response, ApiConfig.ACCESS_USER_ID, accessUserId, expire);

        //扩展记录来自那个系统
        String userFrom = loginUser.getFrom().toString();
        CookieHelper.addCookieByDomain(request, response, ApiConfig.USER_FROM_SYS, userFrom, expire);


        //MyCatPlugin.schemaLocal.set(LoginChannel.fromValue(loginUser.getChannelId()));
    }

    /**
     * 保存理财经理登录cookie
     * @param request
     * @param response
     * @param loginUser
     * @throws Exception
     */
    public void saveManagerLoginCookie(HttpServletRequest request, HttpServletResponse response, LoginUser loginUser)
            throws Exception {

        int expire = SECOND_7_DAYS;
        //第一次生成jwtLoginToken
        String jwtLoginToken = JwtHelper.encode(betaProperties.getSecurity().getLoginSecret(),
                JacksonUtil.toMap(loginUser));
        CookieHelper.addCookieByDomain(request, response, ApiConfig.ACCESS_TOKEN, jwtLoginToken, expire);

        String accessUserId = DesHelper.encrypt(loginUser.getUserId().toString(), ApiConfig.ENCRYPT_USER_ID_KEY);
        CookieHelper.addCookieByDomain(request, response, ApiConfig.ACCESS_USER_ID, accessUserId, expire);
         /**/
        //扩展记录来自那个系统
        String userFrom = loginUser.getFrom().toString();
        CookieHelper.addCookieByDomain(request, response, ApiConfig.USER_FROM_SYS, userFrom, expire);


        //MyCatPlugin.schemaLocal.set(LoginChannel.fromValue(loginUser.getChannelId()));
    }

    /**
     * 获取当前理财经理登录信息
     * @return
     */
    public LoginUser getCurrentManagerUser() {
        logger.info("获取当前理财经理的信息：");
        HttpServletRequest request = this.getCurrentRequest();
        String jwt = CookieHelper.getCookieValueByName(request, ApiConfig.ACCESS_TOKEN);
        logger.info("jwt:"+jwt);
        if(null!=jwt&&""!=jwt){
            Map<String, Object> currentUserMap = JwtHelper.decode(betaProperties.getSecurity().getLoginSecret(), jwt);
            LoginUser loginUser = JacksonUtil.fromMapToObj(currentUserMap, LoginUser.class);
            logger.info("登录理财经理的id:"+loginUser.getUserId());
            return loginUser;
        }else{
            logger.info("登录理财经理的id:为null");
            return null;
        }

    }

    /**
     * 获取当前登陆用户
     * 
     * @return
     */
    public LoginUser getCurrentUser() {
        HttpServletRequest request = this.getCurrentRequest();
        String jwt = CookieHelper.getCookieValueByName(request, ApiConfig.ACCESS_TOKEN);
        if(null!=jwt&&""!=jwt){
        	Map<String, Object> currentUserMap = JwtHelper.decode(betaProperties.getSecurity().getLoginSecret(), jwt);
        	LoginUser loginUser = JacksonUtil.fromMapToObj(currentUserMap, LoginUser.class);
        	return loginUser;
        }else{
        	return null;
        }

    }



    /**
     * 初始化理财经理登录信息
     * @param userInfo
     * @return
     */
    public  LoginUser initManagerLoginUser(UserInfoEntity userInfo){
        LoginUser loginUser = new LoginUser();
        Integer channelId = userInfo.getChannelId();
        String nickName = userInfo.getNickName();
        Integer gender = userInfo.getGender();
        String avatar = userInfo.getAvatar();
        loginUser.setUserId(userInfo.getId());
        loginUser.setAvatar(avatar);
        loginUser.setChannelId(channelId);
        loginUser.setNickName(nickName);
        loginUser.setGender(gender);
        loginUser.setFrom(2);
        return loginUser;
    };

    /**
     * 
     * @return
     */

    public HttpServletRequest getCurrentRequest() {
        return RequestHelper.getCurrentRequst();
    }

    @SuppressWarnings("rawtypes")
    protected Response success() {
        return ResponseGenerator.genResult();
    }

    protected <T> Response<T> success(T data) {
        return ResponseGenerator.genResult(data);
    }

    protected <T> Response<T> success(String message, T data) {
        return ResponseGenerator.genResult(message, data);
    }

    protected void throwError(ApiErrorEnum errorEnum) throws ApiException {
        if (errorEnum.getClass() == ApiErrorEnum.Internal.class) {
            throw new ApiInternalException(errorEnum.getCode(), errorEnum.getMessage());
        } else {
            throw new ApiException(errorEnum.getCode(), errorEnum.getMessage());
        }
    }

    protected void throwError(ApiErrorEnum errorEnum, Object data) throws ApiException {
        if (errorEnum.getClass() == ApiErrorEnum.Internal.class) {
            throw new ApiInternalException(errorEnum.getCode(), errorEnum.getMessage(), data);
        } else {
            throw new ApiException(errorEnum.getCode(), errorEnum.getMessage(), data);
        }
    }

    protected void throwError(ApiErrorEnum errorEnum, String message) throws ApiException {
        if (errorEnum.getClass() == ApiErrorEnum.Internal.class) {
            throw new ApiInternalException(errorEnum.getCode(), message);
        } else {
            throw new ApiException(errorEnum.getCode(), message);
        }
    }

    protected void throwError(ApiErrorEnum errorEnum, String message, Object data) throws ApiException {
        if (errorEnum.getClass() == ApiErrorEnum.Internal.class) {
            throw new ApiInternalException(errorEnum.getCode(), message, data);
        } else {
            throw new ApiException(errorEnum.getCode(), message, data);
        }
    }

    /**
     * @param errorCode
     *            建议为负数如：-100
     * @param message
     *            错误信息
     * @throws ApiException
     */
    protected void throwError(int errorCode, String message) throws ApiException {
        throw new ApiException(errorCode, message);
    }

    /**
     * @param errorCode
     *            建议为负数如：-100
     * @param message
     *            错误信息
     * @param data
     *            错误附加信息
     * @throws ApiException
     */
    protected void throwError(int errorCode, String message, Object data) throws ApiException {
        throw new ApiException(errorCode, message, data);
    }

    // region util

    /**
     * 将逗号分隔字符串转换成Long of List.
     */
    protected List<Long> longsFromString(String str) {

        if (Strings.isNullOrEmpty(str)) {
            return new ArrayList<>();
        }

        final String[] split = str.split(",");

        return Arrays.stream(split).map(Long::valueOf).collect(Collectors.toList());
    }

    /**
     * 将逗号分隔字符串转换成Integer of List.
     */
    protected List<Integer> integersFromString(String idStr) {

        if (Strings.isNullOrEmpty(idStr)) {
            return new ArrayList<>();
        }

        final String[] split = idStr.split(",");

        return Arrays.stream(split).map(Integer::valueOf).collect(Collectors.toList());
    }

    /**
     * 返回已逗号(,)为delimiter的字符串.
     *
     * @return null,"",或者,号分隔字符串.
     */
    protected String stringFormList(List<?> list) {
        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return "";
        }

        return list.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    protected void removeLoginCookie(HttpServletRequest request, HttpServletResponse response) {

        String hostName = RequestHelper.getMainHostName(request);

        CookieHelper.deleteCookieByNameAndDomain(response, hostName, ApiConfig.ACCESS_TOKEN);
    }


}
