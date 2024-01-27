<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout>
<#import "../components.ftl" as components />

<@layout.mainLayout formName="membership.member-details">
    <@components.panel>
        <form class="form-horizontal" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <input type="hidden" name="uuid" value="${uuid}" />
            <input type="hidden" name="category" value="${category}" />
            <input type="hidden" name="priceListItemId" value="${priceListItemId}" />

            <@components.section title="membership.enter-code">
                <#if (errors?size > 0)>
                    <@components.formErrors errors=errors errorKey="membership.registration-code-errors" />
                </#if>

                <@spring.message code="membership.enter-code-text" />

                <div class="row form-group">
                    <label class="col-md-4 control-label">
                        <@spring.message code="membership.registration-code" />
                    </label>
                    <div class="col-md-6">
                        <input class="form-control  c-square c-theme"
                               name="code"
                               type="text"
                               placeholder="<@spring.messageText code="membership.registration-code-placeholder" text="" />"
                               required="required" />
                    </div>
                </div>
            </@components.section>

            <@components.buttonGroup>
                <a href="/register" class="btn btn-danger transition-3d-hover">
                    <@spring.message code="membership.cancel" />
                </a>&nbsp;
                <button type="reset" class="btn btn-light transition-3d-hover" name="action">
                    <@spring.messageText code="membership.reset" text="Clear Form" />
                </button>&nbsp;
                <button type="submit" class="btn btn-primary transition-3d-hover" name="action" value="save">
                    <@spring.message code="membership.next" />
                    <i class="fa fa-check-circle"></i>
                </button>&nbsp;
            </@components.buttonGroup>
        </form>
    </@components.panel>
</@layout.mainLayout>