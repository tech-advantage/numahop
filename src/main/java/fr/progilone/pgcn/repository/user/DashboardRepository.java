package fr.progilone.pgcn.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.progilone.pgcn.domain.user.Dashboard;


public interface DashboardRepository extends JpaRepository<Dashboard, String> {

}
