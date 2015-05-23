
package com.aug.android.http.model;

import android.text.TextUtils;

import com.aug.android.http.utils.ALog;
import com.google.gson.annotations.SerializedName;

/**
 * @author <a href="mailto:lujian.lj@alibaba-inc.com">鲁建</a>
 * @version 2014年8月20日上午11:28:34
 */
public class TopErrorRsp extends HttpModel {
    public static final String INVALID_TOKEN_ERROR_CODE = "27";
    public static final String INVALID_TOKEN_ERROR_MSG = "Invalid session";
    public static final String SECURITY_ERROR_CODE = "53";

    public static final String TOP_RESPONSE_BASE_TAG_ERROR_RESPONSE = "error_response";
    protected static final String TOP_RESPONSE_BASE_TAG_CODE = "code";
    protected static final String TOP_RESPONSE_BASE_TAG_MSG = "msg";
    protected static final String TOP_RESPONSE_BASE_TAG_SUB_CODE = "sub_code";
    protected static final String TOP_RESPONSE_BASE_TAG_SUB_MSG = "sub_msg";

    private static final TopErrorRsp s_errorResp = new TopErrorRsp();
    static {
        s_errorResp.errorMsg = new TopErrorRspMsg();
        s_errorResp.errorMsg.topRspErrCode = INVALID_TOKEN_ERROR_CODE;
        s_errorResp.errorMsg.topRspErrMsg = INVALID_TOKEN_ERROR_MSG;
    }

    @SerializedName(TOP_RESPONSE_BASE_TAG_ERROR_RESPONSE)
    private TopErrorRspMsg errorMsg;

    public static TopErrorRsp makeInvalidSessionRsp() {
        return s_errorResp;
    }

    public TopErrorRspMsg getErrorMsg() {
        return errorMsg;
    }

    public String getTopErrCode() {
        return errorMsg == null ? "" : changeResultToInvalidSesson(errorMsg.getTopRspErrCode());
    }

    public String getTopErrMsg() {
        return errorMsg == null ? "" : errorMsg.getTopRspErrMsg();
    }

    public boolean isTopSuccess() {
        return errorMsg == null;
    }

    public boolean isAuthInvalide() {
        boolean isAuthFail = !isTopSuccess() && !TextUtils.isEmpty(getTopErrCode())
                && getTopErrCode().equals(INVALID_TOKEN_ERROR_CODE);
        if (isAuthFail) {
            ALog.v("授权失败或者过期");
        }
        return isAuthFail;
    }

    public String getTopRspSubErrCode() {
        return errorMsg == null ? "" : errorMsg.getTopRspSubErrCode();
    }

    public String getTopRspSubErrMsg() {
        return errorMsg == null ? "" : errorMsg.getTopRspSubErrMsg();
    }

    public String getRspErrCode() {
        String errCode = getTopRspSubErrCode();
        if (TextUtils.isEmpty(errCode)) {
            errCode = getTopErrCode();
        }
        return errCode;
    }

    public String getRspErrMsg() {
        String errMsg = getTopErrMsg();
        if (TextUtils.isEmpty(errMsg)) {
            errMsg = getTopRspSubErrMsg();
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
    
    public static class TopErrorRspMsg {

        @SerializedName(TOP_RESPONSE_BASE_TAG_CODE)
        private String topRspErrCode;

        @SerializedName(TOP_RESPONSE_BASE_TAG_MSG)
        private String topRspErrMsg;

        @SerializedName(TOP_RESPONSE_BASE_TAG_SUB_CODE)
        private String topRspSubErrCode;

        @SerializedName(TOP_RESPONSE_BASE_TAG_SUB_MSG)
        private String topRspSubErrMsg;

        public String getTopRspErrCode() {
            return topRspErrCode;
        }

        public String getTopRspErrMsg() {
            return topRspErrMsg;
        }

        public String getTopRspSubErrCode() {
            return topRspSubErrCode;
        }

        public String getTopRspSubErrMsg() {
            return topRspSubErrMsg;
        }
    }

}
