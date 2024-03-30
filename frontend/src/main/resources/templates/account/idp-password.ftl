<#import "base.ftl" as home>
<#import "../components.ftl" as components>
<#import "../base.ftl" as layout>
<#import "/spring.ftl" as spring />

<@layout.mainLayout formName="menu.account" withButtonResponsiveButton=true>
    <@home.homeLayout userDetails=userDetails>
        <div class="card mb-3 mb-lg-5">
            <div class="card-header">
                <h5 class="card-title">
                    <@spring.message code="account.password.title" />
                </h5>
            </div>

            <div class="card-body">
                <div class="alert alert-info" role="alert">
                    <h5 class="alert-heading">
                        <@spring.message code="account.password.no-password-change-title"/>
                    </h5>
                </div>

                <p class="text-inherit">
                    <@spring.message code="account.password.no-password-change"/>
                </p>
            </div>
        </div>
    </@home.homeLayout>
</@layout.mainLayout>
