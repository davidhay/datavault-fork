<#macro vaultLayout>
<#import "/spring.ftl" as spring />
<#assign sec=JspTaglibs["http://www.springframework.org/security/tags"] />
<@sec.authentication var="principal" property="principal" />
<!DOCTYPE html>
<html lang="en">
<head>

    <#include "imports.ftl"/>

</head>
<body>
    <div id="datavault-container">
        <div id="datavault-header">
            <#include "header.ftl"/>
            <#include "headerextra.ftl"/>
        </div>
        <div id="datavault-body">
            <#nested/>
        </div>
        <div id="datavault-footer">
            <#include "footer.ftl"/>
        </div>
    </div>
</body>
</html>
</#macro>