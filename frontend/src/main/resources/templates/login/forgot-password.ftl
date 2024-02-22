<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />

<#macro headers>
</#macro>
<#macro imageScripts>
    <script>
        $('#loginWarning')
            .modal();
    </script>
</#macro>

<@layout.mainLayout script=imageScripts headers=headers formName="menu.forgot-password">
    <@components.panel>
        <form class="form-horizontal w-md-75 w-lg-60 mx-md-auto" novalidate="novalidate" action="${processingUrl}" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <@components.section title="forgot-password.subtitle">
                <#if errors??>
                    <@components.formMessages errors=errors errorKey="forgot-password.errors.title" />
                </#if>
                <!-- Form Group -->
                <div class="js-form-message form-group">
                    <label class="input-label" for="email">
                        <@spring.message code="forgot-password.email_address" />
                    </label>
                    <input
                            type="email"
                            class="form-control"
                            name="email"
                            id="email"
                            placeholder="<@spring.message code="forgot-password.email_address" />"
                            aria-label="<@spring.message code="forgot-password.enter_email" />"
                            required=""
                            data-msg="<@spring.message code="forgot-password.invalid_email" />">
                </div>
                <!-- End Form Group -->

                <!-- Button -->
                <div class="row align-items-center mb-5">
                    <div class="col-sm-6 mb-3 mb-sm-0">

                    </div>

                    <div class="col-sm-6 text-sm-right">
                        <button type="submit" class="btn btn-primary transition-3d-hover">
                            <@spring.message code="forgot-password.next" />
                        </button>
                    </div>
                </div>
                <!-- End Button -->
            </@components.section>
        </form>
    </@components.panel>
</@layout.mainLayout>