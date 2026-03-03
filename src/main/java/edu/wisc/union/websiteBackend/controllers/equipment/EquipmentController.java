package edu.wisc.union.websiteBackend.controllers.equipment;

import edu.wisc.union.websiteBackend.auth.JwtUtil;
import edu.wisc.union.websiteBackend.exception.InputErrorException;
import edu.wisc.union.websiteBackend.jpa.Equipment;
import edu.wisc.union.websiteBackend.jpa.EquipmentCheckout;
import edu.wisc.union.websiteBackend.jpa.EquipmentCheckoutRepository;
import edu.wisc.union.websiteBackend.jpa.EquipmentRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentCheckoutRepository equipmentCheckoutRepository;
    private final JwtUtil jwtUtil;

    public EquipmentController(EquipmentRepository equipmentRepository,
                               EquipmentCheckoutRepository equipmentCheckoutRepository,
                               JwtUtil jwtUtil) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentCheckoutRepository = equipmentCheckoutRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<List<Equipment>> getEquipment(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type) {
        List<Equipment> equipment = equipmentRepository.findFiltered(name, type, Sort.by("name"));

        if (JwtUtil.AccessLevel.ANONYMOUS.equals(jwtUtil.getCurrentAccessLevel())) {
            for (Equipment item : equipment) {
                item.setInternalNotes(null);
                item.setCheckoutCount(null);
            }
        }

        return ResponseEntity.ok(equipment);
    }

    @GetMapping("/types")
    public ResponseEntity<List<String>> getTypes() {
        List<String> types = equipmentRepository.findDistinctTypes();
        return ResponseEntity.ok(types);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentDTO> addEquipment(@RequestBody EquipmentDTO dto) {
        if (dto.getId() != null) {
            throw new InputErrorException("E102", "You cannot set the ID of equipment");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new InputErrorException("E103", "The 'name' field is required and cannot be empty or blank.");
        }
        if (equipmentRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new InputErrorException("E104", "Equipment with that name already exists.");
        }

        Equipment equipment = new Equipment();
        BeanUtils.copyProperties(dto, equipment, "createdAt");
        equipment.setAvailableCopies(equipment.getQuantity());
        equipment.setCheckoutCount(0);

        equipment = equipmentRepository.save(equipment);
        dto.setId(equipment.getId());
        dto.setCreatedAt(equipment.getCreatedAt());
        return ResponseEntity.status(201).body(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentDTO> updateEquipment(@PathVariable Long id, @RequestBody EquipmentDTO dto) {
        Equipment existing = equipmentRepository.findById(id)
                .orElseThrow(() -> new InputErrorException("E105", "Equipment not found with ID: " + id));

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new InputErrorException("E103", "The 'name' field is required and cannot be empty or blank.");
        }
        if (!existing.getName().equalsIgnoreCase(dto.getName()) &&
                equipmentRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new InputErrorException("E104", "Equipment with that name already exists.");
        }

        BeanUtils.copyProperties(dto, existing, "id", "createdAt");
        existing = equipmentRepository.save(existing);

        EquipmentDTO updated = new EquipmentDTO();
        BeanUtils.copyProperties(existing, updated);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentDTO> patchEquipment(@PathVariable Long id, @RequestBody EquipmentDTO updates) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new InputErrorException("E105", "Equipment not found with ID: " + id));

        if (updates.getName() != null) equipment.setName(updates.getName());
        if (updates.getType() != null) equipment.setType(updates.getType());
        if (updates.getDescription() != null) equipment.setDescription(updates.getDescription());
        if (updates.getImageUrl() != null) equipment.setImageUrl(updates.getImageUrl());
        if (updates.getQuantity() != null) equipment.setQuantity(updates.getQuantity());
        if (updates.getAvailableCopies() != null) equipment.setAvailableCopies(updates.getAvailableCopies());
        if (updates.getCheckoutCount() != null) equipment.setCheckoutCount(updates.getCheckoutCount());
        if (updates.getInternalNotes() != null) equipment.setInternalNotes(updates.getInternalNotes());
        if (updates.getLocation() != null) equipment.setLocation(updates.getLocation());

        if (equipment.getName() == null || equipment.getName().isBlank()) {
            throw new InputErrorException("E103", "The 'name' field is required and cannot be empty or blank.");
        }
        if (equipmentRepository.existsByNameIgnoreCase(equipment.getName()) && !equipment.getId().equals(id)) {
            throw new InputErrorException("E104", "Equipment with that name already exists.");
        }

        equipmentRepository.save(equipment);

        EquipmentDTO updated = new EquipmentDTO();
        BeanUtils.copyProperties(equipment, updated);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new InputErrorException("E105", "Equipment not found with ID: " + id));

        equipmentCheckoutRepository.deleteByEquipment(equipment);
        equipmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/checkout")
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
    public ResponseEntity<String> checkoutEquipment(@PathVariable Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new InputErrorException("E105", "Equipment not found with ID: " + id));

        if (equipment.getAvailableCopies() == null) {
            if (equipment.getQuantity() == null) {
                equipment.setQuantity(1);
                equipment.setAvailableCopies(1);
            } else {
                equipment.setAvailableCopies(equipment.getQuantity());
            }
        }
        if (equipment.getAvailableCopies() <= 0) {
            throw new InputErrorException("E106", "No copies available for checkout.");
        }

        EquipmentCheckout checkout = new EquipmentCheckout();
        checkout.setEquipment(equipment);
        checkout.setCheckedOutAt(LocalDateTime.now(ZoneId.of("America/Chicago")));
        String username = jwtUtil.getCurrentUsername();
        checkout.setCheckedOutBy(username != null ? username : "anonymous");
        checkout.setActive(true);
        equipmentCheckoutRepository.save(checkout);

        equipment.setAvailableCopies(equipment.getAvailableCopies() - 1);
        equipment.setCheckoutCount((equipment.getCheckoutCount() != null ? equipment.getCheckoutCount() : 0) + 1);
        equipmentRepository.save(equipment);

        return ResponseEntity.ok("Equipment checked out successfully.");
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
    public ResponseEntity<String> returnEquipment(@PathVariable Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new InputErrorException("E105", "Equipment not found with ID: " + id));

        if (equipment.getAvailableCopies() >= equipment.getQuantity()) {
            throw new InputErrorException("E107", "Cannot return equipment, all items already returned");
        }

        List<EquipmentCheckout> activeCheckouts = equipmentCheckoutRepository.findByEquipmentAndActiveTrue(equipment);
        if (!activeCheckouts.isEmpty()) {
            EquipmentCheckout oldestCheckout = activeCheckouts.get(0);
            oldestCheckout.setActive(false);
            oldestCheckout.setReturnedAt(LocalDateTime.now(ZoneId.of("America/Chicago")));
            equipmentCheckoutRepository.save(oldestCheckout);
        }

        equipment.setAvailableCopies(equipment.getAvailableCopies() + 1);
        equipmentRepository.save(equipment);

        return ResponseEntity.ok("Equipment returned successfully.");
    }

    @PutMapping("/return-all")
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<List<EquipmentReturnResponse>> returnAllEquipment() {
        List<Equipment> updatedEquipment = equipmentRepository.findAll().stream()
                .filter(item -> item.getAvailableCopies() == null || !item.getAvailableCopies().equals(item.getQuantity()))
                .peek(item -> item.setAvailableCopies(item.getQuantity()))
                .collect(Collectors.toList());

        equipmentRepository.saveAll(updatedEquipment);

        return ResponseEntity.ok(updatedEquipment.stream()
                .map(item -> new EquipmentReturnResponse(item.getId(), item.getName(), item.getQuantity()))
                .collect(Collectors.toList()));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EquipmentReturnResponse {
        private Long id;
        private String name;
        private Integer quantity;
    }
}
