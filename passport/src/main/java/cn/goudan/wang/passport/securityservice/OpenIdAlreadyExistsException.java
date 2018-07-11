package cn.goudan.wang.passport.securityservice;

/**
 * Created by momo on 2017/5/2.
 */
public class OpenIdAlreadyExistsException extends RuntimeException {

    public OpenIdAlreadyExistsException(String message) {
        super(message);
    }

    public OpenIdAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
