package edu.wisc.wud.games.wud_games_website.physical_item;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;
import edu.wisc.wud.games.wud_games_website.inventory_item.InventoryItemMapper;
import edu.wisc.wud.games.wud_games_website.location.LocationMapper;

@Component
public class PhysicalItemMapper extends EntityMapper<PhysicalItem, PhysicalItemDTO> {
    private final LocationMapper locationMapper;
    private final BarcodeRepository barcodeRepository;

    public PhysicalItemMapper(InventoryItemMapper inventoryItemMapper, LocationMapper locationMapper, BarcodeRepository barcodeRepository) {
        super(inventoryItemMapper, () -> new PhysicalItem(), () -> new PhysicalItemDTO());
        this.locationMapper = locationMapper;
        this.barcodeRepository = barcodeRepository;
    }

    @Override
    public PhysicalItemDTO localToDTO(PhysicalItem entity, PhysicalItemDTO dto) {
        dto.setLocation(locationMapper.toDTO(entity.getLocation()));
        Barcode barcode = entity.getBarcode();
        if (barcode != null) {
            dto.setBarcode(barcode.getId());
        }
        return dto;
    }

    @Override
    public PhysicalItem localToEntity(PhysicalItemDTO dto, PhysicalItem entity) {
        entity.setLocation(locationMapper.toEntity(dto.getLocation()));
        Long existing_code = dto.getBarcode();
        if (existing_code != null) {
            entity.setBarcode(barcodeRepository.findById(existing_code).orElseThrow());
        } else {
            entity.setBarcode(barcodeRepository.save(new Barcode()));
        }
        return entity;
    }
    
}
