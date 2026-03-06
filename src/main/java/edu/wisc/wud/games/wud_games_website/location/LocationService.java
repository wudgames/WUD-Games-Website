package edu.wisc.wud.games.wud_games_website.location;

import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteLocation;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final ApplicationEventPublisher publisher;

    public LocationService(final LocationRepository locationRepository,
            final ApplicationEventPublisher publisher) {
        this.locationRepository = locationRepository;
        this.publisher = publisher;
    }

    public List<LocationDTO> findAll() {
        final List<Location> locations = locationRepository.findAll(Sort.by("id"));
        return locations.stream()
                .map(location -> mapToDTO(location, new LocationDTO()))
                .toList();
    }

    public LocationDTO get(final Long id) {
        return locationRepository.findById(id)
                .map(location -> mapToDTO(location, new LocationDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final LocationDTO locationDTO) {
        final Location location = new Location();
        mapToEntity(locationDTO, location);
        return locationRepository.save(location).getId();
    }

    public void update(final Long id, final LocationDTO locationDTO) {
        final Location location = locationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(locationDTO, location);
        locationRepository.save(location);
    }

    public void delete(final Long id) {
        final Location location = locationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteLocation(id));
        locationRepository.delete(location);
    }

    private LocationDTO mapToDTO(final Location location, final LocationDTO locationDTO) {
        locationDTO.setId(location.getId());
        locationDTO.setName(location.getName());
        return locationDTO;
    }

    private Location mapToEntity(final LocationDTO locationDTO, final Location location) {
        location.setName(locationDTO.getName());
        return location;
    }

    public Map<Long, String> getLocationValues() {
        return locationRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Location::getId, Location::getName));
    }

}

