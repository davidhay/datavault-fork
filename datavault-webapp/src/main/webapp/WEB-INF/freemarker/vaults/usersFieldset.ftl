<fieldset>
    <div id="add-role-vault-dialog" class="form-card">
        <h2 class="fs-title text-center">Vault Users</h2> <br><br>

        <h4>Vault Access</h4>

        <div class="form-group" required>
            <label class="control-label">Are you the owner of the vault:</label>
            <@spring.bind "vault.isOwner" />
            <div class="radio-inline">
                <label>
                    <input type="radio" id="isNominatedDataManagers" name="${spring.status.expression}" value="true" <#if vault.isOwner??>${(vault.isOwner)?then('checked', '')}</#if>> Yes
                </label>
            </div>
            <div class="radio-inline">
                <label>
                    <input type="radio" id="isNominatedDataManagers" name="${spring.status.expression}" value="false" <#if vault.isOwner??>${(!vault.isOwner)?then('checked', '')}</#if>> No
                </label>
            </div>
        </div>
        <div class="form-group" required>
            <label class="col-sm-2 control-label">Owner UUN: </label>
            <@spring.bind "vault.vaultOwner" />
            <input id="vaultOwner" name="${spring.status.expression}" value="${spring.status.value!""}" type="text"  placeholder="autofilled uun with ldap"/>
        </div>

        <#assign ndmCount = 0>
        <div class="ndm-form-group">
            <label class="col-sm-2 control-label">NDMs: </label>
            <@spring.bind "vault.nominatedDataManagers[${ndmCount}]" />
            <input id="nominatedDataManagers" name="${spring.status.expression}" value="${spring.status.value!""}" class="ndm" type="text" />
            <button type="button" id="add-ndm-btn" class="btn btn-default btn-sm">Add a NDM</button>
            <div id="extra-ndm-list"></div>
            <div class="example-ndm hidden col-sm-offset-2">
                <#assign ndmCount++>
                <input type="hidden" id="counter" name="id" value="0"/>
                <@spring.bind "vault.nominatedDataManagers[${ndmCount}]" />
                <input id="nominatedDataManagers" name="${spring.status.expression}" value="${spring.status.value!""}" class="ndm" type="text"  placeholder="autofilled uun with ldap"/>
                <button type="button" class="remove-ndm-btn btn btn-danger btn-xs">Remove</button>
            </div>
        </div>

        <#assign depCount = 0>
        <div id="depositors-form-group" class="form-group">
            <label class="col-sm-2 control-label">Depositors: </label>
            <@spring.bind "vault.depositors[${depCount}]" />
            <input id="depositors" name="${spring.status.expression}" value="${spring.status.value!""}" class="depositor" type="text" placeholder="autofilled uun with ldap"/>
            <button type="button" id="add-depositor-btn" class="btn btn-default btn-sm">Add a Depositor</button>
            <div id="extra-depositor-list"></div>
            <div class="example-depositor hidden col-sm-offset-2">
                <#assign depCount++>
                <@spring.bind "vault.depositors[${depCount}]" />
                <input id="depositors" name="${spring.status.expression}" value="${spring.status.value!""}" class="depositor" type="text"  placeholder="autofilled uun with ldap"/>
                <button type="button" class="remove-depositor-btn btn btn-danger btn-xs">Remove</button>
            </div>
        </div>

        <h4>Pure Information</h4>

        <div id="contact-form-group" class="form-group">
            <label for="contactPerson" class="col-sm-2 control-label">Contact person: </label>
            <@spring.bind "vault.contactPerson" />
            <input id="contactPerson" name="${spring.status.expression}" value="${spring.status.value!""}" class="contact" type="text" placeholder="autofilled uun with ldap"/>
        </div>

        <#assign creatorCount = 0>
        <div id="creators-form-group" class="form-group">
            <label class="col-sm-2 control-label">Data Creator: </label>
            <@spring.bind "vault.dataCreators[${creatorCount}]" />
            <input class="creator" type="text" placeholder="autofilled uun with ldap" id="dataCreators" name="${spring.status.expression}" value="${spring.status.value!""}"/>
            <button type="button" id="add-creator-btn" class="btn btn-default btn-sm">Add a Data Creator</button>
            <div id="extra-creator-list"></div>
            <div class="example-creator hidden col-sm-offset-2">
                <#assign creatorCount++>
                <@spring.bind "vault.dataCreators[${creatorCount}]" />
                <input class="creator" type="text" placeholder="autofilled uun with ldap" id="dataCreators" name="${spring.status.expression}" value="${spring.status.value!""}"/>
                <button type="button" class="remove-creator-btn btn btn-danger btn-xs">Remove</button>
            </div>
        </div>

        <div class="well">
            A statement that encourages them to go to Pure later,
            saying they can go to Pure to edit the dataset record
            we will create automatically to describe this vault,
            so they can link the dataset record to their papers and multiple projects.
            <div class="alert alert-info">
                <strong>We recommend you review the Pure metadata record for accuracy and add links to research outputs and other projects/ people.</strong>
            </div>
        </div>
    </div>
    <button type="button" name="previous" class="previous action-button-previous btn btn-default" >&laquo; Previous</button>
    <button type="submit" name="save" value="Save" class="save action-button-previous btn btn-default" >
        <span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> Save
    </button>
    <button type="button" name="next" class="next action-button btn btn-primary">Next Step &raquo;</button>
    <script>
        $(".ndm").autocomplete({
            autoFocus: true,
            appendTo: "#add-role-vault-dialog",
            minLength: 2,
            source: function (request, response) {
                var term = request.term;
                $.ajax({
                    url: "${springMacroRequestContext.getContextPath()}/vaults/autocompleteuun/" + term,
                    type: 'GET',
                    dataType: "json",
                    success: function (data) {
                        response(data);
                    },
                    error: function(xhr) {
                        ErrorHandler.handleAjaxError('#orphan-dialog-error', xhr);
                    }
                });
            },
            select: function (event, ui) {
                var attributes = ui.item.value.split(" - ");
                this.value = attributes[0];
                return false;
            }
        });

        $(".depositor").autocomplete({
            autoFocus: true,
            appendTo: "#add-role-vault-dialog",
            minLength: 2,
            source: function (request, response) {
                var term = request.term;
                $.ajax({
                    url: "${springMacroRequestContext.getContextPath()}/vaults/autocompleteuun/" + term,
                    type: 'GET',
                    dataType: "json",
                    success: function (data) {
                        response(data);
                    },
                    error: function(xhr) {
                        ErrorHandler.handleAjaxError('#orphan-dialog-error', xhr);
                    }
                });
            },
            select: function (event, ui) {
                var attributes = ui.item.value.split(" - ");
                this.value = attributes[0];
                return false;
            }
        });

        $(".creator").autocomplete({
            autoFocus: true,
            appendTo: "#add-role-vault-dialog",
            minLength: 2,
            source: function (request, response) {
                var term = request.term;
                $.ajax({
                    url: "${springMacroRequestContext.getContextPath()}/vaults/autocompleteuun/" + term,
                    type: 'GET',
                    dataType: "json",
                    success: function (data) {
                        response(data);
                    },
                    error: function(xhr) {
                        ErrorHandler.handleAjaxError('#orphan-dialog-error', xhr);
                    }
                });
            },
            select: function (event, ui) {
                var attributes = ui.item.value.split(" - ");
                this.value = attributes[0];
                return false;
            }
        });

        $("#vaultOwner").autocomplete({
            autoFocus: true,
            appendTo: "#add-role-vault-dialog",
            minLength: 2,
            source: function (request, response) {
                var term = request.term;
                $.ajax({
                    url: "${springMacroRequestContext.getContextPath()}/vaults/autocompleteuun/" + term,
                    type: 'GET',
                    dataType: "json",
                    success: function (data) {
                        response(data);
                    },
                    error: function(xhr) {
                        ErrorHandler.handleAjaxError('#orphan-dialog-error', xhr);
                    }
                });
            },
            select: function (event, ui) {
                var attributes = ui.item.value.split(" - ");
                this.value = attributes[0];
                return false;
            }
        });

        $("#contactPerson").autocomplete({
            autoFocus: true,
            appendTo: "#add-role-vault-dialog",
            minLength: 2,
            source: function (request, response) {
                var term = request.term;
                $.ajax({
                    url: "${springMacroRequestContext.getContextPath()}/vaults/autocompleteuun/" + term,
                    type: 'GET',
                    dataType: "json",
                    success: function (data) {
                        response(data);
                    },
                    error: function(xhr) {
                        ErrorHandler.handleAjaxError('#orphan-dialog-error', xhr);
                    }
                });
            },
            select: function (event, ui) {
                var attributes = ui.item.value.split(" - ");
                this.value = attributes[0];
                return false;
            }
        });
    </script>
</fieldset>