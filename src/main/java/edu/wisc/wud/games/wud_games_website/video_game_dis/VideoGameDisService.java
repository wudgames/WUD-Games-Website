package edu.wisc.wud.games.wud_games_website.video_game_dis;

import edu.wisc.wud.games.wud_games_website.events.BeforeDeleteVideoGameDis;
import edu.wisc.wud.games.wud_games_website.util.CustomCollectors;
import edu.wisc.wud.games.wud_games_website.util.NotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service("VideoGameDisService")
public class VideoGameDisService {

    private final VideoGameDisRepository videoGameDisRepository;
    private final ApplicationEventPublisher publisher;

    public VideoGameDisService(final VideoGameDisRepository videoGameDisRepository,
            final ApplicationEventPublisher publisher) {
        this.videoGameDisRepository = videoGameDisRepository;
        this.publisher = publisher;
    }

    public List<VideoGameDisDTO> findAll() {
        final List<VideoGameDis> videoGameDises = videoGameDisRepository.findAll(Sort.by("id"));
        return videoGameDises.stream()
                .map(videoGameDis -> mapToDTO(videoGameDis, new VideoGameDisDTO()))
                .toList();
    }

    public VideoGameDisDTO get(final Long id) {
        return videoGameDisRepository.findById(id)
                .map(videoGameDis -> mapToDTO(videoGameDis, new VideoGameDisDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final VideoGameDisDTO videoGameDisDTO) {
        final VideoGameDis videoGameDis = new VideoGameDis();
        mapToEntity(videoGameDisDTO, videoGameDis);
        return videoGameDisRepository.save(videoGameDis).getId();
    }

    public void update(final Long id, final VideoGameDisDTO videoGameDisDTO) {
        final VideoGameDis videoGameDis = videoGameDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(videoGameDisDTO, videoGameDis);
        videoGameDisRepository.save(videoGameDis);
    }

    public void delete(final Long id) {
        final VideoGameDis videoGameDis = videoGameDisRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteVideoGameDis(id));
        videoGameDisRepository.delete(videoGameDis);
    }

    private VideoGameDisDTO mapToDTO(final VideoGameDis videoGameDis,
            final VideoGameDisDTO videoGameDisDTO) {
        videoGameDisDTO.setId(videoGameDis.getId());
        return videoGameDisDTO;
    }

    private VideoGameDis mapToEntity(final VideoGameDisDTO videoGameDisDTO,
            final VideoGameDis videoGameDis) {
        return videoGameDis;
    }

    public Map<Long, Long> getVideoGameDisValues() {
        return videoGameDisRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(VideoGameDis::getId, VideoGameDis::getId));
    }

}

