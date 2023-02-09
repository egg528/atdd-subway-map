package subway.domain;

import subway.common.DomainException;
import subway.common.DomainExceptionType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "lineId", cascade = CascadeType.PERSIST)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Long downStationId, Section section) {
        if(!isLineDownStationEqualsSectionUpStation(downStationId, section.getUpStation()))
            throw new DomainException(DomainExceptionType.UPDOWN_STATION_MISS_MATCH);

        if(isContainStation(section.getDownStation()))
            throw new DomainException(DomainExceptionType.DOWN_STATION_EXIST_IN_LINE);

        sections.add(section);
    }

    public void deletionSection(Long downStationId, Station station) {
        if(!isLineDownStation(downStationId, station))
            throw new DomainException(DomainExceptionType.NOT_DOWN_STATION);

        if(hasOnlyOneSection())
            throw new DomainException(DomainExceptionType.CANT_DELETE_SECTION);

        sections.remove(station);
    }

    public List<Station> getStations() {
        var upStations = sections.stream().map(Section::getUpStation);
        var downStations = sections.stream().map(Section::getDownStation);

        return Stream.concat(upStations, downStations).distinct().collect(Collectors.toList());
    }

    public Integer getSectionCount() {
        return sections.size();
    }

    public Section getSectionByDownStatoinId(Long downStationId) {
        return sections.stream()
                .filter(e -> e.getDownStation().getId() == downStationId)
                .findFirst()
                .get();
    }

    private boolean isLineDownStationEqualsSectionUpStation(Long downStationId, Station newSectionUpStation){
        return downStationId == newSectionUpStation.getId();
    }

    private boolean isContainStation(Station station){
        return sections.contains(station);
    }

    private boolean hasOnlyOneSection(){
        return sections.size() == 1;
    }

    private boolean isLineDownStation(Long downStationId, Station station){
        return downStationId == station.getId();
    }
}
