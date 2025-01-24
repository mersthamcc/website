package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.entity.MemberEntity;
import cricket.merstham.graphql.entity.PasskitDeviceRegistrationEntity;
import cricket.merstham.graphql.repository.MemberEntityRepository;
import cricket.merstham.graphql.repository.PassKitDeviceRegistrationEntityRepository;
import cricket.merstham.graphql.services.PassGeneratorService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.text.MessageFormat.format;
import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/passkit/v1")
public class ApplePassKitController {
    public static Logger LOG = LoggerFactory.getLogger(ApplePassKitController.class);

    private final MemberEntityRepository memberEntityRepository;
    private final PassKitDeviceRegistrationEntityRepository
            passKitDeviceRegistrationEntityRepository;
    private final String registeredPassTypeIdentifier;
    private final PassGeneratorService passGeneratorService;

    @Autowired
    public ApplePassKitController(
            MemberEntityRepository memberEntityRepository,
            PassKitDeviceRegistrationEntityRepository passKitDeviceRegistrationEntityRepository,
            @Value("${configuration.wallet.apple.pass-identifier}")
                    String registeredPassTypeIdentifier,
            PassGeneratorService passGeneratorService) {
        this.memberEntityRepository = memberEntityRepository;
        this.passKitDeviceRegistrationEntityRepository = passKitDeviceRegistrationEntityRepository;
        this.registeredPassTypeIdentifier = registeredPassTypeIdentifier;
        this.passGeneratorService = passGeneratorService;
    }

