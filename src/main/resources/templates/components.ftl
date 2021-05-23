<#import "/spring.ftl" as spring />
<#macro topMenuItem item hasNext>
    <li>
        <a href="${item.destinationUrl}">
            <@spring.message code="menu.${item.name}" />
        </a>
    </li>
    <#if hasNext>
        <li class="c-divider">&nbsp;&nbsp;|&nbsp;&nbsp;</li>
    </#if>
</#macro>

<#macro topmenu topMenu userMenu user={}>
    <nav class="c-top-menu c-pull-right">
        <ul class="c-links c-ext c-theme-ul">
            <#list topMenu as item>
                <#if item.roles?? && item.roles?size!=0>
                    <#if user.id?? && user.hasOneOfRoles(item.roles)>
                        <@topMenuItem item=item hasNext=item?has_next />
                    </#if>
                <#else>
                    <@topMenuItem item=item hasNext=item?has_next />
                </#if>
            </#list>
            <li class="c-lang c-last">
                <#if user.id??>
                    <a href="#">${user.givenName}</a>
                    <ul class="dropdown-menu pull-right" role="menu">
                        <#list userMenu as item>
                            <li>
                                <a href="${item.destinationUrl}">
                                    <@spring.message code="menu.${item.name}" />
                                </a>
                            </li>
                        </#list>
                    </ul>
                <#else>
                    <a href="/login"><@spring.message code="menu.login" /></a>
                </#if>
            </li>
            <li class="c-search hide">
                <!-- BEGIN: QUICK SEARCH -->
                <form action="#">
                    <input type="text" name="query" placeholder="Search..." value="" class="form-control" autocomplete="off">
                    <i class="fa fa-search"></i>
                </form>
                <!-- END: QUICK SEARCH -->
            </li>
        </ul>
    </nav>
</#macro>

<#macro panel title type>
    <div class="panel panel-${type}">
        <div class="panel-heading">
            <h3 class="panel-title">
                <@spring.message code="${title}" />
            </h3>
        </div>
        <div class="panel-body">
            <#nested />
        </div>
    </div>
</#macro>

<#macro membershipCategories categories _csrf uuid>
    <#list categories as category>
        <div class="c-content-title-1 c-title-md c-margin-b-20 clearfix">
            <h3 class="c-font-thin"><@spring.message code="membership.${category.key()}" /></h3>
            <div class="c-line-left c-theme-bg"></div>
            <@categoryPricelist category=category _csrf=_csrf uuid=uuid/>
        </div>
    </#list>
</#macro>

<#macro categoryPricelist category _csrf uuid>
    <div class="row">
        <div class="c-content-pricing-1 c-opt-1">
            <div class="col-md-2 c-sm-hidden">
                <div class="c-content c-column-odd c-padding-adjustment">
                    <div class="c-row c-type c-font-20 c-align-left">&nbsp;<br />&nbsp;</div>
                    <div class="c-row c-title c-font-17">Age(s)</div>
                    <div class="c-row c-title c-font-17">Gender</div>
                    <div class="c-row c-title c-font-17"></div>
                    <div class="c-row c-title c-font-17">Includes Match Fees</div>
                    <div class="c-row c-empty">&nbsp;</div>
                    <div class="c-row c-empty">&nbsp;</div>
                </div>
            </div>
            <#list category.pricelistItem() as item>
                <div class="col-md-2 col-sm-3">
                    <div class="c-content c-column-<#if item?is_even_item>even<#else>odd</#if> c-padding-adjustment">
                        <div class="c-row c-type c-font-20">
                            ${item.description()}
                        </div>
                        <div class="c-row c-font-17">
                            ${item.minAge()}
                            <#if item.maxAge()??>
                                ${item.maxAge()}
                            <#else>
                                and up
                            </#if>
                            <span class="c-sub-title">Age(s)</span>
                        </div>
                        <div class="c-row">
                            All <span class="c-sub-title">Gender</span>
                        </div>
                        <div class="c-row">

                        </div>
                        <div class="c-row">
                            <#if (item.includesMatchFees())?? && item.includesMatchFees()>
                                <i class="fa fa-check c-font-20"></i>
                            <#elseif (item.includesMatchFees())??>
                                <i class="fa fa-times c-font-20"></i>
                            <#else>
                                N/A
                            </#if>
                            <span class="c-sub-title">Match fees included</span>
                        </div>
                        <div class="c-row c-price">
                            <span class="c-dollar c-font-20">&pound;</span> <span class="c-font-30 c-font-bold">${item.currentPrice()}</span>
                        </div>
                        <div class="c-row c-purchase">
                            <form class="form-horizontal" method="post" name="subscription" action="/register/select-membership">
                                <input type="hidden" name="_csrf" value="${_csrf.token}" />
                                <input type="hidden" name="category" value="${category.key()}" />
                                <input type="hidden" name="uuid" value="${subscription.uuid}" />

                                <button class="btn btn-md c-btn-square c-btn-green c-btn-uppercase c-btn-bold" value="${item.id()}" name="pricelistItemId" id="pricelistItemId-${item.id()}" type="submit"><@spring.message code="membership.select" /></button>
                            </form>
                        </div>
                    </div>
                </div>
            </#list>
        </div>
    </div>
