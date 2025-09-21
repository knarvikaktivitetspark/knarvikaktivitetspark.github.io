<div class="row text-center border-top mt-5">
    <div class="col-12 col-md-6">
        <h2 class="display-4 text-center mt-5">Framdrift</h2>

        <table class="table table-striped text-start border rounded shadow-sm">
        <#list plan.progress as p>
            <tr>
            <td>${p.month}</td>
            <td>
                <#list p.entries as e>
                <p <#if e?is_last>class="m-0"</#if>>${e}</p>
                </#list>
            </td>
            </tr>
        </#list>
        </table>
    </div>
    <div class="col-12 col-md-6">
        <h2 class="display-4 text-center mt-5">Plan videre</h2>

        <table class="table table-striped text-start border rounded shadow-sm">
        <#list plan.plan as p>
            <tr>
            <td>${p.month}</td>
            <td>
                <#list p.entries as e>
                <p <#if e?is_last>class="m-0"</#if>>${e}</p>
                </#list>
            </td>
            </tr>
        </#list>
        </table>
    </div>
</div>