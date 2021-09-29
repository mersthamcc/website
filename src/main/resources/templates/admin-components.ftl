<#import "/spring.ftl" as spring />

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

<#macro adminTableCard id title selectable=false searchable=true searchPrompt="search" idField="id" cardClass="mb-3 mb-lg-5" data=[] columns=[] defaultPageLength=50 pageLengths=[10, 50, 100]>
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
                        <th class="table-column-pr-0 sorting_disabled">Actions</th>
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
                                           id="${id}Check' + row.id + '" \
                                           name="selected" \
                                           value="' + row.id + '"> \
                                    <label class="custom-control-label" for="${id}Check' + row.id + '"></label> \
                                </div> \
                            </td>';
                } },
            </#if>
            <#list columns as column>
                { "data": "${column.fieldName}" },
            </#list>
            { "data": function(row, type, set, meta) {
                let actions = "";
                if ( row.editLink ) actions = actions + '<a class="js-edit btn btn-sm btn-white" href="' + row.editLink +'"><i class="js-edit-icon tio-edit"></i> Edit</a>';
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