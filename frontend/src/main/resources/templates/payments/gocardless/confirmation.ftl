<#import "/spring.ftl" as spring />
<#import "../../base.ftl" as layout />
<#import "../../components.ftl" as components />
<@layout.mainLayout formName="membership.confirmation">
    <@components.panel>
        <@components.section title="payments.gocardless-short">
            <div class="row">
                <div class="col-md-9">
                    <@spring.message code="payments.gocardless-confirmation" />
                </div>
                <div class="col-md-3 right-align">
                    <img src="${resourcePrefix}/mcc/img/gocardless-logo.png" alt="GoCardless Logo" width="200px" />
                </div>
            </div>
        </@components.section>

        <@components.section title="payments.gocardless-payment-schedule">
            <@spring.message code="payments.gocardless-payment-schedule-text" />

            <table class="table table-borderless table-thead-bordered table-nowrap table-align-middle">
                <thead class="thead-light">
                <tr>
                    <th><@spring.message code="payments.gocardless-payment-schedule-date" /></th>
                    <th><@spring.message code="payments.gocardless-payment-schedule-reference" /></th>
                    <th><@spring.message code="payments.gocardless-payment-schedule-amount" /></th>
                </tr>
                </thead>
                <tbody>
                    <#list order.payment as payment>
                        <tr scope="row">
                            <td>${(payment.date).format('dd/MM/yyyy')}</td>
                            <td>${payment.reference}</td>
                            <td>${payment.amount?string.currency}</td>
                        </tr>
                    </#list>
                </tbody>
            </table>

            <p>
                You will receive a copy of your mandate and Direct Debit Guarantee directly from GoCardless via e-mail shortly.
            </p>
            <p>
                <b>Mandate cancellation</b>: please note the date of the last payment above, after this date no further payments will
                be taken but the mandate will remain active so it can be re-used next year. Please do not cancel the mandate before all
                payments are taken.
            </p>
            <p>
                If you need to change your bank account details, or have any other questions, please contact the club treasurer, Chris Clayson,
                by e-mailing <a href="mailto:treasurer@mersthamcc.co.uk">treasurer@mersthamcc.co.uk</a>.
            </p>
        </@components.section>

        <@components.buttonGroup>
            <a class="btn btn-success btn-xlg transition-3d-hover" href="/">
                <@spring.message code="membership.complete" />
                <i class="fa fa-icon-home"></i>
            </a>
        </@components.buttonGroup>
    </@components.panel>
</@layout.mainLayout>
