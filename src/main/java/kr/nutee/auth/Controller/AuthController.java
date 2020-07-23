package kr.nutee.auth.Controller;

import kr.nutee.auth.DTO.SendOTP;
import kr.nutee.auth.DTO.SignupDTO;
import kr.nutee.auth.DTO.Token;
import kr.nutee.auth.Domain.Interest;
import kr.nutee.auth.Domain.Major;
import kr.nutee.auth.Domain.Member;
import kr.nutee.auth.Exception.ConflictException;
import kr.nutee.auth.Repository.InterestRepository;
import kr.nutee.auth.Repository.MajorRepository;
import kr.nutee.auth.Repository.OtpRepository;
import kr.nutee.auth.jwt.JwtGenerator;
import kr.nutee.auth.service.AuthService;
import kr.nutee.auth.service.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import kr.nutee.auth.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(path = "/auth",consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@ResponseBody
@Slf4j
public class AuthController {

    private final MemberService memberService;
    private final AuthService authService;
    private final InterestRepository interestRepository;
    private final MajorRepository majorRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtUserDetailsService userDetailsService;
    private final JwtGenerator jwtGenerator;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder bcryptEncoder;

    @GetMapping(path="/test")
    public ResponseEntity<Object> test(){
        return new ResponseEntity<>("test", HttpStatus.valueOf(200));
    }
    /*
        내용 : 회원가입
    */
    @PostMapping(path="/signup")
    public ResponseEntity<Member> signUp(@RequestBody @Valid SignupDTO signupDTO) {
        if(!memberService.userIdCheck(signupDTO.getUserId())){
           throw new ConflictException("아이디가 중복되었습니다.",HttpStatus.CONFLICT);
        }
        if(!memberService.nicknameCheck(signupDTO.getNickname())){
            throw new ConflictException("닉네임이 중복되었습니다.",HttpStatus.CONFLICT);
        }
        if(!memberService.emailCheck(signupDTO.getSchoolEmail())){
            throw new ConflictException("이메일이 중복되었습니다.",HttpStatus.CONFLICT);
        }
        if(!authService.checkOtp(signupDTO.getOtp())){
            throw new ConflictException("교내 이메일 인증에 실패 하였습니다.",HttpStatus.UNAUTHORIZED);
        }

        String password = bcryptEncoder.encode(signupDTO.getPassword());
        Member member = Member.builder()
                .userId(signupDTO.getUserId())
                .nickname(signupDTO.getNickname())
                .schoolEmail(signupDTO.getSchoolEmail())
                .password(password)
                .build();

        member = memberService.insertUser(member);

        Member finalMember = member;
        signupDTO.getInterests()
                .forEach(v -> interestRepository.save(
                        Interest.builder()
                                .interest(v)
                                .member(finalMember)
                                .build()
                ));

        Member finalMember1 = member;
        signupDTO.getMajors()
                .forEach(v -> majorRepository.save(
                        Major.builder()
                                .major(v)
                                .member(finalMember1)
                                .build()
                ));

        //Todo :return값에 password가 포함되어있어 삭제해서 보내주거나 아예 return을 하지 않는다.
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    /*
        내용 : 작성한 이메일로 NUTEE 인증 번호를 보낸다.
    */
    @PostMapping(path = "/sendotp")
    public ResponseEntity<String> sendOtp(@RequestBody SendOTP sendOTP){
        String otpNumber;
        //관리자 이메일
        if(sendOTP.getSchoolEmail().equals("nutee.skhu.2020@gmail.com")){
            otpNumber = "000000";
        }else{
            //관리자 제외 유저 이메일
            otpNumber = authService.generateOtpNumber();
        }
        authService.sendOtp(sendOTP.getSchoolEmail(),otpNumber);
        authService.setOtp(otpNumber);
        return new ResponseEntity<>("otp를 메일로 전송하였습니다.",HttpStatus.OK);
    }

    /*
        내용 : 회원가입에 필요한 아이디 중복 체크
    */
    @PostMapping(path="/idcheck")
    public ResponseEntity<Object> idCheck(@ModelAttribute Member member) {
        if(memberService.userIdCheck(member.getUserId())){
            return new ResponseEntity<>("아이디 중복 체크 성공.",HttpStatus.OK);
        }else{
            return new ResponseEntity<>("중복 되는 아이디 입니다.",HttpStatus.CONFLICT);
        }
    }

    /*
        내용 : 회원가입에 필요한 닉네임 중복 체크
    */
    @PostMapping(path="/nicknamecheck")
    public ResponseEntity<Object> nicknameCheck(@ModelAttribute Member member) {
        if(memberService.nicknameCheck(member.getNickname())){
            return new ResponseEntity<>("닉네임 중복 체크 성공.",HttpStatus.OK);
        }else{
            return new ResponseEntity<>("중복 되는 닉네임 입니다.",HttpStatus.CONFLICT);
        }
    }

    /*
        내용 : 회원가입에 필요한 이메일 중복 체크
    */
    @PostMapping(path="/emailcheck")
    public ResponseEntity<Object> emailCheck(@ModelAttribute Member member) {
        if(memberService.emailCheck(member.getSchoolEmail())){
            return new ResponseEntity<>("이메일 중복 체크 성공.",HttpStatus.OK);
        }else{
            return new ResponseEntity<>("중복 되는 이메일 입니다.",HttpStatus.CONFLICT);
        }
    }

    /*
        내용 : 회원가입에 필요한 인증 넘버 체크
    */
    @PostMapping(path = "/otpcheck")
    public ResponseEntity<Object> checkOtp(@ModelAttribute @Valid SignupDTO signupDTO){
        String otpNumber = signupDTO.getOtp();
        Boolean isChecked = authService.checkOtp(otpNumber);
        if (isChecked){
            return new ResponseEntity<>("otp 인증에 성공하였습니다.",HttpStatus.OK);
        }else{
            return new ResponseEntity<>("otp 인증에 실패하였습니다.",HttpStatus.UNAUTHORIZED);
        }
    }

    /*
        내용 : 사용자가 폼으로 입력한 내용을 통해 로그인 인증을 하고 성공시 refreshToken과 AccessToken 발행
    */
    @PostMapping(path = "/login")
    public ResponseEntity<Object> login(@RequestBody Member member) throws Exception {
        final String userId = member.getUserId();
        log.info("logined user : " + userId);

        //로그인
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userId, member.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        final String accessToken = jwtGenerator.generateAccessToken(userDetails);
        final String refreshToken = jwtGenerator.generateRefreshToken(userId);
        log.info("generated access token: " + accessToken);
        log.info("generated refresh token: " + refreshToken);

        //refreshToken -> redis
        stringRedisTemplate.opsForValue().set("refresh-" + userId, refreshToken);

        //map에 데이터 담아서 client에 전송
        Map<String,Object> map = new HashMap<>();
        map.put("errorCode", 10);
        map.put("accessToken", accessToken);
        map.put("refreshToken", refreshToken);
        return new ResponseEntity<>(map,HttpStatus.OK);
    }

    /*
        내용 : 사용자의 accessToken이 만료되었을 시 Redis에 저장된 유저의 refreshToken 인증을 통해
              accessToken 재발급
    */
    @PostMapping(path="/refresh")
    public ResponseEntity<Object>  requestForNewAccessToken(@ModelAttribute Token m) {
        Map<String, Object> map = new HashMap<>();
        String username;

        String expiredAccessToken = m.getAccessToken();
        String refreshToken = m.getRefreshToken();

        try {
            username = jwtGenerator.getUserIdFromToken(expiredAccessToken);
        } catch (ExpiredJwtException e) {
            username = e.getClaims().getSubject();
            log.info("username from expired access token: " + username);
        }
        if (username == null) throw new IllegalArgumentException();

        String refreshTokenFromRedis = stringRedisTemplate.opsForValue().get("refresh-"+username);
        log.info("refreshTokenFromRedis: " + refreshTokenFromRedis);

        //유저가 가진 refreshToken과 Redis에 저장된 refreshToken이 일치하는지 확인
        if (!refreshToken.equals(refreshTokenFromRedis)) {
            map.put("errorCode", 58);
            return new ResponseEntity<>(map,HttpStatus.OK);
        }

        //유저가 가진 refreshToekn이 6개월 기간만료된 토큰인지 확인
        if (jwtGenerator.isTokenExpired(refreshToken)) {
            map.put("errorCode", 57);
        }

        //모든 조건 충족 되었을 시 새로운 토큰 발행
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String newAccessToken =  jwtGenerator.generateAccessToken(userDetails);
        map.put("errorCode", 10);
        map.put("accessToken", newAccessToken);
        return new ResponseEntity<>(map,HttpStatus.OK);
    }

    /*
        내용 : Redis에 저장된 사용자의 refreshToken 강제 기간만료하여 삭제
    */
    @PostMapping(path="/logout")
    public ResponseEntity<Object> logout(@ModelAttribute Token token) {
        Map<String, Object> map = new HashMap<>();
        String accessToken = token.getAccessToken();
        String userId;

        try {
            userId = jwtGenerator.getUserIdFromToken(accessToken);
        } catch (ExpiredJwtException e) {
            userId = e.getClaims().getSubject();
            log.info("in logout: userId: " + userId);
        }

        stringRedisTemplate.delete("refresh-" + userId);

        map.put("errorCode", 10);
        return new ResponseEntity<>(map,HttpStatus.OK);
    }

    /*
        내용 : 아이디 찾기
    */
    @PostMapping(path = "/findid")
    public ResponseEntity<Object> findId(@ModelAttribute Member member){
        Map<String, Object> map = new HashMap<>();
        map.put("errorCode",10);
        map.put("response",authService.findId(member).getUserId());
        return new ResponseEntity<>(map,HttpStatus.OK);
    }

    /*
        내용 : 비밀번호 찾기
    */
    @PostMapping(path = "/findpw")
    public ResponseEntity<Object> findPassword(@ModelAttribute Member member){
        Map<String, Object> map = new HashMap<>();
        authService.findPassword(member);
        map.put("errorCode",10);
        map.put("message","새 비밀번호를 메일로 전송하였습니다.");
        return new ResponseEntity<>(map,HttpStatus.OK);
    }
}