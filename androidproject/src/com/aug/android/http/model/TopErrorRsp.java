
package com.aug.android.http.model;

import com.aug.android.http.utils.LogUtils;

import android.text.TextUtils;

public class TopErrorRsp extends HttpModel {
    private static final String INVALID_TOKEN_ERROR_CODE = "27";
    private static final String INVALID_TOKEN_ERROR_MSG = "Invalid session";
    private static final String SECURITY_ERROR_CODE = "53";

    public static final String TOP_RESPONSE_BASE_TAG_ERROR_RESPONSE = "error_response";

    private static final TopErrorRsp s_errorResp = new TopErrorRsp();
    static {
        s_errorResp.error_response = new TopErrorRspMsg();
        s_errorResp.error_response.code = INVALID_TOKEN_ERROR_CODE;
        s_errorResp.error_response.msg = INVALID_TOKEN_ERROR_MSG;
    }

    private TopErrorRspMsg error_response;

    /**
     * 获取标准top错误结构体
     */
    public static TopErrorRsp makeInvalidSessionRsp() {
        return s_errorResp;
    }

    // TOP返回的错误码
    private String getTopErrCode() {
        return error_response == null ? "" : changeResultToInvalidSesson(error_response.getCode());
    }

    // TOP返回的错误描述
    private String getTopErrMsg() {
        return error_response == null ? "" : error_response.getMsg();
    }

    public boolean isTopSuccess() {
        return error_response == null;
    }

    public boolean isAuthInvalide() {
        boolean isAuthFail = !isTopSuccess() && !TextUtils.isEmpty(getTopErrCode())
                && getTopErrCode().equals(INVALID_TOKEN_ERROR_CODE);
        if (isAuthFail) {
            LogUtils.v("授权失败或者过期");
        }
        return isAuthFail;
    }

    // TOP返回的业务错误码
    private String getTopRspSubErrCode() {
        return error_response == null ? "" : error_response.getSubCode();
    }

    // TOP返回的业务错误描述
    private String getTopRspSubErrMsg() {
        return error_response == null ? "" : error_response.getSubMsg();
    }

    /**
     * 获取错误码，业务逻辑错误优先
     */
    public String getRspErrCode() {
        String errCode = getTopRspSubErrCode();
        if (TextUtils.isEmpty(errCode)) {
            errCode = getTopErrCode();
        }
        return errCode;
    }

    /**
     * 获取错误描述，业务逻辑错误优先
     */
    public String getRspErrMsg() {
        String errMsg = getTopRspSubErrMsg();
        if (TextUtils.isEmpty(errMsg)) {
            errMsg = getTopErrMsg();
        }
        return errMsg;
    }

    private String changeResultToInvalidSesson(String errCode) {
        if (TextUtils.isEmpty(errCode)) {
            return null;
        }

        // 添加需要转化的错误码
        if (errCode.equals(SECURITY_ERROR_CODE)) {
            errCode = INVALID_TOKEN_ERROR_CODE;
        }

        return errCode;
    }

    /**
     * 解析完主体结构后，是否需要后续处理。如果需要，就重写这个方法。默认不需要后续处理，直接返回this
     * @return
     */
    public Object onPostParse() {
        return this;
    }
    
    private static class TopErrorRspMsg {

        private String code;

        private String msg;

        private String sub_code;

        private String sub_msg;

        public String getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public String getSubCode() {
            return sub_code;
        }

        public String getSubMsg() {
            return sub_msg;
        }
    }

}
