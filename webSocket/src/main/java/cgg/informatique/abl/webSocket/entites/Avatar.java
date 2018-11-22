package cgg.informatique.abl.webSocket.entites;

import javax.persistence.*;

@Entity
@Table(name="AVATAR")
public class Avatar {
    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = Integer.MAX_VALUE, name = "AVATAR")
    private String image;

    public Avatar() {}

    public Avatar(String imageB64) {
        this.image = imageB64;
    }

    public Avatar(Long id) {
        this.id = id;
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
