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
                <ul class="list-unstyled">
                    <#list orders as order>
                        <li class="card card-bordered shadow-none mb-3">
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
                                    <div class="col-md-8">
                                        <h5><@spring.message code="account.order.members" /></h5>
                                        <#list order.memberSubscription as subscription>
                                            <div class="row mx-n1">
                                                <span>${subscription.member.fullName} - ${subscription.priceListItem.description}</span>
                                            </div>
                                        </#list>
                                    </div>

                                    <div class="col-md-4">
                                        <a class="btn btn-sm btn-block btn-white mb-2" href="/account/billing/${order.uuid}">
                                            <i class="fas fa-shopping-cart fa-sm mr-2"></i> <@spring.message code="account.order.view" />
                                        </a>
<#--                                        <a class="btn btn-sm btn-block btn-primary" href="#">Buy it again</a>-->
                                    </div>
                                </div>
                            </div>
                        </li>
                    </#list>
                </ul>
            </div>
        </div>
    </@home.homeLayout>
</@layout.mainLayout>
