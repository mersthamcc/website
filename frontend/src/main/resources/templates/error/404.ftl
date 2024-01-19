<#import "../base.ftl" as layout>
<#import "/spring.ftl" as spring>

<@layout.mainLayout>
    <div class="container space-1">
        <div class="footer-height-offset d-flex justify-content-center align-items-center flex-column">
            <div class="row align-items-sm-center w-100">
                <div class="col-sm-6">
                    <div class="text-center text-sm-right mr-sm-4 mb-5 mb-sm-0">
                        <img class="w-60 w-sm-100 mx-auto"
                             src="${resourcePrefix}/front/admin/assets/svg/illustrations/think.svg"
                             alt="Error"
                             style="max-width: 15rem;">
                    </div>
                </div>

                <div class="col-sm-6 col-md-4 text-center text-sm-left">
                    <h1 class="display-1 mb-0">404</h1>
                    <p class="lead">
                        <@spring.message code="errors.404" />
                    </p>
                    <a class="btn btn-primary" href="/">
                        <@spring.message code="errors.home" />
                    </a>
                </div>
            </div>
        </div>
    </div>
</@layout.mainLayout>
