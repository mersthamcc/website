<#import "/spring.ftl" as spring />
<#import "../../base.ftl" as layout />
<#import "../../components.ftl" as components />
<@layout.mainLayout formName="membership.confirmation">
    <@components.panel>
        <@components.section title="payments.paypal-short">
            <div class="row">
                <div class="col-md-9">
                    <@spring.message code="payments.stripe-confirmation" />
                </div>
                <div class="col-md-3 right-align">
                    <img src="${resourcePrefix}/mcc/img/stripe-logo.png" alt="Stripe Logo" width="200px" />
                </div>
            </div>
        </@components.section>

        <@components.section title="payments.stripe-details">
            <table class="table table-borderless table-thead-bordered table-nowrap table-align-middle">
                <thead class="thead-light">
                <tr>
                    <th><@spring.message code="payments.stripe-reference" /></th>
                    <th class="align-right"><@spring.message code="payments.stripe-amount" /></th>
                </tr>
                </thead>
                <tbody>
                <#assign payment=order.payment[0] />
                <tr scope="row">
                    <td>${payment.reference}</td>
                    <td class="align-right">${payment.amount?string.currency}</td>
                </tr>
                </tbody>
            </table>
            <@spring.message code="payments.stripe-details-text" />
        </@components.section>

        <@components.buttonGroup>
            <a class="btn btn-success btn-xlg transition-3d-hover" href="/">
                <@spring.message code="membership.complete" />
                <i class="fa fa-icon-home"></i>
            </a>
        </@components.buttonGroup>
    </@components.panel>
</@layout.mainLayout>
