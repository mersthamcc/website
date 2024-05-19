<#import "/spring.ftl" as spring />

<#macro noButtons>
</#macro>

<#macro section title action footer="" headerRight="" footerClasses="d-flex justify-content-end">
    <form class="form-horizontal" method="post" name="action" action="${action}">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <!-- Card -->
        <div class="card mb-3 mb-lg-5">
            <#if title!="">
                <div class="card-header">
                    <h5 class="card-title"><@spring.messageText code="${title}" text="${title}" /></h5>
                    <#if headerRight?is_directive>
                        <@headerRight />
                    <#else>
                        ${headerRight}
                    </#if>
                </div>
            </#if>

            <!-- Body -->
            <div class="card-body">
                <#nested />
            </div>
            <!-- End Body -->
            <#if footer?is_directive>
                <div class="card-footer ${footerClasses}">
                    <@footer />
                </div>
            </#if>
        </div>
        <!-- End Card -->
    </form>
</#macro>

<#macro form action>
    <form class="form-horizontal" method="post" name="action" action="${action}">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <#nested />
    </form>
</#macro>

<#macro card title buttons="">
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
        <#if buttons?is_directive>
            <div class="card-footer d-flex justify-content-end">
                <@buttons />
            </div>
        </#if>
    </div>
    <!-- End Card -->
</#macro>

<#macro memberAdminField attribute data localeCategory="membership">
    <#if attribute.mandatory>
        <#assign required>required="required"</#assign>
    <#else>
        <#assign required></#assign>
    </#if>
    <div class="row form-group">
        <label class="col-md-4 control-label"><@spring.message code="${localeCategory}.${attribute.definition.key}" /></label>
        <div class="col-md-6">
            <#switch attribute.definition.type.toString()>
                <#case "String">
                    <@memberAdminInputField type="text"
                    required=required
                    data=data
                    key=attribute.definition.key
                    localeCategory=localeCategory />
                    <#break>
                <#case "Number">
                    <@memberAdminInputField type="number"
                    required=required
                    data=data
                    key=attribute.definition.key
                    localeCategory=localeCategory />
                    <#break>
                <#case "Email">
                    <@memberAdminInputField type="email"
                    required=required
                    data=data
                    key=attribute.definition.key
                    localeCategory=localeCategory />
                    <#break>
                <#case "Date">
                    <@memberAdminDateField
                    required=required
                    data=data
                    key=attribute.definition.key
                    localeCategory=localeCategory />
                    <#break>
                <#case "Option">
                    <#list attribute.definition.choices as choice>
                        <div class="radio">
                            <label for="data-${attribute.definition.key}-${choice}">
                                <input
                                        type="radio"
                                        name="${attribute.definition.key}"
                                        id="data-${attribute.definition.key}-${choice}"
                                        value="${choice}"
                                        <#if data?keys?seq_contains(attribute.definition.key)
                                        && choice == data[attribute.definition.key]>
                                            checked="checked"
                                        </#if>
                                        ${required} />
                                &nbsp;&nbsp;<@spring.messageText code="${localeCategory}.${choice}" text="${choice}"/>
                            </label>
                        </div>
                    </#list>
                    <#break>
                <#case "List">
                    <input
                            type="hidden"
                            name="${attribute.definition.key}"
                            value="nothing-checked" />
                    <#list attribute.definition.choices as choice>
                        <div class="checkbox">
                            <label for="data-${attribute.definition.key}-${choice}">
                                <input
                                        type="checkbox"
                                        name="${attribute.definition.key}"
                                        id="data-${attribute.definition.key}-${choice}"
                                        value="${choice}"
                                        <#if data?keys?seq_contains(attribute.definition.key)
                                        && data[attribute.definition.key]?seq_contains(choice)>
                                            checked="checked"
                                        </#if>
                                        ${required} />
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

<#macro memberAdminInputField type required data key localeCategory>
    <input class="form-control c-square c-theme"
           name="${key}"
           type="${type}"
           placeholder="<@spring.messageText code="${localeCategory}.${key}-placeholder" text="" />"
           value="${data[key]!""}"
            ${required}
    />
</#macro>

