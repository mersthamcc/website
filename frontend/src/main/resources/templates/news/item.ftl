<#import "../base.ftl" as layout>
<@layout.mainLayout>
    <div class="container space-1">
        <div class="row justify-content-lg-between">
            <div class="w-lg-60 mx-lg-auto">
                <div class="mb-4">
                    <h1 class="h2">${news.title}</h1>
                </div>

                <div class="border-top border-bottom py-4 mb-5">
                    <div class="row align-items-md-center">
                        <div class="col-md-7 mb-5 mb-md-0">
                            <div class="media align-items-center">
                                <div class="avatar avatar-soft-dark avatar-circle avatar-border-lg avatar-centered">
                                    <span class="avatar-initials">${news.authorInitials}</span>
                                </div>
                                <div class="media-body font-size-1 ml-3">
                                    <span class="h6">${news.author}</span>
                                    <span class="d-block text-muted">${news.displayPublishDate}</span>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-5">
<#--                            <div class="d-flex justify-content-md-end align-items-center">-->
<#--                                <span class="d-block small font-weight-bold text-cap mr-2">Share:</span>-->

<#--                                <a class="btn btn-xs btn-icon btn-soft-secondary rounded-circle ml-2" href="#">-->
<#--                                    <i class="fab fa-facebook-f"></i>-->
<#--                                </a>-->
<#--                                <a class="btn btn-xs btn-icon btn-soft-secondary rounded-circle ml-2" href="#">-->
<#--                                    <i class="fab fa-twitter"></i>-->
<#--                                </a>-->
<#--                                <a class="btn btn-xs btn-icon btn-soft-secondary rounded-circle ml-2" href="#">-->
<#--                                    <i class="fab fa-instagram"></i>-->
<#--                                </a>-->
<#--                                <a class="btn btn-xs btn-icon btn-soft-secondary rounded-circle ml-2" href="#">-->
<#--                                    <i class="fab fa-telegram"></i>-->
<#--                                </a>-->
<#--                            </div>-->
                        </div>
                    </div>
                </div>

                ${news.body}
            </div>

            <div class="col-lg-3">

            </div>
        </div>
    </div>
</@layout.mainLayout>