<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />
<@layout.mainLayout>
    <@components.panel title="membership.confirmation" type="info">
        <form class="form-horizontal" method="post" name="action">
            <input type="hidden" name="_csrf" value="${_csrf.token}" />

            <@components.section title="Order Number">
                Your order number is ${order}.
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
