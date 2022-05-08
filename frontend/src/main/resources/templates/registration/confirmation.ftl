<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />
<@layout.mainLayout formName="membership.confirmation">
    <@components.panel>
        <form class="form-horizontal" method="post" name="payment" action="/payments">
            <input type="hidden" name="_csrf" value="${_csrf.token}" />
            <input type="hidden" name="id" value="${order.id}" />
            <input type="hidden" name="uuid" value="${order.uuid}" />

            <@components.section title="Order Number">
                Your order number is ${order.webReference}.
            </@components.section>

            <@components.section title="Payment">
                <div class="form-group">
                    <label class="col-md-4 control-label"><@spring.message code="payments.how-to-pay" /></label>
                    <div class="col-md-6">
                        <#list paymentTypes as paymentType>
                            <div class="radio">
                                <label>
                                    <input
                                        type="radio"
                                        name="payment-type"
                                        id="payment-type"
                                        value="${paymentType}"
                                        required="required" />
                                        &nbsp;&nbsp;<@spring.messageText code="payments.${paymentType}" text="${paymentType}"/>
                                </label>
                            </div>
                        </#list>
                    </div>
                </div>
            </@components.section>

            <@components.buttonGroup>
                <button type="submit" class="btn btn-primary btn-xlg transition-3d-hover" name="action" value="next">
                    <@spring.message code="membership.next" />
                    <i class="fa fa-arrow-circle-o-right"></i>
                </button>&nbsp;
            </@components.buttonGroup>
        </form>
    </@components.panel>
</@layout.mainLayout>
