package cricket.merstham.website.frontend.controller;

import cricket.merstham.shared.dto.User;
import cricket.merstham.website.frontend.model.ChangePassword;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.CognitoService;
import cricket.merstham.website.frontend.service.MembershipService;
import cricket.merstham.website.frontend.service.account.PassGeneratorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectTo;
import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Controller
@PreAuthorize("isAuthenticated()")
public class AccountController {

    public static final String ERRORS = "ERRORS";
    private static final String INFO = "INFO";

    private final CognitoService service;
    private final MembershipService membershipService;
    private final PassGeneratorService passGeneratorService;

    @Autowired
    public AccountController(
            CognitoService service,
            MembershipService membershipService,
            PassGeneratorService passGeneratorService) {
        this.service = service;
        this.membershipService = membershipService;
        this.passGeneratorService = passGeneratorService;
    }

    @GetMapping(value = "/account", name = "account-home")
    public ModelAndView home(HttpServletRequest request) {
        var model = baseModel(request);
        return new ModelAndView(
                "account/home",
                model,
                model.containsKey("errors") ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK);
    }

    @PostMapping(value = "/account", name = "account-home-update-user")
    public RedirectView updateUser(User user, RedirectAttributes redirectAttributes) {
        var errors = service.updateUser(user);
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute(ERRORS, errors);
        } else {
            redirectAttributes.addFlashAttribute(INFO, List.of("account.success.update-details"));
        }
        return redirectTo("/account");
    }

    @GetMapping(value = "/account/security", name = "account-home-security")
    public ModelAndView securityHome(HttpServletRequest request) {
        var model = baseModel(request);

        if (service.isIdentityProviderUser()) {
            return new ModelAndView("account/idp-password", model);
        } else {
            var requirements = service.getPasswordRequirements();
            model.put("passwordRequirements", requirements);

            return new ModelAndView(
                    "account/change-password",
                    model,
                    model.containsKey("errors") ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK);
        }
    }

    @PostMapping(value = "/account/change-password", name = "account-home-change-password")
    public RedirectView changePassword(
            @Valid ChangePassword changePassword,
            Errors errors,
            RedirectAttributes redirectAttributes) {

        if (errors.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    ERRORS,
                    errors.getAllErrors().stream()
                            .map(e -> format("account.error.{0}", e.getDefaultMessage()))
                            .toList());
        } else {
            var result = service.changePassword(changePassword);
            if (!result.isEmpty()) {
                redirectAttributes.addFlashAttribute(ERRORS, result);
            } else {
                redirectAttributes.addFlashAttribute(
                        INFO, List.of("account.success.password-change"));
            }
        }
        return redirectTo("/account/security");
    }

    @GetMapping(value = "/account/members", name = "account-members")
    public ModelAndView membersHome(
            HttpServletRequest request, CognitoAuthentication cognitoAuthentication) {
        var model = baseModel(request);
        model.put(
                "members",
                membershipService.getMyMembers(cognitoAuthentication.getOAuth2AccessToken()));
        return new ModelAndView(
                "account/member-list",
                model,
                model.containsKey("errors") ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK);
    }

    @GetMapping(value = "/account/billing", name = "account-members-billing")
    public ModelAndView billingHome(HttpServletRequest request) {
        return home(request);
    }

    @GetMapping(value = "/account/pass/{uuid}/apple", name = "apple-wallet-membership-card")
    public ResponseEntity<Resource> appleWalletMemberShipCard(
            @PathVariable String uuid, CognitoAuthentication cognitoAuthentication)
            throws IOException {
        var member =
                membershipService
                        .getMyMembers(cognitoAuthentication.getOAuth2AccessToken())
                        .stream()
                        .filter(m -> m.getUuid().equals(uuid))
                        .findFirst()
                        .orElseThrow();
        var serialNumber = member.getApplePassSerial();
        if (isNull(member.getApplePassSerial())) {
            serialNumber = UUID.randomUUID().toString();
            membershipService.addApplePassSerial(
                    member.getId(), serialNumber, cognitoAuthentication.getOAuth2AccessToken());
        }
        var entity =
                new ByteArrayResource(
                        passGeneratorService.createAppleWalletPass(member, serialNumber));
        return ResponseEntity.ok()
                .contentLength(entity.contentLength())
                .contentType(MediaType.valueOf("application/vnd.apple.pkpass"))
                .body(entity);
    }

    @GetMapping(value = "/account/pass/{uuid}/google", name = "google-wallet-membership-card")
    public RedirectView googleWalletMemberShipCard(
            @PathVariable String uuid, CognitoAuthentication cognitoAuthentication) {
        var member =
                membershipService
                        .getMyMembers(cognitoAuthentication.getOAuth2AccessToken())
                        .stream()
                        .filter(m -> m.getUuid().equals(uuid))
                        .findFirst()
                        .orElseThrow();
        var serialNumber = member.getGooglePassSerial();
        if (isNull(member.getGooglePassSerial())) {
            serialNumber = UUID.randomUUID().toString();
            membershipService.addGooglePassSerial(
                    member.getId(), serialNumber, cognitoAuthentication.getOAuth2AccessToken());
        }
        return redirectTo(passGeneratorService.createGoogleWalletPass(member, serialNumber));
    }

    private Map<String, Object> baseModel(HttpServletRequest request) {
        var model = new HashMap<String, Object>();
        model.put("userDetails", service.getUserDetails());

        var flash = RequestContextUtils.getInputFlashMap(request);
        if (nonNull(flash)) {
            if (flash.containsKey(ERRORS)) model.put("errors", flash.get(ERRORS));
            if (flash.containsKey(INFO)) model.put("info", flash.get(INFO));
        }
        return model;
    }
}
