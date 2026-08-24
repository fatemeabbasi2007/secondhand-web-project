package org.example.secondhandweb.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity                          // Tells Hibernate: this is a database table
@Table(name = "attribute_rules")
public class AttributeRule {
    @Id
    @Column(name = "attribute_id", nullable = false)
    private String id;
    @Column(name = "category_id", nullable = false)
    private String categoryId;     // مثلاً id مربوط به "لپ‌تاپ"
    @Column(name = "attribute_name", nullable = false)
    private String attributeName;  // مثلاً "RAM"

    @JsonProperty("isRequired") // 👈 به جکسون می‌گوید این فیلد در JSON با نام isRequired خوانده و نوشته شود
    @Column(name = "required", nullable = false)
    private Boolean required;
    public boolean isRequired(){
        if (required){
            return true;
        }else{
            return false;
        }
    }
}
