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
                    <@components.section title="Registration cart" rightTitle="${basket.chargeableSubscriptions?size} members">
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
                                                    <@actions id=id subscription=subscription />
                                                </div>

                                                <#switch subscription.action.name()>
                                                    <#case "NEW">
                                                        <div class="text-body font-size-1 mb-1">
                                                            <span>
                                                                <@spring.message code="membership.new-registration" />
                                                                <@spring.message code="membership.${subscription.category!'unknown'}" />
                                                            </span>
                                                        </div>
                                                        <#break>
                                                    <#case "RENEW">
                                                        <div class="text-body font-size-1 mb-1">
                                                            <span>
                                                                <@spring.message code="membership.renewing" />&nbsp;${subscription.priceListItem.description}
                                                            </span>
                                                        </div>
                                                        <#break>
                                                    <#default>
                                                        <#if subscription.member.mostRecentSubscription??>
                                                            <#if subscription.member.registeredInYear(registrationYear)>
                                                                <div class="text-body font-size-1 mb-1">
                                                                    <span><@spring.message code="membership.already-subscribed" /> ${subscription.member.mostRecentSubscription.priceListItem.description}</span>
                                                                </div>
                                                            <#else>
                                                                <div class="text-body font-size-1 mb-1">
                                                                    <span><@spring.message code="membership.last-subscription" /> ${subscription.year?c}</span>
                                                                    <span class="link-underline ml-2">
                                                                        <@spring.message code="membership.renewal-required" />
                                                                    </span>
                                                                </div>
                                                            </#if>
                                                        </#if>
                                                    <#break>
                                                </#switch>

                                            </div>

                                            <div class="col-md-2">
                                                <div class="row">
                                                    <div class="col-auto">
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="col-4 col-md-3 d-none d-md-inline-block text-right">
                                                <@actions id=id subscription=subscription />
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
                                    <span class="d-block font-size-1 mr-3">Item subtotal (${basket.chargeableSubscriptions?size})</span>
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

                        <#if coupons?has_content>
                            <div class="card shadow-soft p-4 mb-4">
                                <div class="border-bottom pb-4 mb-4">
                                    <h2 class="h3 mb-0">
                                        <@spring.message code="membership.coupon-title" />
                                    </h2>
                                </div>
                                <div class="border-bottom pb-4 mb-4">
                                    <#list coupons as coupon>
                                        <div class="media align-items-center mb-3">
                                            <div class="text-body font-size-1 mb-1">
                                                <div>${coupon.description}</div>
                                                <div class="ml-2">${coupon.code}</div>
                                                <div class="ml-2">${coupon.value?string.currency}</div>
                                            </div>

                                            <div class="media-body text-right">
                                                <button type="submit" class="btn btn-pill btn-primary btn-xs" name="apply-coupon" value="${coupon.code}">
                                                    <@spring.message code="membership.apply-coupon" />
                                                </button>
                                            </div>
                                        </div>
                                    </#list>
                                </div>
                            </div>
                        </#if>
                    </div>
                </div>
            </div>

        </form>
    </@components.panel>
</@layout.mainLayout>

<#macro actions id subscription>
    <#if !subscription.member.registeredInYear(registrationYear)>
        <#if subscription.price??>
            <span class="h5 d-block mb-1">
                ${subscription.price?string.currency}
            </span>

            <button type="submit" class="btn btn-pill btn-secondary btn-xs mb-md-2 w-100" name="edit-member" value="${id}">
                <i class="fa fa-edit"></i>
                <@spring.message code="membership.review" />
            </button>
            <#if subscription.action.name() == "NEW">
                <button type="submit" class="btn btn-pill btn-soft-danger btn-xs w-100" name="delete-member" value="${id}">
                    <i class="far fa-trash-alt"></i>
                    <@spring.message code="membership.delete-member" />
                </button>
            <#else>
                <button type="submit" class="btn btn-pill btn-soft-danger btn-xs w-100" name="reset-member" value="${id}">
                    <i class="far fa-recycle"></i>
                    <@spring.message code="membership.reset" />
                </button>
            </#if>
        <#else>
            <button type="submit" class="btn btn-pill btn-primary btn-xs w-100" name="edit-member" value="${id}">
                <@spring.message code="membership.renew" />
            </button>
        </#if>
    </#if>
</#macro>