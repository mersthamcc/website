<#import "/spring.ftl" as spring />
<#import "../../base.ftl" as layout />
<#import "../../components.ftl" as components />
<#import "components.ftl" as gcComponents />

<@layout.mainLayout headers=gcComponents.goCardlessHeader script=gcComponents.goCardlessScripts formName="membership.checkout">

    <@components.panel>
        <@components.section title="payments.gocardless">
            <div class="row">
                <div class="col-md-9">
                    <@spring.message code="payments.gocardless-checkout" />
                </div>
                <div class="col-md-3 right-align">
                    <img src="${resourcePrefix}/mcc/img/gocardless-logo.png" alt="GoCardless Logo" width="200px" />
                </div>
            </div>
        </@components.section>
    </@components.panel>

    <@components.panel>
        <form class="form-horizontal" method="post" name="payment" action="/payments/gocardless/authorise" id="payment-schedule-form" data-end-date="${endDate}">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

            <@gcComponents.existingMandateList existingMandates=existingMandates />
            <@gcComponents.paymentScheduleOptions schedules=schedules endDate=endDate />

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
