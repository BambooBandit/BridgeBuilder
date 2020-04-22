package com.bamboo.bridgebuilder;

/** Used to link textfields together by seeing all the same textfields, making lambdas to change them, change the original textfield then loop over the lambdas*/
public interface TextFieldAction
{
    void action();
}