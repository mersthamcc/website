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
            <input type="hidden" name="verificationAttribute" value="${pendingUser.attributeName}" />
            <input type="hidden" name="userId" value="${pendingUser.userId}" />
            <!-- Title -->
            <div class="mb-5 mb-md-7">
                <h1 class="h2 mb-0">
                    <@spring.message code="verification.title" />
                </h1>
                <p>
                    <@spring.messageArgs code="verification.subtitle" args=[pendingUser.destination] />
                </p>
            </div>
            <!-- End Title -->

            <div class="form-group">
                <label class="input-label" for="code">
                    <@spring.message code="verification.code" />
                </label>
                <div class="input-group input-group-merge">
                    <div class="input-group-prepend">
                        <span class="input-group-text">
                          <i class="far fa-keyboard"></i>
                        </span>
                    </div>
                    <input
                            type="number"
                            name="code"
                            id="code"
                            class="form-control"
                            placeholder="<@spring.message code="verification.code_placeholder" />"
                            aria-label="<@spring.message code="verification.code" />">
                </div>
            </div>

            <!-- Button -->
            <div class="row align-items-center mb-5">
                <div class="col-sm-6 mb-3 mb-sm-0">

                </div>

                <div class="col-sm-6 text-sm-right">
                    <button type="submit" class="btn btn-primary transition-3d-hover">
                        <@spring.message code="verification.verify" />
                    </button>
                </div>
            </div>
            <!-- End Button -->
        </form>
    </div>
</@layout.mainLayout>