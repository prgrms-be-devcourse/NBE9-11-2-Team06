package com.back.nbe9112team06.global.exception;

import com.back.nbe9112team06.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("필수 필드 5개가 모두 정확히 응답된다 (RFC 9457)")
    void requiredFields_areReturned() throws Exception {
        String expectedCode = ErrorCode.NOT_FOUND.getCode();  // "COMMON-004"

        mockMvc.perform(get("/test/business"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://api.example.com/errors/common/not-found"))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("회원을 찾을 수 없습니다."))  // 필요시 커스텀 메시지
                .andExpect(jsonPath("$.errorCode").value(expectedCode))
                .andExpect(jsonPath("$.instance").value("/test/business"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("traceId 가 요청에 포함되면 응답에도 동일하게 포함된다")
    void traceId_ifPresent_hasValidFormat() throws Exception {
        String testTraceId = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";

        mockMvc.perform(get("/test/business")
                        .requestAttr("traceId", testTraceId))  // ✅ MockMvc 에서 request attribute 설정
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.traceId").value(testTraceId));
    }

    @Test
    @DisplayName("검증 실패 시 validationErrors 확장 필드가 포함된다")
    void validationFailure_returnsProblemDetailWithErrors() throws Exception {
        String invalidJson = """
                {
                    "name": "",
                    "email": "not-an-email"
                }
                """;

        mockMvc.perform(post("/test/valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://api.example.com/errors/validation/failed"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION-001"))
                .andExpect(jsonPath("$.instance").value("/test/valid"))
                // ✅ 확장 필드: validationErrors 배열
                .andExpect(jsonPath("$.validationErrors").isArray())
                .andExpect(jsonPath("$.validationErrors[?(@.field=='email')]").exists());
    }

    @Test
    @DisplayName("내내부 오류 시 민감 정보가 detail 에 노출되지 않는다")
    void unexpectedException_doesNotExposeSensitiveInfo() throws Exception {
        mockMvc.perform(get("/test/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                // ✅ RFC 9457 표준 필드
                .andExpect(jsonPath("$.type").value("https://api.example.com/errors/common/internal-error"))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("서버 내부 오류가 발생했습니다. 관리자에게 문의하세요."))
                .andExpect(jsonPath("$.errorCode").value("COMMON-001"))
                .andExpect(jsonPath("$.instance").value("/test/unexpected"))
                // ✅ 보안 검증: $.detail 에서 민감 정보 노출 금지 확인
                .andExpect(jsonPath("$.detail", not(containsString("테스트용 예외"))))
                .andExpect(jsonPath("$.stackTrace").doesNotExist())
                .andExpect(jsonPath("$.detail", not(containsString("RuntimeException"))));
    }

    @Test
    @DisplayName("타입 변환 실패 시 파라미터 정보가 포함된다")
    void handleTypeMismatch() throws Exception {
        mockMvc.perform(get("/test/param-type")
                        .param("id", "not-a-number"))  // ✅ Integer 파라미터에 문자열 전달
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errorCode").value("TYPE_MISMATCH"))
                .andExpect(jsonPath("$.detail", allOf(
                        containsString("타입 변환"),
                        containsString("id")
                )))
                .andExpect(jsonPath("$.parameterName").value("id"))
                .andExpect(jsonPath("$.receivedValue").value("not-a-number"));
    }

    @Test
    @DisplayName("필수 파라미터 누락 시 파라미터 이름이 포함된다")
    void handleMissingParam() throws Exception {
        mockMvc.perform(get("/test/param-missing"))  // ✅ name 파라미터 없이 요청
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errorCode").value("PARAM_MISSING"))
                .andExpect(jsonPath("$.detail", containsString("name")))
                .andExpect(jsonPath("$.parameterName").value("name"));
    }

    @Test
    @DisplayName("파라미터 @NotBlank 검증 실패 시 필드명이 포함된다")
    void handleMethodValidationFailure() throws Exception {
        mockMvc.perform(get("/test/validated")
                        .param("code", "  "))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://api.example.com/errors/validation/failed"))
                .andExpect(jsonPath("$.detail", allOf(
                        containsString("code"),
                        containsStringIgnoringCase("blank")
                )));
    }

    @Test
    @DisplayName("지원하지 않는 HTTP 메서드 호출 시 405 응답")
    void handleMethodNotAllowed() throws Exception {
        // ✅ POST 엔드포인트를 GET 으로 호출
        mockMvc.perform(get("/test/method-test"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errorCode").value("METHOD_NOT_ALLOWED"))
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.detail", containsString("지원하지 않는 HTTP 메서드")));
    }

    @Test
    @DisplayName("지원하지 않는 Content-Type 요청 시 415 응답")
    void handleUnsupportedMediaType() throws Exception {
        // ✅ JSON 이 필요한 엔드포인트에 text/plain 전송
        mockMvc.perform(post("/test/media-type-test")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("not-json"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errorCode").value("UNSUPPORTED_MEDIA_TYPE"))
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.detail").value("지원하지 않는 Content-Type 입니다."));
    }

    @Test
    @DisplayName("존재하지 않는 경로 요청 시 404 응답 (spring.mvc.throw-exception-if-no-handler-found)")
    void handleNotFound() throws Exception {
        mockMvc.perform(get("/test/this-path-does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errorCode").value("COMMON-004"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail", containsString("찾을 수 없습니다")));
    }

    @Test
    @DisplayName("DB 무결성 위반 시 일반화된 409 응답")
    void handleDataIntegrityViolation() throws Exception {
        // ✅ TestController 에서 직접 예외 던지기로 시뮬레이션
        mockMvc.perform(get("/test/db-violation"))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errorCode").value("DUPLICATE_RESOURCE"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("이미 존재하는 리소스입니다. 또는 데이터 무결성 제약조건에 위반됩니다."))
                // ✅ 보안: 내부 예외 메시지 노출 금지
                .andExpect(jsonPath("$.detail", not(containsString("Duplicate entry"))));
    }
}