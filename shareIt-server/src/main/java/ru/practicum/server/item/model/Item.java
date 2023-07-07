package ru.practicum.server.item.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.request.model.ItemRequest;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "items", schema = "public")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "name")
    String name;
    @Column(name = "description")
    String description;
    @Column(name = "available")
    Boolean available;
    @Column(name = "owner")
    int owner;
    @OneToMany(
            targetEntity = Booking.class,
            mappedBy = "item",
            fetch = FetchType.EAGER
    )
    @JsonManagedReference
    List<Booking> bookings;
    @OneToMany(
            targetEntity = Comment.class,
            mappedBy = "item",
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    List<Comment> comments;
    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    @JsonBackReference
    ItemRequest request;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        return id != 0 && id == (((Item) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
