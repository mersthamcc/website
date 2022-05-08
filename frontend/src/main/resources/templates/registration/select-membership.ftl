<#import "../base.ftl" as layout>
<#import "../components.ftl" as components />

<@layout.mainLayout formName="membership.choose-membership-type">
    <@components.panel>
        <@components.membershipCategories categories=categories _csrf=_csrf uuid=subscription.uuid/>
    </@components.panel>
</@layout.mainLayout>