    @PostMapping(
            path =
                    "/devices/{deviceLibraryIdentifier}/registrations/{passTypeIdentifier}/{serialNumber}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> registerDevice(
            @RequestHeader("Authorization") String authToken,
            @PathVariable String deviceLibraryIdentifier,
            @PathVariable String passTypeIdentifier,
            @PathVariable String serialNumber,
            @RequestBody Map<String, Object> body) {
        if (!registeredPassTypeIdentifier.equals(passTypeIdentifier)) {
            return ResponseEntity.notFound().build();
        }
        LOG.info("Received request to register device for member {}", authToken);

        var member = authorise(authToken);
        if (member.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        LOG.info("Found member {}", member.get().getId());

        var serialAuthorisationErrors = validateSerialNumber(member, serialNumber);
        if (serialAuthorisationErrors.isPresent()) {
            return serialAuthorisationErrors.get();
        }

        var pushToken = body.get("pushToken").toString();
        var registration =
                passKitDeviceRegistrationEntityRepository
                        .findFirstByDeviceLibraryIdentifier(deviceLibraryIdentifier)
                        .orElseGet(
                                () ->
                                        PasskitDeviceRegistrationEntity.builder()
                                                .deviceLibraryIdentifier(deviceLibraryIdentifier)
                                                .pushToken(pushToken)
                                                .members(new HashSet<>())
                                                .build());

        if (registration.getMembers().contains(member.get())) {
            LOG.info("Device already exists for member {}", member.get().getId());
            return ResponseEntity.ok().build();
        }

        registration.getMembers().add(member.get());
        passKitDeviceRegistrationEntityRepository.saveAndFlush(registration);
        LOG.info("Successfully registered device for member {}", member.get().getId());
        return ResponseEntity.status(201).build();
    }

    @GetMapping(
            path = "/devices/{deviceLibraryIdentifier}/registrations/{passTypeIdentifier}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getUpdatablePasses(
            @PathVariable String deviceLibraryIdentifier,
            @PathVariable String passTypeIdentifier,
            @RequestParam(required = false) String passesUpdatedSince) {
        if (!registeredPassTypeIdentifier.equals(passTypeIdentifier)) {
            return ResponseEntity.notFound().build();
        }

        LOG.info("Received request to get device registrations");

        var registration =
                passKitDeviceRegistrationEntityRepository.findFirstByDeviceLibraryIdentifier(
                        deviceLibraryIdentifier);

        if (registration.isEmpty()) {
            return ResponseEntity.status(204).build();
        }

        var result = new HashMap<String, Object>();
        var passes = new ArrayList<String>();
        result.put("lastUpdated", Long.toString(Instant.now().getEpochSecond()));
        if (nonNull(registration.get().getMembers())) {
            var members = registration.get().getMembers();
            var since = 0L;
            if (nonNull(passesUpdatedSince)) {
                since = Long.parseLong(passesUpdatedSince);
            }
            passes.addAll(detectNewPasses(members, since));
        }
        result.put("serialNumbers", passes);
        return ResponseEntity.ok(result);
    }

    @GetMapping(
            path = "/passes/{passTypeIdentifier}/{serialNumber}",
            produces = "application/vnd.apple.pkpass")
    public ResponseEntity<ByteArrayResource> getUpdatedPass(
            @RequestHeader("Authorization") String authToken,
            @PathVariable String passTypeIdentifier,
            @PathVariable String serialNumber)
            throws IOException {
        if (!registeredPassTypeIdentifier.equals(passTypeIdentifier)) {
            return ResponseEntity.notFound().build();
        }

        LOG.info("Received request to get updated pass");

        var member = authorise(authToken);
        if (member.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        Optional<ResponseEntity<ByteArrayResource>> serialAuthorisationErrors =
                validateSerialNumber(member, serialNumber);
        if (serialAuthorisationErrors.isPresent()) {
            return serialAuthorisationErrors.get();
        }

        var pass = passGeneratorService.createAppleWalletPass(member.get(), serialNumber);
        var entity = new ByteArrayResource(pass);
        return ResponseEntity.ok()
                .contentLength(entity.contentLength())
                .lastModified(member.get().getSubscriptionEpochSecond())
                .contentType(MediaType.valueOf("application/vnd.apple.pkpass"))
                .body(entity);
    }

    @DeleteMapping(
            "/devices/{deviceLibraryIdentifier}/registrations/{passTypeIdentifier}/{serialNumber}")
    public ResponseEntity<Object> deleteDevice(
            @RequestHeader("Authorization") String authToken,
            @PathVariable String deviceLibraryIdentifier,
            @PathVariable String passTypeIdentifier,
            @PathVariable String serialNumber) {
        if (!registeredPassTypeIdentifier.equals(passTypeIdentifier)) {
            return ResponseEntity.notFound().build();
        }

        LOG.info("Received request to delete device");
        var member = authorise(authToken);
        if (member.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        var serialAuthorisationErrors = validateSerialNumber(member, serialNumber);
        if (serialAuthorisationErrors.isPresent()) {
            return serialAuthorisationErrors.get();
        }
        var registration =
                passKitDeviceRegistrationEntityRepository.findFirstByDeviceLibraryIdentifier(
                        deviceLibraryIdentifier);

        registration.ifPresent(
                r -> {
                    r.getMembers().remove(member.get());
                    if (r.getMembers().isEmpty()) {
                        passKitDeviceRegistrationEntityRepository.delete(r);
                        passKitDeviceRegistrationEntityRepository.flush();
                    } else {
                        passKitDeviceRegistrationEntityRepository.saveAndFlush(r);
                    }
                });
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/log", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> log(@RequestBody Log body) {
        LOG.info("Logs received from ApplePassKit");
        body.getLogs().forEach(log -> LOG.info("PassKit Device Log: {}", log));
        return ResponseEntity.ok().build();
    }

    private <T> Optional<ResponseEntity<T>> validateSerialNumber(
            Optional<MemberEntity> member, String serialNumber) {
        var requestSerial = parseSerialNumber(serialNumber);
        var passSerial = member.map(m -> m.getIdentifiers().get("APPLE_PASS_SERIAL"));
        if (passSerial.isEmpty() || !passSerial.get().equals(requestSerial)) {
            return Optional.of(ResponseEntity.status(401).build());
        }
        LOG.info("Matched Serial: {}", requestSerial);
        return Optional.empty();
    }

    private Optional<MemberEntity> authorise(String authToken) {
        try {
            return memberEntityRepository.findFirstByUuid(parseAuthToken(authToken));
        } catch (IllegalArgumentException e) {
            LOG.error("Error getting member", e);
            return Optional.empty();
        }
    }

    private String parseAuthToken(String authToken) {
        if (authToken.startsWith("ApplePass ")) {
            return authToken.substring("ApplePass ".length());
        }
        throw new IllegalArgumentException("Invalid auth token: " + authToken);
    }

    public String parseSerialNumber(String serialNumber) {
        return serialNumber.split("--")[1];
    }

    private Collection<String> detectNewPasses(Set<MemberEntity> members, long since) {
        return members.stream()
                .filter(member -> member.getSubscriptionEpochSecond() > since)
                .map(
                        member ->
                                format(
                                        "{0}--{1,number,#########}",
                                        member.getUuid(), member.getSubscriptionEpochSecond()))
                .toList();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Log {
        private List<String> logs;
    }
}
