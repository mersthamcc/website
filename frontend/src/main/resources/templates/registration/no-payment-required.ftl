<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />
<@layout.mainLayout formName="membership.checkout">
    <@components.panel>
        <form class="form-horizontal" method="post" name="payment" action="/payments">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <input type="hidden" name="payment-type" value="complementary"/>
            <@components.section title="Payment">
                <h3>
                    <@spring.message code="payments.no-payment-required" />
                </h3>
                <p>
                    <@spring.message code="payments.no-payment-required-body" />
                </p>
            </@components.section>

            <@components.buttonGroup>
                <a href="/register" class="btn btn-danger transition-3d-hover">
                    <@spring.message code="membership.cancel" />
                </a>&nbsp;
                <button type="submit" class="btn btn-primary btn-xlg transition-3d-hover" name="action" value="next">
                    <@spring.message code="membership.next" />
                    <i class="fa fa-arrow-circle-o-right"></i>
                </button>&nbsp;
            </@components.buttonGroup>
        </form>
    </@components.panel>
</@layout.mainLayout>
