<#import "/spring.ftl" as spring />

<#macro breadcrumbs breadcrumbs currentRoute>
    <div class="bg-light">
        <div class="container py-3">
            <div class="row justify-content-md-between align-items-md-center">
                <div class="col-md-5 mb-3 mb-md-0">
                    <!-- Breadcrumb -->
                    <nav class="d-inline-block rounded" aria-label="breadcrumb">
                        <ol class="breadcrumb breadcrumb-no-gutter font-size-1 mb-0">
                            <@breadcrumbItems breadcrumbs=breadcrumbs currentRoute=currentRoute />
                        </ol>
                    </nav>
                    <!-- End Breadcrumb -->
                </div>
            </div>
        </div>
    </div>
</#macro>

<#macro breadcrumbItems breadcrumbs currentRoute>
    <#if breadcrumbs??>
        <#list breadcrumbs as breadcrumb>
            <#if breadcrumb?has_next>
                <li class="breadcrumb-item">
                    <a href="${breadcrumb.destinationUrl}">
                        <@spring.messageArgs
                        code="menu.${breadcrumb.name}"
                        args=breadcrumb.argumentValues
                        />
                    </a>
                </li>
            <#else>
                <li class="breadcrumb-item active" aria-current="page">
                    <@spring.messageArgs
                    code="menu.${breadcrumb.name}"
                    args=breadcrumb.argumentValues
                    />
                </li>
            </#if>
        </#list>
    <#else>
        <li class="breadcrumb-item active" aria-current="page">
            <@spring.messageArgsText
            code="menu.${currentRoute.name}"
            args=currentRoute.argumentValues
            text=currentRoute.name
            />
        </li>
    </#if>
</#macro>

<#macro formHeader formName resourcePrefix breadcrumbs currentRoute>
    <div class="bg-dark" style="background-image: url(${resourcePrefix}/assets/svg/components/abstract-shapes-20.svg);">
        <div class="container space-1 space-top-lg-2 space-bottom-lg-3">
            <div class="row align-items-center">
                <div class="col">
                    <div class="d-none d-lg-block">
                        <h1 class="h2 text-white">
                            <@spring.messageText
                                code="${formName}"
                                text=formName!""
                            />
                        </h1>
                    </div>

                    <ol class="breadcrumb breadcrumb-light breadcrumb-no-gutter mb-0">
                        <@breadcrumbItems breadcrumbs=breadcrumbs currentRoute=currentRoute />
                    </ol>
                </div>

                <div class="col-auto">&nbsp;</div>
            </div>
        </div>
    </div>
</#macro>

<#macro userMenu userMenu user={}>
    <div class="hs-unfold">
        <#if user.id??>
            <a class="js-hs-unfold-invoker dropdown-nav-link dropdown-toggle d-flex align-items-center" href="javascript:;"
               data-hs-unfold-options='{"target": "#userDropdown", "type": "css-animation", "event": "hover", "hideOnScroll": "true"}'>
                <i class="fas fa-user-circle"></i>
            </a>

            <div id="userDropdown" class="hs-unfold-content dropdown-menu">
                <#list userMenu as item>
                    <a class="dropdown-item" href="${item.destinationUrl}">
                        <@spring.message code="menu.${item.name}" />
                    </a>
                </#list>
            </div>
        <#else>
            <a class="js-hs-unfold-invoker btn btn-icon btn-xs btn-ghost-secondary" href="/login">
                <i class="fas fa-user-circle"></i>
            </a>
        </#if>

    </div>
</#macro>

<#macro topmenu topMenu user={}>
    <div class="ml-auto">
        <!-- Jump To -->
        <div class="hs-unfold d-sm-none mr-2">
            <a class="js-hs-unfold-invoker dropdown-nav-link dropdown-toggle d-flex align-items-center" href="javascript:;"
               data-hs-unfold-options='{"target": "#jumpToDropdown","type": "css-animation","event": "hover","hideOnScroll": "true"}'>
                Jump to
            </a>

            <div id="jumpToDropdown" class="hs-unfold-content dropdown-menu">
                <#list topMenu as item>
                    <#if item.roles?? && item.roles?size!=0>
                        <#if user.id?? && user.hasOneOfRoles(item.roles)>
                            <@topMenuItem item=item className="dropdown-item" />
                        </#if>
                    <#else>
                        <@topMenuItem item=item className="dropdown-item" />
                    </#if>
                </#list>
            </div>
        </div>
        <!-- End Jump To -->

        <!-- Links -->
        <div class="nav nav-sm nav-y-0 d-none d-sm-flex ml-sm-auto">
            <#list topMenu as item>
                <#if item.roles?? && item.roles?size!=0>
                    <#if user.id?? && user.hasOneOfRoles(item.roles)>
                        <@topMenuItem item=item />
                    </#if>
                <#else>
                    <@topMenuItem item=item />
                </#if>
            </#list>
        </div>
        <!-- End Links -->
    </div>
</#macro>

