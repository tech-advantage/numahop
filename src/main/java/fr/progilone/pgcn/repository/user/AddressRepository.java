package fr.progilone.pgcn.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.progilone.pgcn.domain.user.Address;

public interface AddressRepository extends JpaRepository<Address, String> {
	
}
