package kr.nutee.auth.Controller;

import java.util.List;
import kr.nutee.auth.DTO.Request.ChangeInterestsRequest;
import kr.nutee.auth.DTO.Request.ChangeMajorsRequest;
import kr.nutee.auth.DTO.Request.ChangeNicknameRequest;
import kr.nutee.auth.DTO.Request.ChangePasswordRequest;
import kr.nutee.auth.DTO.Request.ChangeProfileRequest;
import kr.nutee.auth.DTO.Request.SignupDTO;
import kr.nutee.auth.Repository.MemberRepository;
import kr.nutee.auth.Service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerTest extends BaseControllerTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    AuthService authService;

    final ResultMatcher MEMBER_EXPECT = ResultMatcher.matchAll(
        jsonPath("body.id").exists(),
        jsonPath("body.nickname").value("moon1"),
        jsonPath("body.profileUrl").isEmpty(),
        jsonPath("body.interests[0]").value("INTER1"),
        jsonPath("body.interests[1]").value("INTER2"),
        jsonPath("body.majors[0]").value("MAJOR1"),
        jsonPath("body.majors[1]").value("MAJOR2")
    );

    @BeforeEach
    void setMember() {
        List<String> interests = List.of("INTER1", "INTER2");

        List<String> majors = List.of("MAJOR1", "MAJOR2");

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

    @Test
    @DisplayName("내 데이터 호출 성공")
    void getMyUserData() throws Exception {
        //given

        //when
        MockHttpServletRequestBuilder builder = get("/auth/user/me")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token)
            .accept(MediaTypes.HAL_JSON_VALUE);

        //then
        mockMvc.perform(builder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header()
                .string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
            .andExpect(jsonPath("code").exists())
            .andExpect(jsonPath("message").exists())
            .andExpect(jsonPath("body").exists())
            .andExpect(MEMBER_EXPECT)
            .andExpect(jsonPath("_links.self").exists())
            .andDo(document("get-me",
                links(
                    linkWithRel("self").description("link to self")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                ),
                responseFields(
                    fieldWithPath("code").description("label code number"),
                    fieldWithPath("message").description("message"),
                    fieldWithPath("body").description("body of the response"),
                    fieldWithPath("body.id").description("user's id"),
                    fieldWithPath("body.nickname").description("user's nickname"),
                    fieldWithPath("body.profileUrl").description("user's profile image"),
                    fieldWithPath("body.interests").description("user's interests"),
                    fieldWithPath("body.majors").description("user's majors"),
                    fieldWithPath("_links.self.href").description("link to self")
                )
            ));
    }

    @Test
    @DisplayName("유저 데이터 호출 성공")
    void getUserData() throws Exception {
        //given
        long memberId = 1L;

        //when
        MockHttpServletRequestBuilder builder = get("/auth/user/" + memberId)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token)
            .accept(MediaTypes.HAL_JSON_VALUE);

        //then
        mockMvc.perform(builder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header()
                .string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
            .andExpect(jsonPath("code").exists())
            .andExpect(jsonPath("message").exists())
            .andExpect(jsonPath("body").exists())
            .andExpect(MEMBER_EXPECT)
            .andExpect(jsonPath("_links.self").exists())
            .andDo(document("get-me",
                links(
                    linkWithRel("self").description("link to self")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                ),
                responseFields(
                    fieldWithPath("code").description("label code number"),
                    fieldWithPath("message").description("message"),
                    fieldWithPath("body").description("body of the response"),
                    fieldWithPath("body.id").description("user's id"),
                    fieldWithPath("body.nickname").description("user's nickname"),
                    fieldWithPath("body.profileUrl").description("user's profile image"),
                    fieldWithPath("body.interests").description("user's interests"),
                    fieldWithPath("body.majors").description("user's majors"),
                    fieldWithPath("_links.self.href").description("link to self")
                )
            ));
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changeMemberPassword() throws Exception {
        //given
        String password = "P@ssw0rd";
        ChangePasswordRequest body = ChangePasswordRequest.builder()
            .password(password)
            .build();

        //when
        MockHttpServletRequestBuilder builder = patch("/auth/user/1/password")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token)
            .accept(MediaTypes.HAL_JSON_VALUE)
            .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header()
                .string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
            .andExpect(jsonPath("code").exists())
            .andExpect(jsonPath("message").exists())
            .andExpect(jsonPath("body").isEmpty())
            .andDo(document("change-password",
                links(
                    linkWithRel("self").description("link to self")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                ),
                requestFields(
                    fieldWithPath("password").description("new password you want")
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

    @Test
    @DisplayName("닉네임 변경 성공")
    void changeNickname() throws Exception {
        //given
        String newNickname = "바보바보";
        ChangeNicknameRequest body = ChangeNicknameRequest.builder()
            .nickname(newNickname)
            .build();

        //when
        MockHttpServletRequestBuilder builder = patch("/auth/user/nickname")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token)
            .accept(MediaTypes.HAL_JSON_VALUE)
            .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header()
                .string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
            .andExpect(jsonPath("code").exists())
            .andExpect(jsonPath("message").exists())
            .andExpect(jsonPath("body").value("바보바보"))
            .andDo(document("change-nickname",
                links(
                    linkWithRel("self").description("link to self")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                ),
                requestFields(
                    fieldWithPath("nickname").description("new nickname you want")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                ),
                responseFields(
                    fieldWithPath("code").description("label code number"),
                    fieldWithPath("message").description("message"),
                    fieldWithPath("body").description("nickname that change success"),
                    fieldWithPath("_links.self.href").description("link to self")
                )
            ));

    }

    @Test
    @DisplayName("멤버 프로필 이미지 변경 성공")
    void changeMemberProfile() throws Exception {
        //given
        String newProfileUrl = "newProfileUrl.jpg";
        ChangeProfileRequest body = new ChangeProfileRequest(newProfileUrl);

        //when
        MockHttpServletRequestBuilder builder = patch("/auth/user/profile")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token)
            .accept(MediaTypes.HAL_JSON_VALUE)
            .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header()
                .string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
            .andExpect(jsonPath("code").exists())
            .andExpect(jsonPath("message").exists())
            .andExpect(jsonPath("body").value("newProfileUrl.jpg"))
            .andDo(document("change-profile",
                links(
                    linkWithRel("self").description("link to self")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                ),
                requestFields(
                    fieldWithPath("profileUrl").description("new profile image url that you want")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                ),
                responseFields(
                    fieldWithPath("code").description("label code number"),
                    fieldWithPath("message").description("message"),
                    fieldWithPath("body").description("response of your new profile imageUrl"),
                    fieldWithPath("_links.self.href").description("link to self")
                )
            ));

    }

    @Test
    @DisplayName("흥미 변경 성공")
    void changeMemberInterests() throws Exception {
        //given
        List<String> interests = List.of("INTER3","INTER4");
        ChangeInterestsRequest body = ChangeInterestsRequest.builder()
            .interests(interests)
            .build();

        //when
        MockHttpServletRequestBuilder builder = patch("/auth/user/interests")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token)
            .accept(MediaTypes.HAL_JSON_VALUE)
            .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header()
                .string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
            .andExpect(jsonPath("code").exists())
            .andExpect(jsonPath("message").exists())
            .andExpect(jsonPath("body").exists())
            .andExpect(jsonPath("body[0]").value("INTER3"))
            .andExpect(jsonPath("body[1]").value("INTER4"))
            .andDo(document("change-interests",
                links(
                    linkWithRel("self").description("link to self")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                ),
                requestFields(
                    fieldWithPath("interests").description("new interest list you want")
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

    @Test
    @DisplayName("흥미 변경 성공")
    void changeMemberMajors() throws Exception {
        //given
        List<String> majors = List.of("MAJOR3","MAJOR4");
        ChangeMajorsRequest body = ChangeMajorsRequest.builder()
            .majors(majors)
            .build();

        //when
        MockHttpServletRequestBuilder builder = patch("/auth/user/majors")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token)
            .accept(MediaTypes.HAL_JSON_VALUE)
            .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header()
                .string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
            .andExpect(jsonPath("code").exists())
            .andExpect(jsonPath("message").exists())
            .andExpect(jsonPath("body").exists())
            .andExpect(jsonPath("body[0]").value("MAJOR3"))
            .andExpect(jsonPath("body[1]").value("MAJOR4"))
            .andDo(document("change-majors",
                links(
                    linkWithRel("self").description("link to self")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                ),
                requestFields(
                    fieldWithPath("majors").description("new major list you want")
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
