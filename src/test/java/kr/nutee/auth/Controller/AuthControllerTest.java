package kr.nutee.auth.Controller;

import kr.nutee.auth.Common.RestDocsConfiguration;
import kr.nutee.auth.DTO.Request.SendOTP;
import kr.nutee.auth.DTO.Request.SignupDTO;
import kr.nutee.auth.Repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(RestDocsConfiguration.class)
@ExtendWith(RestDocumentationExtension.class)
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest extends BaseControllerTest {

    @Autowired
    MemberRepository memberRepository;

    @Test @Order(1)
    @DisplayName("회원가입")
    void signUp() throws Exception {

        //given
        List<String> interests = List.of("INTER1","INTER2");

        List<String> majors = List.of("MAJOR1","MAJOR2");

        SignupDTO body = SignupDTO.builder()
                .userId("mf0004")
                .password("P@ssw0rd")
                .nickname("moon4")
                .schoolEmail("mf0004@gmail.com")
                .otp("000000")
                .interests(interests)
                .majors(majors)
                .build();


        //when
        MockHttpServletRequestBuilder builder = post("/auth/signup")
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
                .andExpect(jsonPath("body.id").exists())
                .andExpect(jsonPath("body.nickname").value("moon4"))
                .andExpect(jsonPath("body.image").doesNotExist())
                .andExpect(jsonPath("body.interests[0]").value("INTER1"))
                .andExpect(jsonPath("body.interests[1]").value("INTER2"))
                .andExpect(jsonPath("body.majors[0]").value("MAJOR1"))
                .andExpect(jsonPath("body.majors[1]").value("MAJOR2"))
                .andExpect(jsonPath("body.postNum").value(0))
                .andExpect(jsonPath("body.commentNum").value(0))
                .andExpect(jsonPath("body.likeNum").value(0))
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
                                fieldWithPath("body.image").type(JsonFieldType.OBJECT).description("user's image").optional(),
                                fieldWithPath("body.interests").description("user's interests"),
                                fieldWithPath("body.majors").description("user's majors"),
                                fieldWithPath("body.postNum").description("how many times user write posts"),
                                fieldWithPath("body.commentNum").description("how many times user write comments"),
                                fieldWithPath("body.likeNum").description("how many times user like posts"),
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
        MockHttpServletRequestBuilder builder = post("/auth/sendotp")
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
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
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