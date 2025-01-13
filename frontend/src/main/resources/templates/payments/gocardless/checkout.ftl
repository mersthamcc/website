<#import "/spring.ftl" as spring />
<#import "../../base.ftl" as layout />
<#import "../../components.ftl" as components />
<#macro goCardlessHeader>
    <script src="${resourcePrefix}/mcc/js/js-joda/js-joda.min.js"></script>
</#macro>
<#macro goCardlessScripts>
    <script>
        const latestPaymentDate = JSJoda.LocalDate.parse($('#payment-schedule-form').data('end-date'));
        const LocalDate = JSJoda.LocalDate;
        const ChronoUnit = JSJoda.ChronoUnit;
        const DayOfWeek = JSJoda.DayOfWeek;

        LocalDate.prototype.plusBusinessDays = function (numberOfDays) {
            let i = 0;
            let result = LocalDate.of(this.year(), this.month(), this.dayOfMonth());
            while (i < numberOfDays) {
                result = result.plusDays(1);
                if (result.dayOfWeek().value() <= DayOfWeek.FRIDAY.value()) {
                    i = i + 1;
                }
            }
            return result;
        }

        $(function(){
            $('#payment_day').change(function(){
                if ($('#payment_day').val() == "") {
                    $("#number_payments").prop('disabled',true);
                    $("#number_payments").val("");
                } else {
                    let dayOfMonth = $("#payment_day").val();
                    let todayPlus3 = LocalDate.now().plusBusinessDays(3);
                    let startDate;
                    if (dayOfMonth == -1) {
                        startDate = todayPlus3.withDayOfMonth(todayPlus3.lengthOfMonth());
                    } else {
                        startDate = todayPlus3.withDayOfMonth(dayOfMonth);
                        if (startDate.isBefore(todayPlus3)) {
                            startDate = startDate.plusMonths(1);
                        }
                    }
                    $("#first-payment").val(startDate.toLocaleString());
                    let lastDate = latestPaymentDate.withDayOfMonth(dayOfMonth);
                    let numberMonths = startDate.until(lastDate, ChronoUnit.MONTHS) + 1;
                    let max = 1;
                    $("#number_payments").prop('disabled',false);
                    for (let i = 1 ; i <= 10 ; i++) {
                        $("#number_payments").find('[value=' + i + ']').prop('selected',false);
                        if ( i <= numberMonths ){
                            $("#number_payments").find('[value=' + i + ']').prop('disabled',false);
                            max = i;
                        }else{
                            $("#number_payments").find('[value=' + i + ']').prop('disabled',true);
                        }
                    }
                    $("#number_payments").find('[value=' + max + ']').prop('selected',true);
                    $("#number_payments").change();
                }
            });
            $("#number_payments").change(function(){
                let dayOfMonth = parseInt($("#payment_day").val());
                let todayPlus3 = LocalDate.now().plusBusinessDays(3);
                let startDate;
                if (dayOfMonth === -1) {
                    startDate = todayPlus3.withDayOfMonth(todayPlus3.lengthOfMonth());
                } else {
                    startDate = todayPlus3.withDayOfMonth(dayOfMonth);
                    if (startDate.isBefore(todayPlus3)) {
                        startDate = startDate.plusMonths(1);
                    }
                }
                let numberOfPayments = parseInt($("#number_payments").val()) - 1;
                let actualLastDate = startDate.plusMonths(numberOfPayments);
                if (dayOfMonth == -1) {
                    actualLastDate = actualLastDate.withDayOfMonth(actualLastDate.lengthOfMonth());
                }else{
                    actualLastDate = actualLastDate.withDayOfMonth(dayOfMonth);
                }
                $("#last-payment").val(actualLastDate.toLocaleString());
            });
            $('#payment_day').change();
        });
    </script>
