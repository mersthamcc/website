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
                        <@spring.messageArgsText
                        code="menu.${breadcrumb.name}"
                        args=breadcrumb.argumentValues
                        text=breadcrumb.displayName
                        />
                    </a>
                </li>
            <#else>
                <li class="breadcrumb-item active" aria-current="page">
                    <@spring.messageArgsText
                    code="menu.${breadcrumb.name}"
                    args=breadcrumb.argumentValues
                    text=breadcrumb.displayName
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

<#macro formHeader formName resourcePrefix breadcrumbs currentRoute withButtonResponsiveButton=false>
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

                <div class="col-auto">
                    <#if withButtonResponsiveButton>
                        <!-- Responsive Toggle Button -->
                        <button type="button" class="navbar-toggler btn btn-icon btn-sm rounde-circle d-lg-none" aria-label="Toggle navigation" aria-expanded="true" aria-controls="sidebarNav" data-toggle="collapse" data-target="#sidebarNav">
                            <span class="navbar-toggler-default">
                                <svg width="14" height="14" viewBox="0 0 18 18" xmlns="http://www.w3.org/2000/svg">
                                    <path fill="currentColor" d="M17.4,6.2H0.6C0.3,6.2,0,5.9,0,5.5V4.1c0-0.4,0.3-0.7,0.6-0.7h16.9c0.3,0,0.6,0.3,0.6,0.7v1.4C18,5.9,17.7,6.2,17.4,6.2z M17.4,14.1H0.6c-0.3,0-0.6-0.3-0.6-0.7V12c0-0.4,0.3-0.7,0.6-0.7h16.9c0.3,0,0.6,0.3,0.6,0.7v1.4C18,13.7,17.7,14.1,17.4,14.1z"></path>
                                </svg>
                            </span>
                            <span class="navbar-toggler-toggled">
                                <svg width="14" height="14" viewBox="0 0 18 18" xmlns="http://www.w3.org/2000/svg">
                                    <path fill="currentColor" d="M11.5,9.5l5-5c0.2-0.2,0.2-0.6-0.1-0.9l-1-1c-0.3-0.3-0.7-0.3-0.9-0.1l-5,5l-5-5C4.3,2.3,3.9,2.4,3.6,2.6l-1,1 C2.4,3.9,2.3,4.3,2.5,4.5l5,5l-5,5c-0.2,0.2-0.2,0.6,0.1,0.9l1,1c0.3,0.3,0.7,0.3,0.9,0.1l5-5l5,5c0.2,0.2,0.6,0.2,0.9-0.1l1-1 c0.3-0.3,0.3-0.7,0.1-0.9L11.5,9.5z"></path>
                                </svg>
                            </span>
                        </button>
                        <!-- End Responsive Toggle Button -->
                    </#if>
                </div>
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
            <a class="js-hs-unfold-invoker btn btn-icon btn-xs btn-ghost-secondary" href="/account">
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

<#macro adminMenuItems items=[] user={}>
    <#list items as item>
        <#if user.id?? && user.hasOneOfRoles(item.roles)>
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
        </#if>
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
                                <@spring.messageArgsText code="menu.${item.name}" args=item.argumentValues text=item.displayName />
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
                                                <@spring.messageArgsText code="menu.${subitem.name}" args=subitem.argumentValues text=subitem.displayName />
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
                                                        <@spring.messageArgsText code="menu.${leafItem.name}" args=leafItem.argumentValues text=leafItem.displayName />
                                                    </a>
                                                </#list>
                                            </div>
                                        </div>
                                    <#else>
                                        <a class="dropdown-item ${classes}" href="${subitem.destinationUrl}">
                                            <@spring.messageArgsText code="menu.${subitem.name}" args=subitem.argumentValues text=subitem.displayName />
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
                                <@spring.messageArgsText code="menu.${item.name}" args=item.argumentValues text=item.displayName />
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

