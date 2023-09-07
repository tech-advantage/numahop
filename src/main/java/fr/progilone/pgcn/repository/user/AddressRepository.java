package fr.progilone.pgcn.repository.user;

import fr.progilone.pgcn.domain.user.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, String> {

}
