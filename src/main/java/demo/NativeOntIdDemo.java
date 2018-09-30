package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.ontid.Attribute;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.info.IdentityInfo;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeOntIdDemo {


    public static void main(String[] args) {

        String password = "0t8JuUQVF2QRvZ1n";
        final class IdentityData {
            public String Id;
            public String Name;
            public String Birthday;
            public Identity Identity;
            public Transaction Tx;
            public Attribute[] Attributes =new Attribute[2];
            public IdentityData(String id, String name, String birth){
                Id = id;
                Name = name;
                Birthday = birth;
            }
    
        }

        IdentityData[] data = new IdentityData[]{
            new IdentityData("52250219601117581x", "曹正喜", "1960-11-17"),
            new IdentityData("52250219601117556x", "曹正", "1960-11-18"),
            new IdentityData("52250219601117589y", "曹喜", "1960-11-27"),
            new IdentityData("522502196011175800", "曹操", "1960-11-37"),
            // new IdentityData("52250219601117581x", "曹正喜", "1960-11-17"),
            // new IdentityData("52250219601117556x", "曹正", "1960-11-18"),
            // new IdentityData("52250219601117589y", "曹喜", "1960-11-27"),
            // new IdentityData("522502196011175800", "曹操", "1960-11-37"),
            // new IdentityData("52250219601117581x", "曹正喜", "1960-11-17"),
            // new IdentityData("52250219601117556x", "曹正", "1960-11-18"),
            // new IdentityData("52250219601117589y", "曹喜", "1960-11-27"),
            // new IdentityData("522502196011175800", "曹操", "1960-11-37"),
            // new IdentityData("52250219601117581x", "曹正喜", "1960-11-17"),
            // new IdentityData("52250219601117556x", "曹正", "1960-11-18"),
            // new IdentityData("52250219601117589y", "曹喜", "1960-11-27"),
            // new IdentityData("522502196011175800", "曹操", "1960-11-37"),                                    
        };


        try {
            OntSdk ontSdk = getOntSdk();
            Account payer = ontSdk.getWalletMgr().createAccount(password);
            com.github.ontio.account.Account payerAcct = ontSdk.getWalletMgr().getAccount(payer.address,password,ontSdk.getWalletMgr().getWallet().getAccount(payer.address).getSalt());
            String privatekey0 = "c19f16785b8f3543bbaf5e1dbb5d398dfa6c85aaad54fc9d71203ce83e505c07";
            String privatekey1 = "2ab720ff80fcdd31a769925476c26120a879e235182594fbb57b67c0743558d7";
            com.github.ontio.account.Account account1 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1),SignatureScheme.SHA256WITHECDSA);


            if (true){
                // prepare 
                for(int i=0; i< data.length; i++){
                    ontSdk.openWalletFile(data[i].Id+".json");
                    data[i].Identity = ontSdk.getWalletMgr().createIdentity(password);
                    data[i].Attributes[0] = new Attribute("name".getBytes(),"String".getBytes(),data[i].Name.getBytes());
                    data[i].Attributes[1] = new Attribute("birthday".getBytes(),"String".getBytes(),data[i].Birthday.getBytes());                    
                    IdentityInfo info = ontSdk.getWalletMgr().getIdentityInfo(data[i].Identity.ontid, password,data[i].Identity.controls.get(0).getSalt());
                    String ontid = info.ontid;
                    data[i].Tx = ontSdk.nativevm().ontId().makeRegisterWithAttrs(ontid, password,data[i].Identity.controls.get(0).getSalt(), data[i].Attributes, payerAcct.getAddressU160().toBase58(), ontSdk.DEFAULT_GAS_LIMIT, 0);
                    ontSdk.signTx(data[i].Tx, ontid, password,data[i].Identity.controls.get(0).getSalt());
                    ontSdk.addSign(data[i].Tx, payerAcct);
                    ontSdk.getWalletMgr().getWallet().addOntIdController(ontid, info.encryptedPrikey, info.ontid,info.pubkey);
                    ontSdk.getWalletMgr().writeWallet();   
                }   
             

                //work 
                long t1 = System.currentTimeMillis();
                for(int i=0; i< data.length; i++){
                    // ontSdk.openWalletFile(data[i].Id+".json");
                    ontSdk.getConnect().sendRawTransaction(data[i].Tx.toHexString());
                    // ontSdk.nativevm().ontId().sendRegisterWithAttrs(data[i].Identity,password,data[i].Attributes,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
                }
                // for(int i=0; i< data.length; i++){
                //     ontSdk.openWalletFile(data[i].Id+".json");
                //     ontSdk.nativevm().ontId().sendRegister(data[i].Identity,password,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
                // }
                // for(int i=0; i< data.length; i++){
                //     System.out.println("Attribute - "+i);
                //     ontSdk.openWalletFile(data[i].Id+".json");
                //     ontSdk.nativevm().ontId().sendAddAttributes(data[i].Identity.ontid,password,data[i].Identity.controls.get(0).getSalt(),data[i].Attributes,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
                // }
                long t2 = System.currentTimeMillis();
                System.out.println("time is :" + (t2 -t1)/1000.0 );


                // show result 
                Thread.sleep(6000);
                for(int i=0; i< data.length; i++){                
                    String ddo = ontSdk.nativevm().ontId().sendGetDDO(data[i].Identity.ontid);
                    System.out.println(ddo);
                }
                
                System.exit(0);                              
            }

            if(false){
                Identity identity3 = ontSdk.getWalletMgr().createIdentity(password);
                Attribute[] attributes = new Attribute[1];
                attributes[0] = new Attribute("key1".getBytes(),"String".getBytes(),"value1".getBytes());
                ontSdk.nativevm().ontId().sendRegisterWithAttrs(identity3,password,attributes,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
                ontSdk.getWalletMgr().writeWallet();
                Thread.sleep(6000);
                String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity3.ontid);
                System.out.println(ddo);
                String attr = ontSdk.nativevm().ontId().sendGetAttributes(identity3.ontid);
                System.out.println(attr);
                System.exit(0);
            }
            if(true){
                if(ontSdk.getWalletMgr().getWallet().getIdentities().size() < 1){
                    Identity identity = ontSdk.getWalletMgr().createIdentity(password);
                    ontSdk.nativevm().ontId().sendRegister(identity,password,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
                    ontSdk.getWalletMgr().writeWallet();
                    Thread.sleep(6000);
                }
                Identity identity = ontSdk.getWalletMgr().getWallet().getIdentities().get(0);
               String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
               System.out.println(ddo);

                Attribute[] attributes = new Attribute[1];
                attributes[0] = new Attribute("key4".getBytes(),"String6".getBytes(),"value7".getBytes());
                byte[] salt = identity.controls.get(0).getSalt();
               ontSdk.nativevm().ontId().sendAddAttributes(identity.ontid,password,identity.controls.get(0).getSalt(),attributes,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
//                ontSdk.nativevm().ontId().sendRemoveAttribute(identity.ontid,password,identity.controls.get(0).getSalt(),"key1",payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
//                ontSdk.nativevm().ontId().sendAddRecovery(identity.ontid,password,salt,account1.getAddressU160().toBase58(),payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
//                ontSdk.nativevm().ontId().sendAddPubKey(identity.ontid,password,salt,Helper.toHexString(account1.serializePublicKey()),payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
                // ontSdk.nativevm().ontId().sendRemovePubKey(identity.ontid,password,salt,Helper.toHexString(account1.serializePublicKey()),payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                String ddo2 = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
                System.out.println(ddo2);
                System.out.println(account1.getAddressU160().toBase58());
                System.exit(0);
            }
            Account account = ontSdk.getWalletMgr().createAccountFromPriKey(password,privatekey0);
            if(ontSdk.getWalletMgr().getWallet().getIdentities().size() < 3){
                Identity identity = ontSdk.getWalletMgr().createIdentity(password);
                Transaction tx = ontSdk.nativevm().ontId().makeRegister(identity.ontid,password,new byte[]{},payer.address,ontSdk.DEFAULT_GAS_LIMIT,0);
                ontSdk.signTx(tx,identity.ontid.replace(Common.didont,""),password,new byte[]{});
                ontSdk.addSign(tx,payerAcct);
                ontSdk.getConnect().sendRawTransaction(tx);

                Identity identity2 = ontSdk.getWalletMgr().createIdentity(password);
                ontSdk.nativevm().ontId().sendRegister(identity2,password,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);

                Identity identity3 = ontSdk.getWalletMgr().createIdentity(password);
                Attribute[] attributes = new Attribute[1];
                attributes[0] = new Attribute("key1".getBytes(),"String".getBytes(),"value1".getBytes());
                ontSdk.nativevm().ontId().sendRegisterWithAttrs(identity3,password,attributes,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
                ontSdk.getWalletMgr().writeWallet();
                Thread.sleep(6000);

            }
            List<Identity> dids = ontSdk.getWalletMgr().getWallet().getIdentities();
            System.out.println("dids.get(0).ontid:" + dids.get(0).ontid);
//            System.out.println("dids.get(1).ontid:" + dids.get(1).ontid);
//            System.out.println("dids.get(2).ontid:" + dids.get(2).ontid);
            String ddo1 = ontSdk.nativevm().ontId().sendGetDDO(dids.get(0).ontid);
//            String publicKeys = ontSdk.nativevm().ontId().sendGetPublicKeys(dids.get(0).ontid);
//            String ddo2 = ontSdk.nativevm().ontId().sendGetDDO(dids.get(1).ontid);
//            String ddo3 = ontSdk.nativevm().ontId().sendGetDDO(dids.get(2).ontid);

            System.out.println("ddo1:" + ddo1);
//            System.out.println("ddo2:" + ddo2);
//            System.out.println("ddo3:" + ddo3);

            IdentityInfo info2 = ontSdk.getWalletMgr().getIdentityInfo(dids.get(1).ontid,password,new byte[]{});
            IdentityInfo info3 = ontSdk.getWalletMgr().getIdentityInfo(dids.get(2).ontid,password,new byte[]{});

            com.github.ontio.account.Account acct = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey0),SignatureScheme.SHA256WITHECDSA);
            com.github.ontio.account.Account acct2 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1),SignatureScheme.SHA256WITHECDSA);
            Address multiAddr = Address.addressFromMultiPubKeys(2,acct.serializePublicKey(),acct2.serializePublicKey());

            if(false){
                Account account2 = ontSdk.getWalletMgr().createAccountFromPriKey(password, privatekey1);
//                ontSdk.nativevm().ontId().sendChangeRecovery(dids.get(0).ontid,account2.address,account.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
                String txhash2 = ontSdk.nativevm().ontId().sendAddRecovery(dids.get(0).ontid,password,new byte[]{},multiAddr.toBase58(),payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
                Thread.sleep(6000);
                Object obj = ontSdk.getConnect().getSmartCodeEvent(txhash2);
                System.out.println(obj);
                System.out.println(ontSdk.nativevm().ontId().sendGetDDO(dids.get(0).ontid));
            }

            if(false){
                ontSdk.nativevm().ontId().sendAddPubKey(dids.get(0).ontid,password,new byte[]{},info3.pubkey,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
                ontSdk.nativevm().ontId().sendRemovePubKey(dids.get(0).ontid,account.address,password,new byte[]{},info2.pubkey,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
                ontSdk.nativevm().ontId().sendAddPubKey(dids.get(0).ontid,account.address,password,new byte[]{},info2.pubkey,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
                Transaction tx = ontSdk.nativevm().ontId().makeAddPubKey(dids.get(0).ontid,multiAddr.toBase58(),null,info2.pubkey,payer.address,ontSdk.DEFAULT_GAS_LIMIT,0);
    //          ontSdk.signTx(tx,new com.github.ontio.account.Account[][]{{acct,acct2}});
    //          ontSdk.addSign(tx,payerAcc.address,password);
    //          ontSdk.getConnect().sendRawTransaction(tx.toHexString());
            }


            if(false){
                String ddo4 = ontSdk.nativevm().ontId().sendGetDDO(dids.get(0).ontid);
                System.out.println("ddo4:" + ddo4);
                System.exit(0);
                System.out.println("ddo1:" + ddo1);
                System.out.println("publicKeysState:" + ontSdk.nativevm().ontId().sendGetKeyState(dids.get(0).ontid,1));
                System.out.println("attributes:" + ontSdk.nativevm().ontId().sendGetAttributes(dids.get(0).ontid));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static OntSdk getOntSdk() throws Exception {
        String ip = "http://127.0.0.1";
//        String ip = "http://polaris1.ont.io";
//        String ip = "http://139.219.129.55";
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());

        wm.openWalletFile("NativeOntIdDemo.json");
        return wm;
    }
}
