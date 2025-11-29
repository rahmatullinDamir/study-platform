package rahmatullin.dev.studyplatform.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import rahmatullin.dev.studyplatform.models.enums.UserRoles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "account")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String hashPassword;

    private String firstName;
    private String lastName;

    @Enumerated(value = EnumType.STRING)
    private UserRoles role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return email != null && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }
}
