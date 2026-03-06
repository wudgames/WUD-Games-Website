package edu.wisc.wud.games.wud_games_website.general_dis;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GeneralDisRepository extends JpaRepository<GeneralDis, Long> {

    List<GeneralDis> findAllByTagsId(Long id);

}

