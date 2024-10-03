package com.asap.persistence.jpa.letter.entity

import com.asap.persistence.jpa.common.AggregateRoot
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "receive_letter")
class ReceiveLetterEntity(
    id: String,
) : AggregateRoot<ReceiveLetterEntity>(id)
