<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />

<#macro headers>
</#macro>
<#macro imageScripts>
    <script>

    </script>
</#macro>

<@layout.mainLayout script=imageScripts headers=headers>
    <div class="container space-2 space-lg-3">
        <form class="js-validate w-md-75 w-lg-50 mx-md-auto" novalidate="novalidate" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <!-- Title -->
            <div class="mb-5 mb-md-7">
                <h1 class="h2 mb-0">
                    <@spring.message code="signup.title" />
                </h1>
                <p>
                    <@spring.message code="signup.subtitle" />
                </p>
            </div>
            <!-- End Title -->

            <!-- Form Group -->
            <div class="js-form-message form-group">
                <label class="input-label" for="givenName">
                    <@spring.message code="signup.given_name" />
                </label>
                <input
                        type="text"
                        class="form-control"
                        name="givenName"
                        id="givenName"
                        placeholder="<@spring.message code="signup.given_name_placeholder" />"
                        aria-label="<@spring.message code="signup.given_name" />"
                        required="required">
            </div>
            <!-- End Form Group -->

            <!-- Form Group -->
            <div class="js-form-message form-group">
                <label class="input-label" for="familyName">
                    <@spring.message code="signup.family_name" />
                </label>
                <input
                        type="text"
                        class="form-control"
                        name="familyName"
                        id="familyName"
                        placeholder="<@spring.message code="signup.family_name_placeholder" />"
                        aria-label="<@spring.message code="signup.family_name" />"
                        required="required">
            </div>
            <!-- End Form Group -->

            <!-- Form Group -->
            <div class="js-form-message form-group">
                <label class="input-label" for="email">
                    <@spring.message code="signup.email_address" />
                </label>
                <input
                        type="email"
                        class="form-control"
                        name="email"
                        id="email"
                        placeholder="<@spring.message code="signup.enter_email" />"
                        aria-label="<@spring.message code="signup.enter_email" />"
                        required="required"
                        data-msg="<@spring.message code="signup.invalid_email" />">
            </div>
            <!-- End Form Group -->

            <!-- Form Group -->
            <div class="js-form-message form-group">
                <label class="input-label" for="password">
                    <@spring.message code="signup.password" />
                </label>
                <input
                        type="password"
                        class="form-control"
                        name="password"
                        id="password"
                        placeholder="********"
                        aria-label="<@spring.message code="signup.enter_password" />"
                        required="required"
                        data-msg="<@spring.message code="signup.invalid_password" />">
            </div>
            <!-- End Form Group -->

            <!-- Form Group -->
            <div class="js-form-message form-group">
                <label class="input-label" for="confirmPassword">
                    <@spring.message code="signup.confirm_password" />
                </label>
                <input
                        type="password"
                        class="form-control"
                        name="confirmPassword"
                        id="confirmPassword"
                        placeholder="********"
                        aria-label="<@spring.message code="signup.enter_confirm_password" />"
                        required="required"
                        data-msg="<@spring.message code="signup.invalid_confirm_password" />">
            </div>
            <!-- End Form Group -->

            <!-- Checkbox -->
            <div class="js-form-message mb-5">
                <div class="custom-control custom-checkbox d-flex align-items-center text-muted">
                    <input type="checkbox" class="custom-control-input" id="termsCheckbox" name="termsCheckbox" required="" data-msg="Please accept our Terms and Conditions.">
                    <label class="custom-control-label" for="termsCheckbox">
                        <small>
                            <@spring.message code="signup.i_agree" />
                            <a class="link-underline" href="/pages/terms">
                                <@spring.message code="signup.terms_and_conditions" />
                            </a>
                        </small>
                    </label>
                </div>
            </div>
            <!-- End Checkbox -->

            <!-- Button -->
            <div class="row align-items-center mb-5">
                <div class="col-sm-6 mb-3 mb-sm-0">
                    <span class="font-size-1 text-muted">
                        <@spring.message code="signup.existing_account" />
                    </span>
                    <a class="font-size-1 font-weight-bold" href="/login">
                        <@spring.message code="menu.login" />
                    </a>
                </div>

                <div class="col-sm-6 text-sm-right">
                    <button type="submit" class="btn btn-primary transition-3d-hover">
                        <@spring.message code="signup.signup" />
                    </button>
                </div>
            </div>
            <!-- End Button -->
        </form>
    </div>
</@layout.mainLayout>