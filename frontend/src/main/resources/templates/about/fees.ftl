<#import "../base.ftl" as layout>
<#import "/spring.ftl" as spring />
<#import "../components.ftl" as components>
<#macro headers>
</#macro>
<#macro tabscript>
    <script src="../assets/vendor/hs-transform-tabs-to-btn/dist/hs-transform-tabs-to-btn.min.js"></script>

    <script>
        $(document).on('ready', function () {
            $('.js-tabs-to-dropdown').each(function () {
                var transformTabsToBtn = new HSTransformTabsToBtn($(this)).init();
            });
        });
    </script>
</#macro>

<@layout.mainLayout headers=headers script=tabscript>
    <div class="container space-top-2 space-bottom-1">
        <div class="tab-content">
            <div class="text-center">
                <ul class="js-tabs-to-dropdown nav nav-segment nav-fill nav-lg-down-break mb-5"
                    id="subsModalTab"
                    role="tablist"
                    data-hs-transform-tabs-to-btn-options='{
                      "transformResolution": "lg",
                      "btnClassNames": "btn btn-block btn-white dropdown-toggle justify-content-center mb-3"
                    }'>
                    <#list categories as cat>
                        <#if !cat.empty>
                            <li class="nav-item">
                                <a class="nav-link <#if cat?is_first>active</#if>"
                                   href="#${cat.key}"
                                   id="${cat.key}-tab"
                                   data-toggle="tab"
                                   role="tab">
                                    <@spring.messageText code="membership.${cat.key}" text=cat.key />
                                </a>
                            </li>
                        </#if>
                    </#list>
                    <#if fees.content?has_content>
                        <li class="nav-item">
                            <a class="nav-link <#if !categories?has_content>active</#if>"
                               href="#fees"
                               id="fees-tab"
                               data-toggle="tab"
                               role="tab">
                                <@spring.messageText code="membership.fees" text="Match Fees" />
                            </a>
                        </li>
                    </#if>
                </ul>
            </div>

            <div class="container space-1">
                <div class="tab-content" id="subsModalTabContent">
                    <#list categories as cat>
                        <div class="tab-pane fade <#if cat?is_first>show active</#if>" id="${cat.key}" role="tabpanel" aria-labelledby="${cat.key}-tab">
                            <@components.categoryPricelist category=cat _csrf="" subscriptionId=""/>
                        </div>
                    </#list>
                    <#if fees.content?has_content>
                        <div class="tab-pane fade justify-content-lg-between <#if !categories?has_content>show active</#if>" id="fees" role="tabpanel" aria-labelledby="fees-tab">
                            <div class="col-12">
                                ${fees.content}
                            </div>
                        </div>
                    </#if>
                </div>
            </div>
        </div>
    </div>
</@layout.mainLayout>