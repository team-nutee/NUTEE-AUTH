package kr.nutee.auth.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nutee.auth.Common.RestDocsConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(RestDocsConfiguration.class)
@ExtendWith(RestDocumentationExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class BaseControllerTest {

    protected String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtZjAwMDEiLCJyb2xlIjoiUk9MRV9NQU5BR0VSIiwiaWQiOjEsImV4cCI6MTkxMjA2NDU4NiwiaWF0IjoxNTk2NzA0NTg2fQ.VmpRq6R0NhyteAp2ToaPPbjAANcSfZTMKvrXxCd3iFBcm3gVLn9GYd6lJQ07gRIyk_U38x4t7VEpzA2qcbMAgA";
    protected String refresh = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtZjAwMDEiLCJpYXQiOjE1OTY5ODMxNjQsImV4cCI6MTYxMjUzNTE2NH0.6UqF-2kaLYbonMsbhJYIJr5ezJEFo30jzQ_4K16haja286kdn4ajv93mlHJJL4NaZS5xOjYFwBA-523cYS06dw";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

}
