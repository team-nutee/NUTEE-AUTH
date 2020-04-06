package kr.nutee.auth.Controller;

import kr.nutee.auth.Domain.Member;
import kr.nutee.auth.Repository.MemberRepository;
import kr.nutee.auth.jwt.JwtGenerator;
import kr.nutee.auth.service.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import kr.nutee.auth.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final RedisTemplate<String, Object> memberRedisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtUserDetailsService userDetailsService;
    private final JwtGenerator jwtGenerator;
    private final AuthenticationManager am;
    private final PasswordEncoder bcryptEncoder;

    @PostMapping(path="/testing")
    public String test() {
        log.info("test");
        return "TEST";
    }

    @PostMapping(path="/signup")
    public Member signUp(@RequestBody Member member) {
        return memberService.insertUser(member);
    }


    public String getSHA512Token(String passwordToHash, String salt){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    @Value("${my.ip}")
    private String myIp;

//    @Autowired
//    public JavaMailSenderImpl javaMailSender;
//
//    @Async
//    public void sendMail(String email, String username, int type) throws Exception {
//        MimeMessage message = javaMailSender.createMimeMessage();
//        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
//        message.setSubject("[본인인증] quadcore 이메일 인증");
//        int rand = new Random().nextInt(999999);
//        String formatted = String.format("%06d",rand);
//        String hash = getSHA512Token(username, formatted);
//        String redisKey = null;
//        String htmlStr = null;
//        if (type == 0) {
//            redisKey = "email-" + username;
//            stringRedisTemplate.opsForValue().set(redisKey, hash);
//            htmlStr = "안녕하세요 " + username + "님. 인증하기를 눌러주세요"
//                    + "<a href='http://"+myIp+":8080" + "/auth/verify?username="+ username +"&key="+hash+"'>인증하기</a></p>";
//        } else if (type == 1) {
//            redisKey = "changepw-" + username;
//            stringRedisTemplate.opsForValue().set(redisKey, hash);
//            htmlStr ="안녕하세요 " + username + "님. 비밀번호 변경하를 눌러주세요"
//                    + "<a href='http://"+myIp+":8080" + "/auth/vfpwemail?username="+ username +"&key="+hash+"'>비밀번호 변경하기</a></p>";
//        }
//        stringRedisTemplate.expire(redisKey, 10*24*1000, TimeUnit.MILLISECONDS); // for one day
//
//        message.setText(htmlStr, "UTF-8", "html");
//        javaMailSender.send(message);
//    }

//    @GetMapping(path="/auth/verify")
//    public Map<String, Integer> verifyEmail(@RequestParam("username") String username, @RequestParam("key") String hash) {
//        Map<String, Integer> m = new HashMap<>();
//
//        logger.info("redis get : " + stringRedisTemplate.opsForValue().get("email-"+username));
//        logger.info("hash : " + hash);
//        if (stringRedisTemplate.opsForValue().get("email-"+username).equals(hash)) {
//            ValueOperations<String, Object> memvop = memberRedisTemplate.opsForValue();
//            Member member = (Member) memvop.get("toverify-"+username);
//            memberRepository.save(member);
//            stringRedisTemplate.delete("email-"+username);
//            memberRedisTemplate.delete("toverify-"+username);
//
//            ArrayList<String> x = new ArrayList<>();
//            Follow f = new Follow();
//            f.setUsername(username);
//            f.setTweetMembers(x);
//            f.setId(String.valueOf(f.hashCode()));
//            followRepository.save(f);
//
//
//            m.put("errorCode", 10);
//
//        } else m.put("errorCode", 70);
//        return m;
//    }

    //verify email to set new password
    @GetMapping(path="/vfpwemail")
    public Map<String, Integer> changePassword(@RequestParam("username") String username, @RequestParam("key") String hash) {
        Map<String, Integer> m = new HashMap<>();
        log.info("redis get : " + stringRedisTemplate.opsForValue().get("changepw-"+username));
        log.info("hash : " + hash);
        if (stringRedisTemplate.opsForValue().get("changepw-"+username).equals(hash)) {
            stringRedisTemplate.delete("changepw-"+username);
            m.put("errorCode", 10);
            //redirect to password setting page
        } else m.put("errorCode", 73);
        return m;
    }

    //send email to authorize user
//    @PostMapping(path="/auth/getpwmail")
////    public Map<String, Integer> findPassword(@RequestBody Map<String, String> m) {
////        Map<String, Integer> map = new HashMap<>();
////        String nickname = m.get("nickname");
////        String email = memberRepository.findByNickname(nickname).getSchoolEmail();
////        try {
////            sendMail(email, nickname, 1);
////        } catch(MessagingException e) {
////            logger.warn("email err: "+e);
////            map.put("errorCode", 68);
////            return map;
////        } catch (Exception e) {
////            logger.warn("email err: "+e);
////            map.put("errorCode", 66);
////            return map;
////        }
////
////        return map;
////    }


//    @PostMapping(path="/auth/register")
//    public Map<String, Object> addNewUser (@RequestBody Member member) {
//        String nickname = member.getNickname();
//
//
//
//        Map<String, Object> map = new HashMap<>();
//        System.out.println("회원가입요청 아이디: "+nickname + "비번: " + member.getPassword());
//        member.setNickname(nickname);
//        member.setId((long) 325);
//        member.setSchoolEmail(member.getSchoolEmail());
//        //member.setEmail_status(0);
//        if (nickname.equals("admin")) {
//            member.setGrade(0);
//        } else {
//            member.setGrade(1);
//        }
//        try {
//            sendMail(member.getSchoolEmail(), nickname, 0);
//        } catch(MessagingException e) {
//            map.put("errorCode", 68);
//            return map;
//        } catch (Exception e) {
//            map.put("errorCode", 66);
//            return map;
//        }
//
//        member.setPassword(bcryptEncoder.encode(member.getPassword()));
//        map.put("errorCode", 10);
//        ValueOperations<String, Object> vop = memberRedisTemplate.opsForValue();
//        vop.set("toverify-"+nickname, member);
//
//        return map;
//    }

//    @PostMapping(path = "/login")
//    public HashMap<String, Object> login(@RequestBody HashMap<String, Object> map) throws Exception {
//        final String userId = map.get("nickname");
//        logger.info("test input username: " + userId);
//
//        Member member = memberRepository.findByNickname(userId);
//
//        if (stringRedisTemplate.opsForValue().get("email-"+userId) != null) {
//            map.put("errorCode", 69);
//            return map;
//        }
//
//        member.setAccessAt(new Date());
//        memberRepository.save(member);
//        am.authenticate(new UsernamePasswordAuthenticationToken(userId, map.get("password")));
//
//        final UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
//        final String accessToken = jwtGenerator.generateAccessToken(userDetails);
//        final String refreshToken = jwtGenerator.generateRefreshToken(userId);
//
//        //generate Token and save in redis
//        stringRedisTemplate.opsForValue().set("refresh-" + userId, refreshToken);
//
//        logger.info("generated access token: " + accessToken);
//        logger.info("generated refresh token: " + refreshToken);
//        map.put("errorCode", 10);
//        map.put("accessToken", accessToken);
//        map.put("refreshToken", refreshToken);
//        return map;
//    }


//    @PostMapping(path="/checkemail")
//    public Map<String, Object>  checkEmail (@RequestBody Map<String, String> m) {
//        Map<String, Object> map = new HashMap<>();
//        System.out.println("이메일체크 요청 이메일: " + m.get("email"));
//        if (userRepository.findBySchoolEmail(m.get("email")) == null) {
//            map.put("errorCode", 10);
//        }
//        else map.put("errorCode", 53);
//        return map;
//    }


    @PostMapping(path="/refresh")
    public Map<String, Object>  requestForNewAccessToken(@RequestBody Map<String, String> m) {
        String username = null;
        Map<String, Object> map = new HashMap<>();
        String expiredAccessToken = m.get("accessToken");
        String refreshToken = m.get("refreshToken");
        log.info("get expired access token: " + expiredAccessToken);

        try {
            username = jwtGenerator.getUsernameFromToken(expiredAccessToken);
        } catch (ExpiredJwtException e) {
            username = e.getClaims().getSubject();
            log.info("username from expired access token: " + username);
        }
        if (username == null) throw new IllegalArgumentException();


        String refreshTokenFromDb = stringRedisTemplate.opsForValue().get("refresh-"+username);
        log.info("rtfrom db: " + refreshTokenFromDb);

        //user refresh token doesnt match with cache
        if (!refreshToken.equals(refreshTokenFromDb)) {
            map.put("errorCode", 58);
            return map;
        }

        //refresh token is expired
        if (jwtGenerator.isTokenExpired(refreshToken)) {
            map.put("errorCode", 57);
        }

        //generate access token if valid refresh token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String newAccessToken =  jwtGenerator.generateAccessToken(userDetails);
        map.put("errorCode", 10);
        map.put("accessToken", newAccessToken);
        return map;
    }

    @GetMapping(path="/normal")
    public Map<String, Object> onlyNormal() {
        Map<String, Object> map = new HashMap<>();
        map.put("errorCode", 10);
        return map;
    }

    @PostMapping(path="/logout")
    public Map<String, Object> logout(@RequestBody Map<String, String> m) {
        Map<String, Object> map = new HashMap<>();
        String accessToken = m.get("accessToken");
        String username = null;
        try {
            username = jwtGenerator.getUsernameFromToken(accessToken);
        } catch (ExpiredJwtException e) {
            username = e.getClaims().getSubject();
            log.info("in logout: username: " + username);
        }

        stringRedisTemplate.delete("refresh-" + username);
        //cache logout token for 10 minutes!
        log.info(" logout ing : " + accessToken);
        stringRedisTemplate.opsForValue().set(accessToken, "true");
        stringRedisTemplate.expire(accessToken, 10*6*1000, TimeUnit.MILLISECONDS);
        map.put("errorCode", 10);
        return map;
    }


    @PostMapping(path="/auth/name")
    public Map<String, Object> checker(@RequestBody Map<String, String> m) {
        Map<String, Object> map = new HashMap<>();
        String username = null;
        String accessToken = m.get("accessToken");
        try {
            username = jwtGenerator.getUsernameFromToken(accessToken);
        } catch (ExpiredJwtException e) {
            username = e.getClaims().getSubject();
            log.info("in logout: username: " + username);
        }

        map.put("errorCode", 10);
        map.put("username", username);
        return map;
    }
}