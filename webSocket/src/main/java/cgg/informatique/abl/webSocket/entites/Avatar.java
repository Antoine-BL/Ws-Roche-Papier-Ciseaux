package cgg.informatique.abl.webSocket.entites;

import javax.persistence.*;

@Entity
@Table(name="AVATARS")
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = Integer.MAX_VALUE)
    private String image;

    public Avatar() {}

    public Avatar(String imageB64) {
        this.image = imageB64;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String imageB64) {
        this.image = imageB64;
    }
}
