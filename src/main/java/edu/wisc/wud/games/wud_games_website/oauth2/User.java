package edu.wisc.wud.games.wud_games_website.oauth2;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(unique = true, nullable = false)	
	private String email;

    @Column
    private String password;

	@Column
    private boolean isHost;
    // Allows reading, creating, updating, and deleting: some checkout records
    // Allows updating: event atendence

    @Column
    private float hoursHosted;

    @Column
    private boolean canManagePhysicalInventory;
    // Allows creating, updating, and deleting: all physical items
    // Allows creating, updating, and deleting descriptions of: consoles, equipment, physical games and game expantion

    @Column
    private boolean canManageDigitalInventory;
    // Allows creating, updating, and deleting all digital items
    // Allows creating, updating, and deleting descriptions of: accounts, digital games

    @Column
    private boolean canManageRentals;
    // Allows reading, creating, updating, and deleting: some rental records
    // Allows reading, creating, updating, and deleting: some checkout records

    @Column
    private boolean canManageEvents;
    // Allows reading, creating, updating, and deleting: some event records

    @Column
    private boolean canManageMetaData;
    // Allows reading, creating, updating, and deleting: tags and locations

    @Column
    private boolean isAdmin;
    // Allows reading, creating, updating, and deleting: user records
    // May not allow some descructive actions

    @Column
    private OffsetDateTime lastLogin;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

	@Override
	public String getUsername() {
		return this.email;
	}

    @Override
    public @Nullable String getPassword() {
        return this.password;
    }
	
    @Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if(this.isAdmin) authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if(this.canManageEvents) authorities.add(new SimpleGrantedAuthority("ROLE_EVENTS_MANAGER"));
        if(this.canManageMetaData) authorities.add(new SimpleGrantedAuthority("ROLE_METADATA_MANAGER"));
        if(this.canManagePhysicalInventory) authorities.add(new SimpleGrantedAuthority("ROLE_PHYSICAL_INVENTORY_MANAGER"));
        if(this.canManageDigitalInventory) authorities.add(new SimpleGrantedAuthority("ROLE_DIGITAL_INVENTORY_MANAGER"));
        if(this.canManageRentals) authorities.add(new SimpleGrantedAuthority("ROLE_RENTALS_MANAGER"));
        if(this.isHost) authorities.add(new SimpleGrantedAuthority("ROLE_HOST"));

		return authorities;
	}
}