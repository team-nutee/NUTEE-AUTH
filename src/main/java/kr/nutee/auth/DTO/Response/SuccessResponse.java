package kr.nutee.auth.DTO.Response;

public class SuccessResponse extends Response{

    public SuccessResponse(Object body) {
        super(10, "SUCCESS", body);
    }
}
