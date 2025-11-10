package co.edu.icesi.sidgymicesi.services.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.ContractType;

import java.util.List;
import java.util.Optional;

public interface IContractTypeService {

    ContractType save(ContractType contractType);

    List<ContractType> findAll();

    Optional<ContractType> findById(String name);

    void deleteById(String name);
}
