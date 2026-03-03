package edu.wisc.union.websiteBackend.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SteamAccountRequestRepository extends JpaRepository<SteamAccountRequest, Long> {
    List<SteamAccountRequest> findByStatus(String status);

    List<SteamAccountRequest> findByAssignedAccount(SteamAccount account);
}