<#macro membershipCategories categories _csrf subscriptionId>
    <#list categories as category>
        <@section title="membership.${category.key}">
            <div class="w-lg-80 mx-lg-auto position-relative">
                <@categoryPricelist category=category _csrf=_csrf subscriptionId=subscriptionId />
            </div>
            <p>
                <sup>*&nbsp;</sup>&nbsp;<@spring.message code="membership.age-footnote" />
            </p>
        </@section>
    </#list>
</#macro>

<#macro categoryPricelist category _csrf subscriptionId>
    <div class="row position-relative z-index-2 mx-n2 mb-5">
        <#list category.priceListItem as item>
            <div class="col-md-5 col-sm-4">
                <div class="px-2 mb-3">
                    <div class="card h-100">
                        <div class="card-header text-center">
                            <span class="d-block h3">
                                ${item.description}
                            </span>
                            <span class="d-block mb-2">
                                <span class="text-dark align-top">&pound;</span>
                                <span class="font-size-4 text-dark font-weight-bold mr-n2">
                                <span id="pricingCount1">
                                    ${item.currentPrice}
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
                                    Ages ${item.minAge}
                                    <#if item.maxAge??>
                                        to ${item.maxAge}
                                    <#else>
                                        and up
                                    </#if>
                                    <sup>*</sup>
                                </div>
                            </div>
                            <#if (item.includesMatchFees)?? && item.includesMatchFees>
                            <div class="media font-size-1 text-body mb-3">
                                <i class="fas fa-check-circle text-success mt-1 mr-2"></i>
                                <div class="media-body">
                                    Includes Match Fees
                                </div>
                            </div>
                            </#if>
                        </div>
                        <!-- End Body -->

                        <div class="card-footer border-0">
                            <form class="form-horizontal" method="post" name="subscription" action="/register/select-membership">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                <input type="hidden" name="category" value="${category.key}" />
                                <input type="hidden" name="uuid" value="${subscriptionId}" />
                                <button
                                        type="submit"
                                        class="btn btn-soft-primary btn-block transition-3d-hover"
                                        value="${item.id}"
                                        name="priceListItemId"
                                        id="priceListItemId-${item.id}">
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

<#macro memberField attribute subscription data localeCategory="membership">
    <#if attribute.mandatory>
        <#assign required>required="required"</#assign>
    <#else>
        <#assign required></#assign>
    </#if>
    <div class="row form-group">
        <label class="col-md-4 control-label"><@spring.messageText code="${localeCategory}.${attribute.definition.key}" text="${localeCategory}.${attribute.definition.key}" /></label>
        <div class="col-md-6">
            <#switch attribute.definition.type.toString()>
                <#case "String">
                    <@memberInputField type="text"
                        required=required
                        subscription=subscription
                        key=attribute.definition.key
                        localeCategory=localeCategory
                        data=data />
                    <#break>
                <#case "Number">
                    <@memberInputField type="number"
                        required=required
                        subscription=subscription
                        key=attribute.definition.key
                        localeCategory=localeCategory
                        data=data />
                    <#break>
                <#case "Email">
                    <@memberInputField type="email"
                        required=required
                        subscription=subscription
                        key=attribute.definition.key
                        localeCategory=localeCategory
                        data=data />
                    <#break>
                <#case "Date">
                    <@memberInputField type="date"
                        required=required
                        subscription=subscription
                        key=attribute.definition.key
                        localeCategory=localeCategory
                        data=data />
                    <#break>
                <#case "Boolean">
                    <@memberSwitchField
                        subscription=subscription
                        key=attribute.definition.key
                        localeCategory=localeCategory
                        data=data />
                    <#break>
                <#case "Option">
                    <#list attribute.definition.choices as choice>
                        <div class="radio">
                            <label>
                                <input
                                        type="radio"
                                        name="${attribute.definition.key}"
                                        id="member-${attribute.definition.key}"
                                        value="${choice}"
                                        <#if data?keys?seq_contains(attribute.definition.key)
                                            && choice == data[attribute.definition.key]?first>
                                            checked="checked"
                                        </#if>
                                        ${required} />
                                &nbsp;&nbsp;<@spring.messageText code="${localeCategory}.${choice}" text="${choice}"/>
                            </label>
                        </div>
                    </#list>
                    <#break>
                <#case "List">
                    <#list attribute.definition.choices as choice>
                        <div class="checkbox">
                            <label>
                                <input
                                        type="checkbox"
                                        name="${attribute.definition.key}"
                                        id="member-${attribute.definition.key}"
                                        value="${choice}"
                                        <#if data?keys?seq_contains(attribute.definition.key)
                                        && data[attribute.definition.key]?seq_contains(choice)>
                                            checked="checked"
                                        </#if>
                                        <#if (required?has_content || (choice)?starts_with("ACCEPT-"))>
                                            required="required"
                                        </#if>
                                />
                                &nbsp;&nbsp;<@spring.messageText code="${localeCategory}.${choice}" text="${choice}"/>
                            </label>
                        </div>
                    </#list>
                    <#break>
            </#switch>
            <span class="help-block"><@spring.messageText code="${localeCategory}.${attribute.definition.key}-help" text="" /></span>
        </div>
    </div>
