package kr.nutee.auth.controller;

import kr.nutee.auth.dto.request.*;
import kr.nutee.auth.dto.resource.ResponseResource;
import kr.nutee.auth.dto.response.LoginResponse;
import kr.nutee.auth.dto.response.RefreshResponse;
import kr.nutee.auth.dto.response.Response;
import kr.nutee.auth.dto.response.UserData;
import kr.nutee.auth.enums.ErrorCode;
import kr.nutee.auth.exception.ConflictException;
import kr.nutee.auth.exception.NotExistException;
import kr.nutee.auth.jwt.JwtGenerator;
import kr.nutee.auth.repository.MemberRepository;
import kr.nutee.auth.service.AuthService;
import kr.nutee.auth.service.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import kr.nutee.auth.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(path = "/auth", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@ResponseBody
@Slf4j
public class AuthController {

    private final MemberService memberService;
    private final AuthService authService;
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtGenerator jwtGenerator;

    /*
        내용 : 회원가입
    */
    @PostMapping(path = "/user")
    public ResponseEntity<ResponseResource> signUp(@RequestBody @Valid SignupDTO signupDTO) {
        UserData body = authService.signUp(signupDTO);
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(body)
                .build();

        ResponseResource resource = new ResponseResource(response, AuthController.class, "signup");
        WebMvcLinkBuilder selfLinkBuilder = linkTo(AuthController.class).slash("signup");
        URI createdURI = selfLinkBuilder.toUri();

        return ResponseEntity.created(createdURI).body(resource);
    }

    /*
        내용 : 작성한 이메일로 NUTEE 인증 번호를 보낸다.
    */
    @PostMapping(path = "/otp")
    public ResponseEntity<ResponseResource> sendOtp(@RequestBody @Valid SendOTP sendOTP) {
        authService.sendOtp(sendOTP.getSchoolEmail(), setOtpNumber(sendOTP));
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body("해당 이메일로 OTP를 전송하였습니다.")
                .build();

        ResponseResource resource = new ResponseResource(response, AuthController.class);

        return ResponseEntity.ok().body(resource);
    }

    /*
        내용 : 회원가입에 필요한 아이디 중복 체크
    */
    @PostMapping(path = "/check/user-id")
    public ResponseEntity<ResponseResource> checkUserId(@RequestBody @Valid CheckIdDTO requestBody) {
        if (!memberService.checkUserId(requestBody.getUserId())) {
            throw new ConflictException("아이디가 중복되었습니다.", ErrorCode.CONFLICT, HttpStatus.CONFLICT);
        }

        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body("아이디 중복체크 성공.")
                .build();

        ResponseResource resource = new ResponseResource(response, AuthController.class);

        return ResponseEntity.ok().body(resource);
    }

    /*
        내용 : 회원가입에 필요한 닉네임 중복 체크
    */
    @PostMapping(path = "/check/nickname")
    public ResponseEntity<ResponseResource> nicknameCheck(@RequestBody @Valid CheckNicknameDTO checkNicknameDTO) {
        if (!memberService.checkNickname(checkNicknameDTO.getNickname())) {
            throw new ConflictException("닉네임이 중복되었습니다.", ErrorCode.CONFLICT, HttpStatus.CONFLICT);
        }

        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body("닉네임 중복 체크 성공.")
                .build();

        ResponseResource resource = new ResponseResource(response, AuthController.class);

        return ResponseEntity.ok().body(resource);
    }

    /*
        내용 : 회원가입에 필요한 이메일 중복 체크
    */
    @PostMapping(path = "/check/email")
    public ResponseEntity<ResponseResource> emailCheck(@RequestBody @Valid CheckEmailDTO checkEmailDTO) {
        if (!memberService.checkEmail(checkEmailDTO.getSchoolEmail())) {
            throw new ConflictException("이메일이 중복되었습니다.", ErrorCode.CONFLICT, HttpStatus.CONFLICT);
        }

        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body("이메일 중복 체크 성공.")
                .build();

        ResponseResource resource = new ResponseResource(response, AuthController.class);

        return ResponseEntity.ok().body(resource);
    }

    /*
        내용 : 회원가입에 필요한 인증 넘버 체크
    */
    @PostMapping(path = "/check/otp")
    public ResponseEntity<ResponseResource> checkOtp(@RequestBody @Valid CheckOtpDTO checkOtpDTO) {
        if (!authService.checkOtp(checkOtpDTO.getOtp())) {
            throw new NotExistException("otp 인증에 실패하였습니다.", ErrorCode.NOT_EXIST, HttpStatus.UNAUTHORIZED);
        }

        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body("OTP 인증 성공.")
                .build();

        ResponseResource resource = new ResponseResource(response, AuthController.class);

        return ResponseEntity.ok().body(resource);
    }

    /*
        내용 : 사용자가 폼으로 입력한 내용을 통해 로그인 인증을 하고 성공시 refreshToken과 AccessToken 발행
    */
    @PostMapping(path = "/login")
    public ResponseEntity<ResponseResource> login(@RequestBody @Valid LoginDTO loginDTO) {
        LoginResponse body = authService.login(loginDTO);
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(body)
                .build();

        ResponseResource resource = new ResponseResource(response, AuthController.class);

        return ResponseEntity.ok().body(resource);
    }

    /*
        내용 : 사용자의 accessToken이 만료되었을 시 Redis에 저장된 유저의 refreshToken 인증을 통해
              accessToken 재발급
    */
    @PostMapping(path = "/refresh")
    public ResponseEntity<ResponseResource> requestForNewAccessToken(@RequestBody RefreshRequest token) {
        RefreshResponse body = authService.refresh(token);

        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(body)
                .build();

        ResponseResource resource = new ResponseResource(response, AuthController.class);

        return ResponseEntity.ok().body(resource);
    }

    /*
        내용 : Redis에 저장된 사용자의 refreshToken 강제 기간만료하여 삭제
    */
    @PostMapping(path = "/logout")
    public ResponseEntity<ResponseResource> logout(@RequestBody LogoutRequest token) {
        String accessToken = token.getAccessToken();
        String userId;

        try {
            userId = jwtGenerator.getUserIdFromToken(accessToken);
        } catch (ExpiredJwtException e) {
            userId = e.getClaims().getSubject();
            log.info("in logout: userId: " + userId);
        }

        stringRedisTemplate.delete("refresh-" + userId);

        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body("로그아웃에 성공했습니다.")
                .build();

        ResponseResource resource = new ResponseResource(response, AuthController.class);

        return ResponseEntity.ok().body(resource);
    }

    /*
        내용 : 아이디 찾기
    */
    @GetMapping(path = "/user-id")
    public ResponseEntity<Object> findId(@RequestBody FindIdRequest body) {
        String userId = authService.findId(body.getSchoolEmail()).getUserId();

        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(userId)
                .build();

        ResponseResource resource = new ResponseResource(response, AuthController.class);

        return ResponseEntity.ok().body(resource);
    }

    /*
        내용 : 비밀번호 변경
    */
    @PatchMapping(path = "/password")
    public ResponseEntity<ResponseResource> changePassword(@RequestBody FindPassWordRequest body) {
        authService.findPassword(body.getSchoolEmail(),body.getUserId());

        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body("새 비밀번호를 메일로 전송하였습니다.")
                .build();

        ResponseResource resource = new ResponseResource(response, AuthController.class);

        return ResponseEntity.ok().body(resource);
    }

    private String setOtpNumber(@RequestBody SendOTP sendOTP) {
        String otpNumber;//관리자 이메일
        if (sendOTP.getSchoolEmail().equals("nutee.skhu.2020@gmail.com")) {
            otpNumber = "000000";
        } else {
            //관리자 제외 유저 이메일
            otpNumber = authService.generateOtpNumber();
        }
        return otpNumber;
    }
}