<#macro memberAdminDateField required data key localeCategory>
    <input class="form-control c-square c-theme"
           name="${key}"
           type="date"
           placeholder="<@spring.messageText code="${localeCategory}.${key}-placeholder" text="" />"
           value="${(data[key]).format('yyyy-MM-dd')!""}"
            ${required}
    />
</#macro>

<#macro adminFormField name localeCategory data required=false type="text" additionalClasses="">
    <#if required>
        <#assign requiredAttribute>required="required"</#assign>
    <#else>
        <#assign requiredAttribute></#assign>
    </#if>
    <div class="row form-group">
        <label class="col-md-2 control-label text-right align-middle">
            <@spring.message code="${localeCategory}.${name}" />
        </label>
        <div class="col-md-10">
            <input class="form-control c-square c-theme ${additionalClasses}"
                   name="${name}"
                   id="item-${name}"
                   type="${type}"
                   placeholder="<@spring.messageText code="${localeCategory}.${name}-placeholder" text="" />"
                   value="${data}"
                   ${requiredAttribute} />
            <#nested />
            <span class="help-block"><@spring.messageText code="${localeCategory}.${name}-help" text="" /></span>
        </div>
    </div>
</#macro>

<#macro adminSelectField name localeCategory data options required=false additionalClasses="">
    <#if required>
        <#assign requiredAttribute>required="required"</#assign>
    <#else>
        <#assign requiredAttribute></#assign>
    </#if>
    <div class="row form-group">
        <label class="col-md-2 control-label text-right align-middle">
            <@spring.message code="${localeCategory}.${name}" />
        </label>
        <div class="col-md-10">
            <select class="form-control c-square c-theme ${additionalClasses}"
                   name="${name}"
                   value="${data}"
                    ${requiredAttribute}>
                <#list options?keys as option>
                    <option value="${option}" <#if option==data>selected="selected"</#if>>
                        <@spring.messageText code=options[option] text=options[option] />
                    </option>
                </#list>
            </select>
        </div>
    </div>
</#macro>

<#macro adminDateField name localeCategory data>
    <div class="row form-group">
        <label for="${name}Label" class="col-md-2 control-label text-right align-middle">
            <@spring.message code="${localeCategory}.${name}" />
        </label>

        <div class="col-md-10">
            <div id="${name}Flatpickr"
                 class="js-flatpickr flatpickr-custom input-group input-group-merge"
                 data-hs-flatpickr-options='{
                    "appendTo": "#${name}Flatpickr",
                    "dateFormat": "d/m/Y",
                    "wrap": true
                  }'>
                <div class="input-group-prepend" data-toggle>
                    <div class="input-group-text">
                        <i class="tio-date-range"></i>
                    </div>
                </div>

                <input
                        type="text"
                        name="${name}"
                        class="flatpickr-custom-form-control form-control"
                        id="${name}Label"
                        placeholder="<@spring.messageText code="${localeCategory}.${name}-placeholder" text="" />"
                        data-input
                        <#if data>value=${data.format("dd/MM/yyyy")}</#if> />
            </div>
        </div>
    </div>
</#macro>

<#macro adminDateTimeField name localeCategory data>
    <div class="row form-group">
        <label for="${name}Label" class="col-md-2 control-label text-right align-middle">
            <@spring.message code="${localeCategory}.${name}" />
        </label>

        <div class="col-md-10">
            <div id="${name}Flatpickr"
                 class="js-flatpickr flatpickr-custom input-group input-group-merge"
                 data-hs-flatpickr-options='{
                    "appendTo": "#${name}Flatpickr",
                    "enableTime": true,
                    "dateFormat": "Z",
                    "altInput": true,
                    "altFormat": "d/m/Y h:i K",
                    "wrap": true
                  }'>
                <div class="input-group-prepend" data-toggle>
                    <div class="input-group-text">
                        <i class="tio-date-range"></i>
                    </div>
                </div>

                <input
                        type="text"
                        name="${name}"
                        class="flatpickr-custom-form-control form-control"
                        id="${name}Label"
                        placeholder="<@spring.messageText code="${localeCategory}.${name}-placeholder" text="" />"
                        data-input
                        value=${data.format()} />
            </div>
        </div>
    </div>
</#macro>

