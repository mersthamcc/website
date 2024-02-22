<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />

<#macro headers>
</#macro>
<#macro imageScripts>
    <script>
        $(document).ready(function () {
            let key = "loginMessageAcknowledged";
            let acknowledged = localStorage.getItem(key);

            if (!acknowledged) {
                $('#loginWarning')
                    .modal();
            }
            $('#loginWarning').on('shown.bs.modal', function () {
                localStorage.setItem(key, "1");
            })
        });
    </script>
</#macro>

<@layout.mainLayout script=imageScripts headers=headers formName="menu.login">
    <@components.panel>
        <form class="form-horizontal w-md-75 w-lg-60 mx-md-auto" novalidate="novalidate" action="${processingUrl}" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <@components.section title="login.subtitle">
                <#if errors??>
                    <@components.formMessages errors=errors errorKey="login.errors.title" />
                </#if>

                <#if info??>
                    <@components.formMessages errors=info errorKey="forgot-passwords.info.title" class="alert-soft-info"/>
                </#if>
                <!-- Form Group -->
                <div class="js-form-message form-group">
                    <label class="input-label" for="email">
                        <@spring.message code="login.email_address" />
                    </label>
                    <input
                            type="email"
                            class="form-control"
                            name="email"
                            id="email"
                            placeholder="<@spring.message code="login.email_address" />"
                            aria-label="<@spring.message code="login.enter_email" />"
                            required=""
                            data-msg="<@spring.message code="login.invalid_email" />">
                </div>
                <!-- End Form Group -->

                <!-- Form Group -->
                <div class="js-form-message form-group">
                    <label class="input-label" for="password">
                        <span class="d-flex justify-content-between align-items-center">
                            <@spring.message code="login.password" />
                            <a class="link-underline text-capitalize font-weight-normal" href="/forgot-password">
                                <@spring.message code="login.forgot_password" />
                            </a>
                        </span>
                    </label>
                    <input
                            type="password"
                            class="form-control"
                            name="password"
                            id="password"
                            placeholder="********"
                            aria-label="<@spring.message code="login.enter_password" />"
                            required=""
                            data-msg="<@spring.message code="login.invalid_password" />" />
                </div>
                <!-- End Form Group -->

                <!-- Button -->
                <div class="row align-items-center mb-5">
                    <div class="col-sm-6 mb-3 mb-sm-0">
                        <span class="font-size-1 text-muted">
                            <@spring.message code="login.no_account" />
                        </span>
                        <a class="font-size-1 font-weight-bold" href="/sign-up">
                            <@spring.message code="login.sign_up" />
                        </a>
                    </div>

                    <div class="col-sm-6 text-sm-right">
                        <button type="submit" class="btn btn-primary transition-3d-hover">
                            <@spring.message code="login.sign_in" />
                        </button>
                    </div>
                </div>
                <!-- End Button -->
            </@components.section>
        </form>

        <div id="loginWarning" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="loginWarningTitle" aria-hidden="false">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <!-- Header -->
                    <div class="modal-top-cover bg-primary text-center">
                        <figure class="position-absolute right-0 bottom-0 left-0">
                            <svg preserveAspectRatio="none" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" viewBox="0 0 1920 100.1">
                                <path fill="#fff" d="M0,0c0,0,934.4,93.4,1920,0v100.1H0L0,0z"/>
                            </svg>
                        </figure>

                        <div class="modal-close">
                            <button type="button" class="btn btn-icon btn-sm btn-ghost-light" data-dismiss="modal" aria-label="Close">
                                <svg width="16" height="16" viewBox="0 0 18 18" xmlns="http://www.w3.org/2000/svg">
                                    <path fill="currentColor" d="M11.5,9.5l5-5c0.2-0.2,0.2-0.6-0.1-0.9l-1-1c-0.3-0.3-0.7-0.3-0.9-0.1l-5,5l-5-5C4.3,2.3,3.9,2.4,3.6,2.6l-1,1 C2.4,3.9,2.3,4.3,2.5,4.5l5,5l-5,5c-0.2,0.2-0.2,0.6,0.1,0.9l1,1c0.3,0.3,0.7,0.3,0.9,0.1l5-5l5,5c0.2,0.2,0.6,0.2,0.9-0.1l1-1 c0.3-0.3,0.3-0.7,0.1-0.9L11.5,9.5z"/>
                                </svg>
                            </button>
                        </div>
                    </div>
                    <!-- End Header -->

                    <div class="modal-top-cover-avatar">
                        <img class="avatar avatar-lg avatar-circle avatar-border-lg avatar-centered shadow-soft" src="${resourcePrefix}/mcc/img/logos/mcc-logo-header.svg" alt="Logo">
                    </div>

                    <div class="modal-body">
                        <@spring.message code="login.require_new" />
                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-white" data-dismiss="modal">
                            <@spring.message code="login.close" />
                        </button>
                        <a class="btn btn-primary" href="/sign-up">
                            <@spring.message code="login.sign_up_now" />
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </@components.panel>
</@layout.mainLayout>