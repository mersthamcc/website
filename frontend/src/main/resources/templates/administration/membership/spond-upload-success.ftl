<#import "/spring.ftl" as spring />
<#import "../../admin-base.ftl" as layout>
<#import "../../components.ftl" as components>
<#import "../../admin-components.ftl" as admin>

<@layout.mainLayout>
    <div class="row">
        <div class="col-lg-12">
            <@admin.adminTableCard
                id="processedTable"
                selectable=false
                searchable=false
                defaultPageLength=10
                pageLengths=[10,25,50]
                title="match-fee.processed"
                columns=columns
                data=processed
                />

            <@admin.adminTableCard
                id="skippedTable"
                selectable=false
                searchable=false
                defaultPageLength=10
                pageLengths=[10,25,50]
                title="match-fee.skipped"
                columns=columns
                data=skipped
                />
        </div>
    </div>
</@layout.mainLayout>
