package edu.wisc.wud.games.wud_games_website.user_account;

import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;

@Component
public class UserAccountMapper extends EntityMapper<UserAccount, UserAccountDTO> {

    public UserAccountMapper() {
        super(null, () -> new UserAccount(), () -> new UserAccountDTO());
        //TODO Auto-generated constructor stub
    }

    @Override
    protected UserAccountDTO localToDTO(UserAccount entity, UserAccountDTO dto) {
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        //userAccountDTO.setPassword(userAccount.getPassword());
        dto.setHost(entity.isHost());
        dto.setHoursHosted(entity.getHoursHosted());
        dto.setPhysicalInventoryManager(entity.isPhysicalInventoryManager());
        dto.setDigitalInventoryManager(entity.isDigitalInventoryManager());
        dto.setRentalsManager(entity.isRentalsManager());
        dto.setEventsManager(entity.isEventsManager());
        dto.setMetaDataManager(entity.isMetaDataManager());
        dto.setAdmin(entity.isAdmin());
        dto.setLastLogin(entity.getLastLogin());
        return dto;
    }

    @Override
    protected UserAccount localToEntity(UserAccountDTO dto, UserAccount entity) {
        entity.setId(dto.getId());
        entity.setEmail(dto.getEmail());
        //userAccount.setPassword(userAccountDTO.getPassword());
        entity.setHost(dto.isHost());
        entity.setHoursHosted(dto.getHoursHosted());
        entity.setPhysicalInventoryManager(dto.isPhysicalInventoryManager());
        entity.setDigitalInventoryManager(dto.isDigitalInventoryManager());
        entity.setRentalsManager(dto.isRentalsManager());
        entity.setEventsManager(dto.isEventsManager());
        entity.setMetaDataManager(dto.isMetaDataManager());
        entity.setAdmin(dto.isAdmin());
        entity.setLastLogin(dto.getLastLogin());
        return entity;
    }
    
}
