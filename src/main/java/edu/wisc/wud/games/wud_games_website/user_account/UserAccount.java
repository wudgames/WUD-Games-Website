package edu.wisc.wud.games.wud_games_website.user_account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class UserAccount implements UserDetails {

	@Id
	@SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
	private Long id;

	@Column(unique = true, length = 255, nullable = false)
	private String email;
    
    @Column
    @Nullable
    private String password;

	@Column
    private boolean isHost;
    // Allows reading, creating, updating, and deleting: some checkout records
    // Allows updating: event atendence

    @Column
    private float hoursHosted;

    @Column
    private boolean isPhysicalInventoryManager;
    // Allows creating, updating, and deleting: all physical items
    // Allows creating, updating, and deleting descriptions of: consoles, equipment, physical games and game expantion

    @Column
    private boolean isDigitalInventoryManager;
    // Allows creating, updating, and deleting all digital items
    // Allows creating, updating, and deleting descriptions of: accounts, digital games

    @Column
    private boolean isRentalsManager;
    // Allows reading, creating, updating, and deleting: some rental records
    // Allows reading, creating, updating, and deleting: some checkout records

    @Column
    private boolean isEventsManager;
    // Allows reading, creating, updating, and deleting: some event records

    @Column
    private boolean isMetaDataManager;
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
        return null;
    }
	
    @Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if(this.isAdmin) authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if(this.isEventsManager) authorities.add(new SimpleGrantedAuthority("ROLE_EVENTS_MANAGER"));
        if(this.isMetaDataManager) authorities.add(new SimpleGrantedAuthority("ROLE_METADATA_MANAGER"));
        if(this.isPhysicalInventoryManager) authorities.add(new SimpleGrantedAuthority("ROLE_PHYSICAL_INVENTORY_MANAGER"));
        if(this.isDigitalInventoryManager) authorities.add(new SimpleGrantedAuthority("ROLE_DIGITAL_INVENTORY_MANAGER"));
        if(this.isRentalsManager) authorities.add(new SimpleGrantedAuthority("ROLE_RENTALS_MANAGER"));
        if(this.isHost) authorities.add(new SimpleGrantedAuthority("ROLE_HOST"));

		return authorities;
	}
}