</#macro>

<#macro memberSwitchField subscription key localeCategory data>
    <label class="toggle-switch d-flex align-items-center mb-3" for="${key}">
        <input
                type="checkbox"
                class="toggle-switch-input"
                id="${key}"
                <#if (data[key]?first)!false>
                    checked="checked"
                </#if> />
        <span class="toggle-switch-label">
            <span class="toggle-switch-indicator"></span>
        </span>
        <span class="toggle-switch-content">
            <span class="d-block">
            &nbsp;&nbsp;<@spring.messageText
                code="${localeCategory}.${key}-switch"
                text="${localeCategory}.${key}-switch" />
            </span>
        </span>
    </label>
</#macro>

<#macro memberInputField type required subscription key localeCategory data>
    <input class="form-control  c-square c-theme"
           name="${key}"
           type="${type}"
           placeholder="<@spring.messageText code="${localeCategory}.${key}-placeholder" text="" />"
           value="${(data[key]?first)!""}"
            ${required} />
</#macro>

<#macro teamList activeTeam season teams=[]>
    <div class="mb-7">
        <ul class="list-group">
            <#list teams as team>
                <a
                        class="list-group-item <#if team.id == activeTeam.id>active</#if>"
                        href="/fixtures/${season?c}/${team.id?c}">
                    ${team.name}
                </a>
            </#list>
        </ul>
    </div>
</#macro>

