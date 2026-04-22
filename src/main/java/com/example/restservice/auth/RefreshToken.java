package com.example.restservice.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;

import org.springframework.security.crypto.codec.Hex;

import com.example.restservice.model.Utilisateur;

import jakarta.persistence.*;


@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(columnList = "token_hash")
})

public class RefreshToken {
    @Id @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "issue_at") private Instant issueAt;
    @Column(name = "expire_at") private Instant expireAt;
    @Column(name = "revoked") private Boolean revoked = false;

    @Column(name = "replaced_by") private String replacedBy;

    public RefreshToken() {

    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    public String getTokenHash() {
        return tokenHash;
    }
    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }
    public Instant getIssueAt() {
        return issueAt;
    }
    public void setIssueAt(Instant issueAt) {
        this.issueAt = issueAt;
    }
    public Instant getExpireAt() {
        return expireAt;
    }
    public void setExpireAt(Instant expireAt) {
        this.expireAt = expireAt;
    }
    public Boolean getRevoked() {
        return revoked;
    }
    public void setRevoked(Boolean revoked) {
        this.revoked = revoked;
    }
    public String getReplacedBy() {
        return replacedBy;
    }
    public void setReplacedBy(String replacedBy) {
        this.replacedBy = replacedBy;
    }

    // public String hash(String token){
    //     MessageDigest md = MessageDigest.getInstance("SHA-256");
    //     byte[] digest = md.digest(token.getBytes(StandardCharsets.UTF_8));
    //     return Hex.encodeHexString(digest);
    // }


}
