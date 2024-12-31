<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />
<@layout.mainLayout formName="menu.register">
    <@components.panel>
        <form class="form-horizontal" method="post" name="action" action="/register/policies">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

            <#if errors??>
                <@components.formMessages errors=errors errorKey="membership.errors" class="alert-danger"/>
            </#if>

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
