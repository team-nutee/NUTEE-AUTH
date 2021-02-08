package kr.nutee.auth.dto.response;

public class SuccessResponse extends Response{

    public SuccessResponse(Object body) {
        super(10, "SUCCESS", body);
    }
}
