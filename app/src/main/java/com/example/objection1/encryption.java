package com.example.objection1;

import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.util.KeyHelper;

import java.util.List;

public class encryption {

    public void whenInstall(){
        IdentityKeyPair identityKeyPair = KeyHelper.generateIdentityKeyPair();
        int                registrationId  = KeyHelper.generateRegistrationId(false);
       /* List<PreKeyRecord> preKeys         = KeyHelper.generatePreKeys(startId, 100);*/
      /*  SignedPreKeyRecord signedPreKey    = KeyHelper.generateSignedPreKey(identityKeyPair, 5);*/
    }
}
