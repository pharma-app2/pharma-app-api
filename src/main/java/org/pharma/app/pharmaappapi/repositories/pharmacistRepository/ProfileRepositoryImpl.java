package org.pharma.app.pharmaappapi.repositories.pharmacistRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.*;

// O nome DEVE ser o nome da interface do repositório + "Impl"
public class ProfileRepositoryImpl implements ProfileRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Set<ProfileByParamsProjection> findProfilesByParams(
            String pharmacistName, String ibgeApiCity, String ibgeApiState, Boolean acceptsRemote) {

        // 1. Começamos com a parte da query que é sempre igual
        StringBuilder sql = new StringBuilder(
                "SELECT u.full_name AS pharmacistName, p.id, pl.address, p.accepts_remote AS acceptsRemote, " +
                        "pl.ibge_api_city AS ibgeApiCity, pl.ibge_api_state AS ibgeApiState " +
                        "FROM pharmacists p " +
                        "LEFT JOIN users u ON u.id = p.user_id " +
                        "INNER JOIN pharmacist_locations pl ON pl.pharmacist_id = p.id " +
                        "WHERE 1=1 " // Truque para facilitar a adição de cláusulas AND
        );

        // 2. Usamos um Map para guardar os parâmetros que forem necessários
        Map<String, Object> params = new HashMap<>();

        // 3. Adicionamos as cláusulas WHERE dinamicamente

        // O estado é sempre obrigatório
        sql.append("AND UPPER(TRIM(pl.ibge_api_state)) = UPPER(TRIM(:ibgeApiState)) ");
        params.put("ibgeApiState", ibgeApiState);

        if (pharmacistName != null && !pharmacistName.isBlank()) {
            sql.append("AND LOWER(unaccent(TRIM(u.full_name))) LIKE LOWER(unaccent(:pharmacistName)) ");
            params.put("pharmacistName", "%" + pharmacistName.trim() + "%");
        }

        if (ibgeApiCity != null && !ibgeApiCity.isBlank()) {
            sql.append("AND LOWER(unaccent(TRIM(pl.ibge_api_city))) LIKE LOWER(unaccent(:ibgeApiCity)) ");
            params.put("ibgeApiCity", "%" + ibgeApiCity.trim() + "%");
        }

        if (acceptsRemote != null) {
            sql.append("AND p.accepts_remote = :acceptsRemote ");
            params.put("acceptsRemote", acceptsRemote);
        }

        sql.append("ORDER BY u.full_name ASC");

        // 4. Criamos a query a partir da string SQL construída
        Query query = entityManager.createNativeQuery(sql.toString(), "ProfileByParamsProjectionMapping");

        // 5. Definimos os parâmetros
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        // Como o resultado não é uma entidade, precisamos mapear manualmente ou usar um ResultTransformer
        // A forma de mapear pode variar dependendo da sua projeção (interface vs classe)
        // A conversão para Set pode ser feita após a obtenção da lista.
        @SuppressWarnings("unchecked")
        List<ProfileByParamsProjection> resultList = query.getResultList();
        return new HashSet<>(resultList);
    }
}
