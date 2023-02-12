<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />

<#macro headers>
</#macro>
<#macro imageScripts>
    <script>

    </script>
</#macro>

<@layout.mainLayout script=imageScripts headers=headers formName="menu.login">
    <@components.panel>
        <form class="form-horizontal w-md-75 w-lg-60 mx-md-auto" novalidate="novalidate" action="${processingUrl}" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <@components.section title="login.subtitle">
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
    </@components.panel>
</@layout.mainLayout>