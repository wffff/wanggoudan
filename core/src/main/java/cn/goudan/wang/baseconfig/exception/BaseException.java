package cn.goudan.wang.baseconfig.exception;

import cn.goudan.wang.baseconfig.ReturnMessage;
import cn.goudan.wang.baseconfig.constant.SystemConstants;
import com.alibaba.fastjson.JSON;

/**
 * Created by Adam.yao on 2017/10/25.
 */
public class BaseException extends RuntimeException {

    public BaseException(ErrorCode errorCode) {
        super(createFriendlyErrMsg(errorCode.des, errorCode.code));
    }

    public BaseException(String errorMsg, Number errorCode) {
        super(createFriendlyErrMsg(errorMsg, errorCode));
    }
    public BaseException(String errorMsg) {
        super(errorMsg);
    }

    private static String createFriendlyErrMsg(String msgBody, Number errorCode) {
        ReturnMessage<String> returnMessage = ReturnMessage.message(SystemConstants.FAILED, errorCode.toString(), msgBody);
        return JSON.toJSONString(returnMessage);
    }
}