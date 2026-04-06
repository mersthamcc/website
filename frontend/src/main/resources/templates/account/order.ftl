<#import "base.ftl" as home>
<#import "../components.ftl" as components>
<#import "../base.ftl" as layout>
<#import "/spring.ftl" as spring />

<@layout.mainLayout formName="menu.account" withButtonResponsiveButton=true>
    <@home.homeLayout userDetails=userDetails>
        <div class="card mb-3 mb-lg-5">
            <div class="card-header">
                <h5 class="card-title">
                    <@spring.message code="account.menu.account-members-billing" />
                </h5>
            </div>

            <div class="card-body">
                <div class="row">
                    <div class="col-6 col-md mb-3 mb-md-0">
                        <small class="text-cap"><@spring.message code="account.order.total" /></small>
                        <small class="text-dark font-weight-bold">&pound;${order.total}</small>
                    </div>
                    <div class="col-6 col-md mb-3 mb-md-0">
                        <small class="text-cap"><@spring.message code="account.order.status" /></small>
                        <small class="text-dark font-weight-bold">
                            <#if order.fullyPaid>
                                <div class="text-body font-size-1 mb-1">
                                                    <span class="badge badge-success">
                                                        <@spring.message code="account.order.fully-paid" />
                                                    </span>
                                </div>
                            </#if>
                            <#if order.partPaid>
                                <div class="text-body font-size-1 mb-1">
                                                    <span class="badge badge-info">
                                                        <@spring.message code="account.order.part-paid" />
                                                    </span>
                                </div>
                            </#if>
                            <#if order.outstanding>
                                <div class="text-body font-size-1 mb-1">
                                                    <span class="badge badge-warning">
                                                        <@spring.message code="account.order.outstanding" />
                                                    </span>
                                </div>
                            </#if>
                            <#if order.cancelled>
                                <div class="text-body font-size-1 mb-1">
                                                    <span class="badge badge-danger">
                                                        <@spring.message code="account.order.cancelled" />
                                                    </span>
                                </div>
                            </#if>
                        </small>
                    </div>
                    <div class="col-6 col-md">
                        <small class="text-cap"><@spring.message code="account.order.order-reference" /></small>
                        <small class="text-dark font-weight-bold">${order.webReference}</small>
                    </div>
                    <div class="col-6 col-md">
                        <small class="text-cap"><@spring.message code="account.order.placed" /></small>
                        <small class="text-dark font-weight-bold">${(order.createDate).format("dd/MM/yyyy")}</small>
                    </div>
                </div>
                <hr>
                <div class="row">
                    <div class="col-md-12">
                        <h5><@spring.message code="account.order.members" /></h5>
                        <div class="row mx-n1">
                            <table class="table">
                                <thead class="thead-light">
                                <tr>
                                    <th scope="col">#</th>
                                    <th scope="col">Name</th>
                                    <th scope="col">Category</th>
                                    <th scope="col">Price</th>
                                </tr>
                                </thead>
                                <tbody>
                                    <#list order.memberSubscription as subscription>
                                        <tr>
                                            <th scope="row">${subscription?counter}</th>
                                            <td>${subscription.member.fullName}</td>
                                            <td>${subscription.priceListItem.description}</td>
                                            <td>&pound;${subscription.price}</td>
                                        </tr>
                                    </#list>
                                </tbody>
                                <tfoot class="thead-light">
                                    <tr>
                                        <th scope="row"></th>
                                        <th></th>
                                        <th><b>Discounts</b></th>
                                        <th>&pound;${order.discount}</th>
                                    </tr>
                                    <tr>
                                        <th scope="row"></th>
                                        <th></th>
                                        <th><b>Total</b></th>
                                        <th>&pound;${order.total}</th>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>
                    </div>
                </div>
                <hr>
                <div class="row">
                    <div class="col-md-12">
                        <h5><@spring.message code="account.order.payments" /></h5>
                        <div class="row mx-n1">
                            <table class="table">
                                <thead class="thead-light">
                                <tr>
                                    <th scope="col">Date</th>
                                    <th scope="col">Status</th>
                                    <th scope="col">Type</th>
                                    <th scope="col">Amount</th>
                                </tr>
                                </thead>
                                <tbody>
                                    <#list order.payment as payment>
                                        <tr>
                                            <td>${(payment.date).format("dd/MM/yyyy")}</td>
                                            <td>
                                                <@spring.messageText code="payments.status.${payment.status}" text=payment.status />
                                            </td>
                                            <td>
                                                <@spring.messageText code="payments.${payment.type}-short" text=payment.type />
                                            </td>
                                            <td>&pound;${payment.amount}</td>
                                        </tr>
                                    </#list>
                                </tbody>
                                <tfoot class="thead-light">
                                    <tr>
                                        <th scope="row"></th>
                                        <th></th>
                                        <th><b>Collected</b></th>
                                        <th>&pound;${order.collected}</th>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </@home.homeLayout>
</@layout.mainLayout>
