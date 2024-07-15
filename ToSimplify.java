public class ToSimplify {

	public List<ESRSupplierDirectorOwner> getAcraLiveSupplierDirwners(String suppCode){
		List<ESRSupplierDirOwnWH> dirOwnWHsList;
		List<String> codeList = new ArrayList<String>();
		List<ESRSupplierDirectorOwner> directorOwners = null;
		HashMap<Character, String> positionMap = new HashMap<Character, String>();
		List<ESRSupplierDirectorOwner> directorOwnerMapList = new ArrayList<ESRSupplierDirectorOwner>();
		HashMap<String, List<ESRSupplierDirectorOwner>> supplierDirOwnerMap = new HashMap<String, List<ESRSupplierDirectorOwner>>();
		ESRSupplierDirectorOwner esrSupplierDirectorOwner;
		try{
			positionMap.put('D', "Director");
			positionMap.put('P', "Partner");
			positionMap.put('O', "Owner");				
			TypedQuery<ESRSupplierDirectorOwner> directorOwnerQry = entityManager.
					createQuery("FROM ESRSupplierDirectorOwner WHERE companyCode=?1", ESRSupplierDirectorOwner.class);
			directorOwners   = (List<ESRSupplierDirectorOwner>) directorOwnerQry.setParameter(1, suppCode).getResultList();
			TypedQuery<ESRSupplierDirOwnWH> acraQuery = entityManager.
					createQuery("FROM ESRSupplierDirOwnWH WHERE code=?1 and companyCode=?2", ESRSupplierDirOwnWH.class);
			for (ESRSupplierDirectorOwner directorOwner : directorOwners) {
				directorOwnerMapList = new ArrayList<ESRSupplierDirectorOwner>();
				if ('Y' == directorOwner.getLogicalDeleteFlag()) {
					acraQuery.setParameter(1, directorOwner.getCode());
					acraQuery.setParameter(2, directorOwner.getCompanyCode());
					dirOwnWHsList = acraQuery.getResultList();
					for (ESRSupplierDirOwnWH dirOwnWH : dirOwnWHsList) {
						esrSupplierDirectorOwner = new ESRSupplierDirectorOwner();
						esrSupplierDirectorOwner.setCode(dirOwnWH.getCode());
						esrSupplierDirectorOwner.setCompanyCode(dirOwnWH.getCompanyCode());
						esrSupplierDirectorOwner.setEntryDate(dirOwnWH.getEntryDate());
						esrSupplierDirectorOwner.setWithdrawDate(dirOwnWH.getWithdrawDate());
						esrSupplierDirectorOwner.setName(dirOwnWH.getName());
						esrSupplierDirectorOwner.setNationality(dirOwnWH.getNationality());
						esrSupplierDirectorOwner.setPositionHeld(positionMap.get(dirOwnWH.getPositionHeld()));
						esrSupplierDirectorOwner.setFbrn(dirOwnWH.getFbrn());
						directorOwnerMapList.add(esrSupplierDirectorOwner);
					}
					if (directorOwnerMapList.size() > 0) {
						codeList.add(directorOwner.getCode());
						supplierDirOwnerMap.put(directorOwner.getCode(), directorOwnerMapList);
					}
				} else {		
					acraQuery.setParameter(1, directorOwner.getCode());
					acraQuery.setParameter(2, directorOwner.getCompanyCode());
					dirOwnWHsList = acraQuery.getResultList();
					for (ESRSupplierDirOwnWH dirOwnWH : dirOwnWHsList) {
						esrSupplierDirectorOwner = new ESRSupplierDirectorOwner();
						esrSupplierDirectorOwner.setCode(dirOwnWH.getCode());
						esrSupplierDirectorOwner.setCompanyCode(dirOwnWH.getCompanyCode());
						esrSupplierDirectorOwner.setEntryDate(dirOwnWH.getEntryDate());
						esrSupplierDirectorOwner.setWithdrawDate(dirOwnWH.getWithdrawDate());
						esrSupplierDirectorOwner.setName(dirOwnWH.getName());
						esrSupplierDirectorOwner.setNationality(dirOwnWH.getNationality());
						esrSupplierDirectorOwner.setPositionHeld(positionMap.get(dirOwnWH.getPositionHeld()));
						esrSupplierDirectorOwner.setFbrn(dirOwnWH.getFbrn());
						directorOwnerMapList.add(esrSupplierDirectorOwner);						
					}
					codeList.add(directorOwner.getCode());
					directorOwnerMapList.add(directorOwner);
					supplierDirOwnerMap.put(directorOwner.getCode(), directorOwnerMapList);
				}
			}
			if (codeList.size() > 0) {
				String esrDirOwnQuery = "SELECT esrRcbSupplier FROM EsrRcbSupplier esrRcbSupplier WHERE esrRcbSupplier.code in :codeList";
				TypedQuery<EsrRcbSupplier> directorOwnerquery = entityManager.createQuery(esrDirOwnQuery, EsrRcbSupplier.class);
				directorOwnerquery.setParameter("codeList", codeList);
				List<EsrRcbSupplier> esrRcbSuppliersList = directorOwnerquery.getResultList();
				List<ESRSupplierDirectorOwner> esrRcbDirListWithType = new ArrayList<ESRSupplierDirectorOwner>();
				for (EsrRcbSupplier esrRcbSupplier : esrRcbSuppliersList) {
					if (supplierDirOwnerMap.containsKey(esrRcbSupplier.getCode())) {
						for (ESRSupplierDirectorOwner esrSupplierDirectorOwner1 : supplierDirOwnerMap.get(esrRcbSupplier.getCode())) {
							esrSupplierDirectorOwner1.setDirOwnerType('E');
						}
						esrRcbDirListWithType.addAll(supplierDirOwnerMap.get(esrRcbSupplier.getCode()));
						supplierDirOwnerMap.remove(esrRcbSupplier.getCode());
					}

				}

				for (Map.Entry<String, List<ESRSupplierDirectorOwner>> entry : supplierDirOwnerMap.entrySet()) {
					esrRcbDirListWithType.addAll(entry.getValue());
				}

				return esrRcbDirListWithType;
			
			}
			else{
				directorOwners=new ArrayList<ESRSupplierDirectorOwner>();
			}	
		
		}catch (Exception e) {
			e.printStackTrace();
		}		

	return directorOwners;
}



}