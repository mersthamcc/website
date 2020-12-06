<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "title">
        ${msg("updatePhoneNumberTitle", realm.name)}
    <#elseif section = "header">
        ${msg("updatePhoneNumberTitle", realm.name)}
    <#elseif section = "form">
        <div class="${properties.kcFormGroupClass!}">
            <div class="${properties.kcLabelWrapperClass!}">
                <p>${msg("updatePhoneNumberMessage")}</p>
            </div>
            <form id="kc-totp-login-form" class="${properties.kcFormClass!} govuk-grid-column-two-thirds" action="${url.loginAction}" method="post">
                <div class="${properties.kcFormGroupClass!}">
                    <div class="${properties.kcLabelWrapperClass!}">
                        <label for="mobileNumber" class="govuk-label">${msg("phoneNumber")}</label>
                    </div>

                    <div class="${properties.kcInputWrapperClass!}">
                        <input type="tel" id="mobileNumber" class="${properties.kcInputClass!}" name="mobile_number" value="${(phoneNumber!'')}" autocomplete="mobile tel" aria-describedby="mobileNumber-hint" autofocus/>
                    </div>
                </div>

                <div class="${properties.kcFormGroupClass!}">
                    <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                        <div class="${properties.kcFormOptionsWrapperClass!}">
                        </div>
                    </div>

                    <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                        <input
                            class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                            name="verify" id="kc-verify" type="submit" value="${msg("verify")}" />
                    </div>
                </div>
            </form>
        </div>
    </#if>
</@layout.registrationLayout>
