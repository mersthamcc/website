<#import "../base.ftl" as layout>
<@layout.mainLayout>
    <div class="container space-1">
        <div class="row justify-content-lg-between">
            <div class="w-lg-60 mx-lg-auto">
                <div class="mb-4">
                    <h1 class="h2">${event.title}</h1>
                </div>

                ${event.body}
            </div>


            <div class="col-lg-3">

            </div>
        </div>
    </div>
</@layout.mainLayout>
