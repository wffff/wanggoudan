package com.goudan.wang.baseconfig;

import java.io.Serializable;

/**
 * 统一返回数据封装
 *
 * @param <T>
 */
public class ReturnMessage<T> implements Serializable {

    /**
     * code -1表示失败，大于等于0表示成功
     */
    private int code;
    /**
     * msg 文本描述信息
     */
    private String msg;

    /**
     * 分页总记录数
     */
    private int count;
    /**
     * data 返回数据信息
     */
    private T data;

    public ReturnMessage(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ReturnMessage(int code, String msg, int count, T data) {
        this.code = code;
        this.msg = msg;
        this.count = count;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <V> ReturnMessage<V> message(int code, String msg, V data) {
        return new ReturnMessage<>(code, msg, data);
    }

    public static <V> ReturnMessage<V> message(int code, String msg, int count, V data) {
        return new ReturnMessage<>(code, msg, count, data);
    }

    public static <V> ReturnMessage<V> success(String msg, V data) {
        return new ReturnMessage<>(SystemConstants.SUCCESS, msg, data);
    }

    public static <V> ReturnMessage<V> success(int count, V data) {
        return new ReturnMessage<>(SystemConstants.SUCCESS, "", count, data);
    }

    public static ReturnMessage success(String msg) {
        return new ReturnMessage<>(SystemConstants.SUCCESS, msg, null);
    }

    public static <V> ReturnMessage<V> success() {
        return new ReturnMessage<>(SystemConstants.SUCCESS, "", null);
    }

    public static <V> ReturnMessage<V> failed(String msg, V data) {
        return new ReturnMessage<>(SystemConstants.FAILED, msg, data);
    }

    public static <V> ReturnMessage<V> failed(V data) {
        return new ReturnMessage<>(SystemConstants.FAILED, "", data);
    }

    public static ReturnMessage failed(String msg) {
        return new ReturnMessage<>(SystemConstants.FAILED, msg, null);
    }

    public static <V> ReturnMessage<V> failed() {
        return new ReturnMessage<>(SystemConstants.FAILED, "", null);
    }
}