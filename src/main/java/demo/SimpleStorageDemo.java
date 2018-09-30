package demo;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.block.Block;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleStorageDemo {
    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();
            String password = "111111";

            Account payerAccInfo = ontSdk.getWalletMgr().createAccount(password);
            com.github.ontio.account.Account payerAcc = ontSdk.getWalletMgr().getAccount(payerAccInfo.address,password,payerAccInfo.getSalt());

            if (ontSdk.getWalletMgr().getWallet().getIdentities().size() < 2) {
                Identity identity = ontSdk.getWalletMgr().createIdentity(password);
                ontSdk.nativevm().ontId().sendRegister(identity,password,payerAcc,ontSdk.DEFAULT_GAS_LIMIT,0);
                Identity identity2 = ontSdk.getWalletMgr().createIdentity(password);
                ontSdk.nativevm().ontId().sendRegister(identity2,password,payerAcc,ontSdk.DEFAULT_GAS_LIMIT,0);
                ontSdk.getWalletMgr().writeWallet();
                Thread.sleep(6000);
            }

            List<Identity> dids = ontSdk.getWalletMgr().getWallet().getIdentities();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("Issuer", dids.get(0).ontid);
            map.put("Subject", dids.get(1).ontid);
            Map clmRevMap = new HashMap();
            clmRevMap.put("typ","AttestContract");
            clmRevMap.put("addr",dids.get(1).ontid.replace(Common.didont,""));
            String claim = ontSdk.nativevm().ontId().createOntIdClaim(dids.get(0).ontid,password,dids.get(0).controls.get(0).getSalt(), "claim:context", map, map,clmRevMap,System.currentTimeMillis()/1000 +100000);
            boolean b = ontSdk.nativevm().ontId().verifyOntIdClaim(claim);


            Account account = ontSdk.getWalletMgr().importAccount("blDuHRtsfOGo9A79rxnJFo2iOMckxdFDfYe2n6a9X+jdMCRkNUfs4+C4vgOfCOQ5","111111","AazEvfQPcQ2GEFFPLF1ZLwQ7K5jDn81hve",Base64.getDecoder().decode("0hAaO6CT+peDil9s5eoHyw=="));
            AccountInfo info = ontSdk.getWalletMgr().getAccountInfo(account.address,"111111",account.getSalt());
            com.github.ontio.account.Account account1 = new com.github.ontio.account.Account(Helper.hexToBytes("75de8489fcb2dcaf2ef3cd607feffde18789de7da129b5e97c81e001793cb7cf"),SignatureScheme.SHA256WITHECDSA);


            String[] claims = claim.split("\\.");

            JSONObject payload = JSONObject.parseObject(new String(Base64.getDecoder().decode(claims[1].getBytes())));

            System.out.println("ClaimId:" + payload.getString("jti"));
            
            String testKey = "MykeyRing1";
            String testValue= "asdfajt4twetewjor23rv";


            String commitHash = ontSdk.neovm().SimpleStorage().sendPost(dids.get(0).ontid,password,dids.get(0).controls.get(0).getSalt(),dids.get(1).ontid,testKey, testValue ,account1,ontSdk.DEFAULT_GAS_LIMIT,0);
            System.out.println("commitRes:" + commitHash);
            Thread.sleep(6000);

            String getstatusRes = ontSdk.neovm().SimpleStorage().sendGet(testKey);
            System.out.println("[Get]:" + getstatusRes);
            Thread.sleep(6000);

            
            testValue= "1234567";
            String revokeHash = ontSdk.neovm().SimpleStorage().sendPost(dids.get(0).ontid,password,dids.get(0).controls.get(0).getSalt(),dids.get(1).ontid,testKey, testValue ,account1,ontSdk.DEFAULT_GAS_LIMIT,0);
            System.out.println("revokeRes:" + revokeHash);
            Thread.sleep(6000);
            //testKey ="dsf";
            String getstatusRes2 = ontSdk.neovm().SimpleStorage().sendGet(testKey);

            System.out.println("[Get2]:" + getstatusRes2);

            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {

        String ip = "http://127.0.0.1";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        //String ip = "http://120.79.231.116";
        // String ip = "http://47.95.1.199";
        // String restUrl = ip + ":" + "8013";
        // String rpcUrl = ip + ":" + "8012";
        // String wsUrl = ip + ":" + "8014";        

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());

        wm.openWalletFile("SimpleStorageDemo.json");

        return wm;
    }
}

