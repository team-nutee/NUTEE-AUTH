package kr.nutee.auth.Controller;

import kr.nutee.auth.DTO.Request.*;
import kr.nutee.auth.DTO.Resource.ResponseResource;
import kr.nutee.auth.DTO.Response.LoginResponse;
import kr.nutee.auth.DTO.Response.RefreshResponse;
import kr.nutee.auth.DTO.Response.Response;
import kr.nutee.auth.DTO.Response.UserData;
import kr.nutee.auth.DTO.Token;
import kr.nutee.auth.Domain.Member;
import kr.nutee.auth.Enum.ErrorCode;
import kr.nutee.auth.Exception.ConflictException;
import kr.nutee.auth.Exception.NotExistException;
import kr.nutee.auth.jwt.JwtGenerator;
import kr.nutee.auth.Service.AuthService;
import kr.nutee.auth.Service.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import kr.nutee.auth.Service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.*;

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
    private final JwtUserDetailsService userDetailsService;
    private final JwtGenerator jwtGenerator;
    private final AuthenticationManager authenticationManager;

    /*
        내용 : 회원가입
    */
    @PostMapping(path = "/member")
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
    @GetMapping(path = "/check/user-id")
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
    @GetMapping(path = "/check/nickname")
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
    @GetMapping(path = "/check/email")
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
    @GetMapping(path = "/check/otp")
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
        final String userId = loginDTO.getUserId();

        //로그인
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userId, loginDTO.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        final String accessToken = jwtGenerator.generateAccessToken(userDetails);
        final String refreshToken = jwtGenerator.generateRefreshToken(userId);

        //refreshToken -> redis
        stringRedisTemplate.opsForValue().set("refresh-" + userId, refreshToken);

        LoginResponse body = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

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
        String username;

        String expiredAccessToken = token.getAccessToken();
        String refreshToken = token.getRefreshToken();

        try {
            username = jwtGenerator.getUserIdFromToken(expiredAccessToken);
        } catch (ExpiredJwtException e) {
            username = e.getClaims().getSubject();
            log.info("username from expired access token: " + username);
        }
        if (username == null) throw new IllegalArgumentException();

        String refreshTokenFromRedis = stringRedisTemplate.opsForValue().get("refresh-" + username);
        log.info("refreshTokenFromRedis: " + refreshTokenFromRedis);

        //유저가 가진 refreshToken과 Redis에 저장된 refreshToken이 일치하는지 확인
        // Todo : 캐싱을 어떻게 할 지 방법을 찾지 못해 주석처리 해두었음. 테스트 해야함.
//        if (!refreshToken.equals(refreshTokenFromRedis)) {
//            throw new IllegalArgumentException("refreshToken error");
//        }

        //유저가 가진 refreshToken이 6개월 기간만료된 토큰인지 확인
        if (jwtGenerator.isTokenExpired(refreshToken)) {
            throw new IllegalArgumentException("refreshToken expired");
        }

        //모든 조건 충족 되었을 시 새로운 토큰 발행
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        RefreshResponse body = RefreshResponse.builder()
                .accessToken(jwtGenerator.generateAccessToken(userDetails))
                .build();

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
