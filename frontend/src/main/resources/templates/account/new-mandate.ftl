<#import "base.ftl" as home>
<#import "../components.ftl" as components>
<#import "../base.ftl" as layout>
<#import "/spring.ftl" as spring />
<#import "../payments/gocardless/components.ftl" as gcComponents />

<@layout.mainLayout formName="menu.account" withButtonResponsiveButton=true headers=gcComponents.goCardlessHeader script=gcComponents.goCardlessScripts>
    <@home.homeLayout userDetails=userDetails>
        <div class="card mb-3 mb-lg-5">
            <div class="card-header">
                <h5 class="card-title">
                    <@spring.message code="account.menu.account-billing-new-mandate" />
                </h5>
            </div>

            <div class="card-body">
                <div class="row">
                    <div class="col-6 col-md">
                        <small class="text-cap"><@spring.message code="account.order.placed" /></small>
                        <small class="text-dark font-weight-bold">${(order.createDate).format("dd/MM/yyyy")}</small>
                    </div>
                    <div class="col-6 col-md">
                        <small class="text-cap"><@spring.message code="account.order.order-reference" /></small>
                        <small class="text-dark font-weight-bold">${order.webReference}</small>
                    </div>
                    <div class="col-6 col-md mb-3 mb-md-0">
                        <small class="text-cap"><@spring.message code="account.order.status" /></small>
                        <@components.orderStatus order=order />
                    </div>
                    <div class="col-6 col-md mb-3 mb-md-0">
                        <small class="text-cap"><@spring.message code="account.order.outstanding-amount" /></small>
                        <small class="text-dark font-weight-bold">&pound;${order.getOutstanding()}</small>
                    </div>
                </div>
                <hr>
                <div class="row">
                    <form class="form-horizontal" method="post" name="payment" action="new-mandate/authorise" id="payment-schedule-form" data-end-date="${endDate}">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                        <@gcComponents.paymentScheduleOptions schedules=schedules endDate=endDate />

                        <@components.buttonGroup>
                            <a href="/account/billing/${order.uuid}" class="btn btn-danger transition-3d-hover">
                                <@spring.message code="membership.cancel" />
                            </a>&nbsp;
                            <button type="submit" class="btn btn-primary btn-xlg transition-3d-hover" name="action" value="next">
                                <@spring.message code="membership.next" />
                                <i class="fa fa-arrow-circle-o-right"></i>
                            </button>&nbsp;
                        </@components.buttonGroup>
                    </form>
                </div>
            </div>
        </div>
    </@home.homeLayout>
</@layout.mainLayout>
