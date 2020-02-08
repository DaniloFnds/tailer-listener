package br.com.fromtis.tailorlistener.repository;

import br.com.fromtis.tailorlistener.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigDao extends JpaRepository<Config, Integer> {
}