<#macro adminFormDisplayField name localeCategory data labelWidth=2>
    <div class="row form-group">
        <label class="col-md-${labelWidth} control-label text-right align-middle">
            <@spring.message code="${localeCategory}.${name}" />
        </label>
        <label class="col-md-${12 - labelWidth} control-label align-middle">
            ${data}
        </label>
    </div>
</#macro>

<#macro adminSwitchField name localeCategory checked=false>
    <div class="row form-group">
        <label class="col-md-2 control-label text-right align-middle" for="${name}">
            <@spring.messageText code="${localeCategory}.${name}" text=name />
        </label>
        <div class="col-md-10">
            <div class="d-flex mb-5">
                <label class="toggle-switch mx-2" for="${name}">
                    <input type="checkbox" class="js-toggle-switch toggle-switch-input"
                           id="${name}"
                           name="${name}"
                           data-hs-toggle-switch-options='{}'
                           <#if checked>checked="checked"</#if>
                    />
                    <span class="toggle-switch-label">
                    <span class="toggle-switch-indicator"></span>
                </span>
                </label>
            </div>
        </div>
    </div>
</#macro>

<#macro adminCkEditorField name localeCategory data required=false type="text" rows=25>
    <#if required>
        <#assign requiredAttribute>required="required"</#assign>
    <#else>
        <#assign requiredAttribute></#assign>
    </#if>
    <div class="row form-group">
        <label class="col-md-2 control-label text-right">
            <@spring.message code="${localeCategory}.${name}" />
        </label>
        <div class="col-md-10">
            <textarea name="${name}" id="${name}-editor" class="form-control c-square c-theme" size="${rows}" ${requiredAttribute}>
                ${data}
            </textarea>
        </div>
    </div>
</#macro>

