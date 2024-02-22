<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />

<#macro headers>
</#macro>
<#macro pageScripts>
    <@components.otpScript />
</#macro>

<@layout.mainLayout script=pageScripts headers=headers formName="menu.forgot-password">
    <@components.panel>
        <form class="js-validate form-horizontal w-md-75 w-lg-60 mx-md-auto" novalidate method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <input type="hidden" name="email" value="${email}" />
            <@components.section title="forgot-password.code.subtitle">
                <div class="mb-4 card-body-centered">
                    <img class="avatar avatar-xxl avatar-4by3" src="${resourcePrefix}/front/admin/assets/svg/illustrations/unlock.svg" alt="Image Description">
                </div>

                <#if errors??>
                    <@components.formMessages errors=errors errorKey="forgot-password.errors.title" />
                </#if>

                <div class="mb-5">
                    <p>
                        <@spring.messageText code="forgot-password.code-sent" text="forgot-password.code-sent"/>
                    </p>
                </div>
                <@components.otpCode />

                <!-- Form Group -->
                <div class="js-form-message form-group">
                    <label class="input-label" for="password">
                        <@spring.message code="forgot-password.password" />
                    </label>
                    <input
                            type="password"
                            class="form-control"
                            name="password"
                            id="password"
                            placeholder="********"
                            aria-label="<@spring.message code="forgot-password.enter_password" />"
                            required="required"
                            data-msg="<@spring.message code="forgot-password.invalid_password" />">
                </div>
                <!-- End Form Group -->

                <!-- Form Group -->
                <div class="js-form-message form-group">
                    <label class="input-label" for="confirmPassword">
                        <@spring.message code="forgot-password.confirm_password" />
                    </label>
                    <input
                            type="password"
                            class="form-control"
                            name="confirmPassword"
                            id="confirmPassword"
                            placeholder="********"
                            aria-label="<@spring.message code="forgot-password.enter_confirm_password" />"
                            required="required"
                            data-msg="<@spring.message code="forgot-password.invalid_confirm_password" />">
                </div>
                <!-- End Form Group -->

                <!-- Button -->
                <div class="row align-items-center mb-5">
                    <div class="col-sm-6 mb-3 mb-sm-0">
                        <a class="font-size-1 font-weight-bold" href="/forgot-password">
                            <@spring.message code="forgot-password.start-again" />
                        </a>
                    </div>

                    <div class="col-sm-6 text-sm-right">
                        <button type="submit" class="btn btn-primary transition-3d-hover">
                            <@spring.message code="forgot-password.reset-password" />
                        </button>
                    </div>
                </div>
                <!-- End Button -->
            </@components.section>
        </form>
    </@components.panel>
</@layout.mainLayout>