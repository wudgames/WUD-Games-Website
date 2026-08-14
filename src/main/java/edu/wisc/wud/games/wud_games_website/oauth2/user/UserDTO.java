package edu.wisc.wud.games.wud_games_website.oauth2.user;

import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    
    private Long id;

    private String email;
    //private String password;

    private boolean isHost;
    private float hoursHosted;
    private boolean isPhysicalInventoryManager;
    private boolean isDigitalInventoryManager;
    private boolean isRentalsManager;
    private boolean isEventsManager;
    private boolean isMetaDataManager;
    private boolean isAdmin;
    private OffsetDateTime lastLogin;
}
