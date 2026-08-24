package org.example.secondhandweb.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity                          // Tells Hibernate: this is a database table
@Table(name = "categories")
public class Category {
    @Id
    @Column(name = "category_id", nullable = false)
    private String id;          // Unique ID like "ELECTRONICS" or "LAPTOPS"
    @Column(name = "name", length = 72, nullable = false)
    private String name;        // Display name like "Electronics" or "Laptops"
    @Column(name = "parent_category_id", nullable = true)
    private String parentCategoryId;


}
