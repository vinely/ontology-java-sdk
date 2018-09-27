/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.ontio.smartcontract.neovm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Common;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.neovm.abi.AbiFunction;
import com.github.ontio.smartcontract.neovm.abi.AbiInfo;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static demo.NeoVmDemo.abi;

public class SimpleStorage {
    private OntSdk sdk;
    private String contractAddress = "ad6b2d3dad99381e2670b3e2f379c15d1e940178";

    private String abi = "{\"hash\":\"0xad6b2d3dad99381e2670b3e2f379c15d1e940178\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"Post\",\"parameters\":[{\"name\":\"key\",\"type\":\"ByteArray\"},{\"name\":\"value\",\"type\":\"ByteArray\"}],\"returntype\":\"Boolean\"},{\"name\":\"Put\",\"parameters\":[{\"name\":\"key\",\"type\":\"ByteArray\"},{\"name\":\"value\",\"type\":\"ByteArray\"}],\"returntype\":\"Boolean\"},{\"name\":\"Get\",\"parameters\":[{\"name\":\"key\",\"type\":\"ByteArray\"}],\"returntype\":\"ByteArray\"},{\"name\":\"Remove\",\"parameters\":[{\"name\":\"key\",\"type\":\"ByteArray\"}],\"returntype\":\"Boolean\"}],\"events\":[{\"name\":\"ErrorMsg\",\"parameters\":[{\"name\":\"id\",\"type\":\"ByteArray\"},{\"name\":\"error\",\"type\":\"String\"}],\"returntype\":\"Void\"}]}";

    public SimpleStorage(OntSdk sdk) {
        this.sdk = sdk;
    }

    public void setContractAddress(String codeHash) {
        this.contractAddress = codeHash.replace("0x", "");
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public String sendPost(String issuerOntid, String password, byte[] salt, String subjectOntid, String key, String value, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if(issuerOntid==null||issuerOntid.equals("")||password==null||password.equals("")||subjectOntid==null||subjectOntid.equals("")
                ||payerAcct == null){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("gaslimit or gasprice is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = issuerOntid.replace(Common.didont,"");
        Transaction tx = makeStorage("Post", key ,value,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx, addr, password,salt);
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public String sendPut(String issuerOntid, String password, byte[] salt, String subjectOntid, String key, String value, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        if(issuerOntid==null||issuerOntid.equals("")||password==null||password.equals("")||subjectOntid==null||subjectOntid.equals("")
                ||payerAcct == null){
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if(gaslimit < 0 || gasprice < 0){
            throw new SDKException(ErrorCode.ParamErr("gaslimit or gasprice is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        String addr = issuerOntid.replace(Common.didont,"");
        Transaction tx = makeStorage("Put", key ,value, payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx, addr, password,salt);
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }   

    public Transaction makeStorage(String method, String key, String value, String payer, long gaslimit, long gasprice)throws Exception{
        AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
        AbiFunction func = abiinfo.getFunction(method);
        func.setParamsValue(key.getBytes(), value.getBytes());
        byte[] params = BuildParams.serializeAbiFunction(func);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddress), null, params, payer,gaslimit, gasprice);
        return tx;    
    }

    public String sendRemove(String key) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if (key == null || key == ""){
            throw new SDKException(ErrorCode.NullKeyOrValue);
        }
        AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
        String name = "Remove";
        AbiFunction func = abiinfo.getFunction(name);
        func.name = name;
        func.setParamsValue(key.getBytes());
        Object obj =  sdk.neovm().sendTransaction(Helper.reverse(contractAddress),null,null,0,0,func, true);
        String res = ((JSONObject)obj).getString("Result");
        if(res.equals("")){
            return "";
        }
        return new String(Helper.hexToBytes(res));
    }

    public String sendGet(String key) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if (key == null || key == ""){
            throw new SDKException(ErrorCode.NullKeyOrValue);
        }
        AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
        String name = "Get";
        AbiFunction func = abiinfo.getFunction(name);
        func.name = name;
        func.setParamsValue(key.getBytes());
        Object obj =  sdk.neovm().sendTransaction(Helper.reverse(contractAddress),null,null,0,0,func, true);
        String res = ((JSONObject)obj).getString("Result");
        if(res.equals("")){
            return "";
        }
        return new String(Helper.hexToBytes(res));
    }
}
