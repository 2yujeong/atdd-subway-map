package wooteco.subway.admin.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wooteco.subway.admin.domain.Station;

import java.util.List;
import java.util.Set;

public interface StationRepository extends CrudRepository<Station, Long> {
    @Query("SELECT * FROM station WHERE id in (:ids)")
    Set<Station> findAllById(@Param("ids") List<Long> ids);

    @Query("SELECT id FROM station WHERE name=:name")
    Long findIdByName(@Param("name") String name);

    List<Station> findAll();
}