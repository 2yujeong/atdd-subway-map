package wooteco.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.RestAssuredHelper;
import wooteco.subway.controller.dto.LineResponse;
import wooteco.subway.controller.dto.SectionResponse;
import wooteco.subway.controller.dto.StationResponse;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.response.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class SectionAcceptanceTest extends AcceptanceTest {

    private final StationResponse gangnam = new StationResponse(1L, "강남역");
    private final StationResponse yeoksam = new StationResponse(2L, "역삼역");
    private final StationResponse seolleung = new StationResponse(3L, "선릉역");

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();

        Map<String, String> stationParams = new HashMap<>();
        stationParams.put("name", "강남역");
        RestAssuredHelper.jsonPost(stationParams, "/stations");

        Map<String, String> stationParams2 = new HashMap<>();
        stationParams2.put("name", "역삼역");
        RestAssuredHelper.jsonPost(stationParams2, "/stations");

        Map<String, String> lineParams = new HashMap<>();
        lineParams.put("color", "bg-red-600");
        lineParams.put("name", "신분당선");
        lineParams.put("upStationId", "1");
        lineParams.put("downStationId", "2");
        lineParams.put("distance", "10");
        RestAssuredHelper.jsonPost(lineParams, "/lines");

        Map<String, String> stationParams3 = new HashMap<>();
        stationParams3.put("name", "선릉역");
        RestAssuredHelper.jsonPost(stationParams3, "/stations");

        Map<String, String> stationParams4 = new HashMap<>();
        stationParams4.put("name", "삼성역");
        RestAssuredHelper.jsonPost(stationParams4, "/stations");

        Map<String, String> lineParams2 = new HashMap<>();
        lineParams2.put("color", "bg-green-600");
        lineParams2.put("name", "2호선");
        lineParams2.put("upStationId", "3");
        lineParams2.put("downStationId", "4");
        lineParams2.put("distance", "10");
        RestAssuredHelper.jsonPost(lineParams2, "/lines");
    }

    @DisplayName("구간 생성 성공 - 상행 종점역")
    @Test
    void createSectionWithTopStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "3");
        params.put("downStationId", "1");
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        final Section section = response.body().as(Section.class);
        assertThat(section.getLineId()).isEqualTo(1L);
        assertThat(section.getDownStationId()).isEqualTo(1L);
        assertThat(section.getUpStationId()).isEqualTo(3L);
        assertThat(section.getDistance()).isEqualTo(10);

        final LineResponse lineResponse = RestAssuredHelper.jsonGet("/lines/1").body().as(LineResponse.class);
        assertThat(lineResponse.getStations()).containsExactly(seolleung, gangnam, yeoksam);
    }

    @DisplayName("구간 생성 성공 - 하행 종점역")
    @Test
    void createSectionWithBottomStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "2");
        params.put("downStationId", "3");
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        final Section section = response.body().as(Section.class);
        assertThat(section.getLineId()).isEqualTo(1L);
        assertThat(section.getDownStationId()).isEqualTo(3L);
        assertThat(section.getUpStationId()).isEqualTo(2L);
        assertThat(section.getDistance()).isEqualTo(10);

        final LineResponse lineResponse = RestAssuredHelper.jsonGet("/lines/1").body().as(LineResponse.class);
        assertThat(lineResponse.getStations()).containsExactly(gangnam, yeoksam, seolleung);
    }

    @DisplayName("구간 생성 성공 - 종점이 아닌 중간 상행에 구간 추가")
    @Test
    void createSectionAtUpStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "1");
        params.put("downStationId", "3");
        params.put("distance", "7");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        final String location = response.header("Location");
        assertThat(location).isNotBlank();

        final Section section = response.body().as(Section.class);
        assertThat(section.getLineId()).isEqualTo(1L);
        assertThat(section.getDownStationId()).isEqualTo(3L);
        assertThat(section.getUpStationId()).isEqualTo(1L);
        assertThat(section.getDistance()).isEqualTo(7);

        final LineResponse lineResponse = RestAssuredHelper.jsonGet("/lines/1").body().as(LineResponse.class);
        assertThat(lineResponse.getStations()).containsExactly(gangnam, seolleung, yeoksam);

        final SectionResponse oldSectionResponse = RestAssuredHelper.jsonGet("/lines/1/sections?sectionId=1")
                                                                    .body()
                                                                    .as(SectionResponse.class);
        assertThat(oldSectionResponse.getDistance()).isEqualTo(3);

        final SectionResponse newSectionResponse = RestAssuredHelper.jsonGet(location).body().as(SectionResponse.class);
        assertThat(newSectionResponse.getDistance()).isEqualTo(7);
    }

    @DisplayName("구간 생성 성공 - 종점이 아닌 중간 하행에 구간 추가")
    @Test
    void createSectionAtBottomStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "3");
        params.put("downStationId", "2");
        params.put("distance", "7");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        final String location = response.header("Location");
        assertThat(location).isNotBlank();

        final Section section = response.body().as(Section.class);
        assertThat(section.getLineId()).isEqualTo(1L);
        assertThat(section.getDownStationId()).isEqualTo(2L);
        assertThat(section.getUpStationId()).isEqualTo(3L);
        assertThat(section.getDistance()).isEqualTo(7);

        final SectionResponse oldSectionResponse = RestAssuredHelper.jsonGet("/lines/1/sections?sectionId=1")
                                                                    .body()
                                                                    .as(SectionResponse.class);
        assertThat(oldSectionResponse.getDistance()).isEqualTo(3);

        final SectionResponse newSectionResponse = RestAssuredHelper.jsonGet(location).body().as(SectionResponse.class);
        assertThat(newSectionResponse.getDistance()).isEqualTo(7);
    }

    @DisplayName("구간 생성 실패 - 거리가 음수이면 예외 발생")
    @Test
    void createSectionWithMinusDistance() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("downStationId", "3");
        params.put("upStationId", "4");
        params.put("distance", "-1");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getReason()).isEqualTo("0보다 커야 합니다");
    }

    @DisplayName("구간 생성 실패 - 저장되어 있지 않은 역을 입력할 경우 예외 발생")
    @Test
    void createSectionWithDidNotPersistStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "5");
        params.put("downStationId", "1");
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getReason()).isEqualTo("해당 ID와 일치하는 역이 존재하지 않습니다.");
    }

    @DisplayName("구간 생성 실패 - 이미 저장되어 있는 구간일 경우 예외 발생")
    @Test
    void createSectionWithAlreadyPersistedStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getReason()).isEqualTo("이미 저장되어 있는 구간입니다.");
    }

    @DisplayName("구간 생성 실패 - 요청 구간의 역이 노선에 하나도 존재하지 않을 경우 예외 발생")
    @Test
    void createSectionWithMismatchStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "3");
        params.put("downStationId", "4");
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body()
                           .as(ErrorResponse.class)
                           .getReason()).isEqualTo("적어도 구간의 하나의 역은 이미 다른 구간에 저장되어 있어야 합니다.");
    }

    @DisplayName("구간 생성 실패 - 추가하려는 구간의 길이가 기존 구간의 길이 이하일 경우 예외 발생")
    @Test
    void createSectionWithLessDistance() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "1");
        params.put("downStationId", "3");
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getReason()).isEqualTo("추가하려는 구간의 길이는 기존 구간의 길이보다 커야 합니다.");
    }
}
