<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />

<#macro headers>
</#macro>
<#macro pageScripts>
    <@components.otpScript />
</#macro>

<@layout.mainLayout script=pageScripts headers=headers formName="verification.title">
    <@components.panel>
        <form class="form-horizontal w-md-75 w-lg-60 mx-md-auto align-content-center" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <input type="hidden" name="verificationAttribute" value="${pendingUser.attributeName}" />
            <input type="hidden" name="userId" value="${pendingUser.userId}" />
            <@components.section title="">
                <div class="mb-4 card-body-centered">
                    <img class="avatar avatar-xxl avatar-4by3" src="${resourcePrefix}/front/assets/svg/illustrations/account-creation.svg" alt="Verification">
                </div>

                <#if info??>
                    <@components.formErrors errors=info errorKey="verification.resent.title" class="alert-soft-info"/>
                </#if>

                <#if errors??>
                    <@components.formErrors errors=errors errorKey="signup.errors.title" />
                </#if>

                <div class="mb-5">
                    <p>
                        <@spring.messageArgs code="verification.subtitle" args=[pendingUser.destination] />
                    </p>
                </div>

                <@components.otpCode />

                <button type="submit" class="btn btn-primary transition-3d-hover">
                    <@spring.message code="verification.verify" />
                </button>
                <div class="row mb-5">
                    <div class="col-sm-6 mb-3 mb-sm-0">
                        <a class="font-size-1 font-weight-bold" href="/logout">
                            <@spring.message code="login.logout" />
                        </a>
                    </div>

                    <div class="col-sm-6 text-sm-right">
                        <p>
                            <@spring.message code="login.sms_code_not_received" />
                            <a href="/sign-up/verification/resend">
                                <@spring.message code="login.sms_code_resend" />
                            </a>
                        </p>
                    </div>
                </div>
            </@components.section>
        </form>
    </@components.panel>
</@layout.mainLayout>