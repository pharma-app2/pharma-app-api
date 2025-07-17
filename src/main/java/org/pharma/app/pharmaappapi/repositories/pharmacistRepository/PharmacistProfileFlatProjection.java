package org.pharma.app.pharmaappapi.repositories.pharmacistRepository;

public interface PharmacistProfileFlatProjection {
    String getFullName();
    String getEmail();
    Boolean getAcceptsRemote();
    String getCrf();
    String getPlanName();
    String getAddress();
    String getPhone1();
    String getPhone2();
    String getPhone3();
    Integer getIbgeApiCityId();
    String getIbgeApiCity();
    String getIbgeApiState();
    String getModality();
}
