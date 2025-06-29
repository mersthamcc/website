<#import "../base.ftl" as layout>
<#import "/spring.ftl" as spring />
<#import "../components.ftl" as components />

<#macro headers>

</#macro>

<@layout.mainLayout headers=headers formName="club-draw.join">
    <@components.panel>
        <form class="form-horizontal" method="post" name="club-draw-form" id="club-draw-form">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            <input type="hidden" name="subscriptionId" value="${subscriptionId}" />

            <@components.section title="Payment Schedule">
                <div class="form-group">
                    <label class="col-md-3 control-label">Payment Day:</label>
                    <div class="col-md-9">
                        <select class="form-control" name="payment_day" id="payment_day">
                            <#list 1..28 as day>
                                <option value="${day}">${day}</option>
                            </#list>
                            <option value="-1">Last Day of Month</option>
                        </select>
                        <span class="help-block">The day of the month you wish the payment to leave your account. If that day of the current month is less than 3 working days from now, your payments will start next month.</span>
                    </div>
                </div>
            </@components.section>

            <@components.buttonGroup>
                <a href="/register" class="btn btn-danger transition-3d-hover">
                    <@spring.message code="membership.cancel" />
                </a>&nbsp;
                <button type="submit" class="btn btn-primary btn-xlg transition-3d-hover" name="action" value="next">
                    <@spring.message code="membership.next" />
                    <i class="fa fa-arrow-circle-o-right"></i>
                </button>&nbsp;
            </@components.buttonGroup>
        </form>
    </@components.panel>
</@layout.mainLayout>