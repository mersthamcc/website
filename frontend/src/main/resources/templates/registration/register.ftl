<#import "/spring.ftl" as spring />
<#import "../base.ftl" as layout />
<#import "../components.ftl" as components />
<@layout.mainLayout formName="menu.register">
    <@components.panel>
        <form class="form-horizontal" method="post" name="action" action="/register">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

            <#if errors??>
                <@components.formMessages errors=errors errorKey="membership.errors" class="alert-danger"/>
            </#if>

            <div class="row">
                <div class="col-lg-8 mb-7 mb-lg-0">
                    <@components.section title="Registration cart" rightTitle="${basket.subscriptions?size} members">
                        <#list basket.subscriptions as id, subscription>
                            <div class="border-bottom pb-5 mb-5">
                                <div class="media">
                                    <div class="media-body">
                                        <div class="row">
                                            <div class="col-md-7 mb-3 mb-md-0">
                                                <a class="h5 d-block" href="#">
                                                    ${subscription.member.attributeMap["given-name"].asText()!""} ${subscription.member.attributeMap["family-name"].asText()!""}
                                                </a>

                                                <div class="d-block d-md-none">
                                                    <#if subscription.member.registeredInYear(registrationYear)>
                                                        <#if subscription.price??>
                                                            <span class="h5 d-block mb-1">
                                                                ${subscription.price?string.currency}
                                                            </span>
                                                        <#else>
                                                            <button type="submit" class="btn btn-pill btn-primary btn-xs" name="edit-member" value="${id}">
                                                                <@spring.message code="membership.renew" />
                                                            </button>
                                                        </#if>
                                                    </#if>
                                                </div>

                                                <div class="text-body font-size-1 mb-1">
                                                    <span><@spring.message code="membership.action" />:</span>
                                                    <span><@spring.messageText code="membership.${subscription.action.name()!''}" text=subscription.action.name()!'' /></span>
                                                </div>
                                                <div class="text-body font-size-1 mb-1">
                                                    <span><@spring.message code="membership.category" />:</span>
                                                    <span><@spring.message code="membership.${subscription.category!'unknown'}" /></span>
                                                </div>
                                                <#if subscription.member.mostRecentSubscription??>
                                                    <div class="text-body font-size-1 mb-1">
                                                        <span><@spring.message code="membership.last-subscription" />:</span>
                                                        <span>${subscription.year?c}</span>
                                                        <#if subscription.member.registeredInYear(registrationYear)>
                                                            <span class="link-underline ml-2">
                                                                <@spring.message code="membership.renewal-required" />
                                                            </span>
                                                        </#if>
                                                    </div>
                                                </#if>
                                            </div>

                                            <div class="col-md-2">
                                                <div class="row">
                                                    <div class="col-auto">
                                                    </div>
                                                    <#if !subscription.member.registeredInYear(registrationYear)>
                                                        <div class="col-auto">
                                                            <button type="submit" class="btn btn-pill btn-ghost-primary btn-xs" name="edit-member" value="${id}">
                                                                <i class="fa fa-edit"></i>
                                                                <@spring.message code="membership.review" />
                                                            </button>
                                                            <button type="submit" class="btn btn-pill btn-ghost-danger btn-xs" name="delete-member" value="${id}">
                                                                <i class="far fa-trash-alt"></i>
                                                                <@spring.message code="membership.delete-member" />
                                                            </button>
                                                        </div>
                                                    </#if>
                                                </div>
                                            </div>

                                            <div class="col-4 col-md-3 d-none d-md-inline-block text-right">
                                                <#if !subscription.member.registeredInYear(registrationYear)>
                                                    <#if subscription.price??>
                                                        <span class="h5 d-block mb-1">
                                                            ${subscription.price?string.currency}
                                                        </span>
                                                    <#else>
                                                        <button type="submit" class="btn btn-pill btn-primary btn-xs" name="edit-member" value="${id}">
                                                            <@spring.message code="membership.renew" />
                                                        </button>
                                                    </#if>
                                                </#if>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </#list>
                        <div class="d-sm-flex justify-content-end">
                            <button type="submit"
                                    class="btn btn-soft-primary transition-3d-hover"
                                    name="action"
                                    value="add-member">
                                <i class="fa fa-plus"></i>
                                <@spring.message code="membership.add-member" />
                            </button>
                        </div>
                    </@components.section>
                </div>

                <div class="col-lg-4">
                    <div class="pl-lg-4">
                        <!-- Order Summary -->
                        <div class="card shadow-soft p-4 mb-4">
                            <!-- Title -->
                            <div class="border-bottom pb-4 mb-4">
                                <h2 class="h3 mb-0">Order summary</h2>
                            </div>
                            <!-- End Title -->

                            <div class="border-bottom pb-4 mb-4">
                                <div class="media align-items-center mb-3">
                                    <span class="d-block font-size-1 mr-3">Item subtotal (${basket.subscriptions?size})</span>
                                    <div class="media-body text-right">
                                        <span class="text-dark font-weight-bold">
                                            <#if basket.itemTotal??>
                                                ${basket.itemTotal?string.currency}
                                            </#if>
                                        </span>
                                    </div>
                                </div>

                                <#list basket.discounts as name, discount>
                                    <div class="media align-items-center mb-3">
                                        <span class="d-block font-size-1 mr-3">
                                            <@spring.messageText code=name text=name />
                                        </span>
                                        <div class="media-body text-right">
                                            <span class="text-dark font-weight-bold">-${discount?string.currency}</span>
                                        </div>
                                    </div>
                                </#list>