<#macro inningsScoreCard fixture innings>
    <#if fixture.inningsBattingPresent(innings)>
        <table class="table table-align-middle">
        <thead class="thead-light">
            <tr>
                <th scope="col">#</th>
                <th scope="col">
                    <@spring.message code="fixtures.batsman" />
                </th>
                <th scope="col">
                    <@spring.message code="fixtures.runs" />
                </th>
                <th scope="col">
                    <@spring.message code="fixtures.howout" />
                </th>
                <th scope="col">
                    <@spring.message code="fixtures.bowler" />
                </th>
                <th scope="col">
                    <@spring.message code="fixtures.fielder" />
                </th>
                <th scope="col">
                    <@spring.message code="fixtures.fours" />
                </th>
                <th scope="col">
                    <@spring.message code="fixtures.sixes" />
                </th>
            </tr>
        </thead>
        <tbody>
        <#assign x=fixture.getNumberOfBats(innings)>
        <#assign runsWithBat=0>
        <#list 1..x as i>
            <tr>
                <th scope="row">${i}</th>
                <td>${fixture.getBatsmanName(innings, i)}</td>
                <td class="text-right">
                    <#assign runs=fixture.getBatsmanRuns(innings, i)>
                    <#assign runsWithBat=runsWithBat + runs>
                    ${runs}
                </td>
                <td>${fixture.getBatsmanHowOut(innings, i)!""}</td>
                <td>${fixture.getBatsmanWicketBowler(innings, i)!""}</td>
                <td>${fixture.getBatsmanWicketFielder(innings, i)!""}</td>
                <td class="text-right">${fixture.getBatsmanFours(innings, i)}</td>
                <td class="text-right">${fixture.getBatsmanSixes(innings, i)}</td>
            </tr>
        </#list>
        </tbody>
        <tfoot class="thead-light">
            <tr>
                <th scope="col">&nbsp;</th>
                <th scope="col" class="text-right">
                    <@spring.message code="fixtures.total" />
                </th>
                <th scope="col" class="text-right">${runsWithBat}</th>
                <th scope="col">&nbsp;</th>
                <th scope="col">&nbsp;</th>
                <th scope="col">&nbsp;</th>
                <th scope="col">&nbsp;</th>
                <th scope="col">&nbsp;</th>
            </tr>
            <tr>
                <th scope="col">&nbsp;</th>
                <th scope="col" class="text-right">
                    <@spring.message code="fixtures.extras" />
                </th>
                <th scope="col" class="text-right">${fixture.getInningsExtras(innings)}</th>
                <th scope="col">&nbsp;</th>
                <th scope="col">&nbsp;</th>
                <th scope="col">&nbsp;</th>
                <th scope="col">&nbsp;</th>
                <th scope="col">&nbsp;</th>
            </tr>
            <tr>
                <th scope="col">&nbsp;</th>
                <th scope="col" class="text-right">
                    <@spring.message code="fixtures.innings_total" />
                </th>
                <th scope="col" class="text-right">${fixture.getInningsRuns(innings)}</th>
                <th scope="col">&nbsp;</th>
                <th scope="col">&nbsp;</th>
                <th scope="col">&nbsp;</th>
                <th scope="col">&nbsp;</th>
                <th scope="col">&nbsp;</th>
            </tr>
        </tfoot>
    </table>
    <#else>
        <h6>Innings not recorded on PlayCricket</h6>
    </#if>
</#macro>

