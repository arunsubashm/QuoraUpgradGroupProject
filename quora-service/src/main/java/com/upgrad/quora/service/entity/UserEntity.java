package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
@NamedQueries(
        {
                @NamedQuery(name = "getUserByEmail", query = "select u from UserEntity u where u.email=:email"),
                @NamedQuery(name = "getUserByUserName", query = "select u from UserEntity u where u.userName=:username"),
                @NamedQuery(name = "fetchUserByUserId", query = "select u from UserEntity u where u.uuid=:uuid")

        }
)
public class UserEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "firstname")
    @NotNull
    @Size(max = 30)
    private String firstName;

    @Column(name = "lastname")
    @NotNull
    @Size(max = 30)
    private String lastName;

    @Column(name = "username")
    @NotNull
    @Size(max = 30)
    private String userName;

    @Column(name = "email")
    @NotNull
    @Size(max = 50)
    private String email;

    @Column(name = "password")
    @NotNull
    @Size(max = 255)
    private String password;

    @Column(name = "salt")
    @NotNull
    @Size(max = 200)
    private String salt;

    @Column(name = "country")
    @Size(max = 30)
    private String country;

    @Column(name = "aboutme")
    @Size(max = 50)
    private String aboutme;

    @Column(name = "dob")
    @Size(max = 200)
    private String dob;

    @Column(name = "role")
    @Size(max = 200)
    private String role;

    @Column(name = "contactnumber")
    @Size(max = 30)
    private String contactNumber;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return firstname
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return lastname
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return
     */
    public String getSalt() {
        return salt;
    }

    /**
     * @param salt
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * @return
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return aboutme
     */
    public String getAboutme() {
        return aboutme;
    }

    /**
     * @param aboutme
     */
    public void setAboutme(String aboutme) {
        this.aboutme = aboutme;
    }

    /**
     * @return dob
     */
    public String getDob() {
        return dob;
    }

    /**
     * @param dob users date of birth
     */
    public void setDob(String dob) {
        this.dob = dob;
    }

    /**
     * @return role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role role can be admin or non-admin
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return contactNumber
     */
    public String getContactNumber() {
        return contactNumber;
    }

    /**
     * @param contactNumber contactNumber
     */
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
