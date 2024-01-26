<#import "/spring.ftl" as spring />
<#import "../../base.ftl" as layout />
<#import "../../components.ftl" as components />
<@layout.mainLayout formName="membership.checkout">
    <@components.panel>
        <form class="form-horizontal" method="post" name="payment" action="/payments/paypal/authorise">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

            <@components.section title="payments.paypal-short">
                <div class="row">
                    <div class="col-md-9">
                        <@spring.messageArgs
                            code="payments.paypal-checkout"
                            args=[basket.basketTotal?string.currency]/>
                    </div>
                    <div class="col-md-3 right-align">
                        <img src="${resourcePrefix}/mcc/img/paypal-logo.png" alt="PayPal Logo" width="200px" />
                    </div>
                </div>
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