<#macro inningsBowlingSummary fixture innings>
    <#if fixture.inningsBowlingPresent(innings)>
        <table class="table table-align-middle">
        <thead class="thead-light">
        <tr>
            <th scope="col">#</th>
            <th scope="col">
                <@spring.message code="fixtures.bowler" />
            </th>
            <th scope="col">
                <@spring.message code="fixtures.overs" />
            </th>
            <th scope="col">
                <@spring.message code="fixtures.wickets" />
            </th>
            <th scope="col">
                <@spring.message code="fixtures.maidens" />
            </th>
            <th scope="col">
                <@spring.message code="fixtures.runs" />
            </th>
            <th scope="col">
                <@spring.message code="fixtures.noballs" />
            </th>
            <th scope="col">
                <@spring.message code="fixtures.wides" />
            </th>
        </tr>
        </thead>
        <tbody>
        <#assign x=fixture.getNumberOfBowlers(innings)>
        <#assign totalOvers=0>
        <#assign totalWickets=0>
        <#assign totalMaidens=0>
        <#assign totalRuns=0>
        <#assign totalWides=0>
        <#assign totalNoBalls=0>
        <#list 1..x as i>
            <tr>
                <th scope="row">${i}</th>
                <td>${fixture.getBowlerName(innings, i)}</td>
                <td class="text-right">
                    <#assign overs=fixture.getBowlerOvers(innings, i)>
                    <#assign totalOvers=totalOvers + overs>
                    ${overs}
                </td>
                <td class="text-right">
                    <#assign wickets=fixture.getBowlerWickets(innings, i)>
                    <#assign totalWickets=totalWickets + wickets>
                    ${wickets}
                </td>
                <td class="text-right">
                    <#assign maidens=fixture.getBowlerMaidens(innings, i)>
                    <#assign totalMaidens=totalMaidens + maidens>
                    ${maidens}
                </td>
                <td class="text-right">
                    <#assign runs=fixture.getBowlerRuns(innings, i)>
                    <#assign totalRuns=totalRuns + runs>
                    ${runs}
                </td>
                <td class="text-right">
                    <#assign noBalls=fixture.getBowlerNoBalls(innings, i)>
                    <#assign totalNoBalls=totalNoBalls + noBalls>
                    ${noBalls}
                </td>
                <td class="text-right">
                    <#assign wides=fixture.getBowlerWides(innings, i)>
                    <#assign totalWides=totalWides + wides>
                    ${wides}
                </td>
            </tr>
        </#list>
        </tbody>
        <tfoot class="thead-light">
        <tr>
            <th scope="col">&nbsp;</th>
            <th scope="col" class="text-right">
                <@spring.message code="fixtures.total" />
            </th>
            <th scope="col" class="text-right">${totalOvers}</th>
            <th scope="col" class="text-right">${totalWickets}</th>
            <th scope="col" class="text-right">${totalMaidens}</th>
            <th scope="col" class="text-right">${totalRuns}</th>
            <th scope="col" class="text-right">${totalNoBalls}</th>
            <th scope="col" class="text-right">${totalWides}</th>
        </tr>
        <tr>
            <th scope="col">&nbsp;</th>
            <th scope="col">&nbsp;</th>
            <th scope="col">&nbsp;</th>
            <th scope="col">&nbsp;</th>
            <th scope="col" class="text-right">
                <@spring.message code="fixtures.byes" />
            </th>
            <th scope="col" class="text-right">${fixture.getInningsByes(innings)}</th>
            <th scope="col">&nbsp;</th>
            <th scope="col">&nbsp;</th>
        </tr>
        <tr>
            <th scope="col">&nbsp;</th>
            <th scope="col">&nbsp;</th>
            <th scope="col">&nbsp;</th>
            <th scope="col">&nbsp;</th>
            <th scope="col" class="text-right">
                <@spring.message code="fixtures.leg_byes" />
            </th>
            <th scope="col" class="text-right">${fixture.getInningsLegByes(innings)}</th>
            <th scope="col">&nbsp;</th>
            <th scope="col">&nbsp;</th>
        </tr>
        </tfoot>
    </table>
    </#if>
</#macro>

<#macro otpScript fieldPrefix="code">
    <script>
        let input1 = document.getElementById('${fieldPrefix}1'),
            inputs = document.querySelectorAll('input.form-control-otp'),
            splitNumber = function(e) {
                let data = e.data || e.target.value;
                if(!data) return;
                if(data.length === 1) return;
                console.log(e);
                populateNext($(e.target), data);
            },
            populateNext = function(el, data) {
                let next = $(el).parents("div.otp-digit-container").next().find("input.form-control-otp");

                el.val(data[0]);
                data = data.substring(1);

                if ( next && data.length ) {
                    next.focus();
                    populateNext(next, data);
                }
            };

        inputs.forEach(function(input) {
            input.addEventListener('keyup', function(e){
                let previous = $(e.target).parents("div.otp-digit-container").prev().find("input.form-control-otp");
                let next = $(e.target).parents("div.otp-digit-container").next().find("input.form-control-otp");
                // Break if Shift, Tab, CMD, Option, Control.
                if (e.keyCode === 16 || e.keyCode === 9 || e.keyCode === 224 || e.keyCode === 18 || e.keyCode === 17) {
                    return;
                }

                // On Backspace or left arrow, go to the previous field.
                if ((e.keyCode === 8 || e.keyCode === 37) && previous) {
                    previous.select();
                } else if (e.keyCode !== 8 && next) {
                    next.select();
                }
            });

            input.addEventListener('focus', function(e) {
                if (this === input1) return;
                let previous = $(e.target).parents("div.otp-digit-container").prev().find("input.form-control-otp");

                if (input1.value === '') {
                    input1.focus();
                }

                if (previous && previous.value === '') {
                    previous.focus();
                }
            });
        });

        input1.addEventListener('input', splitNumber);
        input1.focus();
    </script>
