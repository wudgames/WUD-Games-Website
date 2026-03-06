package edu.wisc.wud.games.wud_games_website.equipment;

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
@RequestMapping(value = "/api/equipments", produces = MediaType.APPLICATION_JSON_VALUE)
public class EquipmentResource {

    private final EquipmentService equipmentService;

    public EquipmentResource(final EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public ResponseEntity<List<EquipmentDTO>> getAllEquipments() {
        return ResponseEntity.ok(equipmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentDTO> getEquipment(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(equipmentService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createEquipment(
            @RequestBody @Valid final EquipmentDTO equipmentDTO) {
        final Long createdId = equipmentService.create(equipmentDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateEquipment(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final EquipmentDTO equipmentDTO) {
        equipmentService.update(id, equipmentDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable(name = "id") final Long id) {
        equipmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