</#macro>
<@layout.mainLayout headers=goCardlessHeader script=goCardlessScripts formName="membership.checkout">

    <@components.panel>
        <@components.section title="payments.gocardless">
            <div class="row">
                <div class="col-md-9">
                    <@spring.message code="payments.gocardless-checkout" />
                </div>
                <div class="col-md-3 right-align">
                    <img src="${resourcePrefix}/mcc/img/gocardless-logo.png" alt="GoCardless Logo" width="200px" />
                </div>
            </div>
        </@components.section>
    </@components.panel>

    <@components.panel>
        <form class="form-horizontal" method="post" name="payment" action="/payments/gocardless/authorise" id="payment-schedule-form" data-end-date="${endDate}">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

            <#if existingMandates?size != 0 >
                <@components.section title="Use existing mandate?">
                    <#list existingMandates as mandate>
                        <div class="form-group">
                            <div class="custom-control custom-radio">
                                <input
                                        type="radio"
                                        class="custom-control-input"
                                        name="mandate"
                                        id="mandateOptions${mandate.mandate.id}"
                                        value="${mandate.mandate.id}"
                                        <#if mandate?is_first>checked="checked"</#if> />
                                <label class="custom-control-label" for="mandateOptions${mandate.mandate.id}">
                                    <div class="text-body mb-1">
                                        <span><b>Bank:</b></span>
                                        <span>${mandate.customerBankAccount.bankName}</span>
                                    </div>
                                    <div class="text-body mb-1">
                                        <span><b>Account Number:</b></span>
                                        <span>******${mandate.customerBankAccount.accountNumberEnding}</span>
                                    </div>
                                    <div class="text-body mb-1">
                                        <span><b>Account Name:</b></span>
                                        <span>${mandate.customerBankAccount.accountHolderName}</span>
                                    </div>
                                    <div class="text-body mb-1">
                                        <span><b>Reference:</b></span>
                                        <span>${mandate.mandate.reference}</span>
                                    </div>
                                </label>
                            </div>
                        </div>
                    </#list>
                    <div class="form-group">
                        <div class="custom-control custom-radio">
                            <input type="radio" class="custom-control-input" name="mandate" id="mandateOptionsNew" value="new" />
                            <label class="custom-control-label" for="mandateOptionsNew">
                                <div class="text-body mb-1">
                                    Create new mandate
                                </div>
                            </label>
                        </div>
                    </div>
                </@components.section>
            </#if>

            <@components.section title="Payment Schedule">
                <div class="form-group">
                    <label class="col-md-3 control-label">Payment Day:</label>
                    <div class="col-md-9">
                        <select class="form-control" name="payment_day" id="payment_day">
                            <#list 1..28 as day>
                                <option value="${day}">${day}</option>
                            </#list>
                            <option value="-1">Last Day of Month</option>
                        </select>
                        <span class="help-block">The day of the month you wish the payment to leave your account. If that day of the current month is less than 3 working days from now, your payments will start next month.</span>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-md-3 control-label">Number of Payments:</label>
                    <div class="col-md-9">
                        <select class="form-control" name="number_payments" id="number_payments">
                            <#list schedules as schedule>
                                <option value="${schedule.numberOfPayments}">${schedule.numberOfPayments} Months - ${schedule.amount?string.currency} per month <#if schedule.amount != schedule.finalAmount>(final payment ${schedule.finalAmount?string.currency})</#if></option>
                            </#list>
                        </select>
                        <span class="help-block">The latest your final payment can be is ${(endDate).format('EEEE dd MMMM yyyy')}.</span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-3 control-label">Calculated Start Date:</label>
                    <div class="col-md-9">
                        <input class="form-control" name="start_date" type="date" value="" readonly="readonly"  id="first-payment"/>
                        <span class="help-block">or nearest working day.</span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-3 control-label">Calculated Last Date:</label>
                    <div class="col-md-9">
                        <input class="form-control" name="end_date" type="date" value="" readonly="readonly" id="last-payment"/>
                        <span class="help-block">or nearest working day.</span>
                    </div>
                </div>

            </@components.section>

            <@components.buttonGroup>
                <a href="/register" class="btn btn-danger transition-3d-hover">
                    <@spring.message code="membership.cancel" />
                </a>&nbsp;
                <button type="submit" class="btn btn-primary btn-xlg transition-3d-hover" name="action" value="next">
                    <@spring.message code="membership.next" />
                    <i class="fa fa-arrow-circle-o-right"></i>
                </button>&nbsp;
            </@components.buttonGroup>
        </form>
    </@components.panel>
</@layout.mainLayout>
