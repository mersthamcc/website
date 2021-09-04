<#macro section title action buttons=noButtons>
    <form class="form-horizontal" method="post" name="action" action="${action}">
        <input type="hidden" name="_csrf" value="${_csrf.token}" />
        <!-- Card -->
        <div class="card mb-3 mb-lg-5">
            <div class="card-header">
                <h5 class="card-title"><@spring.messageText code="${title}" text="${title}" /></h5>
            </div>

            <!-- Body -->
            <div class="card-body">
                <#nested />
            </div>
            <!-- End Body -->

            <div class="card-footer d-flex justify-content-end">
                <#if buttons?is_directive><@buttons /><#else>${buttons}</#if>
            </div>
        </div>
        <!-- End Card -->
    </form>
</#macro>

<#macro memberAdminField attribute data localeCategory="membership">
    <#if attribute.mandatory()>
        <#assign required>required="required"</#assign>
    <#else>
        <#assign required></#assign>
    </#if>
    <div class="row form-group">
        <label class="col-md-4 control-label"><@spring.message code="${localeCategory}.${attribute.definition().key()}" /></label>
        <div class="col-md-6">
            <#switch attribute.definition().type().rawValue()>
                <#case "String">
                    <@memberAdminInputField type="text"
                    required=required
                    data=data
                    key=attribute.definition().key()
                    localeCategory=localeCategory />
                    <#break>
                <#case "Number">
                    <@memberAdminInputField type="number"
                    required=required
                    data=data
                    key=attribute.definition().key()
                    localeCategory=localeCategory />
                    <#break>
                <#case "Email">
                    <@memberAdminInputField type="email"
                    required=required
                    data=data
                    key=attribute.definition().key()
                    localeCategory=localeCategory />
                    <#break>
                <#case "Date">
                    <@memberAdminInputField type="date"
                    required=required
                    data=data
                    key=attribute.definition().key()
                    localeCategory=localeCategory />
                    <#break>
                <#case "Option">
                    <#list attribute.definition().choices() as choice>
                        <div class="radio">
                            <label for="data-${attribute.definition().key()}-${choice}">
                                <input
                                        type="radio"
                                        name="data[${attribute.definition().key()}]"
                                        id="data-${attribute.definition().key()}-${choice}"
                                        value="${choice}"
                                        <#if data?keys?seq_contains(attribute.definition().key())
                                        && choice == data[attribute.definition().key()]>
                                            checked="checked"
                                        </#if>
                                        ${required} />
                                &nbsp;&nbsp;<@spring.messageText code="${localeCategory}.${choice}" text="${choice}"/>
                            </label>
                        </div>
                    </#list>
                    <#break>
                <#case "List">
                    <#list attribute.definition().choices() as choice>
                        <div class="checkbox">
                            <label for="data-${attribute.definition().key()}-${choice}">
                                <input
                                        type="checkbox"
                                        name="data[${attribute.definition().key()}]"
                                        id="data-${attribute.definition().key()}-${choice}"
                                        value="${choice}"
                                        <#if data?keys?seq_contains(attribute.definition().key())
                                        && data[attribute.definition().key()]?seq_contains(choice)>
                                            checked="checked"
                                        </#if>
                                        ${required} />
                                &nbsp;&nbsp;<@spring.messageText code="${localeCategory}.${choice}" text="${choice}"/>
                            </label>
                        </div>
                    </#list>
                    <#break>
            </#switch>
            <span class="help-block"><@spring.messageText code="${localeCategory}.${attribute.definition().key()}-help" text="" /></span>
        </div>
    </div>
</#macro>

<#macro memberAdminInputField type required data key localeCategory>
    <input class="form-control c-square c-theme"
           name="data[${key}]"
           type="${type}"
           placeholder="<@spring.messageText code="${localeCategory}.${key}-placeholder" text="" />"
           value="${data[key]!""}"
            ${required}
    />
</#macro>