<#import "../base.ftl" as layout>
<#macro headers>
</#macro>
<#macro imageScripts>
    <script>

    </script>
</#macro>

<@layout.mainLayout script=imageScripts headers=headers>
    <div class="container space-2">
        <div class="row justify-content-lg-between">
            <div class="col-lg-9">
                <#list events as n>
                    <article class="row mb-7">
                        <div class="col-md-5">
                            <div class="card-body-centered d-flex flex-column h-100 px-0">
                                <span class="dateBoxFixture dateBoxBig">
                                    <span class="month">${n.displayDate.month}</span>
                                    <span class="day">${n.displayDate.dayOfMonth}</span>
                                </span>
                            </div>
                        </div>
                        <div class="card col-md-7">
                            <div class="card-header-title">
                                <h3><a class="text-inherit" href="/events${n.path}">${n.title}</a></h3>
                            </div>
                            <div class="card-body d-flex flex-column h-100 px-0">
                                <div>
                                    ${n.abstract}
                                </div>
                                <div class="text-right">
                                    <a href="/events${n.path}#readmore">Read more...</a>
                                </div>
                            </div>
                        </div>
                    </article>
                    <#if n?has_next>
                        <div class="text-center mb-5">
                            <span class="divider divider-text"><i class="fas fa-layer-group">&nbsp;</i></span>
                        </div>
                    </#if>
                </#list>
                <!-- Sticky Block End Point -->
                <div id="stickyBlockEndPoint"></div>
            </div>

            <div class="col-lg-3">
                <div class="mb-7">
                    <div class="mb-3">
                        <h3>Newsletter</h3>
                    </div>

                    <!-- Form -->
                    <form class="js-validate mb-2" novalidate="novalidate">
                        <label class="sr-only" for="subscribeSr">Subscribe</label>
                        <div class="input-group input-group-flush mb-3">
                            <input type="email" class="form-control form-control-sm" name="email" id="subscribeSr" placeholder="Your email" aria-label="Your email" required="" data-msg="Please enter a valid email address.">
                        </div>
                        <button type="submit" class="btn btn-sm btn-primary btn-block">Subscribe</button>
                    </form>
                    <!-- End Form -->

                    <p class="small">Get special offers on the latest developments from Front.</p>
                </div>

                <div class="mb-7">
                    <div class="mb-3">
                        <h3>Tags</h3>
                    </div>

                    <a class="btn btn-xs btn-soft-secondary mb-1" href="#">Business</a>
                    <a class="btn btn-xs btn-soft-secondary mb-1" href="#">Adventure</a>
                    <a class="btn btn-xs btn-soft-secondary mb-1" href="#">Community</a>
                    <a class="btn btn-xs btn-soft-secondary mb-1" href="#">Announcements</a>
                    <a class="btn btn-xs btn-soft-secondary mb-1" href="#">Tutorials</a>
                    <a class="btn btn-xs btn-soft-secondary mb-1" href="#">Resources</a>
                    <a class="btn btn-xs btn-soft-secondary mb-1" href="#">Classic</a>
                    <a class="btn btn-xs btn-soft-secondary mb-1" href="#">Photography</a>
                    <a class="btn btn-xs btn-soft-secondary mb-1" href="#">Interview</a>
                </div>
            </div>
        </div>

        <!-- Pagination -->
        <nav aria-label="Page navigation">
            <ul class="pagination mb-0">
                <#if (page > 1)>
                    <li class="page-item">
                        <a class="page-link" href="event?page=${page - 1}" aria-label="Previous">
                            <span class="d-none d-sm-inline-block mr-1">Prev</span>
                            <span aria-hidden="true">«</span>
                        </a>
                    </li>
                </#if>
                <#assign first = [2, page - 5]?max />
                <#assign last = [totalPages - 1, page + 5]?min />
                <li class="page-item <#if page == 1>active</#if>">
                    <a class="page-link" href="event?page=1">1</a>
                </li>
                <#if (first > 2)>
                    <li class="page-item disabled"><a class="page-link" href="">...</a></li>
                </#if>
                <#list first..last as i>
                    <li class="page-item <#if page == i>active</#if>">
                        <a class="page-link" href="event?page=${i}">${i}</a>
                    </li>
                </#list>
                <#if (last < (totalPages - 1))>
                    <li class="page-item disabled"><a class="page-link" href="">...</a></li>
                </#if>
                <li class="page-item <#if page == totalPages>active</#if>">
                    <a class="page-link" href="event?page=${totalPages}">${totalPages}</a>
                </li>
                <#if (page < totalPages)>
                    <li class="page-item">
                        <a class="page-link" href="event?page=${page + 1}" aria-label="Next">
                            <span class="d-none d-sm-inline-block mr-1">Next</span>
                            <span aria-hidden="true">»</span>
                        </a>
                    </li>
                </#if>
            </ul>
        </nav>
        <!-- End Pagination -->
    </div>
</@layout.mainLayout>
