<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />
<@layout.mainLayout>
    <@components.panel title="membership.register" type="info">
        <form class="form-horizontal" method="post" name="action">
            <input type="hidden" name="_csrf" value="${_csrf.token}" />
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
                                <td>${subscription.member["given-name"]}</td>
                                <td>${subscription.member["family-name"]}</td>
                                <td><@spring.message code="membership.${subscription.action}" /></td>
                                <td><@spring.message code="membership.${subscription.category}" /></td>
                                <td align="right">${subscription.price?string.currency}</td>
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
                            <td align="right">${basket.basketTotal?string.currency}</td>
                            <td></td>
                        </tr>
                    </tfoot>
                </table>
                <button type="submit" class="btn btn-info" name="action" value="add-member">
                    <i class="fa fa-plus"></i>
                    <@spring.message code="membership.add-member" />
                </button>&nbsp;
            </@components.section>

            <@components.buttonGroup>
                <button type="submit" class="btn btn-info btn-xlg" name="action" value="next">
                    <@spring.message code="membership.next" />
                    <i class="fa fa-arrow-circle-o-right"></i>
                </button>&nbsp;
            </@components.buttonGroup>
        </form>
    </@components.panel>
</@layout.mainLayout>