<#macro topMenuItem item className="nav-link">
    <a href="${item.destinationUrl}" class="${className}">
        <@spring.message code="menu.${item.name}" />
    </a>
</#macro>

<#macro adminMenuItems items=[]>
    <#list items as item>
        <#if item.isActiveNode(currentRoute)>
            <#assign classes>active</#assign>
            <#assign style></#assign>
        <#elseif item.onActivePath(currentRoute)>
            <#assign classes>show</#assign>
            <#assign style>display: block;</#assign>
        <#else>
            <#assign classes></#assign>
            <#assign style></#assign>
        </#if>
        <#assign itemTitle>
            <@spring.messageArgsText
                code="menu.${item.name}"
                args=item.argumentValues
                text="${item.name}"
            />
        </#assign>
        <li class="navbar-vertical-aside-has-menu ${classes}">
            <#if item.children??>
                <a class="js-navbar-vertical-aside-menu-link nav-link nav-link-toggle ${classes}"
                   href="${item.destinationUrl}"
                   title="${itemTitle}">
                    <i class="${item.icons!""} nav-icon"></i>
                    <span class="navbar-vertical-aside-mini-mode-hidden-elements text-truncate">
                        ${itemTitle}
                    </span>
                </a>

                <ul class="js-navbar-vertical-aside-submenu nav nav-sub "
                    style="${style}">
                    <#list item.children as subItem>
                        <#if subItem.isActiveNode(currentRoute)>
                            <#assign classes>active</#assign>
                        <#elseif subItem.onActivePath(currentRoute)>
                            <#assign classes>show</#assign>
                        <#else>
                            <#assign classes></#assign>
                        </#if>
                        <#assign subTitle>
                            <@spring.messageArgsText
                                code="menu.${subItem.name}"
                                args=subItem.argumentValues
                                text=subItem.name
                            />
                        </#assign>
                        <li class="nav-item">
                            <a class="nav-link ${classes}" href="${subItem.destinationUrl}" title="${subTitle}">
                                <span class="tio-circle nav-indicator-icon"></span>
                                <span class="text-truncate">${subTitle}</span>
                            </a>
                        </li>
                    </#list>
                </ul>
            <#else>
                <a class="js-navbar-vertical-aside-menu-link nav-link ${classes}"
                   href="${item.destinationUrl}"
                   title="${itemTitle}">
                    <i class="tio-home-vs-1-outlined nav-icon"></i>
                    <span class="navbar-vertical-aside-mini-mode-hidden-elements text-truncate">
                        ${itemTitle}
                    </span>
                </a>
            </#if>
        </li>
    </#list>
</#macro>

<#macro navMenu mainMenu=[]>
    <div id="navBar" class="collapse navbar-collapse">
        <div class="navbar-body header-abs-top-inner">
            <ul class="navbar-nav">
                <#list mainMenu as item>
                    <#if item.onActivePath(currentRoute)>
                        <#assign classes>active</#assign>
                    <#else>
                        <#assign classes></#assign>
                    </#if>
                    <#if item.children??>
                        <li class="hs-has-sub-menu navbar-nav-item">
                            <a id="menu-${item.name}"
                               class="hs-mega-menu-invoker nav-link nav-link-toggle ${classes}"
                               href="${item.destinationUrl}"
                               aria-haspopup="true"
                               aria-expanded="false"
                               aria-labelledby="submenu-${item.name}">
                                <@spring.messageArgs code="menu.${item.name}" args=item.argumentValues />
                            </a>

                            <div id="submenu-${item.name}"
                                 class="hs-sub-menu dropdown-menu"
                                 aria-labelledby="menu-${item.name}"
                                 style="min-width: 230px;">
                                <#list item.children as subitem>
                                    <#if subitem.onActivePath(currentRoute)>
                                        <#assign classes>active</#assign>
                                    <#else>
                                        <#assign classes></#assign>
                                    </#if>
                                    <#if subitem.children??>
                                        <div class="hs-has-sub-menu">
                                            <a id="menu-${subitem.name}"
                                               class="hs-mega-menu-invoker dropdown-item dropdown-item-toggle ${classes}"
                                               href="javascript:;"
                                               aria-haspopup="true"
                                               aria-expanded="false"
                                               aria-controls="submenu-${subitem.name}">
                                                <@spring.messageArgs code="menu.${subitem.name}" args=subitem.argumentValues />
                                            </a>
                                            <div id="submenu-${subitem.name}"
                                                 class="hs-sub-menu dropdown-menu"
                                                 aria-labelledby="menu-${subitem.name}"
                                                 style="min-width: 230px;">
                                                <#list subitem.children as leafItem>
                                                    <#if leafItem.onActivePath(currentRoute)>
                                                        <#assign classes>active</#assign>
                                                    <#else>
                                                        <#assign classes></#assign>
                                                    </#if>
                                                    <a class="dropdown-item ${classes}" href="${leafItem.destinationUrl}">
                                                        <@spring.messageArgs code="menu.${leafItem.name}" args=leafItem.argumentValues />
                                                    </a>
                                                </#list>
                                            </div>
                                        </div>
                                    <#else>
                                        <a class="dropdown-item ${classes}" href="${subitem.destinationUrl}">
                                            <@spring.messageArgs code="menu.${subitem.name}" args=subitem.argumentValues />
                                        </a>
                                    </#if>
                                </#list>
                            </div>
                        </li>
                    <#else>
                        <li class="navbar-nav-item">
                            <a id="menu-${item.name}"
                               class="hs-mega-menu-invoker nav-link ${classes}"
                               href="${item.destinationUrl}"
                               aria-haspopup="true"
                               aria-expanded="false"
                               >
                                <@spring.messageArgs code="menu.${item.name}" args=item.argumentValues />
                            </a>
                        </li>
                    </#if>
                </#list>
                <!-- Button -->
                <li class="navbar-nav-last-item">
                    <a class="btn btn-sm btn-primary transition-3d-hover" href="/register">
                        <@spring.message code="menu.register" />
                    </a>
                </li>
                <!-- End Button -->
            </ul>
        </div>
    </div>
