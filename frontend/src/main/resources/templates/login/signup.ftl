<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />

<#macro headers>
</#macro>
<#macro imageScripts>
    <script>

    </script>
</#macro>

<@layout.mainLayout script=imageScripts headers=headers formName="signup.signup">
    <@components.panel>
        <form class="js-validate form-horizontal w-md-75 w-lg-60 mx-md-auto" novalidate method="post">
            <@components.section title="signup.subtitle">
                <#if errors??>
                    <div class="alert alert-soft-danger" role="alert">
                        <b>
                            <@spring.message code="signup.errors.title" />
                        </b>
                        <ul>
                            <#list errors as error>
                                <li>
                                    <@spring.message code="signup.errors.${error.defaultMessage}" />
                                </li>
                            </#list>
                        </ul>
                    </div>
                </#if>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

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
                            required="required"
                            value="${(signUp.givenName)!""}">
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
                            required="required"
                            value="${(signUp.familyName)!""}">
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
                            data-msg="<@spring.message code="signup.invalid_email" />"
                            value="${(signUp.email)!""}">
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
                        <input
                                type="checkbox"
                                class="custom-control-input"
                                id="termsAndConditions"
                                name="termsAndConditions"
                                required=""
                                data-msg="Please accept our Terms and Conditions."
                                <#if (signUp.termsAndConditions)!false>checked="checked"</#if>>
                        <label class="custom-control-label" for="termsAndConditions">
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
            </@components.section>
        </form>
    </@components.panel>
</@layout.mainLayout>