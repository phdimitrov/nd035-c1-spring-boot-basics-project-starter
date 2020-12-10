package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CredentialService {

    private final CredentialMapper credentialMapper;

    public CredentialService(CredentialMapper credentialMapper) {
        this.credentialMapper = credentialMapper;
    }

    public List<Credential> getCredentialsByUserId(Integer userId) {
        return credentialMapper.findByUserId(userId);
    }

    public void saveCredential(Credential credential) {
        if (credential.getCredentialId() == null) {
            credentialMapper.insert(credential);
        } else {
            credentialMapper.update(credential.getCredentialId(), credential.getUrl(), credential.getUsername(),
                    credential.getKey(), credential.getPassword());
        }
    }

    public void deleteCredential(Integer credentialId) {
        credentialMapper.delete(credentialId);
    }
}
