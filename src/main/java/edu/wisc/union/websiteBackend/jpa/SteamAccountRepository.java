package edu.wisc.union.websiteBackend.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SteamAccountRepository extends JpaRepository<SteamAccount, Long> {
    List<SteamAccount> findByAvailableTrue();

    boolean existsBySteamAccountUsername(String username);
}
