package edu.wisc.wud.games.wud_games_website.user_account;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import edu.wisc.wud.games.wud_games_website.general_dis.EntityMapper;

@Component
public class UserAccountMapper extends EntityMapper<UserAccount, UserAccountDTO> {

    private PasswordEncoder encoder;
    private final UserAccountRepository userAccountRepository;

    public UserAccountMapper(final UserAccountRepository userAccountRepository, PasswordEncoder encoder) {
        super(null, () -> new UserAccount(), () -> new UserAccountDTO());
        
        this.userAccountRepository = userAccountRepository;
        this.encoder = encoder;
    }

    @Override
    protected UserAccountDTO localToDTO(UserAccount entity, UserAccountDTO dto) {
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        //userAccountDTO.setPassword(userAccount.getPassword());
        dto.setIsHost(entity.isHost());
        dto.setHoursHosted(entity.getHoursHosted());
        dto.setIsPhysicalInventoryManager(entity.isPhysicalInventoryManager());
        dto.setIsDigitalInventoryManager(entity.isDigitalInventoryManager());
        dto.setIsRentalsManager(entity.isRentalsManager());
        dto.setIsEventsManager(entity.isEventsManager());
        dto.setIsMetadataManager(entity.isMetaDataManager());
        dto.setIsAdmin(entity.isAdmin());
        dto.setLastLogin(entity.getLastLogin());
        return dto;
    }

    @Override
    protected UserAccount localToEntity(UserAccountDTO dto, UserAccount entity) {
        Long id = dto.getId();
        entity.setId(id);
        entity.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            entity.setPassword(encoder.encode(dto.getPassword()));
        } else if (id != null) {
            UserAccount existingAccount = userAccountRepository.findById(id).orElse(null);
            entity.setPassword(existingAccount.getPassword());
        }
        entity.setHost(dto.getIsHost());
        entity.setHoursHosted(dto.getHoursHosted());
        entity.setPhysicalInventoryManager(dto.getIsPhysicalInventoryManager());
        entity.setDigitalInventoryManager(dto.getIsDigitalInventoryManager());
        entity.setRentalsManager(dto.getIsRentalsManager());
        entity.setEventsManager(dto.getIsEventsManager());
        entity.setMetaDataManager(dto.getIsMetadataManager());
        entity.setAdmin(dto.getIsAdmin());
        entity.setLastLogin(dto.getLastLogin());
        return entity;
    }
    
}
