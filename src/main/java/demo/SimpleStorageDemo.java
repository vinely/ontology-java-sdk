package demo;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.SignatureScheme;


public class SimpleStorageDemo {
    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();

            com.github.ontio.account.Account account1 = new com.github.ontio.account.Account(Helper.hexToBytes("75de8489fcb2dcaf2ef3cd607feffde18789de7da129b5e97c81e001793cb7cf"),SignatureScheme.SHA256WITHECDSA);
            long t1,t2;


            t1 = System.currentTimeMillis();

            String testKey = "MykeyRing1";
            String testValue= "asdfajt4twetewjor23rv";
            String commitHash = ontSdk.neovm().SimpleStorage().sendPost(testKey, testValue ,account1,ontSdk.DEFAULT_GAS_LIMIT,0);
            System.out.println("commitRes:" + commitHash);

            t2 = System.currentTimeMillis();
            System.out.println("time is :" + (t2 -t1)/1000.0 );
            Thread.sleep(6000);

            t1 = System.currentTimeMillis();
            
            String getstatusRes = ontSdk.neovm().SimpleStorage().sendGet(testKey);
            System.out.println("[Get]:" + getstatusRes);

            t2 = System.currentTimeMillis();
            System.out.println("time is :" + (t2 -t1)/1000.0 );
            Thread.sleep(6000);

            t1 = System.currentTimeMillis();

            testValue= "1234567";
            String revokeHash = ontSdk.neovm().SimpleStorage().sendPost(testKey, testValue ,account1,ontSdk.DEFAULT_GAS_LIMIT,0);
            System.out.println("revokeRes:" + revokeHash);

            t2 = System.currentTimeMillis();
            System.out.println("time is :" + (t2 -t1)/1000.0 );
            Thread.sleep(6000);

            t1 = System.currentTimeMillis();
            //testKey ="dsf";
            String getstatusRes2 = ontSdk.neovm().SimpleStorage().sendGet(testKey);
            System.out.println("[Get2]:" + getstatusRes2);

            t2 = System.currentTimeMillis();
            System.out.println("time is :" + (t2 -t1)/1000.0 );

            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {

        String ip = "http://127.0.0.1";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";


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

