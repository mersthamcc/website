<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout>
<#import "../components.ftl" as components />

<@layout.mainLayout formName="membership.member-details">
    <@components.panel>
        <form class="form-horizontal" method="post" name="action" action="/register/add-member">
            <input type="hidden" name="_csrf" value="${_csrf.token}" />
            <input type="hidden" name="uuid" value="${subscriptionId}" />
            <input type="hidden" name="action" value="${subscription.action!"huh"}" />
            <#list form as section>
                <@components.section title="membership.${section.section.key}">
                    <#list section.section.attribute as attribute>
                        <@components.memberField attribute=attribute data=data subscription=subscription />
                    </#list>
                </@components.section>
            </#list>

            <@components.buttonGroup>
                <button type="submit" class="btn btn-danger transition-3d-hover" name="action" value="cancel" formnovalidate>
                    <@spring.message code="membership.cancel" />
                </button>&nbsp;
                <button type="reset" class="btn btn-light transition-3d-hover" name="action">
                    <@spring.messageText code="membership.reset" text="Clear Form" />
                </button>&nbsp;
                <button type="submit" class="btn btn-primary transition-3d-hover" name="action" value="save">
                    <@spring.message code="membership.save" />
                    <i class="fa fa-check-circle"></i>
                </button>&nbsp;
            </@components.buttonGroup>
        </form>
    </@components.panel>
</@layout.mainLayout>