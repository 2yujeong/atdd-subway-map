package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;

public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLineTest() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 정보로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateInfoTest() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");

        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getStations() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "신분당선");
        params1.put("color", "red");

        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "분당선");
        params2.put("color", "green");

        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("아이디를 받아 지하철 노선을 단 건 조회한다.")
    @Test
    void getStationByIdTest() {
        /// given
        Map<String, String> params = new HashMap<>();
        params.put("name", "분당선");
        params.put("color", "red");

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Long expectedLineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        Long resultLineId = response.jsonPath().getObject(".", LineResponse.class).getId();

        assertThat(resultLineId).isEqualTo(expectedLineId);
    }

    @DisplayName("지하철 노선을 업데이트한다.")
    @Test
    void updateLine() {
        /// given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when, then
        Map<String, String> newParams = new HashMap<>();
        newParams.put("name", "분당선");
        newParams.put("color", "green");

        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(newParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