<#macro adminTableCard id title selectable=false searchable=true searchPrompt="search" idField="id" cardClass="mb-3 mb-lg-5" data=[] columns=[] defaultPageLength=50 pageLengths=[10, 50, 100] rightHeader="">
    <div class="card ${cardClass}">
        <!-- Header -->
        <div class="card-header">
            <h5 class="card-header-title">
                <@spring.messageText code=title text=title />
            </h5>

            <#if rightHeader?is_directive>
                <@rightHeader />
            <#else>
                ${rightHeader}
            </#if>

            <#if selectable>
                <!-- Datatable Info -->
                <div id="${id}WithCheckboxSelectCounterInfo" class="mr-2" style="display: none;">
                    <div class="d-flex align-items-center">
                        <span class="font-size-sm mr-3">
                          <span id="${id}WithCheckboxSelectCounter">0</span>
                          Selected
                        </span>
                    </div>
                </div>
                <!-- End Datatable Info -->
            </#if>
        </div>
        <!-- End Header -->
        <div class="card-body">
            <#if searchable>
                <div class="row justify-content-between align-items-center flex-grow-1">
                    <div class="col-10 col-md">
                        <form>
                            <!-- Search -->
                            <div class="input-group input-group-merge input-group-flush">
                                <div class="input-group-prepend">
                                    <div class="input-group-text">
                                        <i class="tio-search"></i>
                                    </div>
                                </div>
                                <input id="${id}Search" type="search" class="form-control" placeholder="<@spring.messageText code=searchPrompt text=searchPrompt />..." aria-label="<@spring.messageText code=searchPrompt text=searchPrompt />">
                            </div>
                            <!-- End Search -->
                        </form>
                    </div>
                    <div class="col-auto">
                    </div>
                </div>
            </#if>
            <div class="table-responsive datatable-custom">
                <table id="${id}"
                       class="table table-lg table-borderless table-thead-bordered table-nowrap table-align-middle card-table dataTable no-footer"
                       data-hs-datatables-options='{
                            <#if selectable>
                            "columnDefs": [{
                                "targets": [0],
                                "orderable": false
                            }],
                            "order": [],
                            </#if>
                            "info": {
                                "totalQty": "#${id}WithPaginationInfoTotalQty"
                            },
                            <#if searchable>
                            "search": "#${id}Search",
                            </#if>
                            "entries": "#${id}Entries",
                            "pageLength": ${defaultPageLength},
                            "isResponsive": true,
                            "isShowPaging": false,
                            "pagination": "${id}EntriesPagination"
                            }' role="grid" aria-describedby="${id}_info">
                    <thead class="thead-light">
                        <tr role="row">
                            <#if selectable>
                                <th class="table-column-pr-0 sorting_disabled">
                                    <div class="custom-control custom-checkbox">
                                        <input id="${id}WithCheckboxSelectAll"
                                               type="checkbox"
                                               class="custom-control-input">
                                        <label class="custom-control-label" for="${id}WithCheckboxSelectAll"></label>
                                    </div>
                                </th>
                            </#if>
                            <#list columns as column>
                                <th class="table-column-pl-0 sorting" tabindex="0" aria-controls="${id}" rowspan="1"
                                    colspan="1" aria-label="<@spring.messageText code=column.key text=column.key />: activate to sort column ascending">
                                    <@spring.messageText code=column.key text=column.key />
                                </th>
                            </#list>
                        </tr>
                    </thead>
                    <tbody>
                        <#list data as row>
                            <tr role="row" class="<#if row?is_odd_item>odd<#else>even</#if>">
                                <#if selectable>
                                    <td class="table-column-pr-0">
                                        <div class="custom-control custom-checkbox">
                                            <input type="checkbox"
                                                   class="custom-control-input"
                                                   id="${id}Check${row?counter}"
                                                   name="selected"
                                                   value="${row[idField].value}">
                                            <label class="custom-control-label" for="${id}Check${row?counter}"></label>
                                        </div>
                                    </td>
                                </#if>
                                <#list columns as column>
                                    <td class="table-column-pl-0">
                                        <#if row[column.key].link??>
                                            <a class="d-flex align-items-center" href="${row[column.key].link}">
                                                <div class="avatar avatar-circle">
                                                </div>
                                                <div class="ml-3">
                                                    <span class="h5 text-hover-primary">${row[column.key].value}</span>
                                                </div>
                                            </a>
                                        <#else>
                                            ${row[column.key].value}
                                        </#if>
                                    </td>
                                </#list>
                            </tr>
                        </#list>
                    </tbody>
                </table>
            </div>
        </div>
        <!-- Footer -->
        <div class="card-footer">
            <#if data?size gt pageLengths?min>
                <!-- Pagination -->
                <div class="row justify-content-center justify-content-sm-between align-items-sm-center">
                <div class="col-sm mb-2 mb-sm-0">
                    <div class="d-flex justify-content-center justify-content-sm-start align-items-center">
                        <span class="mr-2">Showing:</span>

                        <!-- Select -->
                        <select id="${id}Entries" class="js-select2-custom"
                                data-hs-select2-options='{
                                    "minimumResultsForSearch": "Infinity",
                                    "customClass": "custom-select custom-select-sm custom-select-borderless",
                                    "dropdownAutoWidth": true,
                                    "width": true
                                  }'>
                            <#list pageLengths as pageLength>
                                <option value="${pageLength}" <#if pageLength==defaultPageLength>selected</#if>>${pageLength}</option>
                            </#list>
                        </select>
                        <!-- End Select -->

                        <span class="text-secondary mr-2">of</span>

                        <!-- Pagination Quantity -->
                        <span id="${id}WithPaginationInfoTotalQty"></span>
                    </div>
                </div>

                <div class="col-sm-auto">
                    <div class="d-flex justify-content-center justify-content-sm-end">
                        <nav id="${id}EntriesPagination" aria-label="<@spring.messageText code=title text=title /> pagination"></nav>
                    </div>
                </div>
            </div>
                <!-- End Pagination -->
            </#if>
        </div>
        <!-- End Footer -->
    </div>
</#macro>

<#macro adminTableScript id>
    var ${id} = $.HSCore.components.HSDatatables.init($('#${id}'), {
        select: {
            style: 'multi',
            selector: 'td:first-child input[type="checkbox"]',
            classMap: {
                checkAll: '#${id}WithCheckboxSelectAll',
                counter: '#${id}WithCheckboxSelectCounter',
                counterInfo: '#${id}WithCheckboxSelectCounterInfo'
            }
        }
    });

    $('#${id}Search').on('mouseup', function (e) {
        var $input = $(this),
        oldValue = $input.val();

        if (oldValue == "") return;

        setTimeout(function(){
            var newValue = $input.val();

            if (newValue == ""){
                ${id}.search('').draw();
            }
        }, 1);
    });
