<#import "/spring.ftl" as spring />
<#import "../../base.ftl" as layout />
<#import "../../components.ftl" as components />
<@layout.mainLayout>
    <@components.panel title="membership.confirmation" type="info">
        <form class="form-horizontal" method="post" name="payment" action="/payments/paypal/authorise">
            <input type="hidden" name="_csrf" value="${_csrf.token}" />
            <input type="hidden" name="id" value="${order.id}" />
            <input type="hidden" name="uuid" value="${order.uuid}" />

            <@components.section title="Order Number">
                Your order number is ${order.webReference}.
            </@components.section>

            <@components.section title="Payment">
                You are about to redirected to PayPal to authorise a payment of ${order.total?string.currency}
            </@components.section>

            <@components.buttonGroup>
                <button type="submit" class="btn btn-info btn-xlg" name="action" value="next">
                    <@spring.message code="membership.next" />
                    <i class="fa fa-arrow-circle-o-right"></i>
                </button>&nbsp;
            </@components.buttonGroup>
        </form>
    </@components.panel>
</@layout.mainLayout>
