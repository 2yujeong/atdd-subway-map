package wooteco.subway.infrastructure.station;

import org.springframework.stereotype.Repository;
import wooteco.subway.domain.station.Station;
import wooteco.subway.domain.station.StationName;
import wooteco.subway.domain.station.StationRepository;

import java.util.List;

@Repository
public class StationRepositoryImpl implements StationRepository {

    private final StationDao stationDao;

    public StationRepositoryImpl(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Override
    public Station save(final Station station) {
        return stationDao.save(station);
    }

    @Override
    public Station findById(Long id) {
        return stationDao.findById(id);
    }

    @Override
    public List<Station> findAll() {
        return stationDao.findAll();
    }

    @Override
    public int delete(Long id) {
        return stationDao.delete(id);
    }
}