</#macro>

<#macro adminSspTableCard id title selectable=false searchable=true searchPrompt="search" idField="id" cardClass="mb-3 mb-lg-5" columns=[] defaultPageLength=50 pageLengths=[10, 50, 100]>
    <div class="card ${cardClass}">
        <!-- Header -->
        <div class="card-header">
            <h5 class="card-header-title">
                <@spring.messageText code=title text=title />
            </h5>

            <#if selectable>
                <!-- Datatable Info -->
                <div id="${id}WithCheckboxSelectCounterInfo" class="mr-2" style="display: none;">
                    <div class="d-flex align-items-center">
                        <span class="font-size-sm mr-3">
                          <span id="${id}WithCheckboxSelectCounter">0</span>
                          Selected
                        </span>
                    </div>
                </div>
                <!-- End Datatable Info -->
            </#if>
        </div>
        <!-- End Header -->
        <div class="card-body">
            <#if searchable>
                <div class="row justify-content-between align-items-center flex-grow-1">
                    <div class="col-10 col-md">
                        <form onsubmit="return false;">
                            <!-- Search -->
                            <div class="input-group input-group-merge input-group-flush">
                                <div class="input-group-prepend">
                                    <div class="input-group-text">
                                        <i class="tio-search"></i>
                                    </div>
                                </div>
                                <input
                                        id="${id}Search"
                                        type="search"
                                        class="form-control"
                                        placeholder="<@spring.messageText code=searchPrompt text=searchPrompt />..."
                                        aria-label="<@spring.messageText code=searchPrompt text=searchPrompt />">
                            </div>
                            <!-- End Search -->
                        </form>
                    </div>
                    <div class="col-auto">
                    </div>
                </div>
            </#if>
            <div class="table-responsive datatable-custom">
                <table id="${id}"
                       class="table table-lg table-borderless table-thead-bordered table-nowrap table-align-middle card-table dataTable no-footer"
                       data-hs-datatables-options='{
                            "columnDefs": [
                            <#if selectable>
                                {
                                    "targets": [0],
                                    "orderable": false
                                },
                            </#if>
                                {
                                    "targets": [${columns?size + (selectable?then(1,0))}],
                                    "orderable": false
                                }
                            ],
                            "order": [],
                            "info": {
                                "totalQty": "#${id}WithPaginationInfoTotalQty"
                            },
                            <#if searchable>
                            "search": "#${id}Search",
                            </#if>
                            "entries": "#${id}Entries",
                            "pageLength": ${defaultPageLength},
                            "isResponsive": true,
                            "isShowPaging": false,
                            "stateSave": true,
                            "pagination": "${id}EntriesPagination"
                            }' role="grid" aria-describedby="${id}_info">
                    <thead class="thead-light">
                    <tr role="row">
                        <#if selectable>
                            <th class="table-column-pr-0 sorting_disabled">
                                <div class="custom-control custom-checkbox">
                                    <input id="${id}WithCheckboxSelectAll"
                                           type="checkbox"
                                           class="custom-control-input">
                                    <label class="custom-control-label" for="${id}WithCheckboxSelectAll"></label>
                                </div>
                            </th>
                        </#if>
                        <#list columns as column>
                            <th class="table-column-pl-0 sorting" tabindex="0" aria-controls="${id}" rowspan="1"
                                colspan="1" aria-label="<@spring.messageText code=column.key text=column.key />: activate to sort column ascending">
                                <@spring.messageText code=column.key text=column.key />
                            </th>
                        </#list>
                        <th>&nbsp;</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
        </div>
        <!-- Footer -->
        <div class="card-footer">
            <!-- Pagination -->
            <div class="row justify-content-center justify-content-sm-between align-items-sm-center">
                <div class="col-sm mb-2 mb-sm-0">
                    <div class="d-flex justify-content-center justify-content-sm-start align-items-center">
                        <span class="mr-2">Showing:</span>

                        <!-- Select -->
                        <select id="${id}Entries" class="js-select2-custom"
                                data-hs-select2-options='{
                                "minimumResultsForSearch": "Infinity",
                                "customClass": "custom-select custom-select-sm custom-select-borderless",
                                "dropdownAutoWidth": true,
                                "width": true
                              }'>
                            <#list pageLengths as pageLength>
                                <option value="${pageLength}" <#if pageLength==defaultPageLength>selected</#if>>${pageLength}</option>
                            </#list>
                        </select>
                        <!-- End Select -->

                        <span class="text-secondary mr-2">of</span>

                        <!-- Pagination Quantity -->
                        <span id="${id}WithPaginationInfoTotalQty"></span>
                    </div>
                </div>

                <div class="col-sm-auto">
                    <div class="d-flex justify-content-center justify-content-sm-end">
                        <nav id="${id}EntriesPagination" aria-label="<@spring.messageText code=title text=title /> pagination"></nav>
                    </div>
                </div>
            </div>
            <!-- End Pagination -->
        </div>
        <!-- End Footer -->
    </div>
