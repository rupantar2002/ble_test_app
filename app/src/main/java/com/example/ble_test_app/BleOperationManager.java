package com.example.ble_test_app;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BleOperationManager {

    private Queue<BleOperation> mOperations=new LinkedList<>();

    private BleOperation mCurrentOperation;


    public synchronized void request(BleOperation operation){
        mOperations.add(operation);
        if(mCurrentOperation==null){
            mCurrentOperation=mOperations.poll();
            //perform current operation
            mCurrentOperation.perform();
        }
    }

    public synchronized void operationCompleted(){
        mCurrentOperation=null;
        if(mOperations.peek()!=null){
            mCurrentOperation=mOperations.poll();
            //perform
            mCurrentOperation.perform();
        }
    }
}
