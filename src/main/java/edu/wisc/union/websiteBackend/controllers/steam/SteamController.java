package edu.wisc.union.websiteBackend.controllers.steam;

import edu.wisc.union.websiteBackend.exception.InputErrorException;
import edu.wisc.union.websiteBackend.jpa.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/steam")
public class SteamController {

    private final SteamAccountRepository steamAccountRepository;
    private final SteamGameRepository steamGameRepository;
    private final SteamAccountRequestRepository steamAccountRequestRepository;

    public SteamController(SteamAccountRepository steamAccountRepository,
                           SteamGameRepository steamGameRepository,
                           SteamAccountRequestRepository steamAccountRequestRepository) {
        this.steamAccountRepository = steamAccountRepository;
        this.steamGameRepository = steamGameRepository;
        this.steamAccountRequestRepository = steamAccountRequestRepository;
    }

    // ========================
    // Steam Accounts
    // ========================

    @GetMapping("/accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SteamDTO.SteamAccountDTO>> getAccounts() {
        List<SteamDTO.SteamAccountDTO> accounts = steamAccountRepository.findAll().stream()
                .map(this::toAccountDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SteamDTO.SteamAccountDTO> createAccount(@RequestBody SteamDTO.SteamAccountDTO dto) {
        if (dto.getId() != null) {
            throw new InputErrorException("S101", "You cannot set the ID of a Steam account.");
        }
        if (dto.getSteamAccountUsername() == null || dto.getSteamAccountUsername().isBlank()) {
            throw new InputErrorException("S102", "The 'steamAccountUsername' field is required and cannot be empty.");
        }
        if (steamAccountRepository.existsBySteamAccountUsername(dto.getSteamAccountUsername())) {
            throw new InputErrorException("S103", "A Steam account with that username already exists.");
        }

        SteamAccount account = new SteamAccount();
        account.setSteamAccountUsername(dto.getSteamAccountUsername());
        account.setAvailable(dto.isAvailable());
        account.setNotes(dto.getNotes());
        account.setGamesOnAccount(resolveGameIds(dto.getGameIds()));

        account = steamAccountRepository.save(account);
        return ResponseEntity.status(201).body(toAccountDTO(account));
    }

    @PutMapping("/accounts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SteamDTO.SteamAccountDTO> updateAccount(@PathVariable Long id,
                                                                    @RequestBody SteamDTO.SteamAccountDTO dto) {
        SteamAccount existing = steamAccountRepository.findById(id)
                .orElseThrow(() -> new InputErrorException("S104", "Steam account not found with ID: " + id));

        if (dto.getSteamAccountUsername() == null || dto.getSteamAccountUsername().isBlank()) {
            throw new InputErrorException("S102", "The 'steamAccountUsername' field is required and cannot be empty.");
        }
        if (!existing.getSteamAccountUsername().equalsIgnoreCase(dto.getSteamAccountUsername()) &&
                steamAccountRepository.existsBySteamAccountUsername(dto.getSteamAccountUsername())) {
            throw new InputErrorException("S103", "A Steam account with that username already exists.");
        }

        existing.setSteamAccountUsername(dto.getSteamAccountUsername());
        existing.setAvailable(dto.isAvailable());
        existing.setNotes(dto.getNotes());
        existing.setGamesOnAccount(resolveGameIds(dto.getGameIds()));

        existing = steamAccountRepository.save(existing);
        return ResponseEntity.ok(toAccountDTO(existing));
    }

    @DeleteMapping("/accounts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        SteamAccount account = steamAccountRepository.findById(id)
                .orElseThrow(() -> new InputErrorException("S104", "Steam account not found with ID: " + id));

        // Check for active requests assigned to this account
        List<SteamAccountRequest> activeRequests = steamAccountRequestRepository.findByAssignedAccount(account)
                .stream()
                .filter(r -> "APPROVED".equals(r.getStatus()))
                .collect(Collectors.toList());
        if (!activeRequests.isEmpty()) {
            throw new InputErrorException("S105", "Cannot delete account with active approved requests. Return them first.");
        }

        steamAccountRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ========================
    // Steam Games
    // ========================

    @GetMapping("/games")
    public ResponseEntity<List<SteamDTO.SteamGameDTO>> getGames() {
        List<SteamDTO.SteamGameDTO> games = steamGameRepository.findAll().stream()
                .map(this::toGameDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(games);
    }

    @PostMapping("/games")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SteamDTO.SteamGameDTO> createGame(@RequestBody SteamDTO.SteamGameDTO dto) {
        if (dto.getId() != null) {
            throw new InputErrorException("S201", "You cannot set the ID of a Steam game.");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new InputErrorException("S202", "The 'name' field is required and cannot be empty.");
        }
        if (steamGameRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new InputErrorException("S203", "A Steam game with that name already exists.");
        }

        SteamGame game = new SteamGame();
        BeanUtils.copyProperties(dto, game, "id", "createdAt");
        game.setCheckoutCount(0);

        game = steamGameRepository.save(game);
        return ResponseEntity.status(201).body(toGameDTO(game));
    }

    @PutMapping("/games/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SteamDTO.SteamGameDTO> updateGame(@PathVariable Long id,
                                                             @RequestBody SteamDTO.SteamGameDTO dto) {
        SteamGame existing = steamGameRepository.findById(id)
                .orElseThrow(() -> new InputErrorException("S204", "Steam game not found with ID: " + id));

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new InputErrorException("S202", "The 'name' field is required and cannot be empty.");
        }
        if (!existing.getName().equalsIgnoreCase(dto.getName()) &&
                steamGameRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new InputErrorException("S203", "A Steam game with that name already exists.");
        }

        BeanUtils.copyProperties(dto, existing, "id", "createdAt");
        existing = steamGameRepository.save(existing);
        return ResponseEntity.ok(toGameDTO(existing));
    }

    @DeleteMapping("/games/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        if (!steamGameRepository.existsById(id)) {
            throw new InputErrorException("S204", "Steam game not found with ID: " + id);
        }
        steamGameRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ========================
    // Account Requests (lending flow)
    // ========================

    @GetMapping("/requests")
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
    public ResponseEntity<List<SteamDTO.SteamAccountRequestDTO>> getRequests(
            @RequestParam(required = false) String status) {
        List<SteamAccountRequest> requests;
        if (status != null && !status.isBlank()) {
            requests = steamAccountRequestRepository.findByStatus(status.toUpperCase());
        } else {
            requests = steamAccountRequestRepository.findAll();
        }
        List<SteamDTO.SteamAccountRequestDTO> dtos = requests.stream()
                .map(this::toRequestDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/requests")
    public ResponseEntity<SteamDTO.SteamAccountRequestDTO> createRequest(
            @RequestBody SteamDTO.SteamAccountRequestDTO dto) {
        if (dto.getId() != null) {
            throw new InputErrorException("S301", "You cannot set the ID of a request.");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new InputErrorException("S302", "The 'name' field is required.");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new InputErrorException("S303", "The 'email' field is required.");
        }
        if (dto.getGameName() == null || dto.getGameName().isBlank()) {
            throw new InputErrorException("S304", "The 'gameName' field is required.");
        }
        if (dto.getRentalStartDay() == null) {
            throw new InputErrorException("S305", "The 'rentalStartDay' field is required.");
        }
        if (dto.getRentalEndDay() == null) {
            throw new InputErrorException("S306", "The 'rentalEndDay' field is required.");
        }
        if (dto.getRentalEndDay().isBefore(dto.getRentalStartDay())) {
            throw new InputErrorException("S307", "The 'rentalEndDay' must not be before 'rentalStartDay'.");
        }

        SteamAccountRequest request = new SteamAccountRequest();
        request.setName(dto.getName());
        request.setEmail(dto.getEmail());
        request.setGameName(dto.getGameName());
        request.setComments(dto.getComments());
        request.setRentalStartDay(dto.getRentalStartDay());
        request.setRentalEndDay(dto.getRentalEndDay());
        request.setStatus("PENDING");

        request = steamAccountRequestRepository.save(request);
        return ResponseEntity.status(201).body(toRequestDTO(request));
    }

    @PutMapping("/requests/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<SteamDTO.SteamAccountRequestDTO> approveRequest(
            @PathVariable Long id,
            @RequestParam Long accountId) {
        SteamAccountRequest request = steamAccountRequestRepository.findById(id)
                .orElseThrow(() -> new InputErrorException("S308", "Request not found with ID: " + id));

        if (!"PENDING".equals(request.getStatus())) {
            throw new InputErrorException("S309", "Only PENDING requests can be approved. Current status: " + request.getStatus());
        }

        SteamAccount account = steamAccountRepository.findById(accountId)
                .orElseThrow(() -> new InputErrorException("S104", "Steam account not found with ID: " + accountId));

        if (!account.isAvailable()) {
            throw new InputErrorException("S310", "The selected Steam account is not currently available.");
        }

        request.setStatus("APPROVED");
        request.setAssignedAccount(account);
        account.setAvailable(false);

        steamAccountRepository.save(account);
        request = steamAccountRequestRepository.save(request);
        return ResponseEntity.ok(toRequestDTO(request));
    }

    @PutMapping("/requests/{id}/deny")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SteamDTO.SteamAccountRequestDTO> denyRequest(@PathVariable Long id) {
        SteamAccountRequest request = steamAccountRequestRepository.findById(id)
                .orElseThrow(() -> new InputErrorException("S308", "Request not found with ID: " + id));

        if (!"PENDING".equals(request.getStatus())) {
            throw new InputErrorException("S309", "Only PENDING requests can be denied. Current status: " + request.getStatus());
        }

        request.setStatus("DENIED");
        request = steamAccountRequestRepository.save(request);
        return ResponseEntity.ok(toRequestDTO(request));
    }

    @PutMapping("/requests/{id}/return")
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<SteamDTO.SteamAccountRequestDTO> returnRequest(@PathVariable Long id) {
        SteamAccountRequest request = steamAccountRequestRepository.findById(id)
                .orElseThrow(() -> new InputErrorException("S308", "Request not found with ID: " + id));

        if (!"APPROVED".equals(request.getStatus())) {
            throw new InputErrorException("S311", "Only APPROVED requests can be returned. Current status: " + request.getStatus());
        }

        request.setStatus("RETURNED");

        SteamAccount account = request.getAssignedAccount();
        if (account != null) {
            account.setAvailable(true);
            steamAccountRepository.save(account);
        }

        request = steamAccountRequestRepository.save(request);
        return ResponseEntity.ok(toRequestDTO(request));
    }

    // ========================
    // DTO conversion helpers
    // ========================

    private SteamDTO.SteamAccountDTO toAccountDTO(SteamAccount account) {
        SteamDTO.SteamAccountDTO dto = new SteamDTO.SteamAccountDTO();
        dto.setId(account.getId());
        dto.setSteamAccountUsername(account.getSteamAccountUsername());
        dto.setAvailable(account.isAvailable());
        dto.setNotes(account.getNotes());
        dto.setCreatedAt(account.getCreatedAt());
        if (account.getGamesOnAccount() != null) {
            dto.setGameIds(account.getGamesOnAccount().stream()
                    .map(SteamGame::getId)
                    .collect(Collectors.toSet()));
        }
        return dto;
    }

    private SteamDTO.SteamGameDTO toGameDTO(SteamGame game) {
        SteamDTO.SteamGameDTO dto = new SteamDTO.SteamGameDTO();
        BeanUtils.copyProperties(game, dto);
        return dto;
    }

    private SteamDTO.SteamAccountRequestDTO toRequestDTO(SteamAccountRequest request) {
        SteamDTO.SteamAccountRequestDTO dto = new SteamDTO.SteamAccountRequestDTO();
        dto.setId(request.getId());
        dto.setStatus(request.getStatus());
        dto.setName(request.getName());
        dto.setEmail(request.getEmail());
        dto.setGameName(request.getGameName());
        dto.setComments(request.getComments());
        dto.setRentalStartDay(request.getRentalStartDay());
        dto.setRentalEndDay(request.getRentalEndDay());
        dto.setCreatedAt(request.getCreatedAt());
        if (request.getAssignedAccount() != null) {
            dto.setAssignedAccountId(request.getAssignedAccount().getId());
        }
        return dto;
    }

    private Set<SteamGame> resolveGameIds(Set<Long> gameIds) {
        if (gameIds == null || gameIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<SteamGame> games = new HashSet<>();
        for (Long gameId : gameIds) {
            SteamGame game = steamGameRepository.findById(gameId)
                    .orElseThrow(() -> new InputErrorException("S204", "Steam game not found with ID: " + gameId));
            games.add(game);
        }
        return games;
    }
}
