<#import "../base.ftl" as layout>
<#import "/spring.ftl" as spring />

<#macro homeLayout userDetails>
    <div class="container space-1 space-top-lg-0 space-bottom-lg-2 mt-lg-n10">
        <div class="row">
            <div class="col-lg-3">
                <div class="navbar-expand-lg navbar-expand-lg-collapse-block navbar-light">
                    <div id="sidebarNav" class="collapse navbar-collapse navbar-vertical">
                        <div class="card mb-5">
                            <div class="card-body">
                                <div class="d-none d-lg-block text-center mb-5">
                                    <div class="avatar avatar-xxl avatar-soft-dark avatar-circle avatar-border-lg avatar-centered mb-3">
                                        <span class="avatar-initials">${userDetails.givenName?cap_first[0]}${userDetails.familyName?cap_first[0]}</span>
                                        <span class="avatar-status avatar-sm-status avatar-status-success"></span>
                                    </div>

                                    <h4 class="card-title">${userDetails.givenName} ${userDetails.familyName}</h4>
                                    <p class="card-text font-size-1">${userDetails.email}</p>
                                </div>

                                <h6 class="text-cap small">
                                    <@spring.message code="account.menu-account" />
                                </h6>

                                <ul class="nav nav-sub nav-sm nav-tabs nav-list-y-2 mb-4">
                                    <li class="nav-item">
                                        <a class="nav-link active" href="/account">
                                            <i class="fas fa-id-card nav-icon"></i> <@spring.message code="account.menu-personal-info" />
                                        </a>
                                    </li>
                                    <li class="nav-item">
                                        <a class="nav-link" href="/account/security">
                                            <i class="fas fa-shield-alt nav-icon"></i> <@spring.message code="account.menu-security" />
                                        </a>
                                    </li>
                                </ul>

                                <h6 class="text-cap small"><@spring.message code="account.menu-billing" /></h6>

                                <ul class="nav nav-sub nav-sm nav-tabs nav-list-y-2">
                                    <li class="nav-item">
                                        <a class="nav-link " href="/account/billing">
                                            <i class="fas fa-credit-card nav-icon"></i> <@spring.message code="account.menu-payment-methods" />
                                        </a>
                                    </li>
                                    <li class="nav-item">
                                        <a class="nav-link " href="/account/members">
                                            <i class="fas fa-users nav-icon"></i> <@spring.message code="account.menu-members" />
                                        </a>
                                    </li>
                                </ul>

                                <div class="d-lg-block">
                                    <div class="dropdown-divider"></div>

                                    <ul class="nav nav-sub nav-sm nav-tabs nav-list-y-2">
                                        <li class="nav-item">
                                            <a class="nav-link text-primary" href="/logout">
                                                <i class="fas fa-sign-out-alt nav-icon"></i> <@spring.message code="account.menu-logout" />
                                            </a>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-lg-9">
                <#nested />
            </div>
        </div>
    </div>
</#macro>
