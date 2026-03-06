package edu.wisc.wud.games.wud_games_website.equipment_dis;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/equipmentDiss", produces = MediaType.APPLICATION_JSON_VALUE)
public class EquipmentDisResource {

    private final EquipmentDisService equipmentDisService;

    public EquipmentDisResource(final EquipmentDisService equipmentDisService) {
        this.equipmentDisService = equipmentDisService;
    }

    @GetMapping
    public ResponseEntity<List<EquipmentDisDTO>> getAllEquipmentDiss() {
        return ResponseEntity.ok(equipmentDisService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentDisDTO> getEquipmentDis(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(equipmentDisService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createEquipmentDis(
            @RequestBody @Valid final EquipmentDisDTO equipmentDisDTO) {
        final Long createdId = equipmentDisService.create(equipmentDisDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateEquipmentDis(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final EquipmentDisDTO equipmentDisDTO) {
        equipmentDisService.update(id, equipmentDisDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipmentDis(@PathVariable(name = "id") final Long id) {
        equipmentDisService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