</#macro>

<#macro panel>
    <div class="container space-1 space-top-lg-0 space-bottom-lg-2 mt-lg-n10">
        <div class="row">
            <div class="col-lg-12">
                <#nested />
            </div>
        </div>
    </div>
</#macro>

<#macro membershipCategories categories _csrf uuid>
    <#list categories as category>
        <@section title="membership.${category.key()}">
            <div class="w-lg-80 mx-lg-auto position-relative">
                <@categoryPricelist category=category _csrf=_csrf uuid=uuid />
            </div>
        </@section>
    </#list>
</#macro>

<#macro categoryPricelist category _csrf uuid>
    <div class="row position-relative z-index-2 mx-n2 mb-5">
        <#list category.pricelistItem() as item>
            <div class="col-md-4 col-sm-3">
                <div class="px-2 mb-3">
                    <div class="card h-100">
                        <div class="card-header text-center">
                            <span class="d-block h3">
                                ${item.description()}
                            </span>
                            <span class="d-block mb-2">
                                <span class="text-dark align-top">&pound;</span>
                                <span class="font-size-4 text-dark font-weight-bold mr-n2">
                                <span id="pricingCount1">
                                    ${item.currentPrice()}
                                </span>
                                </span>
                                <span class="font-size-1">/ year</span>
                            </span>
                        </div>
                        <!-- End Header -->

                        <!-- Body -->
                        <div class="card-body">
                            <div class="media font-size-1 text-body mb-3">
                                <i class="fas fa-check-circle text-success mt-1 mr-2"></i>
                                <div class="media-body">
                                    Ages ${item.minAge()}
                                    <#if item.maxAge()??>
                                        ${item.maxAge()}
                                    <#else>
                                        and up
                                    </#if>
                                </div>
                            </div>
                            <div class="media font-size-1 text-body mb-3">
                                <i class="fas fa-check-circle text-success mt-1 mr-2"></i>
                                <div class="media-body">
                                    <#if (item.includesMatchFees())?? && item.includesMatchFees()>
                                        <i class="fa fa-check c-font-20"></i>
                                    <#elseif (item.includesMatchFees())??>
                                        <i class="fa fa-times c-font-20"></i>
                                    <#else>
                                        N/A
                                    </#if>
                                </div>
                            </div>
                            <div class="media font-size-1 text-body mb-3">
                                <i class="fas fa-check-circle text-success mt-1 mr-2"></i>
                                <div class="media-body">

                                </div>
                            </div>
                            <div class="media font-size-1 text-body">
                                <i class="fas fa-check-circle text-success mt-1 mr-2"></i>
                                <div class="media-body">

                                </div>
                            </div>
                        </div>
                        <!-- End Body -->

                        <div class="card-footer border-0">
                            <form class="form-horizontal" method="post" name="subscription" action="/register/select-membership">
                                <input type="hidden" name="_csrf" value="${_csrf.token}" />
                                <input type="hidden" name="category" value="${category.key()}" />
                                <input type="hidden" name="uuid" value="${subscription.uuid}" />
                                <button
                                        type="submit"
                                        class="btn btn-soft-primary btn-block transition-3d-hover"
                                        value="${item.id()}"
                                        name="pricelistItemId"
                                        id="pricelistItemId-${item.id()}">
                                    <@spring.message code="membership.select" />
                                </button>
                            </form>

                        </div>
                    </div>
                </div>
            </div>
        </#list>
    </div>

</#macro>

<#macro noButtons>

</#macro>

<#macro section title buttons=noButtons>
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

        <#if buttons?is_directive><@buttons /><#else>${buttons}</#if>
    </div>
    <!-- End Card -->
</#macro>

<#macro buttonGroup>
    <div class="card mb-3 mb-lg-5">
        <div class="card-body d-flex justify-content-end">
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
    <div class="row form-group">
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
           type="${type}"
           placeholder="<@spring.messageText code="${localeCategory}.${key}-placeholder" text="" />"
           value="${subscription.member[key]!""}"
            ${required}
    />
</#macro>