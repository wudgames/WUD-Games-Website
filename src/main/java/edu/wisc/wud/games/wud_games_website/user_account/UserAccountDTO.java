package edu.wisc.wud.games.wud_games_website.user_account;

import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAccountDTO {
    
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