</#macro>

<#macro section title>
    <div class="c-content-title-1 c-title-md c-margin-b-20 clearfix">
        <h3 class="c-font-thin"><@spring.messageText code=title text=title /></h3>
        <div class="c-line-left c-theme-bg"></div>
    </div>
    <#nested />
</#macro>

<#macro buttonGroup>
    <div class="form-group c-margin-t-40">
        <div class="col-sm-offset-4 col-md-8">
            <#nested />
        </div>
    </div>
</#macro>

<#macro memberField attribute subscription localeCategory="membership">
    <#if attribute.mandatory()>
        <#assign required>required="required"</#assign>
    <#else>
        <#assign required></#assign>
    </#if>
    <div class="form-group">
        <label class="col-md-4 control-label"><@spring.message code="${localeCategory}.${attribute.definition().key()}" /></label>
        <div class="col-md-6">
            <#switch attribute.definition().type().rawValue()>
                <#case "String">
                    <@memberInputField type="text"
                        required=required
                        subscription=subscription
                        key=attribute.definition().key()
                        localeCategory=localeCategory />
                    <#break>
                <#case "Number">
                    <@memberInputField type="number"
                        required=required
                        subscription=subscription
                        key=attribute.definition().key()
                        localeCategory=localeCategory />
                    <#break>
                <#case "Email">
                    <@memberInputField type="email"
                        required=required
                        subscription=subscription
                        key=attribute.definition().key()
                        localeCategory=localeCategory />
                    <#break>
                <#case "Date">
                    <@memberInputField type="date"
                        required=required
                        subscription=subscription
                        key=attribute.definition().key()
                        localeCategory=localeCategory />
                    <#break>
                <#case "Option">
                    <#list attribute.definition().choices() as choice>
                        <div class="radio">
                            <label>
                                <input
                                        type="radio"
                                        name="member[${attribute.definition().key()}]"
                                        id="member-${attribute.definition().key()}"
                                        value="${choice}"
                                        <#if subscription.member?keys?seq_contains(attribute.definition().key())
                                            && choice == subscription.member[attribute.definition().key()]>
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
                            <label>
                                <input
                                        type="checkbox"
                                        name="member[${attribute.definition().key()}]"
                                        id="member-${attribute.definition().key()}"
                                        value="${choice}"
                                        <#if subscription.member?keys?seq_contains(attribute.definition().key())
                                            && subscription.member[attribute.definition().key()]?seq_contains(choice)>
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

<#macro memberInputField type required subscription key localeCategory>
    <input class="form-control  c-square c-theme"
           name="member[${key}]"
           type="text"
           placeholder="<@spring.messageText code="${localeCategory}.${key}-placeholder" text="" />"
           value="${subscription.member[key]!""}"
            ${required}
    />
</#macro>