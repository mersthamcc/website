<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />

<#macro headers>
</#macro>
<#macro pageScripts>
    <@components.otpScript />
</#macro>

<@layout.mainLayout script=pageScripts headers=headers formName="login.challenge.setup_mfa.sms.title">
    <@components.panel>
        <form class="form-horizontal w-md-75 w-lg-60 mx-md-auto" novalidate="novalidate" action="${processingUrl}" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <input type="hidden" name="sessionId" value="${sessionId}" />
            <input type="hidden" name="userId" value="${userId}" />
            <input type="hidden" name="challengeName" value="${authentication.challengeName}" />
            <@components.section title="login.challenge.setup_mfa.sms.sub_title">
                <div class="mb-5">
                    <p>
                        <@spring.message code="login.challenge.setup_mfa.sms.body" />
                    </p>
                </div>

                <p>
                    <@spring.message code="login.challenge.setup_mfa.sms.setup" />
                </p>

                <!-- Form Group -->
                <div class="js-form-message form-group">
                    <label class="input-label" for="phoneNumber">
                        <@spring.message code="login.challenge.setup_mfa.sms.phone" />
                    </label>
                    <input
                            type="text"
                            class="form-control"
                            name="phoneNumber"
                            id="phoneNumber"
                            placeholder="<@spring.message code="login.challenge.setup_mfa.sms.phone_placeholder" />"
                            aria-label="<@spring.message code="login.challenge.setup_mfa.sms.phone" />"
                            required="required"
                            value="">
                </div>
                <!-- End Form Group -->
                <!-- Button -->
                <div class="row align-items-center mb-5">
                    <div class="col-sm-6 mb-3 mb-sm-0">
                        <a class="font-size-1 font-weight-bold" href="/logout">
                            <@spring.message code="login.logout" />
                        </a>
                    </div>

                    <div class="col-sm-6 text-sm-right">
                        <button type="submit" class="btn btn-primary transition-3d-hover">
                            <@spring.message code="login.challenge.setup_mfa.sms.verify" />
                        </button>
                    </div>
                </div>
                <!-- End Button -->
            </@components.section>
        </form>
    </@components.panel>
</@layout.mainLayout>