<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout>
<#import "../components.ftl" as components />

<@layout.mainLayout>
    <@components.panel title="membership.member-details" type="info">
        <form class="form-horizontal" method="post" name="action" action="/register/add-member">
            <input type="hidden" name="_csrf" value="${_csrf.token}" />
            <input type="hidden" name="uuid" value="${subscription.uuid}" />
            <input type="hidden" name="action" value="${subscription.action}" />
            <#list form as section>
                <@components.section title="membership.${section.section().key()}">
                    <#list section.section().attribute() as attribute>
                        <@components.memberField attribute=attribute />
                    </#list>
                </@components.section>
            </#list>

            <@components.buttonGroup>
                <button type="submit" class="btn btn-default btn-lg" name="action" value="cancel">
                    <@spring.message code="membership.cancel" />
                    <i class="fa fa-cross"></i>
                </button>&nbsp;
                <button type="reset" class="btn btn-white btn-lg" name="action">
                    <@spring.messageText code="membership.reset" text="Clear Form" />
                </button>&nbsp;
                <button type="submit" class="btn btn-info btn-lg" name="action" value="save">
                    <@spring.message code="membership.save" />
                    <i class="fa fa-check-circle"></i>
                </button>&nbsp;
            </@components.buttonGroup>
        </form>
    </@components.panel>
</@layout.mainLayout>