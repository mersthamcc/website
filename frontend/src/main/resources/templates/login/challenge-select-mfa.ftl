<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />

<#macro headers>
</#macro>
<#macro imageScripts>
    <script>

    </script>
</#macro>

<@layout.mainLayout script=imageScripts headers=headers formName="login.challenge.setup_mfa.title">
    <@components.panel>
        <form class="js-validate form-horizontal w-md-75 w-lg-60 mx-md-auto" novalidate="novalidate" action="${processingUrl}" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <input type="hidden" name="sessionId" value="${sessionId}" />
            <input type="hidden" name="userId" value="${userId}" />
            <@components.section title="login.challenge.setup_mfa.sub_title">
                <div class="mb-5 mb-md-7">
                    <p>
                        <@spring.message code="login.challenge.setup_mfa.body" />
                    </p>
                </div>
                <!-- End Title -->

                <div class="form-group">
                    <label class="col-md-12 control-label" for="mfa-type">
                        <@spring.message code="login.challenge.setup_mfa.which_method" />
                    </label>
                    <div class="col-md-12">
                        <#list mfaTypes as mfaType>
                            <div class="radio">
                                <label>
                                    <input
                                            type="radio"
                                            name="mfa-type"
                                            id="mfa-type"
                                            value="${mfaType}"
                                            required="required" />
                                    &nbsp;&nbsp;<@spring.message code="login.challenge.setup_mfa.${mfaType}" />
                                </label>
                            </div>
                        </#list>
                    </div>
                </div>

                <!-- Button -->
                <div class="row align-items-center mb-5">
                    <div class="col-sm-6 mb-3 mb-sm-0">
                        <a class="font-size-1 font-weight-bold" href="/logout">
                            <@spring.message code="login.logout" />
                        </a>
                    </div>

                    <div class="col-sm-6 text-sm-right">
                        <button type="submit" class="btn btn-primary transition-3d-hover">
                            <@spring.message code="login.challenge.setup_mfa.continue" />
                        </button>
                    </div>
                </div>
                <!-- End Button -->
            </@components.section>
        </form>
    </@components.panel>
</@layout.mainLayout>