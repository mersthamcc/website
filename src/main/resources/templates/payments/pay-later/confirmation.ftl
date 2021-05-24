<#import "/spring.ftl" as spring />
<#import "../../base.ftl" as layout />
<#import "../../components.ftl" as components />
<@layout.mainLayout>
    <@components.panel title="membership.confirmation" type="info">
        <@components.section title="Pay Later">
            <@spring.message code="payments.pay-later-confirmation" />
        </@components.section>

        <@components.buttonGroup>
            <a class="btn btn-default btn-xlg" href="/">
                <@spring.message code="membership.complete" />
                <i class="fa fa-icon-home"></i>
            </a>
        </@components.buttonGroup>
    </@components.panel>
</@layout.mainLayout>
