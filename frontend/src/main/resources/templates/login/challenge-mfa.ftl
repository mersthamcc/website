<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />

<#macro headers>
</#macro>
<#macro pageScripts>
    <@components.otpScript />
</#macro>

<@layout.mainLayout script=pageScripts headers=headers formName="login.challenge.mfa.title">
    <@components.panel>
        <form class="form-horizontal w-md-75 w-lg-60 mx-md-auto align-content-center" novalidate="novalidate" action="${processingUrl}" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <@components.section title="login.challenge.mfa.subtitle">
                <div class="mb-4 card-body-centered">
                    <img class="avatar avatar-xxl avatar-4by3" src="${resourcePrefix}/front/admin/assets/svg/illustrations/unlock.svg" alt="Image Description">
                </div>

                <#if errors??>
                    <@components.formMessages errors=errors errorKey="login.errors.title" />
                </#if>
                <div class="mb-5">
                    <p>
                        <#if challengeName == "SMS_MFA">
                            <@spring.messageArgs code="login.challenge.mfa.body.sms" args=[authentication.mfaDestination]/>
                        <#else>
                            <@spring.message code="login.challenge.mfa.body.app" />
                        </#if>
                    </p>
                </div>

                <@components.otpCode />

                <!-- Button -->
                <div class="row align-items-center mb-5">
                    <div class="col-sm-6 mb-3 mb-sm-0">
                        <a class="font-size-1 font-weight-bold" href="/logout">
                            <@spring.message code="login.logout" />
                        </a>
                        <#if challengeName == "SMS_MFA">
                            <p>
                                <@spring.message code="login.sms_code_not_received" />
                                <a href="#">
                                    <@spring.message code="login.sms_code_resend" />
                                </a>
                            </p>
                        </#if>
                    </div>

                    <div class="col-sm-6 text-sm-right">
                        <button type="submit" class="btn btn-primary transition-3d-hover">
                            <@spring.message code="login.challenge.mfa.verify" />
                        </button>
                    </div>
                </div>
                <!-- End Button -->
            </@components.section>
        </form>
    </@components.panel>
</@layout.mainLayout>
