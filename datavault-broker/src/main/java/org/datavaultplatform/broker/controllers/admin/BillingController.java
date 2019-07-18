package org.datavaultplatform.broker.controllers.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.datavaultplatform.broker.services.ExternalMetadataService;
import org.datavaultplatform.broker.services.VaultsService;
import org.datavaultplatform.common.model.Vault;
import org.datavaultplatform.common.response.BillingInformation;
import org.datavaultplatform.common.response.VaultInfo;
import org.datavaultplatform.common.response.VaultsData;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class BillingController {
	private ExternalMetadataService externalMetadataService;
	private VaultsService vaultsService;
	private static final Logger LOGGER = LoggerFactory.getLogger(BillingController.class);

    @RequestMapping(value = "/admin/billing", method = RequestMethod.GET)
    public VaultsData getBillingVaultsAll(@RequestHeader(value = "X-UserID", required = true) String userID,
                                        @RequestParam(value = "sort", required = false)
                                        @ApiQueryParam(name = "sort", description = "Vault sort field", allowedvalues = {"id", "name", "vaultSize", "user", "creationTime", "reviewDate"}, defaultvalue = "creationTime", required = false) String sort,
                                        @RequestParam(value = "order", required = false)
                                        @ApiQueryParam(name = "order", description = "Vault sort order", allowedvalues = {"asc", "dec"}, defaultvalue = "asc", required = false) String order,
                                        @RequestParam(value = "offset", required = false)
									    @ApiQueryParam(name = "offset", description = "Vault row id ", defaultvalue = "0", required = false) String offset,
									    @RequestParam(value = "maxResult", required = false)
									    @ApiQueryParam(name = "maxResult", description = "Number of records", required = false) String maxResult) throws Exception {

        if (sort == null) sort = "";
        if (order == null) order = "asc";
        Long recordsTotal = 0L;
		/*
		 * List<BillingInformation> billingResponses = new ArrayList<>();
		 * List<BillingInfo> billingDetails = billingService.getVaults(sort,
		 * order,offset, maxResult);
		 */
        
        List<VaultInfo> billingResponses = new ArrayList<>();
        List<Vault> vaultDetails = vaultsService.getVaults(sort, order,offset, maxResult);
        
        if(CollectionUtils.isNotEmpty(vaultDetails)) {
			for (Vault vaultList : vaultDetails) {				
				billingResponses.add(vaultList.convertToResponseBilling());
	        }
	        recordsTotal = vaultsService.getTotalNumberOfVaults();
	        //Map of project with its size
	        Map<String, Long> projectSizeMap = vaultsService.getAllProjectsSize();
	        //update project Size in the response
	        for(VaultInfo vault: billingResponses) {
	        	if(vault.getProjectId() != null) {
	        		vault.setProjectSize(projectSizeMap.get(vault.getProjectId()));
	        	}
	        }
        }
        //calculate and create a map of project size
        VaultsData data = new VaultsData();
        data.setRecordsTotal(recordsTotal);
        data.setData(billingResponses);
        return data;
    }

    @RequestMapping(value = "/admin/billing/search", method = RequestMethod.GET)
    public VaultsData searchAllBillingVaults(@RequestHeader(value = "X-UserID", required = true) String userID,
                                                  @RequestParam String query,
                                                  @RequestParam(value = "sort", required = false) String sort,
                                                  @RequestParam(value = "order", required = false)
                                                  @ApiQueryParam(name = "order", description = "Vault sort order", allowedvalues = {"asc", "dec"}, defaultvalue = "asc", required = false) String order,
                                                  @RequestParam(value = "offset", required = false)
											      @ApiQueryParam(name = "offset", description = "Vault row id ", defaultvalue = "0", required = false) String offset,
											      @RequestParam(value = "maxResult", required = false)
											      @ApiQueryParam(name = "maxResult", description = "Number of records", required = false) String maxResult) throws Exception {

        List<VaultInfo> billingResponses = new ArrayList<>();
        Long recordsTotal = 0L;
        Long recordsFiltered = 0L;
        List<Vault> vaults = vaultsService.search(query, sort, order, offset, maxResult);
        if(CollectionUtils.isNotEmpty(vaults)) {
			for (Vault vault : vaults) {
	            billingResponses.add(vault.convertToResponseBilling());
	        }
	        //calculate and create a map of project size
	        Map<String, Long> projectSizeMap = vaultsService.getAllProjectsSize();
	        //update project Size in the response
	        for(VaultInfo vault: billingResponses) {
	        	if(vault.getProjectId() != null) {
	        		vault.setProjectSize(projectSizeMap.get(vault.getProjectId()));
	        	}
	        }
	        recordsTotal = vaultsService.getTotalNumberOfVaults();
	        recordsFiltered = vaultsService.getTotalNumberOfVaults(query);
        }
        
        VaultsData data = new VaultsData();
        data.setRecordsTotal(recordsTotal);
        data.setRecordsFiltered(recordsFiltered);
        data.setData(billingResponses);
        return data;
    }
    
    @RequestMapping(value = "/admin/billing/{vaultId}", method = RequestMethod.GET)
    public BillingInformation getVaultBillingInfo(@RequestHeader(value = "X-UserID", required = true) String userID,
                                  @PathVariable("vaultId") String vaultId) throws Exception {

    	BillingInformation vaultBillingInfo = vaultsService.getVault(vaultId).convertToBillingDetailsResponse();

        return vaultBillingInfo;
    }
    
	public ExternalMetadataService getExternalMetadataService() {
		return externalMetadataService;
	}

	public void setExternalMetadataService(ExternalMetadataService externalMetadataService) {
		this.externalMetadataService = externalMetadataService;
	}

	public VaultsService getVaultsService() {
		return vaultsService;
	}
	public void setVaultsService(VaultsService vaultsService) {
		this.vaultsService = vaultsService;
	}
}
