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
    private Boolean isHost = false;
    private float hoursHosted;
    private Boolean isPhysicalInventoryManager = false;
    private Boolean isDigitalInventoryManager = false;
    private Boolean isRentalsManager = false;
    private Boolean isEventsManager = false;
    private Boolean isMetadataManager = false;
    private Boolean isAdmin = false;
    private OffsetDateTime lastLogin;
}
