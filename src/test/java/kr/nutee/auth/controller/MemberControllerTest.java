package kr.nutee.auth.controller;

import java.util.List;
import kr.nutee.auth.dto.request.ChangeInterestsRequest;
import kr.nutee.auth.dto.request.ChangeMajorsRequest;
import kr.nutee.auth.dto.request.ChangeNicknameRequest;
import kr.nutee.auth.dto.request.ChangePasswordRequest;
import kr.nutee.auth.dto.request.ChangeProfileRequest;
import kr.nutee.auth.dto.request.SignupDTO;
import kr.nutee.auth.repository.MemberRepository;
import kr.nutee.auth.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
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

    @Autowired
    AuthenticationManager authenticationManager;

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
            .andDo(document("get-me"));
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
            .andDo(document("get-user"));
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changeMemberPassword() throws Exception {
        //given
        String userId = "mf0001";
        String nowPassword = "P@ssw0rd";
        String changePassword = "P@ssw0rd2";
        ChangePasswordRequest body = ChangePasswordRequest.builder()
            .nowPassword(nowPassword)
            .changePassword(changePassword)
            .build();

        //when
        MockHttpServletRequestBuilder builder = patch("/auth/user/password")
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
            .andDo(document("change-password"));

        Authentication authenticate = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(userId, changePassword));
        assertTrue(authenticate.isAuthenticated());
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
            .andDo(document("change-nickname"));
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
            .andDo(document("change-profile"));
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
            .andDo(document("change-interests"));
    }

    @Test
    @DisplayName("전공 변경 성공")
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
            .andDo(document("change-majors"));
    }
}
