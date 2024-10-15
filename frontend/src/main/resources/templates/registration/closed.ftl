<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />
<@layout.mainLayout formName="menu.register">
    <@components.panel>
        <@components.section title="membership.closed">
            <div class="w-md-80 w-lg-50 text-center mx-md-auto mb-5 mb-md-9">
                <h4 class="h4">
                    <@spring.message code="membership.closed-message" />
                </h4>
            </div>
        </@components.section>
    </@components.panel>
</@layout.mainLayout>
