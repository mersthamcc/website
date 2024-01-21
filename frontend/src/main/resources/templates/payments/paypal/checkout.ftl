<#import "/spring.ftl" as spring />
<#import "../../base.ftl" as layout />
<#import "../../components.ftl" as components />
<@layout.mainLayout formName="membership.confirmation">
    <@components.panel>
        <form class="form-horizontal" method="post" name="payment" action="/payments/paypal/authorise">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

            <@components.section title="Payment">
                You are about to redirected to PayPal to authorise a payment of ${basket.basketTotal?string.currency}
            </@components.section>

            <@components.buttonGroup>
                <button type="submit" class="btn btn-primary btn-xlg transition-3d-hover" name="action" value="next">
                    <@spring.message code="membership.next" />
                    <i class="fa fa-arrow-circle-o-right"></i>
                </button>&nbsp;
            </@components.buttonGroup>
        </form>
    </@components.panel>
</@layout.mainLayout>
