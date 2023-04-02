<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />
<@layout.mainLayout formName="menu.register">
    <@components.panel>
        <form class="form-horizontal" method="post" name="action" action="/register">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <@components.section title="Members">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th><@spring.message code="membership.given-name" /></th>
                            <th><@spring.message code="membership.family-name" /></th>
                            <th><@spring.message code="membership.action" /></th>
                            <th><@spring.message code="membership.category" /></th>
                            <th><@spring.message code="membership.price" /></th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <#list basket.subscriptions as id, subscription>
                            <tr scope="row">
                                <td>${subscription.member.attributeMap["given-name"].asText()!""}</td>
                                <td>${subscription.member.attributeMap["family-name"].asText()!""}</td>
                                <td><@spring.message code="membership.${subscription.action}" /></td>
                                <td><@spring.message code="membership.${subscription.category!'unknown'}" /></td>
                                <td text-align="right">${subscription.price?string.currency}</td>
                                <td>
                                    <button type="submit" class="btn btn-info btn-xs" name="edit-member" value="${id}">
                                        <i class="fa fa-edit"></i>
                                        <@spring.message code="membership.review" />
                                    </button>&nbsp;

                                    <button type="submit" class="btn c-btn-red btn-xs" name="delete-member" value="${id}">
                                        <i class="fa fa-minus"></i>
                                        <@spring.message code="membership.delete-member" />
                                    </button>&nbsp;
                                </td>
                            </tr>
                        </#list>
                    </tbody>
                    <tfoot>
                        <tr>
                            <td></td>
                            <td></td>
                            <td></td>
                            <th>Total:</th>
                            <td text-align="right">${basket.basketTotal?string.currency}</td>
                            <td></td>
                        </tr>
                    </tfoot>
                </table>
                <button type="submit"
                        class="btn btn-soft-primary transition-3d-hover"
                        name="action"
                        value="add-member">
                    <i class="fa fa-plus"></i>
                    <@spring.message code="membership.add-member" />
                </button>&nbsp;
            </@components.section>

            <@components.buttonGroup>
                <button type="submit" class="btn btn-primary transition-3d-hover" name="action" value="next">
                    <@spring.message code="membership.next" />
                    <i class="fa fa-arrow-circle-o-right"></i>
                </button>&nbsp;
            </@components.buttonGroup>
        </form>
    </@components.panel>
</@layout.mainLayout>
