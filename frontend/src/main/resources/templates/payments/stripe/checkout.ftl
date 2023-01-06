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
<@layout.mainLayout headers=stripeHeaders script=stripeScripts formName="membership.confirmation">
    <@components.panel>
        <form class="form-horizontal" method="post" name="payment" action="/payments/paypal/authorise" data-session="${sessionId}" >
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <input type="hidden" name="id" value="${order.id}" />
            <input type="hidden" name="uuid" value="${order.uuid}" />

            <@components.section title="Order Number">
                Your order number is ${order.webReference}.
            </@components.section>

            <@components.section title="Payment">
                You are about to redirected to Stripe to authorise a payment of ${order.total?string.currency}
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
