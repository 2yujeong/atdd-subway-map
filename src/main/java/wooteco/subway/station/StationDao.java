package wooteco.subway.station;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.exception.repository.DataNotFoundException;
import wooteco.subway.exception.repository.DuplicatedFieldException;

@Repository
public class StationDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final RowMapper<Station> stationRowMapper = (resultSet, rowNum) -> new Station(
        resultSet.getLong("id"),
        resultSet.getString("name")
    );

    public StationDao(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Station save(final Station station) {
        try {
            final String sql = "INSERT INTO station (name) VALUES (:name)";
            final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            final SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(station);
            namedParameterJdbcTemplate.update(sql, sqlParameterSource, keyHolder);
            final long id = keyHolder.getKey().longValue();
            return findById(id).orElseThrow(() -> new DataNotFoundException("해당 역을 찾을 수 없습니다."));
        } catch (DuplicateKeyException e) {
            throw new DuplicatedFieldException("중복된 이름의 지하철역입니다.");
        }
    }

    public void deleteById(final long id) {
        final String sql = "DELETE FROM station WHERE id = :id";
        int deletedCnt = namedParameterJdbcTemplate.update(sql, Collections.singletonMap("id", id));

        if (deletedCnt < 1) {
            throw new DataNotFoundException("해당 Id의 지하철역이 없습니다.");
        }
    }

    public List<Station> findAll() {
        final String sql = "SELECT * FROM station";
        return namedParameterJdbcTemplate.query(sql, stationRowMapper);
    }

    public Optional<Station> findById(final Long id) {
        final String sql = "SELECT * FROM station WHERE id = :id";
        final List<Station> stations = namedParameterJdbcTemplate.query(
            sql, Collections.singletonMap("id", id), stationRowMapper
        );
        return Optional.ofNullable(DataAccessUtils.singleResult(stations));
    }

    public Optional<Station> findByName(final String name) {
        final String sql = "SELECT * FROM station WHERE name = :name";
        final List<Station> stations = namedParameterJdbcTemplate.query(
            sql, Collections.singletonMap("name", name), stationRowMapper
        );
        return Optional.ofNullable(DataAccessUtils.singleResult(stations));
    }

    public List<Station> findByIds(final List<Long> ids) {
        final String sql = "SELECT * FROM station WHERE id IN (:ids)";
        return namedParameterJdbcTemplate.query(sql, Collections.singletonMap("ids", ids), stationRowMapper);
    }
}
