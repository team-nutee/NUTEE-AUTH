package kr.nutee.auth.Controller;

import kr.nutee.auth.DTO.Request.*;
import kr.nutee.auth.Service.AuthService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest extends BaseControllerTest {

    @Autowired
    AuthService authService;

    final ResultMatcher MEMBER_EXPECT = ResultMatcher.matchAll(
        jsonPath("body.id").exists(),
        jsonPath("body.nickname").value("moon2"),
        jsonPath("body.profileUrl").isEmpty(),
        jsonPath("body.interests[0]").value("INTER1"),
        jsonPath("body.interests[1]").value("INTER2"),
        jsonPath("body.majors[0]").value("MAJOR1"),
        jsonPath("body.majors[1]").value("MAJOR2")
    );

    @BeforeEach
    void setMember() {
        List<String> interests = List.of("INTER1","INTER2");
        List<String> majors = List.of("MAJOR1","MAJOR2");

        SignupDTO body = SignupDTO.builder()
                .userId("mf0001")
                .password("P@ssw0rd")
                .nickname("moon1")
                .schoolEmail("mf0001@gmail.com")
                .otp("000000")
                .interests(interests)
                .majors(majors)
                .build();

        authService.signUp(body);
    }

    @AfterEach
    void dropCollection() {
        mongoTemplate.dropCollection("members");
        mongoTemplate.dropCollection("otps");
        mongoTemplate.dropCollection("database_sequences");
    }

    @Test @Order(1)
    @DisplayName("회원가입")
    void signUp() throws Exception {
        //given
        List<String> interests = List.of("INTER1","INTER2");

        List<String> majors = List.of("MAJOR1","MAJOR2");

        SignupDTO body = SignupDTO.builder()
                .userId("mf0002")
                .password("P@ssw0rd")
                .nickname("moon2")
                .schoolEmail("mf0002@gmail.com")
                .otp("000000")
                .interests(interests)
                .majors(majors)
                .build();

        //when
        MockHttpServletRequestBuilder builder = post("/auth/member")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").exists())
                .andExpect(MEMBER_EXPECT)
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("sign-up",
                        links(
                                linkWithRel("self").description("link to self")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        requestFields(
                                fieldWithPath("userId").description("id(not null)"),
                                fieldWithPath("password").description("password(not null)"),
                                fieldWithPath("nickname").description("nickname(not null)"),
                                fieldWithPath("schoolEmail").description("skhu official email(not null)"),
                                fieldWithPath("otp").description("otp number(not null)"),
                                fieldWithPath("interests").description("your interests(not null)"),
                                fieldWithPath("majors").description("your majors(not null)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        responseFields(
                                fieldWithPath("code").description("label code number"),
                                fieldWithPath("message").description("message"),
                                fieldWithPath("body").description("body of the response"),
                                fieldWithPath("body.id").description("user's id"),
                                fieldWithPath("body.nickname").description("user's nickname"),
                                fieldWithPath("body.profileUrl").description("user's image"),
                                fieldWithPath("body.interests").description("user's interests"),
                                fieldWithPath("body.majors").description("user's majors"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ));
    }

    @Test @Order(2)
    @DisplayName("OTP number를 작성한 이메일로 전송")
    void sendOTP() throws Exception {

        //given
        SendOTP body = SendOTP.builder()
                .schoolEmail("skhu.nutee.2020@gmail.com")
                .build();

        //when
        MockHttpServletRequestBuilder builder = post("/auth/otp")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").value("해당 이메일로 OTP를 전송하였습니다."))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("send-otp",
                        links(
                                linkWithRel("self").description("link to self")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        requestFields(
                                fieldWithPath("schoolEmail").description("title of new post(not null)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        responseFields(
                                fieldWithPath("code").description("label code number"),
                                fieldWithPath("message").description("message"),
                                fieldWithPath("body").description("body of the response"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ));
    }

    @Test @Order(3)
    @DisplayName("ID 중복체크")
    void checkId() throws Exception {

        //given
        String userId = "mf0004";
        CheckIdDTO body = CheckIdDTO.builder()
                .userId(userId)
                .build();
        //when
        MockHttpServletRequestBuilder builder = get("/auth/check/user-id")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").value("아이디 중복체크 성공."))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("check-id",
                        links(
                                linkWithRel("self").description("link to self")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        requestFields(
                                fieldWithPath("userId").description("title of new post(not null)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        responseFields(
                                fieldWithPath("code").description("label code number"),
                                fieldWithPath("message").description("message"),
                                fieldWithPath("body").description("body of the response"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ));
    }

    @Test @Order(4)
    @DisplayName("닉네임 중복체크")
    void checkNickname() throws Exception {

        //given
        String nickname = "moon4";
        CheckNicknameDTO body = CheckNicknameDTO
                .builder()
                .nickname(nickname)
                .build();
        //when
        MockHttpServletRequestBuilder builder = get("/auth/check/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").value("닉네임 중복 체크 성공."))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("check-nickname",
                        links(
                                linkWithRel("self").description("link to self")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        requestFields(
                                fieldWithPath("nickname").description("title of new post(not null)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        responseFields(
                                fieldWithPath("code").description("label code number"),
                                fieldWithPath("message").description("message"),
                                fieldWithPath("body").description("body of the response"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ));
    }

    @Test @Order(5)
    @DisplayName("이메일 중복체크")
    void checkEmail() throws Exception {

        //given
        String email = "mf0004@gmail.com";
        CheckEmailDTO body = CheckEmailDTO
                .builder()
                .schoolEmail(email)
                .build();
        //when
        MockHttpServletRequestBuilder builder = get("/auth/check/email")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").value("이메일 중복 체크 성공."))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("check-email",
                        links(
                                linkWithRel("self").description("link to self")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        requestFields(
                                fieldWithPath("schoolEmail").description("title of new post(not null)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        responseFields(
                                fieldWithPath("code").description("label code number"),
                                fieldWithPath("message").description("message"),
                                fieldWithPath("body").description("body of the response"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ));
    }

    @Test @Order(6)
    @DisplayName("OTP 체크")
    void checkOTP() throws Exception {

        //given
        String otp = "000000";
        CheckOtpDTO body = CheckOtpDTO
                .builder()
                .otp(otp)
                .build();

        //when
        MockHttpServletRequestBuilder builder = get("/auth/check/otp")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").value("OTP 인증 성공."))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("check-otp",
                        links(
                                linkWithRel("self").description("link to self")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        requestFields(
                                fieldWithPath("otp").description("title of new post(not null)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        responseFields(
                                fieldWithPath("code").description("label code number"),
                                fieldWithPath("message").description("message"),
                                fieldWithPath("body").description("body of the response"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ));
    }

    @Test @Order(7)
    @DisplayName("로그인 성공")
    void login() throws Exception {

        //given
        String userId = "mf0001";
        String password = "P@ssw0rd";
        LoginDTO body = LoginDTO.builder()
                .userId(userId)
                .password(password)
                .build();
        //when
        MockHttpServletRequestBuilder builder = post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").exists())
                .andExpect(jsonPath("body.accessToken").exists())
                .andExpect(jsonPath("body.refreshToken").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("login",
                        links(
                                linkWithRel("self").description("link to self")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        requestFields(
                                fieldWithPath("userId").description("user's id(not null)"),
                                fieldWithPath("password").description("user's password(not null)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        responseFields(
                                fieldWithPath("code").description("label code number"),
                                fieldWithPath("message").description("message"),
                                fieldWithPath("body").description("body of the response"),
                                fieldWithPath("body.accessToken").description("accessToken(30 Minutes)"),
                                fieldWithPath("body.refreshToken").description("refreshToken(6 Months)"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ));
    }

    @Test @Order(8)
    @DisplayName("리프레시 성공")
    void refresh() throws Exception {

        //given
        RefreshRequest body = RefreshRequest.builder()
                .accessToken(token)
                .refreshToken(refresh)
                .build();
        //when
        MockHttpServletRequestBuilder builder = post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").exists())
                .andExpect(jsonPath("body.accessToken").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("refresh",
                        links(
                                linkWithRel("self").description("link to self")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        requestFields(
                                fieldWithPath("accessToken").description("user's id(not null)"),
                                fieldWithPath("refreshToken").description("user's password(not null)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        responseFields(
                                fieldWithPath("code").description("label code number"),
                                fieldWithPath("message").description("message"),
                                fieldWithPath("body").description("body of the response"),
                                fieldWithPath("body.accessToken").description("accessToken(30 Minutes)"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ));
    }

    @Test @Order(9)
    @DisplayName("로그아웃 성공")
    void logout() throws Exception {

        //given
        LogoutRequest body = LogoutRequest.builder()
                .accessToken(token)
                .build();
        //when
        MockHttpServletRequestBuilder builder = post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").value("로그아웃에 성공했습니다."))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("logout",
                        links(
                                linkWithRel("self").description("link to self")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        requestFields(
                                fieldWithPath("accessToken").description("user's id(not null)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        responseFields(
                                fieldWithPath("code").description("label code number"),
                                fieldWithPath("message").description("message"),
                                fieldWithPath("body").description("body of the response"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ));
    }

    @Test @Order(10)
    @DisplayName("아이디 찾기 성공")
    void findId() throws Exception {

        //given
        String schoolEmail = "mf0001@gmail.com";
        String userId = "mf0001";
        FindIdRequest body = FindIdRequest.builder()
                .schoolEmail(schoolEmail)
                .build();
        //when
        MockHttpServletRequestBuilder builder = get("/auth/user-id")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").value(userId))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("find-id",
                        links(
                                linkWithRel("self").description("link to self")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        requestFields(
                                fieldWithPath("schoolEmail").description("user's email(not null)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        responseFields(
                                fieldWithPath("code").description("label code number"),
                                fieldWithPath("message").description("message"),
                                fieldWithPath("body").description("body of the response"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ));
    }

    @Test @Order(11)
    @DisplayName("비밀번호 찾기 성공")
    void findPassword() throws Exception {

        //given
        String userId = "mf0001";
        String email = "mf0001@gmail.com";
        FindPassWordRequest body = FindPassWordRequest.builder()
                .userId(userId)
                .schoolEmail(email)
                .build();

        //when
        MockHttpServletRequestBuilder builder = patch("/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").value("새 비밀번호를 메일로 전송하였습니다."))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("find-password",
                        links(
                                linkWithRel("self").description("link to self")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        requestFields(
                                fieldWithPath("schoolEmail").description("user's email(not null)"),
                                fieldWithPath("userId").description("user's id(not null)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        responseFields(
                                fieldWithPath("code").description("label code number"),
                                fieldWithPath("message").description("message"),
                                fieldWithPath("body").description("body of the response"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ));
    }
}
