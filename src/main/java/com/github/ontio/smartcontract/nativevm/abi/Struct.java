package com.github.ontio.smartcontract.nativevm.abi;
import java.util.ArrayList;
import java.util.List;


public class Struct {
    public List<Object> list = new ArrayList<Object>();
    public Struct(){

    }
    public Struct add(Object... objs){
        for(int i=0;i<objs.length;i++){
            list.add(objs[i]);
        }
        return this;
    }
}
