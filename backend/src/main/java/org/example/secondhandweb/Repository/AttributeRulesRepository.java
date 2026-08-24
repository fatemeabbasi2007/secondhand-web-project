package org.example.secondhandweb.Repository;

import org.example.secondhandweb.model.AttributeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttributeRulesRepository extends JpaRepository<AttributeRule, String> {

    List<AttributeRule> findByCategoryId(String categoryId);
}