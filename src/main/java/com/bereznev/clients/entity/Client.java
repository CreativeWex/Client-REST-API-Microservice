package com.bereznev.clients.entity;
/*
    =====================================
    @project ClientMicroservice
    @author Bereznev Nikita @CreativeWex
    =====================================
 */

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotEmpty(message = "wrong first name")
    @Size(min = 2, max = 50, message = "Parameter's length must be between 2 and 50 symbols")
    private String firstName;

    @Column(nullable = false)
    @NotEmpty(message = "wrong last name")
    private String lastName;

    @Column(nullable = false)
    @NotEmpty(message = "Wrong email")
    @Email(message = "Wrong email")
    private String email;
}
