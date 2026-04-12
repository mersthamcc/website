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
                        <#if order.actionRequired && !order.scheduled>
                            <a class="btn btn-sm btn-block btn-primary" href="${order.uuid}/new-mandate">
                                <small class="text-cap"><i class="fas fa-plus-circle fa-sm mr-2"></i> New Direct Debit</small>
                            </a>
<#--                            <a class="btn btn-sm btn-block btn-soft-primary" href="${order.uuid}/settlement">-->
<#--                                <small class="text-cap"><i class="fas fa-money-check fa-sm mr-2"></i> Settle in full</small>-->
<#--                            </a>-->
                        </#if>
<#--                        <#if order.scheduled>-->
<#--                            <a class="btn btn-sm btn-block btn-danger" href="${order.uuid}/cancel-payments">-->
<#--                                <small class="text-cap"><i class="fas fa-cross fa-sm mr-2"></i> Cancel Direct Debit</small>-->
<#--                            </a>-->
<#--                        </#if>-->
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
                                    <th scope="col">Name</th>
                                    <th scope="col">Category</th>
                                    <th scope="col">Price</th>
                                </tr>
                                </thead>
                                <tbody>
                                    <#list order.memberSubscription as subscription>
                                        <tr>
                                            <td><small>${subscription.member.fullName}</small></td>
                                            <td><small>${subscription.priceListItem.description}</small></td>
                                            <td><small>&pound;${subscription.price}</small></td>
                                        </tr>
                                    </#list>
                                </tbody>
                                <tfoot class="thead-light">
                                    <#if (order.discount > 0)>
                                        <tr>
                                            <th scope="row"></th>
                                            <th scope="row"><small><b>Discounts</b></small></th>
                                            <th scope="row"><small>&pound;${order.discount}</small></th>
                                        </tr>
                                    </#if>
                                    <tr>
                                        <th scope="row"></th>
                                        <th scope="row"><small><b>Total</b></small></th>
                                        <th scope="row"><small>&pound;${order.total}</small></th>
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
                                            <td><small>${(payment.date).format("dd/MM/yyyy")}</small></td>
                                            <td>
                                                <small><@spring.messageText code="payments.status.${payment.status}" text=payment.status /></small>
                                            </td>
                                            <td>
                                                <small><@spring.messageText code="payments.${payment.type}-user" text=payment.type /></small>
                                            </td>
                                            <td><small>&pound;${payment.amount}</small></td>
                                        </tr>
                                    </#list>
                                </tbody>
                                <tfoot class="thead-light">
                                    <tr>
                                        <th scope="row"></th>
                                        <th scope="row"></th>
                                        <th scope="row"><small class="text-cap"><b>Collected</b></small></th>
                                        <th scope="row"><small>&pound;${order.collected}</small></th>
                                    </tr>
                                    <#if (order.collected < order.total)>
                                        <tr>
                                            <th scope="row"></th>
                                            <th scope="row"></th>
                                            <th scope="row"><small class="text-cap"><b>Outstanding</b></small></th>
                                            <th scope="row"><small>&pound;${order.total - order.collected}</small></th>
                                        </tr>
                                    </#if>
                                </tfoot>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </@home.homeLayout>
</@layout.mainLayout>
