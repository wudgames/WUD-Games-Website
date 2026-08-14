package edu.wisc.wud.games.wud_games_website.oauth2.user;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;

    public UserService(final UserRepository userRepository, final ApplicationEventPublisher publisher) {
        this.userRepository = userRepository;
        this.publisher = publisher;
    }

    public List<UserDTO> findAll() {
        final List<User> users = userRepository.findAll(Sort.by("id"));
        return users.stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .toList();
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        //userDTO.setPassword(user.getPassword());
        userDTO.setHost(user.isHost());
        userDTO.setHoursHosted(user.getHoursHosted());
        userDTO.setPhysicalInventoryManager(user.isPhysicalInventoryManager());
        userDTO.setDigitalInventoryManager(user.isDigitalInventoryManager());
        userDTO.setRentalsManager(user.isRentalsManager());
        userDTO.setEventsManager(user.isEventsManager());
        userDTO.setMetaDataManager(user.isMetaDataManager());
        userDTO.setAdmin(user.isAdmin());
        userDTO.setLastLogin(user.getLastLogin());
        return userDTO;
    }
}