</#macro>

<#macro adminSspTableScript id ajaxSrc selectable=false columns=[]>
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    var ${id} = $.HSCore.components.HSDatatables.init($('#${id}'), {
        serverSide: true,
        processing: true,
        ajax: {
            url: "${ajaxSrc}",
            type: "POST",
            "contentType": "application/json",
            data: function (data) {
                return JSON.stringify(data);
            },
            beforeSend: function (request) {
                request.setRequestHeader(header, token);
            }
        },
        stateSaveCallback: function (settings, data) {
            localStorage.setItem(
                'DataTables_' + settings.sInstance,
                JSON.stringify(data)
            );
        },
        stateLoadCallback: function (settings) {
            let state = JSON.parse(localStorage.getItem('DataTables_' + settings.sInstance));
            if (state && state.search) {
                $("#${id}Search").val(state.search.search);
            }
            return state;
        },
        language: {
            processing: "<div class='loading'><p><i class='fa fa-spinner fa-2x fa-spin'></i></p><p><@spring.message code="fetching" /></p></div>"
        },
        columns: [
            <#if selectable>
                { "data": function(row, type, set, meta) {
                    return '<td class="table-column-pr-0"> \
                                <div class="custom-control custom-checkbox"> \
                                    <input type="checkbox" \
                                           class="custom-control-input" \
                                           id="${id}Check' + row.data.id + '" \
                                           name="selected" \
                                           value="' + row.data.id + '"> \
                                    <label class="custom-control-label" for="${id}Check' + row.data.id + '"></label> \
                                </div> \
                            </td>';
                } },
            </#if>
            <#list columns as column>
                <#if column.function>
                    { "data": function(row, type, set, meta) {
                        let content = ${column.functionName}(row, type, set, meta);
                        return `<td>${'$'}{content}</td>`;
                    }},
                <#else>
                    { "data": "data.${column.fieldName}" },
                </#if>
            </#list>
            { "data": function(row, type, set, meta) {
                let actions = "";
                if ( row.editLink ) actions = actions + '<a class="js-edit btn btn-sm btn-white" href="' + row.editLink +'"><i class="js-edit-icon tio-edit"></i> Edit</a>&nbsp;';
                if ( row.deleteLink ) actions = actions + '<a class="js-edit btn btn-sm btn-white" href="' + row.deleteLink +'"><i class="js-edit-icon tio-delete"></i> Delete</a>&nbsp;';
                return actions;
            }}
        ],
        select: {
            style: 'multi',
            selector: 'td:first-child input[type="checkbox"]',
            classMap: {
                checkAll: '#${id}WithCheckboxSelectAll',
                counter: '#${id}WithCheckboxSelectCounter',
                counterInfo: '#${id}WithCheckboxSelectCounterInfo'
            }
        }
    });

    $('#${id}Search').on('mouseup', function (e) {
        var $input = $(this),
        oldValue = $input.val();

        if (oldValue == "") return;

        setTimeout(function(){
            var newValue = $input.val();

            if (newValue == ""){
                ${id}.search('').draw();
            }
        }, 1);
    });
</#macro>

<#macro formErrors errors errorKey>
    <#if (errors?size > 0)>
        <div class="alert alert-soft-danger" role="alert">
            <h5 class="alert-heading"><@spring.messageText code=errorKey text="An error occured" /></h5>
            <hr />
            <#list errors as error>
                <p class="text-inherit"><@spring.messageText code=error text=error /></p>
            </#list>
        </div>
    </#if>
</#macro>
