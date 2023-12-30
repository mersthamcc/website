<#import "../base.ftl" as layout>
<@layout.mainLayout>
    <div class="container space-1">
        <div class="row justify-content-lg-between">
            <div class="w-lg-60 mx-lg-auto">
                ${page.content}
            </div>

            <div class="col-lg-3">
            </div>
        </div>
    </div>
</@layout.mainLayout>
