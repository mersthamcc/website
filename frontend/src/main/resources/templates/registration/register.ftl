<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />
<@layout.mainLayout formName="menu.register">
    <@components.panel>
        <form class="form-horizontal" method="post" name="action" action="/register">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

            <#if errors??>
                <@components.formErrors errors=errors errorKey="membership.errors" class="alert-danger"/>
            </#if>

            <@components.section title="Members">
                <div class="table-responsive">
                    <table class="table table-borderless table-thead-bordered table-align-middle">
                        <thead class="thead-light">
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
                                    <td class="align-right">${subscription.price?string.currency}</td>
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
                            <#list basket.discounts as name, discount>
                                <tr>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                    <td><i><@spring.messageText code=name text=name /></i></td>
                                    <td class="align-right">-${discount?string.currency}</td>
                                    <td></td>
                                </tr>
                            </#list>
                            <tr>
                                <td></td>
                                <td></td>
                                <td></td>
                                <th>Total:</th>
                                <td class="align-right"><b>${basket.basketTotal?string.currency}</b></td>
                                <td></td>
                            </tr>
                        </tfoot>
                    </table>
                </div>
                <button type="submit"
                        class="btn btn-soft-primary transition-3d-hover"
                        name="action"
                        value="add-member">
                    <i class="fa fa-plus"></i>
                    <@spring.message code="membership.add-member" />
                </button>&nbsp;
            </@components.section>

            <@components.section title="Policies">
                <p>
                    <@spring.message code="membership.declarations-body" />
                </p>
                <div class="row form-group">
                    <label class="col-md-3 control-label">
                        <@spring.message code="membership.policies" />
                    </label>
                    <div class="col-md-9">
                        <ul class="list-checked">
                            <li>
                                <a href="/pages/constitution">Club Constitution</a>
                            </li>
                            <li>
                                <a href="/pages/code-of-conduct">Code of Conduct</a>
                            </li>
                            <li>
                                <a href="/pages/code-of-conduct-juniors">Code of Conduct for Juniors</a>
                            </li>
                            <li>
                                <a href="/pages/safeguarding">Safeguarding Policy</a>
                            </li>
                        </ul>
                    </div>
                </div>
                <div class="row form-group">
                    <label class="col-md-3 control-label">
                        <@spring.message code="membership.declarations" />
                    </label>
                    <div class="col-md-9">
                        <div class="checkbox">
                            <label>
                                <input
                                        type="checkbox"
                                        name="declarations"
                                        id="member-accept-policies"
                                        value="policies" />
                                &nbsp;&nbsp;<@spring.message code="membership.accept-policies" />
                            </label>
                        </div>
                        <div class="checkbox">
                            <label>
                                <input
                                        type="checkbox"
                                        name="declarations"
                                        id="member-accept-terms"
                                        value="terms" />
                                &nbsp;&nbsp;<@spring.message code="membership.accept-terms" />
                            </label>
                        </div>
                    </div>
                </div>
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
