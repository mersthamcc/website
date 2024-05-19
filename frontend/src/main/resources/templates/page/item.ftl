<#import "../base.ftl" as layout>

<@layout.mainLayout pageTitle=page.title>
    <div class="container space-1">
        <div class="row justify-content-lg-between">
            <div class="col-12">
                ${page.content}
            </div>
        </div>
    </div>
</@layout.mainLayout>
