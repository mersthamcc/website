<#import "../../admin-base.ftl" as layout>
<#macro dataScript>
    <script>
        function onPageLoad() {
            var datatable = $.HSCore.components.HSDatatables.init($('#datatable'), {
                select: {
                    style: 'multi',
                    selector: 'td:first-child input[type="checkbox"]',
                    classMap: {
                        checkAll: '#datatableWithCheckboxSelectAll',
                        counter: '#datatableWithCheckboxSelectCounter',
                        counterInfo: '#datatableWithCheckboxSelectCounterInfo'
                    }
                }
            });
            $('.js-select2-custom').each(function () {
                var select2 = $.HSCore.components.HSSelect2.init($(this));
            });
        };
    </script>
</#macro>
<@layout.mainLayout script=dataScript>
    <!-- Card -->
    <div class="card mb-3 mb-lg-5">
        <!-- Header -->
        <div class="card-header">
            <h5 class="card-header-title">Members</h5>

            <!-- Datatable Info -->
            <div id="datatableWithCheckboxSelectCounterInfo" class="mr-2" style="display: none;">
                <div class="d-flex align-items-center">
                    <span class="font-size-sm mr-3">
                      <span id="datatableWithCheckboxSelectCounter">0</span>
                      Selected
                    </span>
                    <a class="btn btn-sm btn-outline-danger" href="javascript:;">
                        <i class="tio-delete-outlined"></i> Delete
                    </a>
                </div>
            </div>
            <!-- End Datatable Info -->
        </div>
        <!-- End Header -->
        <div class="card-body">
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
                            <input id="datatableSearch" type="search" class="form-control" placeholder="Search members..." aria-label="Search members">
                        </div>
                        <!-- End Search -->
                    </form>
                </div>
                <div class="col-auto">
                </div>
            </div>
            <div class="table-responsive datatable-custom">
                <table id="datatable"
                           class="table table-lg table-borderless table-thead-bordered table-nowrap table-align-middle card-table dataTable no-footer"
                           data-hs-datatables-options='{
                                "columnDefs": [{
                                    "targets": [0],
                                    "orderable": false
                                }],
                                "order": [],
                                "info": {
                                "totalQty": "#datatableWithPaginationInfoTotalQty"
                             },
                             "search": "#datatableSearch",
                             "entries": "#datatableEntries",
                             "pageLength": 50,
                             "isResponsive": true,
                             "isShowPaging": false,
                             "pagination": "datatableEntriesPagination"
                            }' role="grid" aria-describedby="datatable_info">
                    <thead class="thead-light">
                        <tr role="row">
                            <th class="table-column-pr-0 sorting_disabled">
                                <div class="custom-control custom-checkbox">
                                    <input id="datatableWithCheckboxSelectAll" type="checkbox" class="custom-control-input">
                                    <label class="custom-control-label" for="datatableWithCheckboxSelectAll"></label>
                                </div>
                            </th>
                            <th class="table-column-pl-0 sorting" tabindex="0" aria-controls="datatable" rowspan="1"
                                colspan="1" aria-label="Family name: activate to sort column ascending">Family name
                            </th>
                            <th class="table-column-pl-0 sorting" tabindex="0" aria-controls="datatable" rowspan="1"
                                colspan="1" aria-label="Given name: activate to sort column ascending">Given name
                            </th>
                            <th class="sorting" tabindex="0" aria-controls="datatable" rowspan="1" colspan="1"
                                aria-label="Category: activate to sort column ascending">Membership category
                            </th>
                            <th class="sorting" tabindex="0" aria-controls="datatable" rowspan="1" colspan="1"
                                aria-label="Last subscription: activate to sort column ascending">Last subscription
                            </th>
                        </tr>
                    </thead>

                    <tbody>
                        <#list members as member>
                            <tr role="row" class="<#if member?is_odd_item>odd<#else>even</#if>">
                                <td class="table-column-pr-0">
                                    <div class="custom-control custom-checkbox">
                                        <input type="checkbox" class="custom-control-input" id="dataCheck${member.id}">
                                        <label class="custom-control-label" for="dataCheck${member.id}"></label>
                                    </div>
                                </td>
                                <td class="table-column-pl-0">
                                    <a class="d-flex align-items-center" href="/administration/membership/edit/${member.id}">
                                        <div class="avatar avatar-circle">
                                        </div>
                                        <div class="ml-3">
                                            <span class="h5 text-hover-primary">${member.familyName}</span>
                                        </div>
                                    </a>
                                </td>
                                <td>
                                    <div class="ml-3">
                                        <span class="h5 text-hover-primary">${member.givenName}</span>
                                    </div>
                                </td>
                                <td>
                                    <div class="ml-3">
                                       ${member.category}
                                    </div>
                                </td>
                                <td>
                                    <div class="ml-3">
                                        ${member.lastSubscription}
                                    </div>
                                </td>
                            </tr>
                        </#list>
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
                        <select id="datatableEntries" class="js-select2-custom"
                                data-hs-select2-options='{
                                    "minimumResultsForSearch": "Infinity",
                                    "customClass": "custom-select custom-select-sm custom-select-borderless",
                                    "dropdownAutoWidth": true,
                                    "width": true
                                  }'>
                            <option value="10">10</option>
                            <option value="50" selected>50</option>
                            <option value="100">100</option>
                        </select>
                        <!-- End Select -->

                        <span class="text-secondary mr-2">of</span>

                        <!-- Pagination Quantity -->
                        <span id="datatableWithPaginationInfoTotalQty"></span>
                    </div>
                </div>

                <div class="col-sm-auto">
                    <div class="d-flex justify-content-center justify-content-sm-end">
                        <!-- Pagination -->
                        <nav id="datatableEntriesPagination" aria-label="Activity pagination"></nav>
                    </div>
                </div>
            </div>
            <!-- End Pagination -->
        </div>
        <!-- End Footer -->
    </div>
</@layout.mainLayout>