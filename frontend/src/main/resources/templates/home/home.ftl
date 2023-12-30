<#import "../base.ftl" as layout>
<#import "/spring.ftl" as spring />

<@layout.mainLayout>
    <div class="container space-1">
        <div class="row justify-content-lg-between">            <!-- Content -->
            <div class="col-lg-12">
                <div class="mb-5 mt-5">
                    <h1 class="mb-3">
                        ${page.title}
                    </h1>

                    ${page.content}

                    <a class="btn btn-primary btn-wide transition-3d-hover" href="/register">
                        <@spring.messageText code="home.join" text="Join Now" />
                    </a>
                    <a class="btn btn-link btn-wide" href="/contacts">
                        <@spring.messageText code="home.contact" text="Contact us" /> <i class="fas fa-angle-right fa-sm ml-1"></i>
                    </a>
                </div>
            </div>
        </div>
    </div>
</@layout.mainLayout>
