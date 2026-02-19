package com.tanfed.user.utils;

import java.util.HashMap;
import java.util.Map;

public class DesignationAndDept {

	public static String[] designation = { "Assistant", "Senior Assistant", "Accountant", "Assistant Manager",
			"Manager", "Deputy General Manager - Fertiliser", "Deputy General Manager - Finance&Accounts",
			"Deputy General Manager - InternalAudit", "Secretary", "General Manager - Finance&Accounts",
			"General Manager - SPAI", "General Manager - Marketing", "General Manager - Fertiliser",
			"Managing Director" };

	public static String[] department = { "All", "Regional Office", "HO - Establishment", "HO - Fertiliser",
			"HO - Marketing", "HO - SPAI", "HO - GeneralAccounts", "HO - BillsAccounts", "HO - Civil",
			"HO - InternalAudit", };

	
	public static Map<String, String> designationMap = new HashMap<String, String>();
	public static Map<String, String[]> role = new HashMap<String, String[]>();
	static {
		role.put("All", new String[] { "SUPERADMIN", "MD" });
		role.put("Regional Office", new String[] { "ROUSER", "ROADMIN" });
		role.put("HO - Establishment", new String[] { "ESTUSER", "ESTADMIN", "ESTHOD" });
		role.put("HO - Fertiliser", new String[] { "FERTUSER", "FERTADMIN", "FERTHOD" });
		role.put("HO - Marketing", new String[] { "MARKUSER", "MARKADMIN", "MARKHOD" });
		role.put("HO - SPAI", new String[] { "SPAIUSER", "SPAIADMIN", "SPAIHOD" });
		role.put("HO - GeneralAccounts", new String[] { "ACCUSER", "ACCADMIN", "ACCHOD" });
		role.put("HO - BillsAccounts", new String[] { "BILLSUSER", "BILLSADMIN" });
		role.put("HO - Civil", new String[] { "CIVILUSER", "CIVILADMIN" });
		role.put("HO - InternalAudit", new String[] { "AUDITUSER", "AUDITADMIN", "AUDITHOD" });

		designationMap.put("Assistant", "Asst");
		designationMap.put("Senior Assistant", "Senior-Asst");
		designationMap.put("Accountant", "Accountant");
		designationMap.put("Assistant Manager", "Asst.Manager");
		designationMap.put("Manager", "Manager");
		designationMap.put("Deputy General Manager - Fertiliser", "DGM-Fert");
		designationMap.put("Deputy General Manager - Finance&Accounts", "DGM-F&Ac");
		designationMap.put("Deputy General Manager - InternalAudit", "DGM-IA");
		designationMap.put("Secretary", "Secretary");
		designationMap.put("General Manager - Finance&Accounts", "GM-F&Ac");
		designationMap.put("General Manager - SPAI", "GM-SPAI");
		designationMap.put("General Manager - Marketing", "GM-Mark");
		designationMap.put("General Manager - Fertiliser", "GM-Fert");
		designationMap.put("Managing Director", "Mng-Dir");

	}

}
