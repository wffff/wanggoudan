package cn.goudan.wang.passport.securityservice;

/**
 * Created by momo on 2017/5/2.
 */
public class NoSuchOpenIdException extends RuntimeException {

    public NoSuchOpenIdException(String message) {
        super(message);
    }

    public NoSuchOpenIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
