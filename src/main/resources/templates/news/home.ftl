<#import "../base.ftl" as layout>
<@layout.mainLayout>
    <div class="container space-2 space-lg-3">
        <div class="row justify-content-lg-between">
            <div class="col-lg-8">
                <#list news as n>
                    <!-- Blog -->
                    <article class="row mb-7">
                        <div class="col-md-5">
                            <img class="card-img" src="./assets/img/400x500/img7.jpg" alt="Image Description">
                        </div>
                        <div class="col-md-7">
                            <div class="card-body d-flex flex-column h-100 px-0">
                                <span class="d-block mb-2">
                                  <a class="font-weight-bold" href="#">News</a>
                                </span>
                                <h3><a class="text-inherit" href="/news${n.link}">${n.title}</a></h3>
                                <div>
                                    ${n.abstract}
                                </div>
                                <div class="media align-items-center mt-auto">
                                    <div class="avatar avatar-sm avatar-soft-dark avatar-circle avatar-border-lg avatar-centered mr-3">
                                        <span class="avatar-initials">${n.authorInitials}</span>
                                    </div>
                                    <div class="media-body">
                                        <span class="text-dark">
                                          <a class="d-inline-block text-inherit font-weight-bold" href="">${n.author}</a>
                                        </span>
                                        <small class="d-block">${n.displayPublishDate}</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </article>
                    <!-- End Blog -->
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
                        <a class="page-link" href="news?page=${page - 1}" aria-label="Previous">
                            <span class="d-none d-sm-inline-block mr-1">Prev</span>
                            <span aria-hidden="true">«</span>
                        </a>
                    </li>
                </#if>
                <#assign first = [2, page - 5]?max />
                <#assign last = [totalPages - 1, page + 5]?min />
                <li class="page-item <#if page == 1>active</#if>">
                    <a class="page-link" href="news?page=1">1</a>
                </li>
                <#if (first > 2)>
                    <li class="page-item disabled"><a class="page-link" href="">...</a></li>
                </#if>
                <#list first..last as i>
                    <li class="page-item <#if page == i>active</#if>">
                        <a class="page-link" href="news?page=${i}">${i}</a>
                    </li>
                </#list>
                <#if (last < (totalPages - 1))>
                    <li class="page-item disabled"><a class="page-link" href="">...</a></li>
                </#if>
                <li class="page-item <#if page == totalPages>active</#if>">
                    <a class="page-link" href="news?page=${totalPages}">${totalPages}</a>
                </li>
                <#if (page < totalPages)>
                    <li class="page-item">
                        <a class="page-link" href="news?page=${page + 1}" aria-label="Next">
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