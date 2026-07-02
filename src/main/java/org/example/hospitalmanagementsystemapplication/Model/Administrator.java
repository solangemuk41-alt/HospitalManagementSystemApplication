package org.example.hospitalmanagementsystemapplication.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "administrators")
@EqualsAndHashCode(callSuper = true)
public class Administrator extends User {
    @OneToOne(mappedBy = "administrator")
    private Hospital hospital;
}