<#--                                    <!-- Checkbox &ndash;&gt;-->
<#--                                    <div class="card shadow-none mb-3">-->
<#--                                        <div class="card-body p-0">-->
<#--                                            <div class="custom-control custom-radio d-flex align-items-center small">-->
<#--                                                <input type="radio" class="custom-control-input" id="deliveryRadio1" name="deliveryRadio" checked="">-->
<#--                                                <label class="custom-control-label ml-1" for="deliveryRadio1">-->
<#--                                                    <span class="d-block font-size-1 font-weight-bold mb-1">Free - Standard delivery</span>-->
<#--                                                    <span class="d-block text-muted">Shipment may take 5-6 business days.</span>-->
<#--                                                </label>-->
<#--                                            </div>-->
<#--                                        </div>-->
<#--                                    </div>-->
<#--                                    <!-- End Checkbox &ndash;&gt;-->

<#--                                    <!-- Checkbox &ndash;&gt;-->
<#--                                    <div class="card shadow-none">-->
<#--                                        <div class="card-body p-0">-->
<#--                                            <div class="custom-control custom-radio d-flex align-items-center small">-->
<#--                                                <input type="radio" class="custom-control-input" id="deliveryRadio2" name="deliveryRadio">-->
<#--                                                <label class="custom-control-label ml-1" for="deliveryRadio2">-->
<#--                                                    <span class="d-block font-size-1 font-weight-bold mb-1">$7.99 - Express delivery</span>-->
<#--                                                    <span class="d-block text-muted">Shipment may take 2-3 business days.</span>-->
<#--                                                </label>-->
<#--                                            </div>-->
<#--                                        </div>-->
<#--                                    </div>-->
<#--                                    <!-- End Checkbox &ndash;&gt;-->
                            </div>

                            <div class="media align-items-center mb-3">
                                <span class="d-block font-size-1 mr-3">
                                    Total:
                                </span>
                                <div class="media-body text-right">
                                    <span class="text-dark font-weight-bold">${basket.basketTotal?string.currency}</span>
                                </div>
                            </div>

                            <div class="row mx-1">
                                <div class="col px-1 my-1">
                                    <button type="submit" class="btn btn-primary btn-block btn-pill transition-3d-hover" name="action" value="next">
                                        <@spring.message code="membership.next" />
                                        <i class="fa fa-arrow-circle-o-right"></i>
                                    </button>&nbsp;
                                </div>
                            </div>
                        </div>
                        <!-- End Order Summary -->

                        <!-- Accordion -->
                        <div id="shopCartAccordion" class="accordion card shadow-soft mb-4">
                            <!-- Card -->
                            <div class="card">
                                <div class="card-header card-collapse" id="shopCartHeadingOne">
                                    <h3 class="mb-0">
                                        <a class="btn btn-link btn-block card-btn font-weight-bold collapsed" href="javascript:;" role="button" data-toggle="collapse" data-target="#shopCartOne" aria-expanded="false" aria-controls="shopCartOne">
                                            Promo code?
                                            <i class="far fa-question-circle text-body ml-1" data-container="body" data-toggle="popover" data-placement="top" data-trigger="hover" title="" data-content="Valid on full priced items only. Some products maybe excluded." data-original-title="Promo code"></i>
                                        </a>
                                    </h3>
                                </div>
                                <div id="shopCartOne" class="collapse" aria-labelledby="shopCartHeadingOne" data-parent="#shopCartAccordion" style="">

                                    <div class="input-group input-group-pill mb-3">
                                        <input type="text" class="form-control" name="name" placeholder="Promo code" aria-label="Promo code">
                                        <div class="input-group-append">
                                            <button type="submit" class="btn btn-block btn-primary btn-pill">Apply</button>
                                        </div>
                                    </div>

                                </div>
                            </div>
                            <!-- End Card -->
                        </div>
                        <!-- End Accordion -->
                    </div>
                </div>
            </div>

        </form>
    </@components.panel>
</@layout.mainLayout>
