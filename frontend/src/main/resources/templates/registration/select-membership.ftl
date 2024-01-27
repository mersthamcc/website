<#import "../base.ftl" as layout>
<#import "../components.ftl" as components />
<#import "/spring.ftl" as spring />

<@layout.mainLayout formName="membership.choose-membership-type">
    <@components.panel>
        <@components.membershipCategories categories=categories _csrf=_csrf subscriptionId=subscriptionId/>

        <@components.buttonGroup>
            <a href="/register" class="btn btn-danger transition-3d-hover">
                <@spring.message code="membership.cancel" />
            </a>&nbsp;
        </@components.buttonGroup>
    </@components.panel>
</@layout.mainLayout>