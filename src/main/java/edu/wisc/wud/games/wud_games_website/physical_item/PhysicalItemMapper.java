package edu.wisc.wud.games.wud_games_website.physical_item;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemMapper;
import edu.wisc.wud.games.wud_games_website.location.LocationMapper;
import edu.wisc.wud.games.wud_games_website.location.LocationRepository;

@Component
public class PhysicalItemMapper extends EntityMapper<PhysicalItem, PhysicalItemDTO> {
    private final LocationMapper locationMapper;
    private final BarcodeRepository barcodeRepository;
    private final LocationRepository locationRepository;

    public PhysicalItemMapper(InventoryItemMapper inventoryItemMapper, LocationMapper locationMapper, BarcodeRepository barcodeRepository, LocationRepository locationRepository) {
        super(inventoryItemMapper, () -> new PhysicalItem(), () -> new PhysicalItemDTO());
        this.locationMapper = locationMapper;
        this.barcodeRepository = barcodeRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public PhysicalItemDTO localToDTO(PhysicalItem entity, PhysicalItemDTO dto) {
        if (entity.getLocation() != null) {
            dto.setLocation(locationMapper.toDTO(entity.getLocation()));
        } else {
            dto.setLocation(locationMapper.toDTO(locationRepository.findByName("Unknown").orElseThrow()));
        }
        Barcode barcode = entity.getBarcode();
        if (barcode != null) {
            dto.setBarcode(barcode.getId());
        }
        return dto;
    }

    @Override
    public PhysicalItem localToEntity(PhysicalItemDTO dto, PhysicalItem entity) {
        if (dto.getLocation() != null) {
            entity.setLocation(locationMapper.toEntity(dto.getLocation()));
        } else {
            entity.setLocation(locationRepository.findByName("Unknown").orElseThrow());
        }
        Long existing_code = dto.getBarcode();
        if (existing_code != null) {
            entity.setBarcode(barcodeRepository.findById(existing_code).orElseThrow());
        } else {
            entity.setBarcode(barcodeRepository.save(new Barcode()));
        }
        return entity;
    }
    
}
