package cn.ntboy.mhttpd.protocol.http;

import lombok.Getter;

public enum  HTTPStatusCode {
    CONTINUE(100,"Continue"),
    OK(200,"OK"),
    REDIRECT(302,"Redirect"),
    NOT_MODIFIED(304, "Not Modified"),
    UNAUTHORIZED(401	,"Unauthorized"),
    NOT_FOUND(404,"Not Found"),
    METHOD_NOT_ALLOWED(405	,"Method Not Allowed"),
    INTERNAL_SERVER_ERROR(500 ,"	Internal Server Error");

    @Getter
    private Integer code;
    @Getter
    private String name;


    HTTPStatusCode(int code, String name) {
        this.code = code;
        this.name = name;
    }
    public static HTTPStatusCode get(int code) throws Exception {
        HTTPStatusCode[] values = HTTPStatusCode.values();
        for(HTTPStatusCode statusCode:values){
            if(statusCode.code ==code){
                return statusCode;
            }
        }
        throw new Exception("不支持的http状态码."+code);
    }
}
