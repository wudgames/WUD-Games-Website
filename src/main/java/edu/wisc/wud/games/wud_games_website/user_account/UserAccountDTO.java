package edu.wisc.wud.games.wud_games_website.user_account;

import java.time.OffsetDateTime;

import edu.wisc.wud.games.wud_games_website.util.HasId;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAccountDTO implements HasId {
    
    private Long id;

    private String email;
    // password is never sent to client
    // There need to be Boolean (not boolean) for Thymleaf and Lambok to pick the same names.
    private Boolean isHost;
    private float hoursHosted;
    private Boolean isPhysicalInventoryManager;
    private Boolean isDigitalInventoryManager;
    private Boolean isRentalsManager;
    private Boolean isEventsManager;
    private Boolean isMetadataManager;
    private Boolean isAdmin;
    private OffsetDateTime lastLogin;
}
