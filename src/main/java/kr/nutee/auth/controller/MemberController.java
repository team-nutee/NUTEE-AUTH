package kr.nutee.auth.controller;

import java.util.List;
import kr.nutee.auth.dto.request.ChangeInterestsRequest;
import kr.nutee.auth.dto.request.ChangeMajorsRequest;
import kr.nutee.auth.dto.request.ChangeNicknameRequest;
import kr.nutee.auth.dto.request.ChangePasswordRequest;
import kr.nutee.auth.dto.request.ChangeProfileRequest;
import kr.nutee.auth.dto.resource.ResponseResource;
import kr.nutee.auth.dto.response.Response;
import kr.nutee.auth.dto.response.SuccessResponse;
import kr.nutee.auth.dto.response.UserData;
import kr.nutee.auth.domain.Member;
import kr.nutee.auth.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    /*
        나의 데이터 호출
     */
    @GetMapping("/me")
    public ResponseEntity<ResponseResource> getMyUserData(
        HttpServletRequest request
    ) {
        Member user = memberService.getUserBy(request);
        UserData userData = new UserData(user);
        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(userData)
            .build();
        ResponseResource resource = new ResponseResource(response, MemberController.class, "/me");
        return ResponseEntity.ok().body(resource);
    }

    /*
        유저 데이터 호출
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<ResponseResource> getUserData(
        HttpServletRequest request,
        @PathVariable Long memberId
    ) {
        UserData userData = new UserData(memberService.getUserBy(request));
        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(userData)
            .build();
        ResponseResource resource = new ResponseResource(response, MemberController.class,
            "/user/" + memberId);
        return ResponseEntity.ok().body(resource);
    }

    /*
        유저 닉네임 변경
     */
    @PatchMapping("/nickname")
    public ResponseEntity<ResponseResource> changeNickname(
        HttpServletRequest request,
        @RequestBody ChangeNicknameRequest body
    ) {
        Member user = memberService.getUserBy(request);
        String changedNickname = memberService.changeNickname(user, body.getNickname());
        Response response = new SuccessResponse(changedNickname);
        ResponseResource resource = new ResponseResource(response,MemberController.class, "/nickname");
        return ResponseEntity.ok().body(resource);
    }

    /*
        유저 비밀번호 변경
     */
    @PatchMapping("/password")
    public ResponseEntity<ResponseResource> changePassword(
        @RequestBody ChangePasswordRequest body,
        HttpServletRequest request
    ) {
        Member user = memberService.getUserBy(request);
        memberService.changePassword(user.getId(),body);
        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(null)
            .build();
        ResponseResource resource = new ResponseResource(response, MemberController.class,
            user.getId() + "/password");
        return ResponseEntity.ok().body(resource);
    }

    /*
        유저 프로필 이미지 변경
     */
    @PatchMapping("/profile")
    public ResponseEntity<ResponseResource> changeProfile(
        @RequestBody ChangeProfileRequest body,
        HttpServletRequest request
    ) {
        Member user = memberService.getUserBy(request);
        String newProfileUrl = memberService.changeProfile(user, body.getProfileUrl());
        Response response = new SuccessResponse(newProfileUrl);
        ResponseResource resource = new ResponseResource(response, MemberController.class, "/profile");
        return ResponseEntity.ok().body(resource);
    }

    /*
        유저 흥미 목록 변경
     */
    @PatchMapping("/interests")
    public ResponseEntity<ResponseResource> changeInterests(
        @RequestBody ChangeInterestsRequest body,
        HttpServletRequest request
    ) {
        Member user = memberService.getUserBy(request);
        List<String> newInterests = memberService.changeInterests(user,body.getInterests());
        Response response = new SuccessResponse(newInterests);
        ResponseResource resource = new ResponseResource(response, MemberController.class, "/interests");
        return ResponseEntity.ok().body(resource);
    }

    /*
        유저 전공 변경
     */
    @PatchMapping("/majors")
    public ResponseEntity<ResponseResource> changeMajors(
        @RequestBody ChangeMajorsRequest body,
        HttpServletRequest request
    ) {
        Member user = memberService.getUserBy(request);
        List<String> newMajors = memberService.changeMajors(user,body.getMajors());
        Response response = new SuccessResponse(newMajors);
        ResponseResource resource = new ResponseResource(response, MemberController.class, "/majors");
        return ResponseEntity.ok().body(resource);
    }

}
