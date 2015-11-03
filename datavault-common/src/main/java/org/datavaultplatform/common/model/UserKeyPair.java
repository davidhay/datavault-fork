package org.datavaultplatform.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.jsondoc.core.annotation.ApiObjectField;

import javax.persistence.*;

/**
 * User: Robin Taylor
 * Date: 29/10/2015
 * Time: 09:03
 */

@Table(name="UserKeyPairs")
public class UserKeyPair {


    // Deposit Identifier
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", unique = true)
    private String id;

    @Column(name = "privateKey", nullable = false)
    private String privateKey;

    @Column(name = "publicKey", nullable = false)
    private String publicKey;


    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }



}
