package com.example.axoncrud.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "gift")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GiftCardEntity {

	@Id
	private UUID id;

	private BigDecimal amount;

}
