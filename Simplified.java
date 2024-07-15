public class ToSimplify {

    public List<ESRSupplierDirectorOwner> getAcraLiveSupplierDirwners(String suppCode) {
        List<ESRSupplierDirectorOwner> resultList = new ArrayList<>();
        HashMap<String, ESRSupplierDirectorOwner> supplierMap = new HashMap<>();

        try {
            // Get all ESRSupplierDirectorOwner for the given supplier code
            TypedQuery<ESRSupplierDirectorOwner> directorOwnerQry = entityManager.createQuery(
                    "FROM ESRSupplierDirectorOwner WHERE companyCode=?1", ESRSupplierDirectorOwner.class);
            directorOwnerQry.setParameter(1, suppCode);
            List<ESRSupplierDirectorOwner> directorOwners = directorOwnerQry.getResultList();

            // Get all ESRSupplierDirOwnWH for each director owner
            TypedQuery<ESRSupplierDirOwnWH> acraQuery = entityManager.createQuery(
                    "FROM ESRSupplierDirOwnWH WHERE code=?1 AND companyCode=?2", ESRSupplierDirOwnWH.class);
            for (ESRSupplierDirectorOwner directorOwner : directorOwners) {
                if ('Y' == directorOwner.getLogicalDeleteFlag()) {
                    continue; // Skip logically deleted directors
                }

                acraQuery.setParameter(1, directorOwner.getCode());
                acraQuery.setParameter(2, directorOwner.getCompanyCode());
                List<ESRSupplierDirOwnWH> dirOwnWHsList = acraQuery.getResultList();

                for (ESRSupplierDirOwnWH dirOwnWH : dirOwnWHsList) {
                    ESRSupplierDirectorOwner esrSupplierDirectorOwner = new ESRSupplierDirectorOwner();
                    esrSupplierDirectorOwner.setCode(dirOwnWH.getCode());
                    esrSupplierDirectorOwner.setCompanyCode(dirOwnWH.getCompanyCode());
                    esrSupplierDirectorOwner.setEntryDate(dirOwnWH.getEntryDate());
                    esrSupplierDirectorOwner.setWithdrawDate(dirOwnWH.getWithdrawDate());
                    esrSupplierDirectorOwner.setName(dirOwnWH.getName());
                    esrSupplierDirectorOwner.setNationality(dirOwnWH.getNationality());
                    esrSupplierDirectorOwner.setPositionHeld(getPosition(dirOwnWH.getPositionHeld()));
                    esrSupplierDirectorOwner.setFbrn(dirOwnWH.getFbrn());

                    supplierMap.put(directorOwner.getCode(), esrSupplierDirectorOwner);
                }
            }

            // Get EsrRcbSupplier data and set DirOwnerType
            if (!supplierMap.isEmpty()) {
                List<String> codeList = new ArrayList<>(supplierMap.keySet());
                TypedQuery<EsrRcbSupplier> directorOwnerquery = entityManager.createQuery(
                        "SELECT esrRcbSupplier FROM EsrRcbSupplier esrRcbSupplier WHERE esrRcbSupplier.code in :codeList", EsrRcbSupplier.class);
                directorOwnerquery.setParameter("codeList", codeList);
                List<EsrRcbSupplier> esrRcbSuppliersList = directorOwnerquery.getResultList();

                for (EsrRcbSupplier esrRcbSupplier : esrRcbSuppliersList) {
                    ESRSupplierDirectorOwner owner = supplierMap.get(esrRcbSupplier.getCode());
                    if (owner != null) {
                        owner.setDirOwnerType('E');
                    }
                }

                resultList.addAll(supplierMap.values());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }

    private String getPosition(char positionCode) {
        HashMap<Character, String> positionMap = new HashMap<>();
        positionMap.put('D', "Director");
        positionMap.put('P', "Partner");
        positionMap.put('O', "Owner");
        return positionMap.get(positionCode);
    }
}
