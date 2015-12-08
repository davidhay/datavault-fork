<div class="masthead">
    <nav class="navbar navbar-default">
        <div class="container">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <!-- Commented out the brand image for the moment -->
                <!--<a class="navbar-brand" href="${springMacroRequestContext.getContextPath()}/">BRAND</a>  -->
            </div>

            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav">
                    <li <#if nav == "home">class="active"</#if>><a href="${springMacroRequestContext.getContextPath()}/">Home <span class="sr-only">(current)</span></a></li>
                    <li><a href="#">Help</a></li>
                    <li <#if nav == "feedback">class="active"</#if>><a href="${springMacroRequestContext.getContextPath()}/feedback/">Feedback</a></li>
                    <li <#if nav == "groups">class="active"</#if>><a href="${springMacroRequestContext.getContextPath()}/groups/">Groups</a></li>
                    <li <#if nav == "admin">class="active"</#if>><a href="${springMacroRequestContext.getContextPath()}/admin/">Administration</a></li>
                </ul>

                <ul class="nav navbar-nav navbar-right">
                    <li class="dropdown<#if nav == "user"> active</#if>">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">${principal.username}<span class="caret"></span></a>
                        <ul class="dropdown-menu">
                            <li><a href="${springMacroRequestContext.getContextPath()}/users/edit/${principal.username}">Edit profile</a></li>
                            <!--<li><a href="${springMacroRequestContext.getContextPath()}/users/${principal.username}/keys">Add SSH keys</a></li>  -->
                            <li><a href="${springMacroRequestContext.getContextPath()}/auth/logout">Logout</a></li>
                        </ul>
                    </li>
                </ul>
            </div><!-- /.navbar-collapse -->
        </div><!-- /.container-fluid -->
    </nav>

</div>

<div class="container">
    <div class="header">
        <h1 class="title">Data Vault</h1>
        <p class="lead description">Keep your research data safe.</p>
    </div>
</div>
