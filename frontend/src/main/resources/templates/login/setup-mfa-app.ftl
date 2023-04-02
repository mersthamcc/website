<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />

<#macro headers>
</#macro>
<#macro pageScripts>
    <script src="${resourcePrefix}/front/assets/vendor/clipboard/dist/clipboard.min.js"></script>
    <@components.otpScript />
    <script>
        $(document).on('ready', function () {
            $('.js-clipboard').each(function() {
                $.HSCore.components.HSClipboard.init(this);
            });
        });
    </script>
</#macro>

<@layout.mainLayout script=pageScripts headers=headers formName="login.challenge.setup_mfa.app.title">
    <@components.panel>
        <form class="form-horizontal w-md-75 w-lg-60 mx-md-auto" novalidate="novalidate" action="${processingUrl}" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <input type="hidden" name="sessionId" value="${sessionId}" />
            <input type="hidden" name="userId" value="${userId}" />
            <input type="hidden" name="challengeName" value="${authentication.challengeName}" />
            <@components.section title="login.challenge.setup_mfa.app.sub_title">
                <div class="mb-5">
                    <p>
                        <@spring.message code="login.challenge.setup_mfa.app.body" />
                    </p>
                </div>

                <p>
                    <@spring.message code="login.challenge.setup_mfa.app.setup" />
                </p>

                <p>
                    <@spring.message code="login.challenge.setup_mfa.app.please_scan" />
                </p>

                <div class="card-body-centered">
                    <img src="${qrCode}" />
                </div>

                <div class="mb-5">
                    <p>
                        <@spring.message code="login.challenge.setup_mfa.app.alternate" />
                    </p>
                    <div class="card-body-centered">
                        <code class="secret">
                            <span id="secret">
                                ${secret}
                            </span>
                            <a
                                    class="js-clipboard"
                                    href="javascript:;"
                                    title="Copy to clipboard!"
                                    data-toggle="popover"
                                    data-placement="top"
                                    data-trigger="hover"
                                    data-content="Copy the secret to the clipboard"
                                    data-hs-clipboard-options='{
                                     "type": "popover",
                                     "successText": "Copied!",
                                     "contentTarget": "#secret"
                                    }'>
                                <i class="fas fa-copy"></i>
                            </a>
                        </code>
                    </div>
                </div>

                <div class="mb-5">
                    <p>
                        <@spring.message code="login.challenge.setup_mfa.app.verify_instruction" />
                    </p>
                    <@components.otpCode />
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
                            <@spring.message code="login.challenge.setup_mfa.app.verify" />
                        </button>
                    </div>
                </div>
                <!-- End Button -->
            </@components.section>
        </form>
    </@components.panel>
</@layout.mainLayout>