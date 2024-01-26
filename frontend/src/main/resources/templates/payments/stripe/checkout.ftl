<#import "/spring.ftl" as spring />
<#import "../../base.ftl" as layout />
<#import "../../components.ftl" as components />
<#macro stripeHeaders>
    <script src="https://js.stripe.com/v3/"></script>
</#macro>
<#macro stripeScripts>
    <script type="text/javascript">
        // Create an instance of the Stripe object with your publishable API key
        var stripe = Stripe("${publishableKey}");
        var checkoutButton = document.getElementById("checkout-button");

        checkoutButton.addEventListener("click", function () {
            return stripe.redirectToCheckout({ sessionId: "${sessionId}" });
        });
    </script>
</#macro>
<@layout.mainLayout headers=stripeHeaders script=stripeScripts formName="membership.checkout">
    <@components.panel>
        <form class="form-horizontal" method="post" name="payment" action="/payments/paypal/authorise" data-session="${sessionId}" >
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

            <@components.section title="payments.stripe-short">
                <div class="row">
                    <div class="col-md-9">
                        <@spring.messageArgs
                            code="payments.stripe-checkout"
                            args=[basket.basketTotal?string.currency]/>
                    </div>
                    <div class="col-md-3 right-align">
                        <img src="${resourcePrefix}/mcc/img/stripe-logo.png" alt="Stripe Logo" width="200px" />
                    </div>
                </div>
            </@components.section>

            <@components.buttonGroup>
                <button type="button" class="btn btn-primary btn-xlg transition-3d-hover" name="action" id="checkout-button">
                    <@spring.message code="membership.next" />
                    <i class="fa fa-arrow-circle-o-right"></i>
                </button>&nbsp;
            </@components.buttonGroup>
        </form>
    </@components.panel>
</@layout.mainLayout>
