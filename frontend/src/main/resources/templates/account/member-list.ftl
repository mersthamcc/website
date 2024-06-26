<#import "base.ftl" as home>
<#import "../components.ftl" as components>
<#import "../base.ftl" as layout>
<#import "/spring.ftl" as spring />

<@layout.mainLayout formName="menu.account" withButtonResponsiveButton=true>
    <@home.homeLayout userDetails=userDetails>
        <div class="card mb-3 mb-lg-5">
            <div class="card-header">
                <h5 class="card-title">
                    <@spring.message code="account.menu.account-members" />
                </h5>
                <div class="right-align">
                    <a href="/register" class="btn btn-ghost-primary">
                        <@spring.message code="account.menu.register-new" />
                    </a>
                </div>
            </div>

            <div class="card-body">
                <#list members as member>
                    <div class="border-bottom pb-5 mb-5">
                        <div class="media">
                            <div class="max-w-15rem w-100 mr-3">
                                <@components.avatar givenName=member.givenName familyName=member.familyName/>
                            </div>
                            <div class="media-body">
                                <div class="row">
                                    <div class="col-md-7 mb-3 mb-md-0">
                                        <a class="h5 d-block" href="#">${member.givenName} ${member.familyName}</a>

                                        <div class="d-block d-md-none">
                                            <span class="h5 d-block mb-1">&pound;${member.lastSubsPrice}</span>
                                        </div>

                                        <div class="text-body font-size-1 mb-1">
                                            <span>
                                                <@spring.message code="account.member.type" />
                                            </span>
                                            <span>${member.description}</span>
                                        </div>
                                        <div class="text-body font-size-1 mb-1">
                                            <span>
                                                <@spring.message code="account.member.registered" />
                                            </span>
                                            <span>${(member.lastSubsDate).format("dd/MM/yyyy")}</span>
                                        </div>
                                        <div class="text-body font-size-1 mb-1">
                                            <span>
                                                <@spring.message code="account.member.age-group" />
                                            </span>
                                            <span>${member.ageGroup!"N/A"}</span>
                                        </div>
                                    </div>

                                    <div class="col-md-3">
                                        <div class="row">
                                            <div class="col-auto">

                                            </div>

                                            <div class="col-auto">
                                                <a class="d-block text-body font-size-1 mb-3" href="/account/pass/${member.uuid}/apple">
                                                    <img src="${resourcePrefix}/mcc/img/pass/apple-wallet.svg" width="130" />
                                                </a>

                                                <a class="d-block text-body font-size-1 mb-3" href="/account/pass/${member.uuid}/google">
                                                    <img src="${resourcePrefix}/mcc/img/pass/google-add-wallet-badge.svg" width="130" />
                                                </a>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-4 col-md-2 d-none d-md-inline-block text-right">
                                        <span class="h5 d-block mb-1">&pound;${member.lastSubsPrice}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </#list>
            </div>
        </div>
    </@home.homeLayout>
</@layout.mainLayout>
