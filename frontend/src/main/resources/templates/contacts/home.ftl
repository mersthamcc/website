<#import "../base.ftl" as layout>
<#import "../components.ftl" as components>
<#macro headers>
</#macro>
<#macro imageScripts>
    <script>

    </script>
</#macro>

<@layout.mainLayout script=imageScripts headers=headers>
    <!-- Nav -->
    <div class="text-center">
        <ul class="nav nav-segment nav-pills scrollbar-horizontal mt-3" role="tablist">
            <#list categories as cat>
                <#assign isCurrent = cat.slug == current.slug />
                <li class="nav-item">
                    <a class="nav-link <#if isCurrent>active</#if>"
                       href="/contacts/${cat.slug}"
                       role="tab"
                       aria-controls=""
                       aria-selected="<#if isCurrent>true<#else>false</#if>">
                        ${cat.title}
                    </a>
                </li>
            </#list>
        </ul>
    </div>
    <!-- End Nav -->

    <div class="container space-1">
        <div class="row">
            <#list current.sortedContacts as c>
                <div class="col-sm-6 col-md-4 mb-5">
                    <@components.contactCard contact=c additionalClasses="h-100" />
                </div>
            </#list>
        </div>
    </div>
</@layout.mainLayout>