</#macro>

<#macro otpCode fieldPrefix="code" noOfDigits=6>
    <fieldset>
        <div class="row mb-6">
            <#list 1..noOfDigits as i>
                <div class="col-md-2 col-4 otp-digit-container">
                    <!-- Form Group -->
                    <div class="form-group">
                        <input
                                type="number"
                                class="form-control form-control-single-number form-control-otp"
                                name="${fieldPrefix}${i}"
                                id="${fieldPrefix}${i}"
                                required="required"
                                placeholder=""
                                aria-label=""
                                min="0"
                                max="9"
                                maxlength="1"
                                pattern="[0-9]+"
                                inputmode="numeric"
                                autocomplete="off"
                                autocapitalize="off"
                                spellcheck="false">
                    </div>
                    <!-- End Form Group -->
                </div>
            </#list>
        </div>
    </fieldset>
</#macro>

<#macro formMessages errors errorKey="An error occured" class="alert-soft-danger">
    <#if (errors?size > 0)>
        <div class="alert ${class}" role="alert">
            <h5 class="alert-heading">
                <@spring.messageText code=errorKey text=errorKey />
            </h5>

            <#list errors as error>
                <p class="text-inherit">
                    <@spring.messageText code=error text=error />
                </p>
            </#list>
        </div>
    </#if>
</#macro>

<#macro contactCard contact additionalClasses="">
    <div class="card card-bordered ${additionalClasses}">
        <div class="card-header">
            <!-- Avatar -->
            <div class="avatar avatar-xl avatar-soft-dark avatar-circle avatar-border-lg avatar-centered mb-3">
                <span class="avatar-initials"><#list contact.name?split(" ")[0..1] as n>${n?cap_first[0]}</#list></span>
                <span class="avatar-status avatar-sm-status"></span>
            </div>
            <!-- End Avatar -->
        </div>
        <div class="card-body">
            <h3 class="align-content-center text-center">
                ${contact.name}
            </h3>
            <h5 class="align-content-center text-center">
                ${contact.position}
            </h5>

            <ul class="list-group list-group-flush">
                <#list contact.methods as method>
                    <#if method.value?has_content>
                        <li class="list-group-item">
                            <#switch method.key>
                                <#case "WHATSAPP">
                                    <i class="fa fa-whatsapp-square">&nbsp;</i>
                                    <#break>
                                <#case "EMAIL">
                                    <i class="fa fa-envelope">&nbsp;</i>
                                    <@obfuscatedEmailLink email=method.value />
                                    <#break>
                                <#case "PHONE">
                                    <i class="fa fa-phone">&nbsp;</i>
                                    <@obfuscatePhone phone=method.value />
                                    <#break>
                            </#switch>
                        </li>
                    </#if>
                </#list>
            </ul>
        </div>
    </div>
</#macro>

<#macro obfuscatedEmailLink email>
    <#assign localPart = email?split("@")[0]>
    <#assign domainPart = email?split("@")[1]>
    <a href="javascript:startMail('${localPart}','${domainPart}');">
        ${localPart?replace(".", "&#46;")}<!-- >@. -->@<!-- >@. -->${domainPart?replace(".", "&#46;")}
    </a>
</#macro>

<#macro obfuscatePhone phone>
    <#if phone?has_content>
        <#assign number = parsePhoneNumber(phone)>
        <a href="javascript:startCall('${number.countryCode}','${number.areaCode}','${number.localNumber}');">
            <#list 0..<number.formatted?length as n>${number.formatted[n]}<span style="display: none;">${.now?long?c}</span></#list>
        </a>
    </#if>
</#macro>

<#macro avatar givenName="?" familyName=" ">
    <div class="avatar avatar-xxl avatar-soft-dark avatar-circle avatar-border-lg avatar-centered mb-3">
        <span class="avatar-initials">${givenName?cap_first[0]}${familyName?cap_first[0]}</span>
    </div>
</#macro>
