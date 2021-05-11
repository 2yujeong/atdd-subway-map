package wooteco.subway.section.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import wooteco.subway.section.exception.SectionException;

public class Sections {

    private static final int FIRST = 0;
    private static final int SECOND = 1;

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public Optional<Section> change(final Section requestedSection) {
        final List<Section> relatedSections = relatedSectionsOf(requestedSection);
        validateRelated(requestedSection, relatedSections);
        for (final Section section : relatedSections) {
            if (section.hasSameStations(requestedSection)) {
                break;
            }
            if (section.canConnect(requestedSection) && relatedSections.size() == 1) {
                return Optional.empty();
            }
            if (section.canJoin(requestedSection)) {
                return Optional.ofNullable(section.combineWith(requestedSection));
            }
        }
        throw new SectionException("해당 노선에 추가될 수 없는 구간입니다.");
    }

    private List<Section> relatedSectionsOf(final Section requestedSection) {
        return sections.stream()
                .filter(section -> section.isRelated(requestedSection))
                .collect(Collectors.toList());
    }

    private void validateRelated(final Section requestedSection, final List<Section> relatedSections) {
        if (relatedSections.size() == 2) {
            final Section firstSection = relatedSections.get(FIRST);
            final Section secondSection = relatedSections.get(SECOND);

            validateConnected(firstSection, secondSection);
            validateDifferent(requestedSection, firstSection, secondSection);
        }
    }

    private void validateConnected(final Section firstSection, final Section secondSection) {
        if (connectedFarAway(firstSection, secondSection)) {
            throw new SectionException("이미 노선에서 서로 연결된 역들입니다.");
        }
    }

    private boolean connectedFarAway(final Section firstSection, final Section secondSection) {
        return !firstSection.canConnect(secondSection);
    }

    private void validateDifferent(final Section requestedSection, final Section firstSection, final Section secondSection) {
        if (isForked(requestedSection, firstSection, secondSection)) {
            throw new SectionException("갈래길을 형성할 수 없습니다.");
        }
    }

    private boolean isForked(final Section requestedSection, final Section firstSection, final Section secondSection) {
        return (requestedSection.canJoin(firstSection) && requestedSection.canJoin(secondSection))
                || (requestedSection.canConnect(firstSection) && requestedSection.canConnect(secondSection));
    }

    public List<Section> removeStation(final Long stationId) {
        validateSectionsLength();

        final List<Section> relatedSections = relatedSectionsOf(stationId);
        relatedSections.forEach(sections::remove);
        return relatedSections;
    }

    private void validateSectionsLength() {
        if (sections.size() == 1) {
            throw new SectionException("구간이 하나밖에 없는 노선에서는 역을 제거할 수 없습니다.");
        }
    }

    private List<Section> relatedSectionsOf(final Long stationId) {
        return sections.stream()
                .filter(section -> section.contains(stationId))
                .collect(Collectors.toList());
    }

    public List<Long> toOrderedStationIds() {
        final Map<Long, Long> stationIdMap = createIdMap(sections);

        final Long beginningUpStation = findBeginningUpStation(stationIdMap);

        return createOrderedStationIdList(beginningUpStation, stationIdMap);
    }

    private Map<Long, Long> createIdMap(final List<Section> sections) {
        final Map<Long, Long> idMap = new HashMap<>();
        for (final Section section : sections) {
            idMap.put(section.getUpStationId(), section.getDownStationId());
        }
        return idMap;
    }

    private Long findBeginningUpStation(final Map<Long, Long> stationIdMap) {
        return stationIdMap.keySet().stream()
                .filter(key -> !stationIdMap.containsValue(key))
                .findFirst()
                .orElseThrow(() -> new SectionException("노선의 구간이 올바르게 정렬되지 않았습니다."));
    }

    private List<Long> createOrderedStationIdList(final Long beginningUpStation, final Map<Long, Long> stationIdMap) {
        List<Long> orderedStationIds = new ArrayList<>();
        orderedStationIds.add(beginningUpStation);

        for (int currentIndex = 0; currentIndex < stationIdMap.size(); currentIndex++) {
            final Long currentId = orderedStationIds.get(currentIndex);
            final Long nextId = stationIdMap.get(currentId);
            orderedStationIds.add(nextId);
        }

        return orderedStationIds;
    }
}
