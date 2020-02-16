package cn.itcast.domain.admin;

import javax.persistence.*;

@Entity
@Table(name = "t_admin")
public class AdminModel {

    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "t_username",length = 20)
    private String username;
    @Column(name = "t_password",length = 20)
    private String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
