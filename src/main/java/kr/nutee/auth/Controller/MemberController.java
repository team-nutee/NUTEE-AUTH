package kr.nutee.auth.Controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import kr.nutee.auth.DTO.Request.ChangePasswordRequest;
import kr.nutee.auth.DTO.Resource.ResponseResource;
import kr.nutee.auth.DTO.Response.Response;
import kr.nutee.auth.Domain.Member;
import kr.nutee.auth.Enum.ErrorCode;
import kr.nutee.auth.Exception.ConflictException;
import kr.nutee.auth.Service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/auth/user", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@ResponseBody
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PatchMapping("/{memberId}/password")
    public ResponseEntity<ResponseResource> changePassword(
            @RequestBody ChangePasswordRequest body,
            HttpServletRequest request,
            @PathVariable String memberId
    ) {
        Member user = memberService.getUser(request);
        memberService.changePassword(user.getId(),Long.parseLong(memberId),body.getPassword());
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .build();
        ResponseResource resource = new ResponseResource(response, MemberController.class,user.getId()+"/password");
        return ResponseEntity.ok().body(resource);
    }

}
